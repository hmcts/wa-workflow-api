package uk.gov.hmcts.reform.waworkflowapi.external.taskservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@FeignClient(
    name = "camunda",
    url = "${camunda.url}"
)
public interface CamundaClient {

    String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    @PostMapping(value = "/message", produces = MediaType.APPLICATION_JSON_VALUE)
    void sendMessage(@RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorisation,
                     SendMessageRequest sendMessageRequest);

    @PostMapping(value = "/decision-definition/key/getTask_{jurisdiction}_{caseType}/evaluate", produces = MediaType.APPLICATION_JSON_VALUE)
    List<Map<String,Object>> evaluateDmn(
        @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorisation,
        @PathVariable("jurisdiction") String jurisdiction,
        @PathVariable("caseType") String caseType,
        EvaluateDmnRequest evaluateDmnRequest
    );

}

