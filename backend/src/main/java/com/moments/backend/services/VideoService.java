package com.moments.backend.services;

import com.moments.backend.entities.Video;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VideoService {

    Video get(String videoId);

    Video getByTitle(String title);

    List<Video> getAll();

    Video save(Video video, MultipartFile file) throws IOException;
}
