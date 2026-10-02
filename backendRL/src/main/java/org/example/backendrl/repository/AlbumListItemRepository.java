package org.example.backendrl.repository;

import org.example.backendrl.entity.AlbumListItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumListItemRepository extends JpaRepository<AlbumListItem, Long> {

    void deleteByAlbumId(Long albumId);
}
