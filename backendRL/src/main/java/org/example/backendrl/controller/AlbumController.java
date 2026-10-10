package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.AlbumRequest;
import org.example.backendrl.dto.AlbumResponse;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.service.AlbumService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/artists/{artistId}/albums")
@Tag(name = "Albums", description = "Albums of an artist")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    @Operation(summary = "Get albums of an artist (paged, filterable)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of albums"),
            @ApiResponse(responseCode = "400", description = "Invalid filter, paging or sort parameter"),
            @ApiResponse(responseCode = "404", description = "Artist not found")
    })
    public PageResponse<AlbumResponse> getAlbums(
            @PathVariable Long artistId,
            @Parameter(description = "Part of the album title (case-insensitive)")
            @RequestParam(required = false) String title,
            @Parameter(description = "Released on or after this date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releasedFrom,
            @Parameter(description = "Released on or before this date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releasedTo,
            @Parameter(description = "Genre name, e.g. Rock")
            @RequestParam(required = false) String genre,
            @ParameterObject @PageableDefault(size = 20, sort = "releaseDate") Pageable pageable
    ) {
        return albumService.getAlbums(artistId, title, releasedFrom, releasedTo, genre, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an album of an artist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Album found"),
            @ApiResponse(responseCode = "404", description = "Artist or album not found")
    })
    public AlbumResponse getAlbum(
            @PathVariable Long artistId,
            @PathVariable Long id
    ) {
        return albumService.getAlbum(artistId, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an album for an artist")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Album created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Artist not found")
    })
    public AlbumResponse createAlbum(
            @PathVariable Long artistId,
            @Valid @RequestBody AlbumRequest request
    ) {
        return albumService.createAlbum(artistId, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an album of an artist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Album updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Artist or album not found")
    })
    public AlbumResponse updateAlbum(
            @PathVariable Long artistId,
            @PathVariable Long id,
            @Valid @RequestBody AlbumRequest request
    ) {
        return albumService.updateAlbum(artistId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an album of an artist")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Album deleted"),
            @ApiResponse(responseCode = "404", description = "Artist or album not found")
    })
    public void deleteAlbum(
            @PathVariable Long artistId,
            @PathVariable Long id
    ) {
        albumService.deleteAlbum(artistId, id);
    }
}
