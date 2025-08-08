package com.moments.backend.services.impl;

import com.moments.backend.entities.Video;
import com.moments.backend.repositories.VideoRepository;
import com.moments.backend.services.VideoService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class VideoServiceImpl implements VideoService {

    @Value("${custom.video.path}")
    String DIR;

    private final VideoRepository videoRepository;

    public VideoServiceImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @PostConstruct
    public void init() {
        File dir = new File(DIR);
        if (!dir.exists()) {
            dir.mkdir();
            System.out.println("Directory created");
        }
    }

    @Override
    public Video get(UUID videoId) {
        return videoRepository.findById(videoId).orElse(null);
    }

    @Override
    public Video getByTitle(String title) {
        return videoRepository.findByTitle(title).orElse(null);
    }

    @Override
    public List<Video> getAll() {
        return videoRepository.findAll();
    }

    @Override
    public Video save(Video video, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        InputStream inputStream = file.getInputStream();

        assert fileName != null;
        String cleanedFileName = StringUtils.cleanPath(fileName);
        String cleanedFolderName = StringUtils.cleanPath(DIR);

        Path path = Paths.get(cleanedFolderName, cleanedFileName);

        video.setFilePath(path.toString());
        video.setContentType(contentType);

        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);

        video.setContentType(contentType);
        video.setFilePath(path.toString());

        return videoRepository.save(video);

    }
}
