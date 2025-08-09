package com.moments.backend.controllers;

import com.moments.backend.entities.Video;
import com.moments.backend.payload.CustomMessage;
import com.moments.backend.services.VideoService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
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

    @GetMapping("/stream/range/{videoId}")
    public ResponseEntity<Resource> streamVideoRange(
            @PathVariable("videoId") UUID videoId,
            @RequestHeader(value = "Range", required = false) String range
    ) {
        Video video = videoService.get(videoId);
        if (video == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Path path = Paths.get(video.getFilePath());

        Resource resource = new FileSystemResource(path.toFile());

        String contentType = video.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        long fileLength = path.toFile().length();

        if (range == null) {
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        }

        long rangeStart, rangeEnd;
        String[] values = range.replace("bytes=", "").split("-");
        rangeStart = Long.parseLong(values[0]);
        if (values.length > 1) {
            rangeEnd = Long.parseLong(values[1]);
        } else {
            rangeEnd = fileLength - 1;
        }

        if (rangeEnd > fileLength - 1) {
            rangeEnd = fileLength - 1;
        }

//        System.out.println("LOG: Range Start -" + rangeStart);
//        System.out.println("LOG: Range End -" + rangeEnd);

        InputStream inputStream;

        try {
            inputStream = Files.newInputStream(path);
            inputStream.skip(rangeStart);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        long contentLength = rangeEnd - rangeStart + 1;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("X-Content-Type-Options", "nosniff");
        headers.setContentLength(contentLength);

        return ResponseEntity
                .status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(contentLength)
                .body(new InputStreamResource(inputStream));

    }
}
