package com.moments.backend.services;

import com.moments.backend.entities.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface VideoService {

    Video get(UUID videoId);

    List<Video> getAll();

    Video save(Video video, MultipartFile file) throws IOException;

    void processVideo(UUID videoId);

    Video update(UUID videoId, Map<String, Object> updates);

    void delete(UUID videoId);

    Page<Video> getAllPublicVideos(Pageable pageable);

    Page<Video> searchPublicVideos(String query, Pageable pageable);

//    Page<Video> getVideosByUploader(Long uploaderId, Pageable pageable);

    Video incrementViews(UUID videoId);

    Video likeVideo(UUID videoId);

    Video dislikeVideo(UUID videoId);

    List<Video> getTrendingVideos();
}
