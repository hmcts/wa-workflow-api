package uk.gov.hmcts.reform.waworkflowapi.external.taskservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.gov.hmcts.reform.waworkflowapi.controllers.startworkflow.ServiceDetails;
import uk.gov.hmcts.reform.waworkflowapi.controllers.startworkflow.Transition;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.waworkflowapi.external.taskservice.DmnValue.dmnIntegerValue;
import static uk.gov.hmcts.reform.waworkflowapi.external.taskservice.DmnValue.dmnStringValue;
import static uk.gov.hmcts.reform.waworkflowapi.external.taskservice.Task.PROCESS_APPLICATION;
import static uk.gov.hmcts.reform.waworkflowapi.external.taskservice.Task.taskForId;

class TaskClientServiceTest {

    private CamundaClient camundaClient;
    private TaskClientService underTest;
    private String expectedTask;
    private Transition transition;
    private DmnRequest<GetTaskDmnRequest> dmnRequest;
    private static final String GROUP = "TCW";
    private ServiceDetails serviceDetails;

    @BeforeEach
    void setUp() {
        camundaClient = mock(CamundaClient.class);
        underTest = new TaskClientService(camundaClient);
        expectedTask = PROCESS_APPLICATION.getId();
        transition = new Transition("startState", "eventName", "endState");
        dmnRequest = new DmnRequest<>(new GetTaskDmnRequest(
            dmnStringValue(transition.getEventId()),
            dmnStringValue(transition.getPostState())
        ));
        serviceDetails = new ServiceDetails("jurisdiction", "caseType");
    }

    @Test
    void getsATaskWithWorkingDaysBasedOnTransition() {
        int workingDaysAllowed = 5;
        List<GetTaskDmnResult> ts = singletonList(new GetTaskDmnResult(
            dmnStringValue(expectedTask),
            dmnStringValue(GROUP),
            dmnIntegerValue(workingDaysAllowed)
        ));
        when(camundaClient.getTask(serviceDetails.getJurisdiction(), serviceDetails.getCaseType(), dmnRequest))
            .thenReturn(ts);

        Optional<TaskToCreate> task = underTest.getTask(serviceDetails, transition);

        assertThat(task, is(Optional.of(new TaskToCreate(taskForId(expectedTask), GROUP, workingDaysAllowed))));
    }

    @Test
    void getsATaskWithoutWorkingDaysBasedOnTransition() {
        List<GetTaskDmnResult> ts = singletonList(new GetTaskDmnResult(
            dmnStringValue(expectedTask),
            dmnStringValue(GROUP),
            null
        ));
        when(camundaClient.getTask(serviceDetails.getJurisdiction(), serviceDetails.getCaseType(), dmnRequest))
            .thenReturn(ts);

        Optional<TaskToCreate> task = underTest.getTask(serviceDetails, transition);

        assertThat(task, is(Optional.of(new TaskToCreate(taskForId(expectedTask), GROUP))));
    }

    @Test
    void noTasksForTransition() {
        List<GetTaskDmnResult> ts = emptyList();
        when(camundaClient.getTask(serviceDetails.getJurisdiction(), serviceDetails.getCaseType(), dmnRequest))
            .thenReturn(ts);

        Optional<TaskToCreate> task = underTest.getTask(serviceDetails, transition);

        assertThat(task, is(Optional.empty()));
    }

    @Test
    void getsMultipleTasksBasedOnTransitionWhichIsInvalid() {
        GetTaskDmnResult dmnResult = new GetTaskDmnResult(dmnStringValue(expectedTask), dmnStringValue("TCW"), dmnIntegerValue(5));
        List<GetTaskDmnResult> ts = asList(dmnResult, dmnResult);
        when(camundaClient.getTask(serviceDetails.getJurisdiction(), serviceDetails.getCaseType(), dmnRequest))
            .thenReturn(ts);

        assertThrows(IllegalStateException.class, () -> {
            underTest.getTask(serviceDetails, transition);
        });
    }

    @Test
    void createsATask() {
        String ccdId = "ccd_id";
        String group = "TCW";
        ZonedDateTime dueDate = ZonedDateTime.now().plusDays(2);
        underTest.createTask(ccdId, new TaskToCreate(PROCESS_APPLICATION, group), dueDate);

        Mockito.verify(camundaClient).sendMessage(
            new SendMessageRequest(
                "createTaskMessage",
                new ProcessVariables(ccdId, PROCESS_APPLICATION, group, dueDate)
            )
        );
    }
}
