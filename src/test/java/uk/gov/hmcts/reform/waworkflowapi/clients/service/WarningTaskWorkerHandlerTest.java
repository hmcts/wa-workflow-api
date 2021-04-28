package uk.gov.hmcts.reform.waworkflowapi.clients.service;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class WarningTaskWorkerHandlerTest {

    @Mock
    private ExternalTask externalTask;
    @Mock
    private ExternalTaskService externalTaskService;
    private final WarningTaskWorkerHandler warningTaskWorkerHandler = new WarningTaskWorkerHandler();

    @Test
    void setTaskWarningFlag() {
        warningTaskWorkerHandler.setTaskWarningFlag(externalTask, externalTaskService);

        verify(externalTaskService).complete(externalTask, null, singletonMap(
            "hasWarnings",
            true
        ));
    }

}
