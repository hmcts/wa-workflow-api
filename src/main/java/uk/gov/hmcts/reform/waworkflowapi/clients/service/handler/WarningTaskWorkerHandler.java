package uk.gov.hmcts.reform.waworkflowapi.clients.service.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.waworkflowapi.clients.TaskManagementServiceApi;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.AddProcessVariableRequest;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.CamundaProcess;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.CamundaProcessVariables;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.CamundaValue;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.Warning;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.WarningValues;
import uk.gov.hmcts.reform.waworkflowapi.clients.service.CamundaClient;
import uk.gov.hmcts.reform.waworkflowapi.config.LaunchDarklyFeatureFlagProvider;
import uk.gov.hmcts.reform.waworkflowapi.domain.taskconfiguration.request.NoteResource;
import uk.gov.hmcts.reform.waworkflowapi.domain.taskconfiguration.request.NotesRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static uk.gov.hmcts.reform.waworkflowapi.config.features.FeatureFlag.RELEASE_2_CFT_TASK_WARNING;

@Slf4j
@Component
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class WarningTaskWorkerHandler {

    final TaskManagementServiceApi taskManagementServiceApi;
    final AuthTokenGenerator authTokenGenerator;
    final LaunchDarklyFeatureFlagProvider launchDarklyFeatureFlagProvider;
    private final CamundaClient camundaClient;

    public WarningTaskWorkerHandler(TaskManagementServiceApi taskManagementServiceApi,
                                    AuthTokenGenerator authTokenGenerator,
                                    LaunchDarklyFeatureFlagProvider launchDarklyFeatureFlagProvider,
                                    CamundaClient camundaClient) {
        this.taskManagementServiceApi = taskManagementServiceApi;
        this.authTokenGenerator = authTokenGenerator;
        this.launchDarklyFeatureFlagProvider = launchDarklyFeatureFlagProvider;
        this.camundaClient = camundaClient;
    }

    public void completeWarningTaskService(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Map<?, ?> variables = externalTask.getAllVariables();
        String caseId = (String) variables.get("caseId");
        log.info("Set processVariables for same processInstance ids with caseId {}", caseId);

        String updatedWarningValues = "[]";

        try {
            updatedWarningValues = mapWarningValues(variables);
        } catch (JsonProcessingException exp) {
            log.error("Exception occurred while parsing json: {}", exp.getMessage(), exp);
        }

        externalTaskService.complete(externalTask, Map.of(
            "hasWarnings",
            true,
            "warningList",
            updatedWarningValues
        ));

        boolean isCftTaskWarningEnabled = launchDarklyFeatureFlagProvider.getBooleanValue(RELEASE_2_CFT_TASK_WARNING);
        if (isCftTaskWarningEnabled) {
            //Also update the warning in CFT Task DB
            addWarningInCftTaskDb(externalTask.getId());
            addWarningToDelayedProcesses(caseId, updatedWarningValues);
        }

    }

    private void addWarningToDelayedProcesses(String caseId, String updatedWarningValues) {
        List<CamundaProcess> processes = getProcesses(caseId);
        processes.forEach(process -> updateProcessWarnings(process, updatedWarningValues));
    }

    private void updateProcessWarnings(CamundaProcess process, String warningToAdd) {
        String serviceToken = authTokenGenerator.generate();
        CamundaProcessVariables processVariables = camundaClient.getProcessInstanceVariables(
            serviceToken,
            process.getId()
        );

        String warning = (String) processVariables.getProcessVariablesMap().get("warningList").getValue();

        try {
            WarningValues values = mapWarningAttributes(warningToAdd, new WarningValues(warning));
            Map<String, WarningValues> warningValues = Map.of("WarningValues", values);
            warning = new ObjectMapper().writeValueAsString(warningValues);

        } catch (JsonProcessingException exp) {
            log.error("Exception occurred while parsing json: {}", exp.getMessage(), exp);
        }

        Map<String, CamundaValue<String>> warningList = Map.of("warningList", CamundaValue.stringValue(warning));
        AddProcessVariableRequest modificationRequest = new AddProcessVariableRequest(warningList);

        camundaClient.updateProcessVariables(
            serviceToken,
            process.getId(),
            modificationRequest
        );
    }

    private void addWarningInCftTaskDb(String taskId) {
        NotesRequest notesRequest = new NotesRequest(
            singletonList(
                new NoteResource(null, "WARNING", null, null)
            )
        );

        taskManagementServiceApi.addTaskNote(authTokenGenerator.generate(), taskId, notesRequest);
    }

    private String mapWarningValues(Map<?, ?> variables) throws JsonProcessingException {
        final String warningStr = (String) variables.get("warningList");
        WarningValues processVariableWarningValues;
        if (warningStr == null) {
            processVariableWarningValues = new WarningValues("[]");
        } else {
            processVariableWarningValues = new WarningValues(warningStr);
        }

        WarningValues combinedWarningValues = mapWarningAttributes((String) variables.get("warningsToAdd"),
                                                                   processVariableWarningValues);

        String caseId = (String) variables.get("caseId");
        log.info("caseId {} and its warning values : {}", caseId, combinedWarningValues.getValuesAsJson());

        return combinedWarningValues.getValuesAsJson();
    }

    private WarningValues mapWarningAttributes( String warningsToAddAsJson,
                                                WarningValues processVariableWarningTextValues) {

        if (!StringUtils.isEmpty(warningsToAddAsJson)) {
            final WarningValues warningValues = new WarningValues(warningsToAddAsJson);
            final List<Warning> warningsToBeAdded = warningValues.getValues();

            final List<Warning> processVariableWarnings = processVariableWarningTextValues.getValues();

            // without duplicate warning attributes
            final List<Warning> warningTextValues = Stream.concat(warningsToBeAdded.stream(), processVariableWarnings.stream())
                .distinct().collect(Collectors.toList());
            return new WarningValues(warningTextValues);
        }
        return processVariableWarningTextValues;
    }

    private List<CamundaProcess> getProcesses(String caseId) {
        String serviceToken = authTokenGenerator.generate();
        List<CamundaProcess> camundaProcesses = camundaClient.getProcessInstancesByVariables(
            serviceToken,
            "caseId_eq_" + caseId
        );

        return camundaProcesses.stream()
            .filter(camundaProcess -> !camundaClient.getJobs(serviceToken, camundaProcess.getId()).isEmpty())
            .collect(Collectors.toList());
    }

}

