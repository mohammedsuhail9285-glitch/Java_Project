package com.sih.navisense;

import com.sih.navisense.dto.CopilotChatDto;
import com.sih.navisense.service.CopilotService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CopilotDynamicQueryTest {

    @Autowired
    private CopilotService copilotService;

    @Test
    @DisplayName("Verify that asking why Capesize is rejected returns domain draft explanation")
    void testCapesizeDraftQuery() {
        CopilotChatDto reply = copilotService.answerQuestion("Why was Capesize rejected for Paradip?");
        assertThat(reply.getAnswer()).contains("16.5m");
        assertThat(reply.getCategory()).isEqualTo("PORT_COMPATIBILITY");
    }

    @Test
    @DisplayName("Verify that asking dynamic query with parcel tonnage returns calculated figures")
    void testDynamicTonnageQuery() {
        CopilotChatDto reply = copilotService.answerQuestion("What vessel should I use for 150,000 tonnes from Australia to Paradip?");
        assertThat(reply.getCategory()).isEqualTo("DYNAMIC_CHARTER_INTELLIGENCE");
        assertThat(reply.getAnswer()).contains("150,000 tonnes");
        assertThat(reply.getAnswer()).contains("Panamax");
        assertThat(reply.getAnswer()).contains("savings of");
    }

    @Test
    @DisplayName("Verify timing query explains forecast trend and laycan recommendation")
    void testBookingTimingQuery() {
        CopilotChatDto reply = copilotService.answerQuestion("Should I book now or wait?");
        assertThat(reply.getCategory()).isEqualTo("FREIGHT_FORECAST");
        assertThat(reply.getAnswer()).contains("BOOK NOW");
    }
}
