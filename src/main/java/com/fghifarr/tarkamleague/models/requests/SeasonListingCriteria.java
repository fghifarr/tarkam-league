package com.fghifarr.tarkamleague.models.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Setter
@Getter
public class SeasonListingCriteria extends ListingCriteria {
    String query = "";
    private String sortBy = "id";
    private Sort.Direction order = Sort.Direction.ASC;

    public SeasonListingCriteria() {
        this.setSort(Sort.by(order, sortBy));
    }

    public SeasonListingCriteria(String query) {
        this();
        this.query = query;
    }

    public SeasonListingCriteria(String query, String sortBy, Sort.Direction order) {
        this(query, 0, 15, sortBy, order);
    }

    public SeasonListingCriteria(String query, int offset, int limit,
                                 String sortBy, Sort.Direction order) {
        super(offset, limit, Sort.by(order, sortBy));
        this.query = query;
    }
}
