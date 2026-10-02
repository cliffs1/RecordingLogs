package org.example.backendrl.repository;

import org.example.backendrl.entity.AlbumList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumListRepository extends JpaRepository<AlbumList, Long> {

    List<AlbumList> findByUserId(Long userId);
}
