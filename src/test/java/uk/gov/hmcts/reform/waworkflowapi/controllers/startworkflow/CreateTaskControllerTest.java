package uk.gov.hmcts.reform.waworkflowapi.controllers.startworkflow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.DmnValue;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.EvaluateDmnRequest;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.EvaluateDmnResponse;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.SendMessageRequest;
import uk.gov.hmcts.reform.waworkflowapi.clients.service.EvaluateDmnService;
import uk.gov.hmcts.reform.waworkflowapi.clients.service.SendMessageService;
import uk.gov.hmcts.reform.waworkflowapi.common.TaskToCreate;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTaskControllerTest {

    private static final String CREATE_TASK_MESSAGE = "createTaskMessage";

    @Mock
    private EvaluateDmnService evaluateDmnService;

    @Mock
    private SendMessageService sendMessageService;

    @Mock
    private SendMessageRequest sendMessageRequest;

    private CreateTaskController createTaskController;

    @BeforeEach
    void setUp() {
        createTaskController = new CreateTaskController(
            evaluateDmnService,
            sendMessageService
        );
    }

    @Test
    void evaluateDmn() {
        final String key = "Key";
        final String tenantId = "id";
        final DmnValue<?> dmnValue = mock(DmnValue.class);
        final List<Map<String, DmnValue<?>>> evaluateDmnResponse = new ArrayList<Map<String, DmnValue<?>>>();
        final Map<String, DmnValue<?>> map = new HashMap<String, DmnValue<?>>();
        map.put(key, dmnValue);
        evaluateDmnResponse.add(map);
        EvaluateDmnRequest evaluateDmnRequest = mock(EvaluateDmnRequest.class);

        when(evaluateDmnService.evaluateDmn(evaluateDmnRequest, key, tenantId)).thenReturn(evaluateDmnResponse);
        ResponseEntity<EvaluateDmnResponse> response = createTaskController.evaluateDmn(evaluateDmnRequest, key, tenantId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertThat(response.getBody(), instanceOf(EvaluateDmnResponse.class));
        assertEquals(evaluateDmnResponse, response.getBody().getResults());
    }

    @Test
    void sendMessage_with_create_task_message() {
        final ZonedDateTime zonedDateTime = ZonedDateTime.parse("2020-12-15T23:02:32.334031Z");
        final TaskToCreate taskToCreate = new TaskToCreate("some taskId",
                2, "test", zonedDateTime.toString());
        final DmnValue<String> dueDate =
            DmnValue.dmnStringValue(ZonedDateTime.parse("2020-12-15T23:02:32.334031Z").toString());
        final DmnValue<String> taskId = DmnValue.dmnStringValue("some taskId");
        final DmnValue<Integer> workingDaysAllowed = DmnValue.dmnIntegerValue(2);
        final DmnValue<String> name = DmnValue.dmnStringValue("test");

        Map<String, DmnValue<?>> map = new HashMap<>(Map.of("dueDate", dueDate,
                                                            "taskId", taskId,
                                                            "workingDaysAllowed", workingDaysAllowed,
                                                            "name", name
        ));


        ResponseEntity<Void> response = createTaskController.sendMessage(sendMessageRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

}
