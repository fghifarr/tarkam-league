package com.fghifarr.tarkamleague.models.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class PlayerListingCriteria extends ListingCriteria {
    private String query = "";
    private Long club;

    private String sortBy = "id";
    private Sort.Direction order = Sort.Direction.ASC;

    public PlayerListingCriteria() {
        this.setSort(Sort.by(order, sortBy));
    }

    public PlayerListingCriteria(String query) {
        this();
        this.query = query;
    }

    public PlayerListingCriteria(String query, Long club, int offset, int limit,
                                 String sortBy, Sort.Direction order) {
        super(offset, limit, Sort.by(order, sortBy));
        this.query = query;
        this.club = club;
    }
}
