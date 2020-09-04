package uk.gov.hmcts.reform.waworkflowapi.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
class GetCamundaTaskByIdTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void TaskByIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                            .get("/task/025c59e3-dbe2-11ea-81e2-661816095024")
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
