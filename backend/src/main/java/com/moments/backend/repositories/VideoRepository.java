package com.moments.backend.repositories;

import com.moments.backend.entities.Video;
import com.moments.backend.enums.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {

    Optional<Video> findByTitle(String title);

    Page<Video> findByTitleContainingIgnoreCaseAndVisibility(String title, Visibility visibility, Pageable pageable);
    List<Video> findTop10ByOrderByViewsDesc();
    Page<Video> findByVisibility(Visibility visibility, Pageable pageable);
}
