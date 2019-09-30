package com.project.bookstore.controller;

import com.project.bookstore.config.SecurityConfig;
import com.project.bookstore.service.impl.UserSecurityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SignInRegisterController.class)
@Import(SecurityConfig.class)
class SignInRegisterControllerTest {

    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=UTF-8";

    @MockBean
    UserSecurityService userSecurityService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void testLoginEndpoint() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_HTML_CHARSET_UTF_8))
                .andExpect(model().attribute("classActiveLogin", "active"))
                .andExpect(view().name("login"));
    }

    @Test
    void testSignUpEndpoint() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_HTML_CHARSET_UTF_8))
                .andExpect(model().attribute("classActiveLogin", "active"))
                .andExpect(view().name("signup"));
    }

    @Test
    void testDeliveryInfoEndpoint() throws Exception {
        mockMvc.perform(get("/deliveryInfo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_HTML_CHARSET_UTF_8))
                .andExpect(view().name("deliveryInformation"));
    }
}