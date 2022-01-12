package com.fghifarr.tarkamleague.controllers;

import com.fghifarr.tarkamleague.configs.constants.RoleConstant;
import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.PersonalDetails;
import com.fghifarr.tarkamleague.entities.Player;
import com.fghifarr.tarkamleague.models.requests.PlayerReq;
import com.fghifarr.tarkamleague.models.requests.PlayerListingCriteria;
import com.fghifarr.tarkamleague.models.requests.init.InitPlayerReq;
import com.fghifarr.tarkamleague.models.responses.PlayerDetailsResp;
import com.fghifarr.tarkamleague.models.responses.PlayerResp;
import com.fghifarr.tarkamleague.services.PlayerService;
import com.fghifarr.tarkamleague.services.transactional.PlayerManagementService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlayerControllerTest extends BaseControllerTest {

    @MockBean
    private PlayerService playerService;
    @MockBean
    private PlayerManagementService playerManagementService;

    //========================
    //-----DATA GENERATOR-----
    //========================
    List<Player> createPlayerList() {
        //Club list
        List<Club> clubList = List.of(
                new Club(1L, "Liverpool"),
                new Club(2L, "Manchester City"),
                new Club(3L, "Manchester United")
        );
        //Raw player list
        List<InitPlayerReq> playerReqs = List.of(
                new InitPlayerReq("Steven Gerrard", "", Date.valueOf("1980-05-30"), "England", 185),
                new InitPlayerReq("Wayne Rooney", "", Date.valueOf("1985-10-24"), "England", 176),
                new InitPlayerReq("Mohamed Salah", "Liverpool", Date.valueOf("1992-06-15"), "Egypt", 175),
                new InitPlayerReq("Trent Alexander-Arnold", "Liverpool", Date.valueOf("1998-10-07"), "England", 175),
                new InitPlayerReq("Andrew Robertson", "Liverpool", Date.valueOf("1994-03-11"), "Scotland", 178),
                new InitPlayerReq("David de Gea", "Manchester United", Date.valueOf("1990-11-07"), "Spain", 192),
                new InitPlayerReq("Kevin De Bruyne", "Manchester City", Date.valueOf("1991-06-28"), "Belgium", 181)
        );
        //Player List
        List<Player> playerList = new ArrayList<>();
        long counter = 1L;
        for (InitPlayerReq playerReq : playerReqs) {
            Club club = clubList.stream()
                    .filter(it -> Objects.equals(it.getName(), playerReq.getName()))
                    .findAny().orElse(null);
            Player player = Player.builder()
                    .name(playerReq.getName())
                    .club(club)
                    .build();
            player.setId(counter);
            PersonalDetails profile = PersonalDetails.builder()
                    .dob(playerReq.getDob())
                    .nationality(playerReq.getNationality())
                    .height(playerReq.getHeight())
                    .player(player)
                    .build();
            player.setProfile(profile);

            playerList.add(player);
            counter++;
        }

        return playerList;
    }

    Page<PlayerResp> createPlayerPage() {
        List<Player> playerList = createPlayerList();
        List<PlayerResp> playerRespList = playerList.stream()
                .map(PlayerResp::new)
                .collect(Collectors.toList());

        return new PageImpl<>(playerRespList);
    }

    //==============
    //-----LIST-----
    //==============
    @Test
    public void list_success() throws Exception {
        Page<PlayerResp> playerPage = createPlayerPage();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/players");

        when(playerService.list(any(PlayerListingCriteria.class))).thenReturn(playerPage);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size", Matchers.is(7)))
                .andExpect(jsonPath("$.content[0].name",
                        Matchers.is(playerPage.getContent().get(0).getName())));
    }

    @Test
    public void list_withFilterOn_success() throws Exception {
        Page<PlayerResp> clubPage = createPlayerPage();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/players")
                .param("limit", "15")
                .param("offset", "0")
                .param("order", "ASC")
                .param("sortBy", "id")
                .param("query", "");

        when(playerService.list(any(PlayerListingCriteria.class))).thenReturn(clubPage);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size", Matchers.is(7)))
                .andExpect(jsonPath("$.content[0].name",
                        Matchers.is(clubPage.getContent().get(0).getName())));
    }

    @Test
    public void list_withFilterOn_invalidLimitAnOffset() throws Exception {
        Page<PlayerResp> clubPage = createPlayerPage();
        String expLimitInvalidMsg = "Limit must not be less than one!";
        String expOffsetInvalidMsg = "Offset index must not be less than zero!";

        when(playerService.list(any(PlayerListingCriteria.class))).thenReturn(clubPage);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/players")
                .param("limit", "-1")
                .param("offset", "-1")
                .param("order", "ASC")
                .param("sortBy", "id")
                .param("query", "");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.limit", Matchers.is(expLimitInvalidMsg)))
                .andExpect(jsonPath("$.offset", Matchers.is(expOffsetInvalidMsg)));
    }

    //=============
    //-----GET-----
    //=============
    @Test
    public void get_success() throws Exception {
        Club club = new Club(1L, "Liverpool");
        Player player = new Player(2L, "Steven Gerrard", club);
        PersonalDetails playerProfile = PersonalDetails.builder()
                .player(player)
                .dob(Date.valueOf("1980-05-30"))
                .nationality("England")
                .height(185)
                .build();
        player.setProfile(playerProfile);

        when(playerService.get(any(Long.class))).thenReturn(new PlayerDetailsResp(player));

        mockMvc.perform(MockMvcRequestBuilders.get("/players/" + player.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(player.getName())));
    }

    @Test
    public void get_notFound() throws Exception {
        long id = 1L;
        String reason = "There is no player with id: " + id;
        String msg = HttpStatus.NOT_FOUND + " \"" + reason + "\"";

        when(playerService.get(any(Long.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/players/" + id))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof ResponseStatusException);
                }).andExpect(result -> {
                    assertEquals(msg, result.getResolvedException().getMessage());
                });
    }

    //==============
    //----CREATE----
    //==============
    @Test @WithMockUser(roles = RoleConstant.ADMINISTRATOR)
    public void create_byAdmin_success() throws Exception {
        Club club = new Club(1L, "Liverpool");
        Player player = new Player(2L, "Steven Gerrard", club);
        PlayerReq playerReq = new PlayerReq(player.getName(), club.getId());
        PlayerResp playerResp = new PlayerResp(player);
        String expectedMsg = "Successfully created a new player: " + playerResp.getName();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(playerReq))
                .accept(MediaType.APPLICATION_JSON);

        when(playerManagementService.create(any(PlayerReq.class))).thenReturn(playerResp);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(expectedMsg)));
    }

    @Test @WithMockUser(roles = RoleConstant.CREATOR)
    public void create_byCreator_success() throws Exception {
        Club club = new Club(1L, "Liverpool");
        Player player = new Player(2L, "Steven Gerrard", club);
        PlayerReq playerReq = new PlayerReq(player.getName(), club.getId());
        PlayerResp playerResp = new PlayerResp(player);
        String expectedMsg = "Successfully created a new player: " + playerResp.getName();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(playerReq))
                .accept(MediaType.APPLICATION_JSON);

        when(playerManagementService.create(any(PlayerReq.class))).thenReturn(playerResp);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(expectedMsg)));
    }

    @Test @WithMockUser(roles = RoleConstant.VIEWER)
    public void create_forbidden() throws Exception {
        Club club = new Club(1L, "Liverpool");
        Player player = new Player(2L, "Steven Gerrard", club);
        PlayerReq playerReq = new PlayerReq(player.getName(), club.getId());
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(playerReq))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden());
    }

    @Test
    public void create_unauthorized() throws Exception {
        Club club = new Club(1L, "Liverpool");
        Player player = new Player(2L, "Steven Gerrard", club);
        PlayerReq playerReq = new PlayerReq(player.getName(), club.getId());
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(playerReq))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isUnauthorized());
    }

    //==============
    //----UPDATE----
    //==============
    @Test @WithMockUser(roles = RoleConstant.ADMINISTRATOR)
    public void update_byAdministrator_success() throws Exception {
        Club club = new Club(1L, "Liverpool");
        Player player = new Player(2L, "Steven Gerrard", club);
        PlayerReq playerReq = new PlayerReq(player.getName(), club.getId());
        PlayerResp playerResp = new PlayerResp(player);
        String expectedMsg = "Successfully updated a player: " + playerResp.getName();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/players/" + player.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(playerReq))
                .accept(MediaType.APPLICATION_JSON);

        when(playerManagementService.update(any(Long.class), any(PlayerReq.class))).thenReturn(playerResp);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(expectedMsg)));
    }

    @Test @WithMockUser(roles = RoleConstant.EDITOR)
    public void update_byEditor_success() throws Exception {
        Club club = new Club(1L, "Liverpool");
        Player player = new Player(2L, "Steven Gerrard", club);
        PlayerReq playerReq = new PlayerReq(player.getName(), club.getId());
        PlayerResp playerResp = new PlayerResp(player);
        String expectedMsg = "Successfully updated a player: " + playerResp.getName();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/players/" + player.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(playerReq))
                .accept(MediaType.APPLICATION_JSON);

        when(playerManagementService.update(any(Long.class), any(PlayerReq.class))).thenReturn(playerResp);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(expectedMsg)));
    }

    @Test @WithMockUser(roles = RoleConstant.VIEWER)
    public void update_forbidden() throws Exception {
        Club club = new Club(1L, "Liverpool");
        Player player = new Player(2L, "Steven Gerrard", club);
        PlayerReq playerReq = new PlayerReq(player.getName(), club.getId());
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/players/" + player.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(playerReq))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden());
    }

    @Test
    public void update_unauthorized() throws Exception {
        Club club = new Club(1L, "Liverpool");
        Player player = new Player(2L, "Steven Gerrard", club);
        PlayerReq playerReq = new PlayerReq(player.getName(), club.getId());
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/players/" + player.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(playerReq))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test @WithMockUser(roles = RoleConstant.ADMINISTRATOR)
    public void update_notFound() throws Exception {
        Club club = new Club(1L, "Liverpool");
        Player player = new Player(2L, "Steven Gerrard", club);
        PlayerReq playerReq = new PlayerReq(player.getName(), club.getId());
        String reason = "There is no player with id: " + player.getId();
        String expectedMsg = HttpStatus.NOT_FOUND + " \"" + reason + "\"";
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/players/" + player.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(playerReq))
                .accept(MediaType.APPLICATION_JSON);

        when(playerManagementService.update(any(Long.class), any(PlayerReq.class))).thenReturn(null);

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof ResponseStatusException);
                }).andExpect(result -> {
                    assertEquals(expectedMsg, result.getResolvedException().getMessage());
                });
    }

    //==============
    //----DELETE----
    //==============
    @Test @WithMockUser(roles = RoleConstant.ADMINISTRATOR)
    public void delete_byAdministrator_success() throws Exception {
        Club club = new Club(1L, "Liverpool");
        Player player = new Player(2L, "Steven Gerrard", club);
        PlayerResp playerResp = new PlayerResp(player);
        String expectedMsg = "Successfully deleted a player: " + playerResp.getName();

        when(playerManagementService.delete(any(Long.class))).thenReturn(playerResp);

        mockMvc.perform(MockMvcRequestBuilders.delete("/players/" + player.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(expectedMsg)));
    }

    @Test @WithMockUser(roles = RoleConstant.VIEWER)
    public void delete_forbidden() throws Exception {
        Club club = new Club(1L, "Liverpool");
        Player player = new Player(2L, "Steven Gerrard", club);

        mockMvc.perform(MockMvcRequestBuilders.delete("/players/" + player.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void delete_unauthorized() throws Exception {
        Club club = new Club(1L, "Liverpool");
        Player player = new Player(2L, "Steven Gerrard", club);

        mockMvc.perform(MockMvcRequestBuilders.delete("/players/" + player.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test @WithMockUser(roles = RoleConstant.ADMINISTRATOR)
    public void delete_notFound() throws Exception {
        Club club = new Club(1L, "Liverpool");
        Player player = new Player(2L, "Steven Gerrard", club);
        String reason = "There is no player with id: " + player.getId();
        String expectedMsg = HttpStatus.NOT_FOUND + " \"" + reason + "\"";

        when(playerManagementService.delete(any(Long.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.delete("/players/" + player.getId()))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof ResponseStatusException);
                }).andExpect(result -> {
                    assertEquals(expectedMsg, result.getResolvedException().getMessage());
                });
    }
}
