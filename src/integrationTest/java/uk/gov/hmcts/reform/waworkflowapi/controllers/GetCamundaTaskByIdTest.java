package uk.gov.hmcts.reform.waworkflowapi.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.hmcts.reform.waworkflowapi.controllers.startworkflow.GetTaskController;
import uk.gov.hmcts.reform.waworkflowapi.models.Task;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class GetCamundaTaskByIdTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    GetTaskController getTaskController;

    @Test
    void taskByIdTest() throws Exception {
        when(getTaskController.getTask("025c59e3-dbe2-11ea-81e2-661816095024"))
            .thenReturn(new Task());
        mockMvc.perform(MockMvcRequestBuilders
                            .get("/task/025c59e3-dbe2-11ea-81e2-661816095024")
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());
    }
}
