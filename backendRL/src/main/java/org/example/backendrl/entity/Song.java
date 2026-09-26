package org.example.backendrl.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "songs")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private Integer durationSeconds;

    private LocalDate originalReleaseDate;

    public Song() {
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public LocalDate getOriginalReleaseDate() {
        return originalReleaseDate;
    }

    public void setOriginalReleaseDate(LocalDate originalReleaseDate) {
        this.originalReleaseDate = originalReleaseDate;
    }
}