package com.fghifarr.tarkamleague.controllers;

import com.fghifarr.tarkamleague.configs.constants.RoleConstant;
import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Player;
import com.fghifarr.tarkamleague.models.requests.PlayerReq;
import com.fghifarr.tarkamleague.models.requests.PlayerListingCriteria;
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

import java.util.List;
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
        Club liverpool = new Club(1L, "Liverpool");
        Club manCity = new Club(2L, "Manchester City");
        Club manUtd = new Club(3L, "Manchester United");

        return List.of(
                new Player(7L, "Steven Gerrard"),
                new Player(8L, "Wayne Rooney"),
                new Player(9L, "Mohammed Salah", liverpool),
                new Player(10L, "Trent Alexander-Arnold", liverpool),
                new Player(11L, "Andrew Robertson", liverpool),
                new Player(12L, "David De Gea", manUtd),
                new Player(13L, "Kevin De Bruyne", manCity)
        );
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

        when(playerService.get(any(Long.class))).thenReturn(new PlayerResp(player));

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
