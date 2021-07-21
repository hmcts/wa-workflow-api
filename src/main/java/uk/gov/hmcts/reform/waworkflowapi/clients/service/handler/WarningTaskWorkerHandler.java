package uk.gov.hmcts.reform.waworkflowapi.clients.service.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.Warning;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.WarningValues;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class WarningTaskWorkerHandler {

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

    }

    private String mapWarningValues(Map<?, ?> variables) throws JsonProcessingException {
        final String warningStr = (String) variables.get("warningList");
        WarningValues processVariableWarningValues;
        if (warningStr == null) {
            processVariableWarningValues = new WarningValues("[]");
        } else {
            processVariableWarningValues = new WarningValues(warningStr);
        }

        final WarningValues handlerWarningValues = mapWarningAttributesFromWarnings(variables);
        final List<Warning> handlerWarnings = handlerWarningValues.getValues();

        final List<Warning> processVariableWarnings = processVariableWarningValues.getValues();

        // without duplicate warning attributes
        final List<Warning> distinctWarnings = Stream.concat(handlerWarnings.stream(), processVariableWarnings.stream())
            .distinct().collect(Collectors.toList());

        WarningValues aggregatedWarnings = new WarningValues(distinctWarnings);

        String caseId = (String) variables.get("caseId");
        log.info("caseId {} and its warning values : {}", caseId, aggregatedWarnings.getValuesAsJson());

        return aggregatedWarnings.getValuesAsJson();
    }

    private WarningValues mapWarningAttributesFromWarnings(Map<?, ?> variables) {
        final String warningsAsJson = (String) variables.get("warnings");

        if (!StringUtils.isEmpty(warningsAsJson)) {
            return new WarningValues(warningsAsJson);
        }
        return new WarningValues();
    }

}

