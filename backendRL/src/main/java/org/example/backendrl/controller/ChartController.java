package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.dto.TopAlbumResponse;
import org.example.backendrl.dto.TopSongResponse;
import org.example.backendrl.service.ChartService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/charts")
@Tag(name = "Charts", description = "Top-rated albums and songs, ranked by average user rating")
public class ChartController {

    private final ChartService chartService;

    public ChartController(ChartService chartService) {
        this.chartService = chartService;
    }

    @GetMapping("/top-albums")
    @Operation(summary = "Top-rated albums",
            description = "Ranked by average rating, ties broken by number of ratings. "
                    + "The order is fixed, so the sort parameter is ignored.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of chart entries"),
            @ApiResponse(responseCode = "400", description = "Invalid parameter")
    })
    public PageResponse<TopAlbumResponse> getTopAlbums(
            @Parameter(description = "Genre name, e.g. Rock")
            @RequestParam(required = false) String genre,
            @Parameter(description = "Album release year")
            @RequestParam(required = false) Integer year,
            @Parameter(description = "Only albums of this artist")
            @RequestParam(required = false) Long artistId,
            @Parameter(description = "Minimum number of ratings an album needs")
            @RequestParam(defaultValue = "1") int minRatings,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable
    ) {
        return chartService.getTopAlbums(genre, year, artistId, minRatings, pageable);
    }

    @GetMapping("/top-songs")
    @Operation(summary = "Top-rated songs",
            description = "Ranked by average rating, ties broken by number of ratings. "
                    + "The order is fixed, so the sort parameter is ignored.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of chart entries"),
            @ApiResponse(responseCode = "400", description = "Invalid parameter")
    })
    public PageResponse<TopSongResponse> getTopSongs(
            @Parameter(description = "Genre name, e.g. Rock")
            @RequestParam(required = false) String genre,
            @Parameter(description = "Release year of the song's album")
            @RequestParam(required = false) Integer year,
            @Parameter(description = "Only songs of this artist")
            @RequestParam(required = false) Long artistId,
            @Parameter(description = "Minimum number of ratings a song needs")
            @RequestParam(defaultValue = "1") int minRatings,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable
    ) {
        return chartService.getTopSongs(genre, year, artistId, minRatings, pageable);
    }
}
