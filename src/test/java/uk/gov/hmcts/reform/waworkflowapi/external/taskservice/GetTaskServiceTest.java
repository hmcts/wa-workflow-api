package uk.gov.hmcts.reform.waworkflowapi.external.taskservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import uk.gov.hmcts.reform.waworkflowapi.camudaRestapiWrapper.GetCamundaTaskService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
class GetTaskServiceTest {

    @Mock
    private GetCamundaTaskService taskService;


    @BeforeEach
    void setUp() {
        taskService = mock(GetCamundaTaskService.class);
        Mockito.when(taskService.getTaskByID("SomeId")).thenReturn("test object string");

    }

    @Test
    void getsATaskBasedOnId() {
    String response = taskService.getTaskByID("SomeId");
    assertEquals(response,"test object string");
    }
}
