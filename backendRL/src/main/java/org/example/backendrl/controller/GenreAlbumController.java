package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.AlbumSummaryResponse;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.service.AlbumGenreService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/genres")
@Tag(name = "Genres")
public class GenreAlbumController {

    private final AlbumGenreService albumGenreService;

    public GenreAlbumController(
            AlbumGenreService albumGenreService
    ) {
        this.albumGenreService = albumGenreService;
    }

    @GetMapping("/{genreId}/albums")
    @Operation(summary = "Get all albums of a genre")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of albums"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    public PageResponse<AlbumSummaryResponse> getAlbumsForGenre(
            @PathVariable Long genreId,
            @Parameter(description = "Part of the album title")
            @RequestParam(required = false) String title,
            @Parameter(description = "Released on or after this date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releasedFrom,
            @Parameter(description = "Released on or before this date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releasedTo,
            @ParameterObject @PageableDefault(size = 20, sort = "album.title") Pageable pageable
    ) {
        return albumGenreService.getAlbumsForGenre(genreId, title, releasedFrom, releasedTo, pageable);
    }
}
