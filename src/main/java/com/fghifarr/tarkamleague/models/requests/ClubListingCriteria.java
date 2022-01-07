package com.fghifarr.tarkamleague.models.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Setter
@Getter
public class ClubListingCriteria extends ListingCriteria {
    String query = "";
    private String sortBy = "id";
    private Sort.Direction order = Sort.Direction.ASC;

    public ClubListingCriteria() {
        this.setSort(Sort.by(order, sortBy));
    }

    public ClubListingCriteria(String query) {
        this();
        this.query = query;
    }

    public ClubListingCriteria(String query, String sortBy, Sort.Direction order) {
        this(query, 0, 15, sortBy, order);
    }

    public ClubListingCriteria(String query, int offset, int limit,
                               String sortBy, Sort.Direction order) {
        super(offset, limit, Sort.by(order, sortBy));
        this.query = query;
    }
}
