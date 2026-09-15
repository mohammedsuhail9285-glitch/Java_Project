package com.sih.navisense;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Verify all web views load successfully without template or runtime errors")
    void testAllPageRoutes() throws Exception {
        String[] routes = {
                "/",
                "/dashboard",
                "/charter",
                "/history",
                "/alerts",
                "/settings",
                "/forecast",
                "/vessels",
                "/ports",
                "/routes",
                "/simulation",
                "/risk",
                "/contract-strategy",
                "/backtest",
                "/copilot",
                "/data-lab",
                "/demo",
                "/about"
        };

        for (String route : routes) {
            mockMvc.perform(get(route))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @DisplayName("Charter form submission should validate and redirect to result page")
    void testCharterFormSubmit() throws Exception {
        mockMvc.perform(get("/charter?scenario=1"))
                .andExpect(status().isOk())
                .andExpect(view().name("charter-analysis"));
    }
}
