package org.example.backendrl.service;

import org.example.backendrl.dto.AlbumListItemRequest;
import org.example.backendrl.dto.AlbumListRequest;
import org.example.backendrl.dto.AlbumListResponse;
import org.example.backendrl.entity.Album;
import org.example.backendrl.entity.AlbumList;
import org.example.backendrl.entity.AlbumListItem;
import org.example.backendrl.entity.User;
import org.example.backendrl.exception.ConflictException;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.exception.UnprocessableEntityException;
import org.example.backendrl.repository.AlbumListRepository;
import org.example.backendrl.repository.AlbumRepository;
import org.example.backendrl.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class AlbumListService {

    private final AlbumListRepository albumListRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    public AlbumListService(
            AlbumListRepository albumListRepository,
            AlbumRepository albumRepository,
            UserRepository userRepository) {

        this.albumListRepository = albumListRepository;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<AlbumListResponse> getAllLists() {

        return albumListRepository.findAll()
                .stream()
                .map(AlbumListResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AlbumListResponse> getListsForUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User with id " + userId + " not found"
            );
        }

        return albumListRepository.findByUserId(userId)
                .stream()
                .map(AlbumListResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public AlbumListResponse getList(Long listId) {

        return AlbumListResponse.fromEntity(findList(listId));
    }

    @Transactional
    public AlbumListResponse createList(AlbumListRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with id " + request.getUserId()
                                        + " not found"
                        ));

        AlbumList albumList = new AlbumList();
        albumList.setUser(user);
        albumList.setName(request.getName());
        albumList.setDescription(request.getDescription());

        AlbumList savedList = albumListRepository.save(albumList);

        return AlbumListResponse.fromEntity(savedList);
    }

    @Transactional
    public AlbumListResponse updateList(
            Long listId,
            AlbumListRequest request) {

        AlbumList albumList = findList(listId);

        checkOwner(albumList, request.getUserId());

        albumList.setName(request.getName());
        albumList.setDescription(request.getDescription());

        AlbumList updatedList = albumListRepository.saveAndFlush(albumList);

        return AlbumListResponse.fromEntity(updatedList);
    }

    @Transactional
    public void deleteList(Long listId) {

        AlbumList albumList = findList(listId);

        albumListRepository.delete(albumList);
    }

    @Transactional
    public AlbumListResponse addAlbum(
            Long listId,
            Long albumId,
            AlbumListItemRequest request) {

        AlbumList albumList = findList(listId);

        checkOwner(albumList, request.getUserId());

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Album with id " + albumId + " not found"
                        ));

        boolean alreadyInList = albumList.getAlbums()
                .stream()
                .anyMatch(item -> item.getAlbum().getId().equals(albumId));

        if (alreadyInList) {
            throw new ConflictException(
                    "Album " + albumId + " is already in list " + listId
            );
        }

        int position = request.getPosition() != null
                ? request.getPosition()
                : albumList.getAlbums().size() + 1;

        AlbumListItem item = new AlbumListItem();
        item.setAlbumList(albumList);
        item.setAlbum(album);
        item.setPosition(position);

        albumList.getAlbums().add(item);

        AlbumList updatedList = albumListRepository.saveAndFlush(albumList);

        return AlbumListResponse.fromEntity(updatedList);
    }

    @Transactional
    public void removeAlbum(
            Long listId,
            Long albumId) {

        AlbumList albumList = findList(listId);

        AlbumListItem item = albumList.getAlbums()
                .stream()
                .filter(listItem -> listItem.getAlbum().getId().equals(albumId))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Album " + albumId + " is not in list " + listId
                        ));

        albumList.getAlbums().remove(item);

        List<AlbumListItem> remaining = albumList.getAlbums()
                .stream()
                .sorted(Comparator.comparing(AlbumListItem::getPosition))
                .toList();

        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setPosition(i + 1);
        }

        albumListRepository.save(albumList);
    }

    private AlbumList findList(Long listId) {

        return albumListRepository.findById(listId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "List with id " + listId + " not found"
                        ));
    }

    private void checkOwner(AlbumList albumList, Long userId) {

        if (!albumList.getUser().getId().equals(userId)) {
            throw new UnprocessableEntityException(
                    "User " + userId + " is not the owner of list "
                            + albumList.getId()
            );
        }
    }
}
