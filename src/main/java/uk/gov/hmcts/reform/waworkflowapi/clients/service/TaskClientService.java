package uk.gov.hmcts.reform.waworkflowapi.clients.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.DmnValue;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.EvaluateDmnRequest;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.SendMessageRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
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
        camundaClient.sendMessage(
            authTokenGenerator.generate(),
            sendMessageRequest
        );
    }

    public List<Map<String, DmnValue<?>>> evaluate(EvaluateDmnRequest evaluateDmnRequest, String key, String tenantId) {
        log.info("RWA-2367 evaluate dmn key:{} tenantId:{} evaluateDmnRequest:{}", key, tenantId, evaluateDmnRequest);
        List<Map<String, DmnValue<?>>> dmnResponse = camundaClient.evaluateDmn(
            authTokenGenerator.generate(),
            key,
            tenantId,
            evaluateDmnRequest
        );

        log.info("RWA-2367 dmnResponse fetched");
        return dmnResponse.stream().map(this::removeSpaces).collect(Collectors.toList());
    }

    private Map<String, DmnValue<?>> removeSpaces(Map<String, DmnValue<?>> dmnResponse) {

        HashMap<String, DmnValue<?>> response = new HashMap<>(dmnResponse);

        for (Map.Entry<String, DmnValue<?>> entry : response.entrySet()) {
            String value = entry.getValue().getValue().toString();
            if (value.contains(",") && value.contains(" ")) {
                String[] valueArray = ((String) entry.getValue().getValue()).split(",");

                List<String> trimmedValues = Arrays.stream(valueArray)
                    .map(String::trim)
                    .collect(Collectors.toList());

                response.put(
                    entry.getKey(),
                    DmnValue.dmnStringValue(
                        String.join(",", trimmedValues)
                    )
                );
            }

        }
        return response;
    }

}
