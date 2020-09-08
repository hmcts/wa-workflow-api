package uk.gov.hmcts.reform.waworkflowapi.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.waworkflowapi.api.CreateTaskRequest;
import uk.gov.hmcts.reform.waworkflowapi.duedate.DateService;
import uk.gov.hmcts.reform.waworkflowapi.duedate.DueDateService;
import uk.gov.hmcts.reform.waworkflowapi.external.taskservice.CamundaClient;
import uk.gov.hmcts.reform.waworkflowapi.external.taskservice.DmnRequest;
import uk.gov.hmcts.reform.waworkflowapi.external.taskservice.GetTaskDmnRequest;
import uk.gov.hmcts.reform.waworkflowapi.external.taskservice.GetTaskDmnResult;
import uk.gov.hmcts.reform.waworkflowapi.external.taskservice.ProcessVariables;
import uk.gov.hmcts.reform.waworkflowapi.external.taskservice.SendMessageRequest;
import uk.gov.hmcts.reform.waworkflowapi.external.taskservice.TaskToCreate;

import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZonedDateTime.parse;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.waworkflowapi.api.CreateTaskRequestCreator.appealSubmittedCreateTaskRequest;
import static uk.gov.hmcts.reform.waworkflowapi.api.CreateTaskRequestCreator.appealSubmittedCreateTaskRequestWithDueDate;
import static uk.gov.hmcts.reform.waworkflowapi.api.CreatorObjectMapper.asJsonString;
import static uk.gov.hmcts.reform.waworkflowapi.external.taskservice.DmnValue.dmnIntegerValue;
import static uk.gov.hmcts.reform.waworkflowapi.external.taskservice.DmnValue.dmnStringValue;
import static uk.gov.hmcts.reform.waworkflowapi.external.taskservice.Task.PROCESS_APPLICATION;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings({"PMD.JUnitTestsShouldIncludeAssert", "PMD.ExcessiveImports"})
class CreateTaskTest {

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private CamundaClient camundaClient;

    @Autowired
    private DueDateService dueDateService;

    @MockBean DateService dateService;

    @DisplayName("Should create task with default due date and 201 response")
    @Test
    void createsTaskForTransitionWithoutDueDate() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        when(dateService.now()).thenReturn(now);

        CreateTaskRequest createTaskRequest = appealSubmittedCreateTaskRequest("1234567890");
        when(camundaClient.getTask(createGetTaskDmnRequest(createTaskRequest)))
            .thenReturn(createGetTaskResponse());
        mockMvc.perform(
            post("/tasks")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(createTaskRequest))
        ).andExpect(status().isCreated()).andReturn();

        ZonedDateTime expectedDueDate = dueDateService.calculateDueDate(
            null,
            new TaskToCreate(null, null, 5)
        );

        verify(camundaClient).sendMessage(
            new SendMessageRequest(
                "createTaskMessage",
                new ProcessVariables(
                    createTaskRequest.getCaseId(),
                    PROCESS_APPLICATION,
                    "TCW",
                    expectedDueDate
                )
            )
        );
    }

    @DisplayName("Should create task with 201 response")
    @Test
    void createsTaskForTransitionAndDueDate() throws Exception {
        CreateTaskRequest createTaskRequest = appealSubmittedCreateTaskRequestWithDueDate("1234567890");
        when(camundaClient.getTask(createGetTaskDmnRequest(createTaskRequest)))
            .thenReturn(createGetTaskResponse());
        mockMvc.perform(
            post("/tasks")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(createTaskRequest))
        ).andExpect(status().isCreated()).andReturn();

        verify(camundaClient).sendMessage(
            new SendMessageRequest(
                "createTaskMessage",
                new ProcessVariables(
                    createTaskRequest.getCaseId(),
                    PROCESS_APPLICATION,
                    "TCW",
                    parse(createTaskRequest.getDueDate())
                )
            )
        );
    }

    @DisplayName("Should not create task with 204 response")
    @Test
    void doesNotCreateTaskForTransition() throws Exception {
        CreateTaskRequest createTaskRequest = appealSubmittedCreateTaskRequest("1234567890");
        when(camundaClient.getTask(createGetTaskDmnRequest(createTaskRequest)))
            .thenReturn(emptyList());
        mockMvc.perform(
            post("/tasks")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(createTaskRequest))
        ).andExpect(status().isNoContent()).andReturn();

        verify(camundaClient, never()).sendMessage(any(SendMessageRequest.class));
    }

    private DmnRequest<GetTaskDmnRequest> createGetTaskDmnRequest(CreateTaskRequest createTaskRequest) {
        return new DmnRequest<>(
            new GetTaskDmnRequest(
                dmnStringValue(createTaskRequest.getTransition().getEventId()),
                dmnStringValue(createTaskRequest.getTransition().getPostState())
            )
        );
    }

    private List<GetTaskDmnResult> createGetTaskResponse() {
        return singletonList(new GetTaskDmnResult(
                                 dmnStringValue(PROCESS_APPLICATION.getId()),
                                 dmnStringValue("TCW"),
                                 dmnIntegerValue(5)
                             )
        );
    }
}
