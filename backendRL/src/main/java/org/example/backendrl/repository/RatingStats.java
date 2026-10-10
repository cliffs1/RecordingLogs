package org.example.backendrl.repository;

/**
 * Aggregated ratings of one album or song, used by the charts.
 */
public interface RatingStats {

    Long getId();

    Double getAverage();

    Long getRatingCount();
}
