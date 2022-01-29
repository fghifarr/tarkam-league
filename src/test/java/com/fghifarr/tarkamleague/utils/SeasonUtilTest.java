package com.fghifarr.tarkamleague.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class SeasonUtilTest {

    @Test
    public void countTotalMatches_success() {
        int totalClubs = 20;
        int expectedTotalMatches = 380;

        assertThat(SeasonUtil.countTotalMatches(totalClubs)).isEqualTo(expectedTotalMatches);
    }

    @Test
    public void countTotalGameweeks_success() {
        int totalClubs = 20;
        int expectedTotalGameweeks = 38;

        assertThat(SeasonUtil.countTotalGameweeks(totalClubs)).isEqualTo(expectedTotalGameweeks);
    }
}
