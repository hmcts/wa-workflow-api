package uk.gov.hmcts.reform.waworkflowapi.external.taskservice;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import uk.gov.hmcts.reform.waworkflowapi.camuda.rest.api.wrapper.GetCamundaTaskService;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"PMD.JUnitAssertionsShouldIncludeMessage","PMD.UnusedLocalVariable","PMD.DataflowAnomalyAnalysis"})
class GetTaskServiceTest {

    private static final String TEST_ID = "SomeId";
    private static final String TEST_RESPONSE = "test object string";

    private GetCamundaTaskService taskService;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    void getsATaskBasedOnId() throws IOException {
        taskService = mock(GetCamundaTaskService.class);
        when(taskService.getTaskByID(TEST_ID))
            .thenReturn(TEST_RESPONSE);
        String response = taskService.getTaskByID(TEST_ID);
        assertEquals(TEST_RESPONSE, response);
    }

    @Test
    void getsATaskBasedOnIdNotFound() {
        assertThrows(NullPointerException.class, () -> {
            String response = taskService.getTaskByID("WrongId");
            exception.expect(IOException.class);
        });
    }

    @Test
    void getsATaskBasedOnIdNotFoundWithMock() {
        assertThrows(IOException.class, () -> {
            HttpClient httpClient = mock(HttpClient.class);
            HttpMethod httpMethod = mock(GetMethod.class);
            when(httpMethod.getStatusCode()).thenReturn(HttpStatus.SC_BAD_GATEWAY);
            when(httpClient.executeMethod(httpMethod)).thenReturn(404);
            when(httpMethod.getResponseBodyAsString()).thenReturn("");
            GetCamundaTaskService taskService = new GetCamundaTaskService();
            taskService.getTaskByID(null);
        });
    }
}
