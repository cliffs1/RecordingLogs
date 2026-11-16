import type { NextConfig } from "next";

// All browser requests go to /api/* on the frontend's own origin and are
// proxied to the Spring Boot backend. This keeps the SameSite=Strict refresh
// token cookie first-party and removes the need for CORS.
const backendUrl = process.env.BACKEND_URL ?? "http://localhost:8080";

const nextConfig: NextConfig = {
  // Produces .next/standalone with a minimal server.js (used for the Azure deployment).
  // BACKEND_URL must be set when building, because the rewrites are baked into the build.
  output: "standalone",
  // Covers are plain <img> tags from external URLs; disabling the optimizer also means
  // the Windows-built "sharp" binary in the standalone bundle is never loaded on Linux.
  images: { unoptimized: true },
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        destination: `${backendUrl}/api/:path*`,
      },
      // Swagger UI of the backend (linked from the footer)
      { source: "/swagger-ui/:path*", destination: `${backendUrl}/swagger-ui/:path*` },
      { source: "/v3/api-docs/:path*", destination: `${backendUrl}/v3/api-docs/:path*` },
      { source: "/v3/api-docs", destination: `${backendUrl}/v3/api-docs` },
    ];
  },
};

export default nextConfig;
