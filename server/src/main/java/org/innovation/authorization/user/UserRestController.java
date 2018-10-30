package org.innovation.authorization.user;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/user")
public class UserRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRestController.class);

    @RequestMapping(method = RequestMethod.GET)
    public Principal getUser(Principal principal) {
        LOGGER.debug("Returning principal: [{}] for REST request", principal.getName());

        return principal;
    }
}
