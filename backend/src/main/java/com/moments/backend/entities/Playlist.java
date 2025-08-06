package com.moments.backend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "playlists")
@Data
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

//    @OneToMany(mappedBy = "playlist")
//    private List<Video> videos;
}
