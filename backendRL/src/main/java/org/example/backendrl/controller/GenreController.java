package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.GenreRequest;
import org.example.backendrl.dto.GenreResponse;
import org.example.backendrl.service.GenreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@Tag(name = "Genres", description = "Music genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    @Operation(summary = "Get all genres")
    @ApiResponse(responseCode = "200", description = "List of genres")
    public List<GenreResponse> getAllGenres() {
        return genreService.getAllGenres();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a genre by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Genre found"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    public GenreResponse getGenre(@PathVariable Long id) {
        return genreService.getGenre(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a genre")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Genre created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public GenreResponse createGenre(
            @Valid @RequestBody GenreRequest request
    ) {
        return genreService.createGenre(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a genre")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Genre updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    public GenreResponse updateGenre(
            @PathVariable Long id,
            @Valid @RequestBody GenreRequest request
    ) {
        return genreService.updateGenre(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a genre")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Genre deleted"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    public void deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
    }
}
