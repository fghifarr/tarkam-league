package com.fghifarr.tarkamleague.services.transactional;

import com.fghifarr.tarkamleague.repositories.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PlayerManagementServiceTest {

    @Mock
    PlayerRepository playerRepository;

    PlayerManagementService playerManagementService;
    @BeforeEach
    void initDependencies() {
        playerManagementService = new PlayerManagementService(playerRepository);
    }

    @Test
    public void test() {
        assertTrue(true);
    }
}
