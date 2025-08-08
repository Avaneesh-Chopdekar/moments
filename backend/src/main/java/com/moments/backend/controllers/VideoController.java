package com.moments.backend.controllers;

import com.moments.backend.entities.Video;
import com.moments.backend.payload.CustomMessage;
import com.moments.backend.services.VideoService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/v1/videos")
@CrossOrigin("*")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping
    public ResponseEntity<CustomMessage<?>> create(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description
    ) {
        try {
            Video video = new Video();
            video.setTitle(title);
            video.setDescription(description);
            Video savedVideo = videoService.save(video, file);
            if (savedVideo != null) {
                return ResponseEntity.ok().body(new CustomMessage<Video>("Video upload successfully", true, video));
            }
            return ResponseEntity.ok(new CustomMessage<String>("Video uploaded successfully", false, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomMessage<Exception>("Video upload failed", false, e));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(videoService.getAll());
    }

    @GetMapping("/stream/{videoId}")
    public ResponseEntity<?> stream(@PathVariable("videoId") UUID videoId) {
        Video video = videoService.get(videoId);
        if (video == null) {
            return ResponseEntity.badRequest().body(new CustomMessage<String>("Video not found", false, null));
        }

        String contentType = video.getContentType();
        String filePath = video.getFilePath();

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        Resource resource = new FileSystemResource(filePath);

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);

    }
}
