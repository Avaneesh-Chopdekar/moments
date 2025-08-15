package com.moments.backend.services.impl;

import com.moments.backend.entities.Video;
import com.moments.backend.enums.Visibility;
import com.moments.backend.repositories.VideoRepository;
import com.moments.backend.services.VideoService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import java.util.Map;
import java.util.UUID;

@Service
public class VideoServiceImpl implements VideoService {

    @Value("${custom.video.path}")
    String DIR;

    @Value("${custom.video.hls_path}")
    String HLS_DIR;

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

        // HSL Directory
        File hlsDir = new File(HLS_DIR);
        if (!hlsDir.exists()) {
            hlsDir.mkdir();
            System.out.println("HSL Directory created");
        }
    }

    @Override
    public Video get(UUID videoId) {
        return videoRepository.findById(videoId).orElse(null);
    }

    @Override
    public List<Video> getAll() {
        try {
            return videoRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
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
        Video savedVideo = videoRepository.save(video);

        processVideo(savedVideo.getId());

        // delete original video file later

        return savedVideo;
    }

    @Override
    public void processVideo(UUID videoId) {

        Video video = this.get(videoId);
        String filePath = video.getFilePath();

        //path where to store data:
        Path videoPath = Paths.get(filePath);


//        String output360p = HLS_DIR + videoId + "/360p/";
//        String output720p = HLS_DIR + videoId + "/720p/";
//        String output1080p = HLS_DIR + videoId + "/1080p/";

        try {
//            Files.createDirectories(Paths.get(output360p));
//            Files.createDirectories(Paths.get(output720p));
//            Files.createDirectories(Paths.get(output1080p));

            // ffmpeg command
            Path outputPath = Paths.get(HLS_DIR, videoId.toString());

            Files.createDirectories(outputPath);


            String ffmpegCmd = String.format(
                    "ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment_%%3d.ts\"  \"%s/master.m3u8\" ",
                    videoPath, outputPath, outputPath
            );

//            StringBuilder ffmpegCmd = new StringBuilder();
//            ffmpegCmd.append("ffmpeg  -i ")
//                    .append(videoPath.toString())
//                    .append(" -c:v libx264 -c:a aac")
//                    .append(" ")
//                    .append("-map 0:v -map 0:a -s:v:0 640x360 -b:v:0 800k ")
//                    .append("-map 0:v -map 0:a -s:v:1 1280x720 -b:v:1 2800k ")
//                    .append("-map 0:v -map 0:a -s:v:2 1920x1080 -b:v:2 5000k ")
//                    .append("-var_stream_map \"v:0,a:0 v:1,a:0 v:2,a:0\" ")
//                    .append("-master_pl_name ").append(HLS_DIR).append(videoId).append("/master.m3u8 ")
//                    .append("-f hls -hls_time 10 -hls_list_size 0 ")
//                    .append("-hls_segment_filename \"").append(HLS_DIR).append(videoId).append("/v%v/fileSequence%d.ts\" ")
//                    .append("\"").append(HLS_DIR).append(videoId).append("/v%v/prog_index.m3u8\"");


            System.out.println(ffmpegCmd);
            //file this command
//            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", ffmpegCmd); // For Linux
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", ffmpegCmd); // For Windows
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            int exit = process.waitFor();
            if (exit != 0) {
                throw new RuntimeException("video processing failed!!");
            }


        } catch (IOException ex) {
            throw new RuntimeException("Video processing fail!!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Video update(UUID videoId, Map<String, Object> updates) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Video not found"));
        updates.forEach((key, value) -> {
            switch (key) {
                case "title":
                    video.setTitle((String) value);
                    break;
                case "description":
                    video.setDescription((String) value);
                    break;
            }
        });
        return videoRepository.save(video);
    }

    @Override
    public void delete(UUID videoId) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Video not found"));
        videoRepository.delete(video);
    }

    @Override
    public Page<Video> getAllPublicVideos(Pageable pageable) {
        return videoRepository.findByVisibility(Visibility.PUBLIC, pageable);
    }

    @Override
    public Page<Video> searchPublicVideos(String query, Pageable pageable) {
        return videoRepository.findByTitleContainingIgnoreCaseAndVisibility(query, Visibility.PUBLIC, pageable);
    }

    @Override
    public Video incrementViews(UUID videoId) {
        Video video = this.get(videoId);
        video.setViews(video.getViews() + 1);
        return videoRepository.save(video);
    }

    @Override
    public Video likeVideo(UUID videoId) {
        Video video = this.get(videoId);
        // In a real system, a separate Like/Dislike entity
        // to ensure a user can only like/dislike once and track their specific action.
        video.setLikes(video.getLikes() + 1);
        return videoRepository.save(video);
    }

    @Override
    public Video dislikeVideo(UUID videoId) {
        Video video = this.get(videoId);
        video.setDislikes(video.getDislikes() + 1);
        return videoRepository.save(video);
    }

    @Override
    public List<Video> getTrendingVideos() {
        // This is a simple trending logic. Real trending would be more complex.
        return videoRepository.findTop10ByOrderByViewsDesc();
    }
}
