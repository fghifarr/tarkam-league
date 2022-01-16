package com.fghifarr.tarkamleague.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fghifarr.tarkamleague.models.responses.AuthenticationResp;
import com.fghifarr.tarkamleague.configs.security.JwtUtils;
import com.fghifarr.tarkamleague.models.requests.LoginReq;
import com.fghifarr.tarkamleague.configs.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final ObjectMapper mapper;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    JwtUtils jwtUtils, ObjectMapper mapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.mapper = mapper;
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@Valid @RequestBody LoginReq loginReq) {
        String respBody = "";
        String jwt = "";

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginReq.getUsername(), loginReq.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            jwt = jwtUtils.generateToken(authentication);

            UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            AuthenticationResp resp = new AuthenticationResp(
                    jwt, "bearer", userDetails.getUsername(), roles
            );
            respBody = mapper.writeValueAsString(resp);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse object to string: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return ResponseEntity.ok(respBody);
    }
}
