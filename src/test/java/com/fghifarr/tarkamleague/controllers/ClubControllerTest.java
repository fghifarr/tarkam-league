package com.fghifarr.tarkamleague.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.models.requests.ClubListingCriteria;
import com.fghifarr.tarkamleague.models.requests.ClubReq;
import com.fghifarr.tarkamleague.models.responses.ClubResp;
import com.fghifarr.tarkamleague.services.ClubManagementService;
import com.fghifarr.tarkamleague.services.ClubService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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

@WebMvcTest(ClubController.class)
@AutoConfigureMockMvc
public class ClubControllerTest {

    @MockBean
    private ClubService clubService;
    @MockBean
    private ClubManagementService clubManagementService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    //========================
    //-----DATA GENERATOR-----
    //========================
    List<Club> createClubList() {
        return List.of(
                new Club(1L, "Liverpool"),
                new Club(2L, "Manchester City"),
                new Club(3L, "Manchester United"),
                new Club(4L, "Newcastle United"),
                new Club(5L, "West Ham United"),
                new Club(6L, "Leicester City")
        );
    }

    Page<ClubResp> createClubPage() {
        List<Club> clubList = createClubList();
        List<ClubResp> clubRespList = clubList.stream()
                .map(ClubResp::new)
                .collect(Collectors.toList());
        return new PageImpl<>(clubRespList);
    }

    //==============
    //-----LIST-----
    //==============
    @Test
    public void list_success() throws Exception {
        Page<ClubResp> clubPage = createClubPage();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/clubs");

        when(clubService.list(any(ClubListingCriteria.class))).thenReturn(clubPage);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size", Matchers.is(6)))
                .andExpect(jsonPath("$.content[0].name",
                        Matchers.is(clubPage.getContent().get(0).getName())));
    }

    @Test
    public void list_withFilterOn_success() throws Exception {
        Page<ClubResp> clubPage = createClubPage();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/clubs")
                .param("limit", "15")
                .param("offset", "0")
                .param("order", "ASC")
                .param("sortBy", "id")
                .param("query", "");

        when(clubService.list(any(ClubListingCriteria.class))).thenReturn(clubPage);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size", Matchers.is(6)))
                .andExpect(jsonPath("$.content[0].name",
                        Matchers.is(clubPage.getContent().get(0).getName())));
    }

    @Test
    public void list_withFilterOn_invalidLimitAnOffset() throws Exception {
        Page<ClubResp> clubPage = createClubPage();
        String expLimitInvalidMsg = "Limit must not be less than one!";
        String expOffsetInvalidMsg = "Offset index must not be less than zero!";

        when(clubService.list(any(ClubListingCriteria.class))).thenReturn(clubPage);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/clubs")
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
        Club liverpool = new Club(1L, "Liverpool");

        when(clubService.get(any(Long.class))).thenReturn(new ClubResp(liverpool));

        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(liverpool.getName())));
    }

    @Test
    public void get_notFound() throws Exception {
        long id = 1L;
        String reason = "There is no club with with id: " + id;
        String msg = HttpStatus.NOT_FOUND + " \"" + reason + "\"";

        when(clubService.get(any(Long.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + id))
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
    @Test
    public void create_success() throws Exception {
        Club club = new Club(1L, "Liverpool");
        ClubReq clubReq = new ClubReq(club.getName());
        ClubResp clubResp = new ClubResp(club);
        String expectedMsg = "Successfully added new club: " + clubResp.getName();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/clubs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(clubReq))
                .accept(MediaType.APPLICATION_JSON);

        when(clubManagementService.create(any(ClubReq.class))).thenReturn(clubResp);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(expectedMsg)));
    }

    //==============
    //----UPDATE----
    //==============
    @Test
    public void update_success() throws Exception {
        Club club = new Club(1L, "Liverpool");
        ClubReq clubReq = new ClubReq(club.getName());
        ClubResp clubResp = new ClubResp(club);
        String expectedMsg = "Successfully updated club: " + clubResp.getName();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/clubs/" + club.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(clubReq))
                .accept(MediaType.APPLICATION_JSON);

        when(clubManagementService.update(any(Long.class), any(ClubReq.class))).thenReturn(clubResp);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(expectedMsg)));
    }

    @Test
    public void update_notFound() throws Exception {
        Club club = new Club(1L, "Liverpool");
        ClubReq clubReq = new ClubReq(club.getName());
        String reason = "There is no club with with id: " + club.getId();
        String expectedMsg = HttpStatus.NOT_FOUND + " \"" + reason + "\"";
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/clubs/" + club.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(clubReq))
                .accept(MediaType.APPLICATION_JSON);

        when(clubManagementService.update(any(Long.class), any(ClubReq.class))).thenReturn(null);

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
    @Test
    public void delete_success() throws Exception {
        Club club = new Club(1L, "Liverpool");
        ClubResp clubResp = new ClubResp(club);
        String expectedMsg = "Successfully deleted club: " + clubResp.getName();

        when(clubManagementService.delete(any(Long.class))).thenReturn(clubResp);

        mockMvc.perform(MockMvcRequestBuilders.delete("/clubs/" + club.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(expectedMsg)));
    }

    @Test
    public void delete_notFound() throws Exception {
        Club club = new Club(1L, "Liverpool");
        String reason = "There is no club with with id: " + club.getId();
        String expectedMsg = HttpStatus.NOT_FOUND + " \"" + reason + "\"";

        when(clubManagementService.delete(any(Long.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.delete("/clubs/" + club.getId()))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof ResponseStatusException);
                }).andExpect(result -> {
                    assertEquals(expectedMsg, result.getResolvedException().getMessage());
                });
    }
}
