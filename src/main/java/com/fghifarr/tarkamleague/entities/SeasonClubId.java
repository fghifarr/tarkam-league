package com.fghifarr.tarkamleague.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeasonClubId implements Serializable {

    private Long seasonId;
    private Long clubId;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result
                + ((seasonId == null) ? 0 : seasonId.hashCode());
        result = prime * result
                + ((clubId == null) ? 0 : clubId.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        SeasonClubId other = (SeasonClubId) obj;

        return Objects.equals(getSeasonId(), other.getSeasonId()) && Objects.equals(getClubId(), other.getClubId());
    }
}
