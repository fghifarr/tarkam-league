package com.fghifarr.tarkamleague.controllers;

import com.fghifarr.tarkamleague.configs.constants.RoleConstant;
import com.fghifarr.tarkamleague.entities.BaseEntity;
import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Season;
import com.fghifarr.tarkamleague.models.requests.SeasonListingCriteria;
import com.fghifarr.tarkamleague.models.requests.SeasonReq;
import com.fghifarr.tarkamleague.models.responses.SeasonResp;
import com.fghifarr.tarkamleague.services.SeasonService;
import com.fghifarr.tarkamleague.services.transactional.SeasonManagementService;
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
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SeasonControllerTest extends BaseControllerTest {

    @MockBean
    private SeasonService seasonService;
    @MockBean
    private SeasonManagementService seasonManagementService;

    //========================
    //-----DATA GENERATOR-----
    //========================
    Set<Club> createClubSet() {
        return Set.of(
                new Club(1L, "Liverpool"),
                new Club(2L, "Manchester City"),
                new Club(3L, "Manchester United"),
                new Club(4L, "Newcastle United"),
                new Club(5L, "West Ham United"),
                new Club(6L, "Leicester City")
        );
    }

    List<Season> createSeasonList() {
        Set<Club> clubList = createClubSet();

        return List.of(
                new Season("2021/22", clubList),
                new Season("2020/21", clubList),
                new Season("2019/20", clubList),
                new Season("2018/19", clubList),
                new Season("2017/18", clubList)
        );
    }

    Page<SeasonResp> createSeasonPage() {
        List<Season> seasonList = createSeasonList();
        List<SeasonResp> seasonRespList = seasonList.stream()
                .map(SeasonResp::new)
                .collect(Collectors.toList());

        return new PageImpl<>(seasonRespList);
    }

    Season createSeason() {
        Set<Club> clubList = createClubSet();

        return new Season(1L, "2021/22", clubList);
    }

    //==============
    //-----LIST-----
    //==============
    @Test
    public void list_success() throws Exception {
        Page<SeasonResp> seasonPage = createSeasonPage();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/seasons");

        when(seasonService.list(any(SeasonListingCriteria.class))).thenReturn(seasonPage);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size", Matchers.is(seasonPage.getContent().size())))
                .andExpect(jsonPath("$.content[0].name",
                        Matchers.is(seasonPage.getContent().get(0).getName())));
    }

    @Test
    public void list_withFilterOn_success() throws Exception {
        Page<SeasonResp> seasonPage = createSeasonPage();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/seasons")
                .param("limit", "15")
                .param("offset", "0")
                .param("order", "ASC")
                .param("sortBy", "id")
                .param("query", "");

        when(seasonService.list(any(SeasonListingCriteria.class))).thenReturn(seasonPage);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size", Matchers.is(seasonPage.getContent().size())))
                .andExpect(jsonPath("$.content[0].name",
                        Matchers.is(seasonPage.getContent().get(0).getName())));
    }

    @Test
    public void list_withFilterOn_invalidLimitAnOffset() throws Exception {
        Page<SeasonResp> seasonPage = createSeasonPage();
        String expLimitInvalidMsg = "Limit must not be less than one!";
        String expOffsetInvalidMsg = "Offset index must not be less than zero!";

        when(seasonService.list(any(SeasonListingCriteria.class))).thenReturn(seasonPage);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/seasons")
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
        Season season = createSeason();

        when(seasonService.get(any(Long.class))).thenReturn(new SeasonResp(season));

        mockMvc.perform(MockMvcRequestBuilders.get("/seasons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(season.getName())));
    }

    @Test
    public void get_notFound() throws Exception {
        long id = 1L;
        String reason = "There is no season with id: " + id;
        String msg = HttpStatus.NOT_FOUND + " \"" + reason + "\"";

        when(seasonService.get(any(Long.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/seasons/" + id))
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
    public void create_byAdministrator_success() throws Exception {
        Season season = createSeason();
        SeasonReq seasonReq = new SeasonReq(season.getName(),
                season.getClubs().stream().map(BaseEntity::getId).collect(Collectors.toSet()));
        SeasonResp clubResp = new SeasonResp(season);
        String expectedMsg = "Successfully added new season: " + clubResp.getName();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/seasons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(seasonReq))
                .accept(MediaType.APPLICATION_JSON);

        when(seasonManagementService.create(any(SeasonReq.class))).thenReturn(clubResp);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(expectedMsg)));
    }

    @Test @WithMockUser(roles = RoleConstant.CREATOR)
    public void create_byCreator_success() throws Exception {
        Season season = createSeason();
        SeasonReq seasonReq = new SeasonReq(season.getName(),
                season.getClubs().stream().map(BaseEntity::getId).collect(Collectors.toSet()));
        SeasonResp clubResp = new SeasonResp(season);
        String expectedMsg = "Successfully added new season: " + clubResp.getName();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/seasons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(seasonReq))
                .accept(MediaType.APPLICATION_JSON);

        when(seasonManagementService.create(any(SeasonReq.class))).thenReturn(clubResp);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(expectedMsg)));
    }

    @Test @WithMockUser(roles = RoleConstant.VIEWER)
    public void create_forbidden() throws Exception {
        Season season = createSeason();
        SeasonReq seasonReq = new SeasonReq(season.getName(),
                season.getClubs().stream().map(BaseEntity::getId).collect(Collectors.toSet()));
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/seasons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(seasonReq))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden());
    }

    @Test
    public void create_unauthorized() throws Exception {
        Season season = createSeason();
        SeasonReq seasonReq = new SeasonReq(season.getName(),
                season.getClubs().stream().map(BaseEntity::getId).collect(Collectors.toSet()));
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/seasons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(seasonReq))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isUnauthorized());
    }

    //==============
    //----UPDATE----
    //==============
    @Test @WithMockUser(roles = RoleConstant.ADMINISTRATOR)
    public void update_byAdministrator_success() throws Exception {
        Season season = createSeason();
        SeasonReq seasonReq = new SeasonReq(season.getName(),
                season.getClubs().stream().map(BaseEntity::getId).collect(Collectors.toSet()));
        SeasonResp clubResp = new SeasonResp(season);
        String expectedMsg = "Successfully updated season: " + clubResp.getName();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/seasons/" + season.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(seasonReq))
                .accept(MediaType.APPLICATION_JSON);

        when(seasonManagementService.update(any(Long.class), any(SeasonReq.class))).thenReturn(clubResp);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(expectedMsg)));
    }

    @Test @WithMockUser(roles = RoleConstant.EDITOR)
    public void update_byEditor_success() throws Exception {
        Season season = createSeason();
        SeasonReq seasonReq = new SeasonReq(season.getName(),
                season.getClubs().stream().map(BaseEntity::getId).collect(Collectors.toSet()));
        SeasonResp clubResp = new SeasonResp(season);
        String expectedMsg = "Successfully updated season: " + clubResp.getName();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/seasons/" + season.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(seasonReq))
                .accept(MediaType.APPLICATION_JSON);

        when(seasonManagementService.update(any(Long.class), any(SeasonReq.class))).thenReturn(clubResp);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(expectedMsg)));
    }

    @Test @WithMockUser(roles = RoleConstant.VIEWER)
    public void update_forbidden() throws Exception {
        Season season = createSeason();
        SeasonReq seasonReq = new SeasonReq(season.getName(),
                season.getClubs().stream().map(BaseEntity::getId).collect(Collectors.toSet()));
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/seasons/" + season.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(seasonReq))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden());
    }

    @Test
    public void update_unauthorized() throws Exception {
        Season season = createSeason();
        SeasonReq seasonReq = new SeasonReq(season.getName(),
                season.getClubs().stream().map(BaseEntity::getId).collect(Collectors.toSet()));
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/seasons/" + season.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(seasonReq))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test @WithMockUser(roles = RoleConstant.ADMINISTRATOR)
    public void update_notFound() throws Exception {
        Season season = createSeason();
        SeasonReq seasonReq = new SeasonReq(season.getName(),
                season.getClubs().stream().map(BaseEntity::getId).collect(Collectors.toSet()));
        String reason = "There is no season with id: " + season.getId();
        String expectedMsg = HttpStatus.NOT_FOUND + " \"" + reason + "\"";
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/seasons/" + season.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(seasonReq))
                .accept(MediaType.APPLICATION_JSON);

        when(seasonManagementService.update(any(Long.class), any(SeasonReq.class))).thenReturn(null);

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
        Season season = createSeason();
        SeasonResp clubResp = new SeasonResp(season);
        String expectedMsg = "Successfully deleted season: " + clubResp.getName();

        when(seasonManagementService.delete(any(Long.class))).thenReturn(clubResp);

        mockMvc.perform(MockMvcRequestBuilders.delete("/seasons/" + season.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(expectedMsg)));
    }

    @Test @WithMockUser(roles = RoleConstant.VIEWER)
    public void delete_forbidden() throws Exception {
        Season season = createSeason();

        mockMvc.perform(MockMvcRequestBuilders.delete("/seasons/" + season.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void delete_unauthorized() throws Exception {
        Season season = createSeason();

        mockMvc.perform(MockMvcRequestBuilders.delete("/seasons/" + season.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test @WithMockUser(roles = RoleConstant.ADMINISTRATOR)
    public void delete_notFound() throws Exception {
        Season season = createSeason();
        String reason = "There is no season with id: " + season.getId();
        String expectedMsg = HttpStatus.NOT_FOUND + " \"" + reason + "\"";

        when(seasonManagementService.delete(any(Long.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.delete("/seasons/" + season.getId()))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof ResponseStatusException);
                }).andExpect(result -> {
                    assertEquals(expectedMsg, result.getResolvedException().getMessage());
                });
    }
}
