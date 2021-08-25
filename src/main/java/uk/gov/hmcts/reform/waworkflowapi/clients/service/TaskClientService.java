package uk.gov.hmcts.reform.waworkflowapi.clients.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.CamundaSendMessageRequest;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.DmnValue;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.EvaluateDmnRequest;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.SendMessageRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Component
public class TaskClientService {
    private final CamundaClient camundaClient;
    private final AuthTokenGenerator authTokenGenerator;

    @Autowired
    public TaskClientService(@Autowired CamundaClient camundaClient,
                             AuthTokenGenerator authTokenGenerator) {
        this.camundaClient = camundaClient;
        this.authTokenGenerator = authTokenGenerator;
    }

    public void sendMessage(SendMessageRequest sendMessageRequest) {
        CamundaSendMessageRequest request = CamundaSendMessageRequest.builder()
            .messageName(sendMessageRequest.getMessageName())
            .all(sendMessageRequest.isAll())
            .correlationKeys(convertVariablesToCamelCase(sendMessageRequest.getCorrelationKeys()))
            .processVariables(convertVariablesToCamelCase(sendMessageRequest.getProcessVariables()))
            .build();

        camundaClient.sendMessage(
            authTokenGenerator.generate(),
            request
        );
    }

    public List<Map<String, DmnValue<?>>> evaluate(EvaluateDmnRequest evaluateDmnRequest, String key, String tenantId) {
        EvaluateDmnRequest evaluateDmnRequest1 = EvaluateDmnRequest.builder()
            .variables(convertVariablesToCamelCase(evaluateDmnRequest.getVariables()))
            .build();

        return camundaClient.evaluateDmn(
            authTokenGenerator.generate(),
            key,
            tenantId,
            evaluateDmnRequest1
        );
    }

    private Map<String, DmnValue<?>> convertVariablesToCamelCase(Map<String, DmnValue<?>> variables) {
        return Objects.nonNull(variables)
            ? variables.entrySet()
                .stream()
                .collect(
                    Collectors.toMap(
                        e -> org.apache.commons.text.CaseUtils.toCamelCase(e.getKey(), false, new char[]{'_'}),
                        e -> e.getValue())
                )
            : null;
    }


}
