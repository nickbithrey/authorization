package org.innovation.authorization.ui;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class HomeControllerTest {

    private HomeController homeController = new HomeController();

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testHome() {
        assertThat(homeController.home()).as("result from home mapping").isEqualTo("index");
    }

    @Test
    public void testHomeMapping() {
        Method homeMethod = ReflectionUtils.findMethod(HomeController.class, "home");
        RequestMapping mapping = homeMethod.getAnnotation(RequestMapping.class);

        softly.assertThat(mapping.path()).containsExactly("/home");
        softly.assertThat(mapping.method()).containsExactly(RequestMethod.GET);
    }

}
