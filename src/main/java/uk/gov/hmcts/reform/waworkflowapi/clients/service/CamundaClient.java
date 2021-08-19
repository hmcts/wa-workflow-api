package uk.gov.hmcts.reform.waworkflowapi.clients.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.CamundaSendMessageRequest;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.DmnValue;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.EvaluateDmnRequest;
import uk.gov.hmcts.reform.waworkflowapi.config.CamundaFeignConfiguration;

import java.util.List;
import java.util.Map;

@SuppressWarnings("PMD.UseObjectForClearerAPI")
@FeignClient(
    name = "camunda",
    url = "${camunda.url}",
    configuration = CamundaFeignConfiguration.class
)
public interface CamundaClient {

    String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    @PostMapping(
        value = "/message",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    void sendMessage(@RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorisation,
                     CamundaSendMessageRequest sendMessageRequest);

    @PostMapping(
        value = "/decision-definition/key/{key}/tenant-id/{tenant-id}/evaluate",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    List<Map<String, DmnValue<?>>> evaluateDmn(
        @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorisation,
        @PathVariable("key") String key,
        @PathVariable("tenant-id") String tenantId,
        EvaluateDmnRequest evaluateDmnRequest
    );

}

