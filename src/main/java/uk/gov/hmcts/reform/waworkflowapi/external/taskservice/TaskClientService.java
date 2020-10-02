package uk.gov.hmcts.reform.waworkflowapi.external.taskservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.waworkflowapi.controllers.startworkflow.ServiceDetails;
import uk.gov.hmcts.reform.waworkflowapi.controllers.startworkflow.Transition;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.reform.waworkflowapi.external.taskservice.DmnValue.dmnStringValue;
import static uk.gov.hmcts.reform.waworkflowapi.external.taskservice.Task.taskForId;

@Component
public class TaskClientService {
    private final CamundaClient camundaClient;

    @Autowired
    public TaskClientService(
        @Autowired CamundaClient camundaClient
    ) {
        this.camundaClient = camundaClient;
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public Optional<TaskToCreate> getTask(ServiceDetails serviceDetails, Transition transition) {
        DmnRequest<GetTaskDmnRequest> requestParameters = new DmnRequest<>(new GetTaskDmnRequest(
            dmnStringValue(transition.getEventId()),
            dmnStringValue(transition.getPostState())
        ));

        List<GetTaskDmnResult> dmnResults = camundaClient.getTask(
            serviceDetails.getJurisdiction(),
            serviceDetails.getCaseType(),
            requestParameters
        );

        if (dmnResults.isEmpty()) {
            return Optional.empty();
        } else if (dmnResults.size() == 1) {
            Task task = taskForId(dmnResults.get(0).getTaskId().getValue());
            String group = dmnResults.get(0).getGroup().getValue();
            DmnValue<Integer> workingDaysAllowed = dmnResults.get(0).getWorkingDaysAllowed();
            String name = dmnResults.get(0).getName().getValue();
            return Optional.of((workingDaysAllowed == null)
                                   ? new TaskToCreate(task, group, name)
                                   : new TaskToCreate(task, group, workingDaysAllowed.getValue(), name)
            );
        }
        throw new IllegalStateException("Should have exactly one task for transition");
    }

    public void createTask(ServiceDetails serviceDetails, String ccdId, TaskToCreate taskToCreate, ZonedDateTime dueDate) {
        ProcessVariables processVariables = new ProcessVariables(
            serviceDetails.getJurisdiction(),
            serviceDetails.getCaseType(),
            ccdId,
            taskToCreate.getTask(),
            taskToCreate.getGroup(),
            dueDate,
            taskToCreate.getName()
        );
        camundaClient.sendMessage(new SendMessageRequest("createTaskMessage", processVariables));
    }
}
