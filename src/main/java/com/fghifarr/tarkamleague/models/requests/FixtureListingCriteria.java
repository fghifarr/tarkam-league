package com.fghifarr.tarkamleague.models.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Setter
@Getter
public class FixtureListingCriteria extends ListingCriteria {
    Long clubId;
    private String sortBy = "kickOff";
    private Sort.Direction order = Sort.Direction.ASC;

    public FixtureListingCriteria() {
        this.setSort(Sort.by(order, sortBy));
    }

    public FixtureListingCriteria(Long clubId) {
        this();
        this.clubId = clubId;
    }

    public FixtureListingCriteria(Long clubId, String sortBy, Sort.Direction order) {
        this(clubId, 0, 15, sortBy, order);
    }

    public FixtureListingCriteria(Long clubId, int offset, int limit,
                                  String sortBy, Sort.Direction order) {
        super(offset, limit, Sort.by(order, sortBy));
        this.clubId = clubId;
    }
}
