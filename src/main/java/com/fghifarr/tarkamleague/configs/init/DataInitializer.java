package com.fghifarr.tarkamleague.configs.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Component
@Profile("dev")
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Value("${spring.jpa.hibernate.ddl-auto}")
    String ddlAuto;

    @Autowired
    private RoleInitService roleInitService;
    @Autowired
    private RoleGroupInitService roleGroupInitService;
    @Autowired
    private UserInitService userInitService;
    @Autowired
    private ClubInitService clubInitService;
    @Autowired
    private PlayerInitService playerInitService;
    @Autowired
    private SeasonInitService seasonInitService;
    @Autowired
    private FixtureInitService fixtureInitService;

    @PostConstruct
    public void init() {
        if (!Objects.equals(ddlAuto, "create-drop"))
            return;

        log.info("Successfully initialized " + roleInitService.init() + " data for Role");
        log.info("Successfully initialized " + roleGroupInitService.init() + " data for RoleGroup");
        log.info("Successfully initialized " + userInitService.init() + " data for User");
        log.info("Successfully initialized " + clubInitService.init() + " data for Club");
        log.info("Successfully initialized " + playerInitService.init() + " data for Player");
//        log.info("Successfully initialized " + seasonInitService.init() + " data for Season");
        log.info("Successfully initialized " + fixtureInitService.init() + " data for Match");
    }
}
