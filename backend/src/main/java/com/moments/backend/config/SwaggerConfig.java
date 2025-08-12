package com.moments.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myCustomConfig() {
        return new OpenAPI().info(
                        new Info()
                                .title("Moments API")
                                .description("REST API for Moments, a video sharing platform")
                                .version("1.0.0")
                )
                .servers(
                        List.of(
                                new Server().url("http://localhost:8080/api")
                                        .description("Local Development Server"),
                                new Server().url("https://moments-backend.onrender.com/api")
                                        .description("Production Server")
                        )
                ) // TODO: Change production server url after deployment
                .tags(
                        List.of(
                                new Tag().name("Video API"),
                                new Tag().name("Playlist API")
                        )
                );
    }
}