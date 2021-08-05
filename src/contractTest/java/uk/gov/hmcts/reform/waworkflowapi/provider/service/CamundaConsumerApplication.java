package uk.gov.hmcts.reform.waworkflowapi.provider.service;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.waworkflowapi.clients.service.CamundaClient;

@EnableFeignClients(clients = {
    CamundaClient.class
})
public class CamundaConsumerApplication {
    @MockBean
    RestTemplate restTemplate;
}

