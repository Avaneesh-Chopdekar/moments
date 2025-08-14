package com.moments.backend.dto;

import com.moments.backend.enums.Visibility;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PlaylistUpdateDto {
    @Size(min = 1, max = 150, message = "Playlist name must be between 1 and 150 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private Visibility visibility;
}