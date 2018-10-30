package org.innovation.authorization.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.security.Principal;

import org.junit.Test;

public class UserRestControllerTest {

    private UserRestController userRestController = new UserRestController();

    @Test
    public void testGetUser() throws Exception {
        Principal principal = mock(Principal.class);
        Principal user = userRestController.getUser(principal);

        assertThat(user).as("returned user from rest call with principal").isEqualTo(principal);
    }
}
