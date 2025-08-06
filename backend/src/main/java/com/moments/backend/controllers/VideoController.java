package com.moments.backend.controllers;

import com.moments.backend.entities.Video;
import com.moments.backend.payload.CustomMessage;
import com.moments.backend.services.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/videos")
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
}
