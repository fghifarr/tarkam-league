package com.fghifarr.tarkamleague.controllers;

import com.fghifarr.tarkamleague.models.requests.ClubListingCriteria;
import com.fghifarr.tarkamleague.models.requests.FixtureListingCriteria;
import com.fghifarr.tarkamleague.models.responses.FixtureResp;
import com.fghifarr.tarkamleague.services.FixtureService;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/fixtures")
public class FixtureController {

    private final FixtureService fixtureService;

    public FixtureController(FixtureService fixtureService) {
        this.fixtureService = fixtureService;
    }

    @GetMapping("")
    public @ResponseBody
    Page<FixtureResp> list(@Valid FixtureListingCriteria criteria, BindingResult bindingResult)
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

        return fixtureService.list(criteria);
    }
}
