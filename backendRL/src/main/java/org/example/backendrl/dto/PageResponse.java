package org.example.backendrl.dto;

import org.springframework.data.domain.Page;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.function.Function;

/**
 * One page of results plus navigation links (self, first, last, next, prev).
 * Links keep the request's other query parameters, so filters and sorting
 * are carried over when following them.
 */
public class PageResponse<T> extends HypermediaResponse {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    private PageResponse(List<T> content, Page<?> source) {
        this.content = content;
        this.page = source.getNumber();
        this.size = source.getSize();
        this.totalElements = source.getTotalElements();
        this.totalPages = source.getTotalPages();

        putLink("self", pageUrl(page));
        putLink("first", pageUrl(0));
        putLink("last", pageUrl(Math.max(totalPages - 1, 0)));

        if (source.hasNext()) {
            putLink("next", pageUrl(page + 1));
        }

        if (source.hasPrevious()) {
            putLink("prev", pageUrl(page - 1));
        }
    }

    public static <E, T> PageResponse<T> from(Page<E> page, Function<E, T> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page
        );
    }

    public static <T> PageResponse<T> of(Page<?> page, List<T> content) {
        return new PageResponse<>(content, page);
    }

    private static String pageUrl(int page) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .replaceQueryParam("page", page)
                .toUriString();
    }

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
