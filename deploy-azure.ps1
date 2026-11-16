<#
.SYNOPSIS
  Creates the Azure resources for RecordingLogs and deploys the backend and frontend.

.DESCRIPTION
  Resources (all in resource group "recordinglogs-rg"):
    - Azure Database for PostgreSQL flexible server  (Burstable B1ms, 32 GB)  recordinglogs-db-<suffix>
    - App Service plan, Linux, Basic B1 (shared by both apps)                 recordinglogs-plan
    - Web app, Java 21 (Spring Boot backend)                                  recordinglogs-api-<suffix>
    - Web app, Node 24 (Next.js frontend)                                     recordinglogs-web-<suffix>

  The script is safe to run again: existing resources are skipped, and the code is rebuilt
  and redeployed. Generated secrets (DB password, JWT secret) are stored in
  .azure-deploy.local.json next to this script. That file is git-ignored; keep it private.

.EXAMPLE
  powershell -ExecutionPolicy Bypass -File .\deploy-azure.ps1 -Suffix rokas
  powershell -ExecutionPolicy Bypass -File .\deploy-azure.ps1 -Suffix rokas -Only frontend
#>
param(
    # Makes the names globally unique, e.g. "rokas" -> recordinglogs-api-rokas.azurewebsites.net
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^[a-z0-9]{3,12}$')]
    [string]$Suffix,

    # Azure region. "Azure for Students" only allows some regions; see them with:
    #   az policy assignment list --disable-scope-strict-match --query "[].parameters.listOfAllowedLocations.value" -o tsv
    # For this subscription: polandcentral, francecentral, swedencentral, italynorth, spaincentral.
    [string]$Location = "polandcentral",

    # First region to try for the database. If B1ms is refused there, the other allowed
    # regions are tried in turn (Poland Central last, since it refused B1ms before).
    [string]$DbLocation = "swedencentral",

    # Run only part of the deployment.
    [ValidateSet("all", "infra", "backend", "frontend")]
    [string]$Only = "all"
)

[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

# Regions this "Azure for Students" subscription may use (from its region policy).
$AllowedRegions = @("polandcentral", "swedencentral", "francecentral", "italynorth", "spaincentral")

$Root        = $PSScriptRoot
$StateFile   = Join-Path $Root ".azure-deploy.local.json"
$RG          = "recordinglogs-rg"
$Plan        = "recordinglogs-plan"
$Db          = "recordinglogs-db-$Suffix"
$DbName      = "music_platform"
$Api         = "recordinglogs-api-$Suffix"
$Web         = "recordinglogs-web-$Suffix"

# ---------------------------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------------------------
function Step([string]$Text) {
    Write-Host ""
    Write-Host "==> $Text" -ForegroundColor Cyan
}

function Info([string]$Text) {
    Write-Host "    $Text" -ForegroundColor DarkGray
}

# Runs an az command and stops the script if it fails. The error message only shows the
# command name (not the arguments) so that secrets are never printed.
function Invoke-Az {
    $output = & az @args
    if ($LASTEXITCODE -ne 0) {
        throw "Azure CLI command failed: az $($args[0]) $($args[1]) $($args[2]) (see the error above)"
    }
    return $output
}

function New-Secret([int]$Length) {
    $chars = [char[]]'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789'
    $bytes = New-Object byte[] $Length
    [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
    return -join ($bytes | ForEach-Object { $chars[$_ % $chars.Length] })
}

# Polls a URL until it answers with HTTP 200 (or the timeout passes).
function Wait-ForUrl([string]$Url, [int]$TimeoutSeconds) {
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        try {
            $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 30
            if ($response.StatusCode -eq 200) { return $true }
        } catch { }
        Write-Host "." -NoNewline
        Start-Sleep -Seconds 10
    }
    Write-Host ""
    return $false
}

# Finds a JDK 21+ (the default JAVA_HOME on this machine may point to a JRE without javac).
function Find-Jdk {
    $candidates = @()
    if ($env:JAVA_HOME) { $candidates += $env:JAVA_HOME }
    foreach ($dir in @("$env:USERPROFILE\.jdks", "C:\Program Files\Java", "C:\Program Files\Eclipse Adoptium", "C:\Program Files\Microsoft", "C:\Program Files\Amazon Corretto")) {
        if (Test-Path $dir) { $candidates += (Get-ChildItem $dir -Directory | ForEach-Object { $_.FullName }) }
    }
    foreach ($jdkHome in $candidates) {
        $javac = Join-Path $jdkHome "bin\javac.exe"
        if (-not (Test-Path $javac)) { continue }
        $version = (& $javac -version 2>&1 | Out-String)
        if ($version -match 'javac (\d+)' -and [int]$Matches[1] -ge 21) { return $jdkHome }
    }
    throw "No JDK 21+ found. Install one (e.g. winget install Microsoft.OpenJDK.21) or set JAVA_HOME to a JDK."
}

# ---------------------------------------------------------------------------------------------
# Pre-flight checks
# ---------------------------------------------------------------------------------------------
Step "Checking the Azure CLI"
if (-not (Get-Command az -ErrorAction SilentlyContinue)) {
    throw "Azure CLI not found. Install it with: winget install Microsoft.AzureCLI  (then open a NEW terminal)"
}
$account = az account show --query "[name, user.name]" -o tsv 2>$null
if ($LASTEXITCODE -ne 0) { throw "You are not logged in. Run: az login" }
Info "Subscription: $($account[0])"
Info "Account:      $($account[1])"
Info "Region:       $Location"
Info "Backend:      https://$Api.azurewebsites.net"
Info "Frontend:     https://$Web.azurewebsites.net"

if ($Only -eq "all" -or $Only -eq "infra") {
    $answer = Read-Host "This creates PAID resources (App Service B1 + PostgreSQL B1ms) in the subscription above. Continue? (y/n)"
    if ($answer -ne "y") { Write-Host "Cancelled."; exit 1 }
}

# Secrets: generated once, then reused on every run.
if (Test-Path $StateFile) {
    $state = Get-Content $StateFile -Raw | ConvertFrom-Json
    Info "Using saved secrets from .azure-deploy.local.json"
} else {
    $state = [pscustomobject]@{
        DbAdmin    = "rladmin"
        DbPassword = "Rl9" + (New-Secret 21)   # upper + lower + digit, no special characters
        JwtSecret  = New-Secret 48
    }
    $state | ConvertTo-Json | Set-Content $StateFile -Encoding ASCII
    Info "Generated new secrets and saved them to .azure-deploy.local.json"
}

# ---------------------------------------------------------------------------------------------
# 1. Infrastructure
# ---------------------------------------------------------------------------------------------
if ($Only -eq "all" -or $Only -eq "infra") {

    Step "Registering resource providers (the first time this can take a few minutes)"
    Invoke-Az provider register --namespace Microsoft.Web --wait | Out-Null
    Invoke-Az provider register --namespace Microsoft.DBforPostgreSQL --wait | Out-Null

    Step "Resource group $RG"
    if ((Invoke-Az group exists -n $RG) -eq "true") {
        Info "already exists"
    } else {
        Invoke-Az group create -n $RG -l $Location -o none
    }

    Step "PostgreSQL server (Burstable B1ms, 32 GB) - this takes 5-10 minutes"

    # Each region attempt uses its own server name (recordinglogs-db-<suffix>-<region>):
    # a refused attempt leaves a "Dropping" server whose name Azure keeps reserved for a while.
    # Reuse a working server from an earlier run if there is one.
    $servers = & az postgres flexible-server list -g $RG --query "[].[name,state]" -o tsv --only-show-errors
    if ($LASTEXITCODE -ne 0) { throw "Could not list PostgreSQL servers" }
    $existing = @($servers | ForEach-Object { $_ -split "`t" } |
        ForEach-Object -Begin { $pair = @() } -Process {
            $pair += $_
            if ($pair.Count -eq 2) { [pscustomobject]@{ Name = $pair[0]; State = $pair[1] }; $pair = @() }
        } |
        Where-Object { $_.Name -like "recordinglogs-db-$Suffix*" -and $_.State -ne "Dropping" })

    if ($existing.Count -gt 0) {
        $Db = $existing[0].Name
        Info "already exists: $Db (state: $($existing[0].State))"
    } else {
        # Student subscriptions are sometimes refused the B1ms tier in a region (SkuNotAvailable)
        # even though the region lists it. Such an attempt costs nothing, so try the allowed
        # regions one by one. The database may sit in a different region than the apps.
        $dbRegions = @($DbLocation) + @($AllowedRegions | Where-Object { $_ -ne $DbLocation -and $_ -ne "polandcentral" })
        if ($DbLocation -ne "polandcentral") { $dbRegions += "polandcentral" }
        $created = $false
        foreach ($region in $dbRegions) {
            $candidate = "recordinglogs-db-$Suffix-$region"
            Info "Trying region $region (server $candidate) ..."
            $out = & az postgres flexible-server create -g $RG -n $candidate -l $region `
                --tier Burstable --sku-name Standard_B1ms --storage-size 32 --version 16 `
                --admin-user $state.DbAdmin --admin-password $state.DbPassword `
                --public-access 0.0.0.0 --yes -o none --only-show-errors 2>&1 | Out-String
            if ($LASTEXITCODE -eq 0) {
                $Db = $candidate
                Info "Database server created in $region"
                $created = $true
                break
            }
            Write-Host $out -ForegroundColor DarkYellow
            Info "Failed in $region, trying the next region"
        }
        if (-not $created) {
            throw "Could not create PostgreSQL B1ms in any allowed region ($($dbRegions -join ', ')). Send the errors above."
        }
    }

    Step "Database $DbName"
    $found = Invoke-Az postgres flexible-server db list -g $RG -s $Db --query "[?name=='$DbName'].name" -o tsv
    if ($found) {
        Info "already exists"
    } else {
        Invoke-Az postgres flexible-server db create -g $RG -s $Db -n $DbName -o none
    }

    Step "App Service plan (Linux, Basic B1)"
    # Student subscriptions often have a B1 quota of 0 in some regions ("additional quota").
    # Try the database's region first (apps next to the DB), then the other allowed regions.
    # Each attempt uses its own plan name (recordinglogs-plan-<region>); the web apps are
    # created in whichever plan exists.
    $existingPlan = @(Invoke-Az appservice plan list -g $RG --query "[].name" -o tsv |
        Where-Object { $_ -like "recordinglogs-plan*" })
    if ($existingPlan.Count -gt 0) {
        $Plan = $existingPlan[0]
        Info "already exists: $Plan"
    } else {
        $dbRegion = (Invoke-Az postgres flexible-server show -g $RG -n $Db --query location -o tsv) -replace '\s', ''
        $dbRegion = $dbRegion.ToLower()
        $planRegions = @($dbRegion) + @($AllowedRegions | Where-Object { $_ -ne $dbRegion -and $_ -ne $Location }) + @($Location)
        $planRegions = @($planRegions | Select-Object -Unique)
        $created = $false
        foreach ($region in $planRegions) {
            $candidate = "recordinglogs-plan-$region"
            Info "Trying region $region (plan $candidate) ..."
            $out = & az appservice plan create -g $RG -n $candidate -l $region --is-linux --sku B1 `
                -o none --only-show-errors 2>&1 | Out-String
            if ($LASTEXITCODE -eq 0) {
                $Plan = $candidate
                Info "App Service plan created in $region"
                $created = $true
                break
            }
            Write-Host $out -ForegroundColor DarkYellow
            Info "Failed in $region, trying the next region"
        }
        if (-not $created) {
            throw "Could not create a B1 App Service plan in any allowed region ($($planRegions -join ', ')). Send the errors above."
        }
    }

    Step "Backend web app $Api (Java 21)"
    $found = Invoke-Az webapp list -g $RG --query "[?name=='$Api'].name" -o tsv
    if ($found) {
        Info "already exists"
    } else {
        Invoke-Az webapp create -g $RG -p $Plan -n $Api --runtime "JAVA:21-java21" -o none
    }

    Step "Frontend web app $Web (Node 24 LTS)"
    $found = Invoke-Az webapp list -g $RG --query "[?name=='$Web'].name" -o tsv
    if ($found) {
        Info "already exists"
    } else {
        Invoke-Az webapp create -g $RG -p $Plan -n $Web --runtime "NODE:24-lts" -o none
    }

    $dbHost  = Invoke-Az postgres flexible-server show -g $RG -n $Db --query fullyQualifiedDomainName -o tsv
    $apiHost = Invoke-Az webapp show -g $RG -n $Api --query defaultHostName -o tsv

    Step "Backend settings (database, JWT secret, secure cookie, Always On, HTTPS only)"
    Invoke-Az webapp config appsettings set -g $RG -n $Api -o none --settings `
        "SERVER_PORT=80" `
        "DB_URL=jdbc:postgresql://${dbHost}:5432/${DbName}?sslmode=require" `
        "DB_USERNAME=$($state.DbAdmin)" `
        "DB_PASSWORD=$($state.DbPassword)" `
        "JWT_SECRET=$($state.JwtSecret)" `
        "APP_COOKIE_SECURE=true"
    Invoke-Az webapp config set -g $RG -n $Api --always-on true -o none
    Invoke-Az webapp update -g $RG -n $Api --https-only true -o none

    Step "Frontend settings (startup command, Always On, HTTPS only)"
    Invoke-Az webapp config appsettings set -g $RG -n $Web -o none --settings `
        "BACKEND_URL=https://$apiHost" `
        "HOSTNAME=0.0.0.0" `
        "WEBSITES_PORT=8080" `
        "SCM_DO_BUILD_DURING_DEPLOYMENT=false"
    Invoke-Az webapp config set -g $RG -n $Web --startup-file "node server.js" --always-on true -o none
    Invoke-Az webapp update -g $RG -n $Web --https-only true -o none
}

# ---------------------------------------------------------------------------------------------
# 2. Backend: build the jar locally and upload it
# ---------------------------------------------------------------------------------------------
if ($Only -eq "all" -or $Only -eq "backend") {
    $apiHost = Invoke-Az webapp show -g $RG -n $Api --query defaultHostName -o tsv

    Step "Building the backend jar"
    $env:JAVA_HOME = Find-Jdk
    Info "JDK: $env:JAVA_HOME"
    Push-Location (Join-Path $Root "backendRL")
    try {
        & .\mvnw.cmd -q -DskipTests clean package
        if ($LASTEXITCODE -ne 0) { throw "Maven build failed" }
        $jar = Get-ChildItem target\*.jar | Where-Object { $_.Name -notlike "*-plain.jar" } |
            Sort-Object LastWriteTime -Descending | Select-Object -First 1
        if (-not $jar) { throw "No jar found in backendRL\target" }
        Info "Jar: $($jar.Name) ($([math]::Round($jar.Length / 1MB)) MB)"
    } finally {
        Pop-Location
    }

    Step "Deploying the backend to $apiHost (1-3 minutes)"
    # The CLI's own status check can report a failure even when the app starts fine a bit
    # later, so a non-zero exit is only a warning; the URL check below decides.
    & az webapp deploy -g $RG -n $Api --src-path $jar.FullName --type jar -o none --only-show-errors
    if ($LASTEXITCODE -ne 0) { Write-Warning "The CLI reported a deployment problem; checking whether the app answers anyway..." }

    Step "Waiting for the backend to start (first start also creates tables and demo data)"
    if (Wait-ForUrl "https://$apiHost/api/genres" 600) {
        Write-Host ""
        Info "Backend is up: https://$apiHost/swagger-ui/index.html"
    } else {
        Write-Warning "Backend did not answer in 10 minutes. Check the logs: az webapp log tail -g $RG -n $Api"
    }
}

# ---------------------------------------------------------------------------------------------
# 3. Frontend: build Next.js (standalone) locally with the production backend URL and upload it
# ---------------------------------------------------------------------------------------------
if ($Only -eq "all" -or $Only -eq "frontend") {
    $apiHost = Invoke-Az webapp show -g $RG -n $Api --query defaultHostName -o tsv
    $webHost = Invoke-Az webapp show -g $RG -n $Web --query defaultHostName -o tsv
    $frontend = Join-Path $Root "frontend"
    $zip = Join-Path $env:TEMP "recordinglogs-frontend.zip"

    Step "Building the frontend for backend https://$apiHost"
    Push-Location $frontend
    try {
        if (-not (Test-Path node_modules)) {
            & npm ci
            if ($LASTEXITCODE -ne 0) { throw "npm ci failed" }
        }
        # Old build output can contain paths longer than 260 characters, which Remove-Item in
        # Windows PowerShell cannot delete. The \\?\ prefix lets .NET handle long paths.
        if (Test-Path .next\standalone) {
            $full = (Resolve-Path .next\standalone).Path
            [IO.Directory]::Delete("\\?\$full", $true)
        }

        # The /api rewrites are baked into the build, so BACKEND_URL must be set now.
        $env:BACKEND_URL = "https://$apiHost"
        try {
            & npm run build
            if ($LASTEXITCODE -ne 0) { throw "Next.js build failed" }
        } finally {
            Remove-Item Env:\BACKEND_URL -ErrorAction SilentlyContinue
        }

        $manifest = Get-Content .next\standalone\.next\routes-manifest.json -Raw
        if ($manifest -notmatch [regex]::Escape($apiHost)) {
            throw "The build does not contain the backend URL - aborting"
        }

        # server.js serves these folders itself once they are copied next to it.
        Copy-Item .next\static .next\standalone\.next\static -Recurse -Force
        if (Test-Path public) { Copy-Item public .next\standalone\public -Recurse -Force }

        # tar.exe (built into Windows 10+) writes zip entries with "/" paths, which Linux needs.
        if (Test-Path $zip) { Remove-Item $zip -Force }
        Push-Location .next\standalone
        try {
            & tar.exe -a -c -f $zip *
            if ($LASTEXITCODE -ne 0) { throw "Creating the zip failed" }
        } finally {
            Pop-Location
        }
        Info "Package: $([math]::Round((Get-Item $zip).Length / 1MB)) MB"
    } finally {
        Pop-Location
    }

    Step "Deploying the frontend to $webHost (1-3 minutes)"
    & az webapp deploy -g $RG -n $Web --src-path $zip --type zip -o none --only-show-errors
    if ($LASTEXITCODE -ne 0) { Write-Warning "The CLI reported a deployment problem; checking whether the app answers anyway..." }

    Step "Waiting for the frontend"
    if ((Wait-ForUrl "https://$webHost/" 300) -and (Wait-ForUrl "https://$webHost/api/genres" 120)) {
        Write-Host ""
        Info "Frontend is up and can reach the backend"
    } else {
        Write-Warning "Frontend did not answer correctly. Check the logs: az webapp log tail -g $RG -n $Web"
    }
}

# ---------------------------------------------------------------------------------------------
$apiHost = Invoke-Az webapp show -g $RG -n $Api --query defaultHostName -o tsv
$webHost = Invoke-Az webapp show -g $RG -n $Web --query defaultHostName -o tsv
Step "Done"
Write-Host "    Website:  https://$webHost" -ForegroundColor Green
Write-Host "    API docs: https://$apiHost/swagger-ui/index.html" -ForegroundColor Green
Write-Host "    Portal:   https://portal.azure.com -> Resource groups -> $RG"
Write-Host ""
Write-Host "    Redeploy after code changes:  .\deploy-azure.ps1 -Suffix $Suffix -Only backend   (or -Only frontend)"
Write-Host "    Delete everything (stops all costs):  az group delete -n $RG --yes"
