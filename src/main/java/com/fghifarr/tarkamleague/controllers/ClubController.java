package com.fghifarr.tarkamleague.controllers;

import com.fghifarr.tarkamleague.configs.constants.RoleConstant;
import com.fghifarr.tarkamleague.models.requests.ClubListingCriteria;
import com.fghifarr.tarkamleague.models.requests.ClubReq;
import com.fghifarr.tarkamleague.models.responses.ClubResp;
import com.fghifarr.tarkamleague.services.transactional.ClubManagementService;
import com.fghifarr.tarkamleague.services.ClubService;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/clubs")
public class ClubController extends BaseController {

    private final ClubService clubService;
    private final ClubManagementService clubManagementService;

    public ClubController(ClubService clubService, ClubManagementService clubManagementService) {
        this.clubService = clubService;
        this.clubManagementService = clubManagementService;
    }

    @GetMapping("")
    public @ResponseBody
    Page<ClubResp> list(@Valid ClubListingCriteria criteria, BindingResult bindingResult)
            throws NoSuchMethodException, MethodArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(
                    new MethodParameter(
                            this.getClass().getDeclaredMethod(
                                    "list", ClubListingCriteria.class, BindingResult.class
                            ), 0),
                    bindingResult
            );
        }

        return clubService.list(criteria);
    }

    @GetMapping("/{id}")
    public @ResponseBody
    ClubResp get(@PathVariable Long id) {
        ClubResp club = clubService.get(id);
        if (club == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "There is no club with id: " + id);

        return club;
    }

    @PreAuthorize(RoleConstant.HAS_ROLE_ADMINISTRATOR_CREATOR)
    @PostMapping("")
    public @ResponseBody
    String create(@Valid @RequestBody ClubReq clubReq) {
        ClubResp newClub = clubManagementService.create(clubReq);

        return "Successfully added new club: " + newClub.getName();
    }

    @PreAuthorize(RoleConstant.HAS_ROLE_ADMINISTRATOR_EDITOR)
    @PutMapping("/{id}")
    public @ResponseBody
    String update(@PathVariable Long id,
                  @Valid @RequestBody ClubReq clubReq) {
        ClubResp updatedClub = clubManagementService.update(id, clubReq);
        if (updatedClub == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "There is no club with id: " + id);

        return "Successfully updated club: " + updatedClub.getName();
    }

    @PreAuthorize(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @DeleteMapping("/{id}")
    public @ResponseBody
    String delete(@PathVariable Long id) {
        ClubResp deletedClub = clubManagementService.delete(id);
        if (deletedClub == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "There is no club with id: " + id);

        return "Successfully deleted club: " + deletedClub.getName();
    }
}
