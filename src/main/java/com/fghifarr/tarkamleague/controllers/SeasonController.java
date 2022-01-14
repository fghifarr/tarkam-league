package com.fghifarr.tarkamleague.controllers;

import com.fghifarr.tarkamleague.configs.constants.RoleConstant;
import com.fghifarr.tarkamleague.models.requests.SeasonListingCriteria;
import com.fghifarr.tarkamleague.models.requests.SeasonReq;
import com.fghifarr.tarkamleague.models.responses.SeasonResp;
import com.fghifarr.tarkamleague.services.SeasonService;
import com.fghifarr.tarkamleague.services.transactional.SeasonManagementService;
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
@RequestMapping("/seasons")
public class SeasonController extends BaseController {

    private final SeasonService seasonService;
    private final SeasonManagementService seasonManagementService;

    public SeasonController(SeasonService seasonService, SeasonManagementService seasonManagementService) {
        this.seasonService = seasonService;
        this.seasonManagementService = seasonManagementService;
    }

    @GetMapping("")
    public @ResponseBody
    Page<SeasonResp> list(@Valid SeasonListingCriteria criteria, BindingResult bindingResult)
            throws NoSuchMethodException, MethodArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(
                    new MethodParameter(
                            this.getClass().getDeclaredMethod(
                                    "list", SeasonListingCriteria.class, BindingResult.class
                            ), 0),
                    bindingResult
            );
        }

        return seasonService.list(criteria);
    }

    @GetMapping("/{id}")
    public @ResponseBody
    SeasonResp get(@PathVariable Long id) {
        SeasonResp season = seasonService.get(id);
        if (season == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "There is no season with id: " + id);

        return season;
    }

    @PreAuthorize(RoleConstant.HAS_ROLE_ADMINISTRATOR_CREATOR)
    @PostMapping("")
    public @ResponseBody
    String create(@Valid @RequestBody SeasonReq seasonReq) {
        SeasonResp newSeason = seasonManagementService.create(seasonReq);

        return "Successfully added new season: " + newSeason.getName();
    }

    @PreAuthorize(RoleConstant.HAS_ROLE_ADMINISTRATOR_EDITOR)
    @PutMapping("/{id}")
    public @ResponseBody
    String update(@PathVariable Long id,
                  @Valid @RequestBody SeasonReq seasonReq) {
        SeasonResp updatedClub = seasonManagementService.update(id, seasonReq);
        if (updatedClub == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "There is no season with id: " + id);

        return "Successfully updated season: " + updatedClub.getName();
    }

    @PreAuthorize(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @DeleteMapping("/{id}")
    public @ResponseBody
    String delete(@PathVariable Long id) {
        SeasonResp deletedClub = seasonManagementService.delete(id);
        if (deletedClub == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "There is no season with id: " + id);

        return "Successfully deleted season: " + deletedClub.getName();
    }
}
