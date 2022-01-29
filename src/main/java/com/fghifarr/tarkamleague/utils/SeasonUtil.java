package com.fghifarr.tarkamleague.utils;

public class SeasonUtil {

    public static int countTotalMatches(int totalClubs) {
        if (totalClubs == 1) return 0;

        return countTotalMatches(totalClubs-1) + 2*(totalClubs-1);
    }

    public static int countTotalGameweeks(int totalClubs) {
        if (totalClubs % 2 == 0)
            return (totalClubs - 1) * 2;

        return totalClubs * 2;
    }
}
