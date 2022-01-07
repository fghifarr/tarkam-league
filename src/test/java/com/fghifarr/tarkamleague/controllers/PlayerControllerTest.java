package com.fghifarr.tarkamleague.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fghifarr.tarkamleague.services.PlayerService;
import com.fghifarr.tarkamleague.services.transactional.PlayerManagementServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PlayerController.class)
@AutoConfigureMockMvc
public class PlayerControllerTest {

    @MockBean
    private PlayerService playerService;
    @MockBean
    private PlayerManagementServiceTest playerManagementService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @Test
    public void test() {
        System.out.println("TEST");
    }
}
