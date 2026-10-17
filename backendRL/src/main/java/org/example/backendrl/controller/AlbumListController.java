package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.backendrl.dto.AlbumListItemRequest;
import org.example.backendrl.dto.AlbumListRequest;
import org.example.backendrl.dto.AlbumListResponse;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.service.AlbumListService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Lists", description = "Personal album lists")
public class AlbumListController {

    private final AlbumListService albumListService;

    public AlbumListController(AlbumListService albumListService) {
        this.albumListService = albumListService;
    }

    @GetMapping("/lists")
    @Operation(summary = "Get all album lists")
    @ApiResponse(responseCode = "200", description = "List of album lists")
    public PageResponse<AlbumListResponse> getAllLists(
            @Parameter(description = "Part of the list name")
            @RequestParam(required = false) String name,
            @Parameter(description = "Only lists of this user")
            @RequestParam(required = false) Long userId,
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return albumListService.getAllLists(name, userId, pageable);
    }

    @GetMapping("/lists/{listId}")
    @Operation(summary = "Get an album list with its albums")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Album list found"),
            @ApiResponse(responseCode = "404", description = "List not found")
    })
    public AlbumListResponse getList(@PathVariable Long listId) {
        return albumListService.getList(listId);
    }

    @GetMapping("/users/{userId}/lists")
    @Operation(summary = "Get all album lists of a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of album lists"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public PageResponse<AlbumListResponse> getListsForUser(
            @PathVariable Long userId,
            @Parameter(description = "Part of the list name")
            @RequestParam(required = false) String name,
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return albumListService.getListsForUser(userId, name, pageable);
    }

    @PostMapping("/lists")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new album list")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "List created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public AlbumListResponse createList(
            @Valid @RequestBody AlbumListRequest request) {

        return albumListService.createList(request);
    }

    @PutMapping("/lists/{listId}")
    @Operation(summary = "Edit album list name and description")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "List not found"),
            @ApiResponse(responseCode = "403", description = "Not the owner of the list")
    })
    public AlbumListResponse updateList(
            @PathVariable Long listId,
            @Valid @RequestBody AlbumListRequest request) {

        return albumListService.updateList(listId, request);
    }

    @DeleteMapping("/lists/{listId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an album list")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "List deleted"),
            @ApiResponse(responseCode = "404", description = "List not found")
    })
    public void deleteList(@PathVariable Long listId) {
        albumListService.deleteList(listId);
    }

    @PostMapping("/lists/{listId}/albums/{albumId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add an album to a list")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Album added, returns the updated list"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "List or album not found"),
            @ApiResponse(responseCode = "409", description = "Album is already in the list"),
            @ApiResponse(responseCode = "403", description = "Not the owner of the list")
    })
    public AlbumListResponse addAlbum(
            @PathVariable Long listId,
            @PathVariable Long albumId,
            @Valid @RequestBody(required = false) AlbumListItemRequest request) {

        return albumListService.addAlbum(listId, albumId, request);
    }

    @DeleteMapping("/lists/{listId}/albums/{albumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove an album from a list")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Album removed"),
            @ApiResponse(responseCode = "404", description = "List not found or album not in list")
    })
    public void removeAlbum(
            @PathVariable Long listId,
            @PathVariable Long albumId) {

        albumListService.removeAlbum(listId, albumId);
    }
}
