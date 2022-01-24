package com.fghifarr.tarkamleague.configs.init;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DummyData {

    public static final List<String> FIRST_NAME_LIST = Stream.of(
            "James", "John", "Robert", "Michael", "William", "David", "Richard", "Charles", "Joseph", "Thomas",
            "Christopher", "Daniel", "Paul", "Mark", "Donald", "George", "Kenneth", "Steven", "Edward", "Brian",
            "Ronald", "Anthony", "Kevin", "Jason", "Matthew", "Gary", "Timothy", "Jose", "Larry", "Jeffrey"
    ).collect(Collectors.toList());

    public static final List<String> MIDDLE_NAME_LIST = Stream.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
            .collect(Collectors.toList());

    public static final List<String> LAST_NAME_LIST = Stream.of(
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
            "Hernandez", "Lopez", "Gonzales", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin",
            "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson"
    ).collect(Collectors.toList());

    private static final List<String> NATIONALITY_LIST = List.of(
            "England", "Spain", "Belgium", "France", "Netherlands", "Brazil", "Scotland", "Ireland", "Wales", "Italy");

    public static String generateDummyName() {
        Random random = new Random();

        return FIRST_NAME_LIST.get(random.nextInt(FIRST_NAME_LIST.size())) + " " +
                MIDDLE_NAME_LIST.get(random.nextInt(MIDDLE_NAME_LIST.size())) + " " +
                LAST_NAME_LIST.get(random.nextInt(LAST_NAME_LIST.size()));
    }

    public static LocalDate generateDOB() {
        long start = LocalDate.of(1992, 1, 1).toEpochDay();
        long end = LocalDate.of(2002, 1, 1).toEpochDay();
        long dob = ThreadLocalRandom.current().nextLong(start, end);

        return LocalDate.ofEpochDay(dob);
    }

    public static String generateNationality() {
        Random random = new Random();

        return NATIONALITY_LIST.get(random.nextInt(NATIONALITY_LIST.size()));
    }

    public static int generateHeight() {
        return ThreadLocalRandom.current().nextInt(170, 200);
    }
}
