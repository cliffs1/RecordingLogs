package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.dto.RoleUpdateRequest;
import org.example.backendrl.dto.UserResponse;
import org.example.backendrl.dto.UserUpdateRequest;
import org.example.backendrl.service.UserService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Platform users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all users (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of users"),
            @ApiResponse(responseCode = "401", description = "Not logged in"),
            @ApiResponse(responseCode = "403", description = "Not an administrator")
    })
    public PageResponse<UserResponse> getAllUsers(
            @Parameter(description = "Part of the username")
            @RequestParam(required = false) String username,
            @Parameter(description = "Part of the display name")
            @RequestParam(required = false) String displayName,
            @ParameterObject @PageableDefault(size = 20, sort = "username") Pageable pageable
    ) {
        return userService.getAllUsers(username, displayName, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "401", description = "Not logged in"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public UserResponse getUserById(
            @PathVariable Long id) {

        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user profile (own profile, or any profile as ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Not logged in"),
            @ApiResponse(responseCode = "403", description = "Not your profile"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Username or email already taken")
    })
    public UserResponse updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {

        return userService.updateUser(id, request);
    }

    @PatchMapping("/{id}/role")
    @Operation(summary = "Change a user's role (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role changed"),
            @ApiResponse(responseCode = "400", description = "Invalid role"),
            @ApiResponse(responseCode = "401", description = "Not logged in"),
            @ApiResponse(responseCode = "403", description = "Not an administrator, or changing own role"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public UserResponse updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request) {

        return userService.updateRole(id, request.getRole());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a user (own account, or any account as ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "401", description = "Not logged in"),
            @ApiResponse(responseCode = "403", description = "Not your account"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public void deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);
    }
}
