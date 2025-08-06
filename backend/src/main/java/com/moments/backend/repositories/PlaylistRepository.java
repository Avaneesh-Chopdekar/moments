package com.moments.backend.repositories;

import com.moments.backend.entities.Playlist;
import com.moments.backend.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {

    Optional<Playlist> findByName(String name);
}
