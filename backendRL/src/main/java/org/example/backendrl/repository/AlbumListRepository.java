package org.example.backendrl.repository;

import org.example.backendrl.entity.AlbumList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AlbumListRepository extends JpaRepository<AlbumList, Long>, JpaSpecificationExecutor<AlbumList> {

    List<AlbumList> findByUserId(Long userId);
}
