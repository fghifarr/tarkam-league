package com.fghifarr.tarkamleague.models.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
public class ListingCriteria implements Pageable {
    @Min(value = 1, message = "Limit must not be less than one!")
    private int limit = 15;
    @Min(value = 0, message = "Offset index must not be less than zero!")
    private int offset = 0;
    private Sort sort;

    public ListingCriteria(int offset, int limit, Sort sort) {
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    public ListingCriteria(int offset, int limit, Sort.Direction direction, String... properties) {
        this(offset, limit, Sort.by(direction, properties));
    }

    public ListingCriteria(int offset, int limit) {
        this(offset, limit, Sort.unsorted());
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new ListingCriteria((int)(getOffset() + getPageSize()), getPageSize(), getSort());
    }

    public ListingCriteria previous() {
        return hasPrevious() ? new ListingCriteria((int)(getOffset() - getPageSize()), getPageSize(), getSort()) : this;
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new ListingCriteria(0, getPageSize(), getSort());
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }
}
