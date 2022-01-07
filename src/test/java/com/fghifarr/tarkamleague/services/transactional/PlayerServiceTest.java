package com.fghifarr.tarkamleague.services.transactional;

import com.fghifarr.tarkamleague.repositories.PlayerRepository;
import com.fghifarr.tarkamleague.services.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    PlayerRepository playerRepository;

    PlayerService playerService;
    @BeforeEach
    void initDependencies() {
        playerService = new PlayerService(playerRepository);
    }

    @Test
    public void test() {
        assertTrue(true);
    }
}
