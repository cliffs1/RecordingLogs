package org.example.backendrl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base for responses that carry HAL-style hypermedia links:
 * "_links": { "self": { "href": "..." }, ... }
 */
public abstract class HypermediaResponse {

    @JsonProperty("_links")
    private final Map<String, Link> links = new LinkedHashMap<>();

    @JsonProperty("_links")
    public Map<String, Link> getLinks() {
        return links;
    }

    /**
     * Adds a link to an API path, e.g. "/api/artists/1".
     * The href is made absolute using the current request's host and port.
     */
    public void addLink(String rel, String path) {
        String href = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(path)
                .toUriString();

        links.put(rel, new Link(href));
    }

    /**
     * Adds a link to an external resource (e.g. a cover image), as is.
     */
    public void addExternalLink(String rel, String href) {
        if (href != null && !href.isBlank()) {
            links.put(rel, new Link(href));
        }
    }

    protected void putLink(String rel, String href) {
        links.put(rel, new Link(href));
    }
}
