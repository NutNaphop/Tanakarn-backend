package com.tanakarn.backend.transfer.controller;

import com.tanakarn.backend.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
public class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @Test
    void shouldBeReturn200AndTransferSuccess() throws Exception {
        String requestBody = """
                {
                    "fromAccountId": 1,
                    "toAccountId": 2,
                    "amount": 200
                }
                """;
        doNothing().when(accountService).transferMoney(1L, 2L, 1000L);

        mockMvc.perform(post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Transfer successful"));

        accountService.transferMoney(1L, 2L, 1000L);
    }

    @Test
    void shouldBeReturnBadRequestWhenTransferFails() throws Exception{
        String requestBody = """
                {
                    "fromAccountId": 1,
                    "toAccountId": 2,
                    "amount" : 200
                }
        """;

        doThrow(new RuntimeException("ยอดเงินไม่พอโอน"))
                .when(accountService).transferMoney(1L, 2L, 200L);

        mockMvc.perform(post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("ยอดเงินไม่พอโอน"));

        verify(accountService).transferMoney(1L, 2L, 200L);
    }
}
