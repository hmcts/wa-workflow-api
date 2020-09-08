package uk.gov.hmcts.reform.waworkflowapi.external.taskservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.waworkflowapi.camuda.rest.api.wrapper.CamundaTaskService;
import uk.gov.hmcts.reform.waworkflowapi.controllers.startworkflow.GetTaskController;
import uk.gov.hmcts.reform.waworkflowapi.models.Task;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
@RunWith(MockitoJUnitRunner.class)
class GetTaskServiceTest {

    @InjectMocks
    private GetTaskController taskController;

    @MockBean
    CamundaTaskService camundaTaskService;

    @BeforeEach
    void setUp() {
        camundaTaskService = mock(CamundaTaskService.class);
        taskController = new GetTaskController(camundaTaskService);
    }

    @Test
    void createsATask() {
        when(taskController.getTask("TestId")).thenReturn(new Task());
        Task task = taskController.getTask("TestId");
        when(camundaTaskService.getTask("TestId")).thenReturn(new Task());
        assertEquals(task.getClass(), Task.class);
    }
}
