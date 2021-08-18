package uk.gov.hmcts.reform.waworkflowapi.clients.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.DmnValue;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.EvaluateDmnRequest;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.SendMessageRequest;

import java.util.List;
import java.util.Map;

@Component
public class TaskClientService {
    private final CamundaClient camundaClient;
    private final AuthTokenGenerator authTokenGenerator;
    private final ObjectMapper objectMapper;

    @Autowired
    public TaskClientService(@Autowired CamundaClient camundaClient,
                             AuthTokenGenerator authTokenGenerator,
                             @Qualifier("camelCaseObjectMapper") ObjectMapper objectMapper) {
        this.camundaClient = camundaClient;
        this.authTokenGenerator = authTokenGenerator;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(SendMessageRequest sendMessageRequest) {

        try {
            String sendMessageRequestJson = objectMapper.writeValueAsString(sendMessageRequest);
            camundaClient.sendMessage(
                authTokenGenerator.generate(),
                sendMessageRequestJson
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    public List<Map<String, DmnValue<?>>> evaluate(EvaluateDmnRequest evaluateDmnRequest, String key, String tenantId) {
        return camundaClient.evaluateDmn(
            authTokenGenerator.generate(),
            key,
            tenantId,
            evaluateDmnRequest
        );
    }


}
