package com.moments.backend.services;

import com.moments.backend.entities.Video;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface VideoService {

    Video get(UUID videoId);

    Video getByTitle(String title);

    List<Video> getAll();

    Video save(Video video, MultipartFile file) throws IOException;

    void processVideo(UUID videoId);

    Video update(UUID videoId, Map<String, Object> updates);

    void delete(UUID videoId);
}
