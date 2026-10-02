package org.example.backendrl.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "RecordingLogs API",
                version = "1.0",
                description = "Music rating and recommendation platform: "
                        + "genres, artists, albums, songs, ratings, reviews, "
                        + "album lists, favorites and follows."
        )
)
public class OpenApiConfig {
}
