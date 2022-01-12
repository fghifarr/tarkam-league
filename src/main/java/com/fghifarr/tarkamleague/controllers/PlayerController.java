package com.fghifarr.tarkamleague.controllers;

import com.fghifarr.tarkamleague.configs.constants.RoleConstant;
import com.fghifarr.tarkamleague.models.requests.PlayerListingCriteria;
import com.fghifarr.tarkamleague.models.requests.PlayerReq;
import com.fghifarr.tarkamleague.models.responses.PlayerDetailsResp;
import com.fghifarr.tarkamleague.models.responses.PlayerResp;
import com.fghifarr.tarkamleague.services.PlayerService;
import com.fghifarr.tarkamleague.services.transactional.PlayerManagementService;
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
@RequestMapping("/players")
public class PlayerController extends BaseController {

    private final PlayerService playerService;
    private final PlayerManagementService playerManagementService;

    public PlayerController(PlayerService playerService, PlayerManagementService playerManagementService) {
        this.playerService = playerService;
        this.playerManagementService = playerManagementService;
    }

    @GetMapping("")
    public @ResponseBody
    Page<PlayerResp> list(@Valid PlayerListingCriteria criteria, BindingResult bindingResult)
            throws NoSuchMethodException, MethodArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(
                    new MethodParameter(
                            this.getClass().getDeclaredMethod(
                                    "list", PlayerListingCriteria.class, BindingResult.class
                            ), 0),
                    bindingResult
            );
        }

        return playerService.list(criteria);
    }

    @GetMapping("/{id}")
    public @ResponseBody
    PlayerDetailsResp get(@PathVariable Long id) {
        PlayerDetailsResp playerDetails = playerService.get(id);
        if (playerDetails == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "There is no player with id: " + id);

        return playerDetails;
    }

    @PreAuthorize(RoleConstant.HAS_ROLE_ADMINISTRATOR_CREATOR)
    @PostMapping("")
    public @ResponseBody
    String  create(@Valid @RequestBody PlayerReq playerReq) {
        PlayerResp newPlayer = playerManagementService.create(playerReq);

        return "Successfully created a new player: " + newPlayer.getName();
    }

    @PreAuthorize(RoleConstant.HAS_ROLE_ADMINISTRATOR_EDITOR)
    @PutMapping("/{id}")
    public @ResponseBody
    String update(@PathVariable Long id, @Valid @RequestBody PlayerReq playerReq) {
        PlayerResp updatedPlayer = playerManagementService.update(id, playerReq);
        if (updatedPlayer == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "There is no player with id: " + id);

        return "Successfully updated a player: " + updatedPlayer.getName();
    }

    @PreAuthorize(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @DeleteMapping("/{id}")
    public @ResponseBody
    String delete(@PathVariable Long id) {
        PlayerResp deletedPlayer = playerManagementService.delete(id);
        if (deletedPlayer == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "There is no player with id: " + id);

        return "Successfully deleted a player: " + deletedPlayer.getName();
    }
}
