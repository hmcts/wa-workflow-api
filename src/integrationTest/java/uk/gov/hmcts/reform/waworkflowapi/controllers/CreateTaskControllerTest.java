package uk.gov.hmcts.reform.waworkflowapi.controllers;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.waworkflowapi.SpringBootIntegrationBaseTest;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.DmnValue;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.EvaluateDmnRequest;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.SendMessageRequest;
import uk.gov.hmcts.reform.waworkflowapi.clients.service.CamundaClient;
import uk.gov.hmcts.reform.waworkflowapi.exceptions.BadRequestException;
import uk.gov.hmcts.reform.waworkflowapi.exceptions.ResourceNotFoundException;
import uk.gov.hmcts.reform.waworkflowapi.exceptions.UnAuthorizedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.waworkflowapi.api.CreatorObjectMapper.asJsonString;
import static uk.gov.hmcts.reform.waworkflowapi.clients.model.DmnValue.dmnIntegerValue;
import static uk.gov.hmcts.reform.waworkflowapi.clients.model.DmnValue.dmnStringValue;

@Slf4j
class CreateTaskControllerTest extends SpringBootIntegrationBaseTest {

    public static final String WORKFLOW_MESSAGE_ENDPOINT = "/workflow/message";
    private static final String BEARER_SERVICE_TOKEN = "Bearer service token";

    @Autowired
    private transient MockMvc mockMvc;
    @MockBean
    private AuthTokenGenerator authTokenGenerator;
    @MockBean
    private CamundaClient camundaClient;

    @Captor
    private ArgumentCaptor<SendMessageRequest> captor;


    @BeforeEach
    void setUp() {
        when(authTokenGenerator.generate()).thenReturn(BEARER_SERVICE_TOKEN);
    }

    @DisplayName("Should evaluate a DMN and return a 200")
    @Test
    void evaluateDmn() throws Exception {

        EvaluateDmnRequest evaluateDmnRequest = new EvaluateDmnRequest(
            Map.of("name", dmnStringValue("Process Application"),
                "workingDaysAllowed", dmnIntegerValue(2),
                "taskId", dmnStringValue("processApplication")
            ));

        when(camundaClient.evaluateDmn(
            eq(BEARER_SERVICE_TOKEN),
            anyString(),
            anyString(),
            eq(evaluateDmnRequest)
        )).thenReturn(getEvalResponse());

        mockMvc.perform(
            post("/workflow/decision-definition/key/getTask_IA_asylum/tenant-id/ia/evaluate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(evaluateDmnRequest))
        ).andExpect(status().isOk()).andReturn();

    }

    @DisplayName("Should evaluate a DMN and return a Camunda service unavailable application problem json error response")
    @Test
    void evaluateDmnAndReturnCamundaServiceUnavailableFailures() throws Exception {

        EvaluateDmnRequest evaluateDmnRequest = new EvaluateDmnRequest(
            Map.of("name", dmnStringValue("Process Application"),
                "workingDaysAllowed", dmnIntegerValue(2),
                "taskId", dmnStringValue("processApplication")
            ));

        Request request = Request.create(Request.HttpMethod.GET, "url",
            new HashMap<>(), null, new RequestTemplate());

        when(camundaClient.evaluateDmn(
            eq(BEARER_SERVICE_TOKEN),
            anyString(),
            anyString(),
            eq(evaluateDmnRequest)
        )).thenThrow(new FeignException.ServiceUnavailable(
            "Service unavailable",
            request,
            null,
            null));

        mockMvc.perform(
            post("/workflow/decision-definition/key/getTask_IA_asylum/tenant-id/ia/evaluate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(evaluateDmnRequest))
        ).andExpect(status().isServiceUnavailable())
            .andExpect(content().contentType("application/problem+json"))
            .andReturn();
    }

    @DisplayName("Should evaluate a DMN and return Camunda Bad Gateway application problem json error response")
    @Test
    void evaluateDmnAndReturnCamundaBadGatewayFailures() throws Exception {

        EvaluateDmnRequest evaluateDmnRequest = new EvaluateDmnRequest(
            Map.of("name", dmnStringValue("Process Application"),
                "workingDaysAllowed", dmnIntegerValue(2),
                "taskId", dmnStringValue("processApplication")
            ));

        Request request = Request.create(Request.HttpMethod.GET, "url",
            new HashMap<>(), null, new RequestTemplate());

        when(camundaClient.evaluateDmn(
            eq(BEARER_SERVICE_TOKEN),
            anyString(),
            anyString(),
            eq(evaluateDmnRequest)
        )).thenThrow(new FeignException.BadGateway(
            "Bad Gateway",
            request,
            null,
            null));

        mockMvc.perform(
                post("/workflow/decision-definition/key/getTask_IA_asylum/tenant-id/ia/evaluate")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(asJsonString(evaluateDmnRequest))
            ).andExpect(status().isBadGateway())
            .andExpect(content().contentType("application/problem+json"))
            .andReturn();
    }

    @DisplayName("Should evaluate a DMN and return work-flow API bad request error response")
    @Test
    void evaluateDmnWorkflowAPIBadRequestException() throws Exception {

        EvaluateDmnRequest evaluateDmnRequest = new EvaluateDmnRequest(
            Map.of("name", dmnStringValue("Process Application"),
                "workingDaysAllowed", dmnIntegerValue(2),
                "taskId", dmnStringValue("processApplication")
            ));

        Request request = Request.create(Request.HttpMethod.GET, "url",
            new HashMap<>(), null, new RequestTemplate());

        when(camundaClient.evaluateDmn(
            eq(BEARER_SERVICE_TOKEN),
            anyString(),
            anyString(),
            eq(evaluateDmnRequest)
        )).thenThrow(new BadRequestException("Bad Request"));

        mockMvc.perform(
                post("/workflow/decision-definition/key/getTask_IA_asylum/tenant-id/ia/evaluate")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(asJsonString(evaluateDmnRequest))
            ).andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"))
            .andReturn();
    }

    @DisplayName("Should evaluate a DMN and return work-flow API resource not found error response")
    @Test
    void evaluateDmnAndReturnWorkflowAPIResourceNotFoundException() throws Exception {

        EvaluateDmnRequest evaluateDmnRequest = new EvaluateDmnRequest(
            Map.of("name", dmnStringValue("Process Application"),
                "workingDaysAllowed", dmnIntegerValue(2),
                "taskId", dmnStringValue("processApplication")
            ));

        Request request = Request.create(Request.HttpMethod.GET, "invalidurl",
            new HashMap<>(), null, new RequestTemplate());

        when(camundaClient.evaluateDmn(
            eq(BEARER_SERVICE_TOKEN),
            anyString(),
            anyString(),
            eq(evaluateDmnRequest)
        )).thenThrow(new ResourceNotFoundException("Resource Not Found"));

        mockMvc.perform(
                post("/workflow/decision-definition/key/getTask_IA_asylum/tenant-id/ia/evaluate")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(asJsonString(evaluateDmnRequest))
            ).andExpect(status().isNotFound())
            .andExpect(content().contentType("application/json"))
            .andReturn();
    }

    @DisplayName("Should evaluate a DMN and return work-flow API UnAuthorized error response")
    @Test
    void evaluateDmnAndReturnWorkflowAPIUnAuthorisedException() throws Exception {

        EvaluateDmnRequest evaluateDmnRequest = new EvaluateDmnRequest(
            Map.of("name", dmnStringValue("Process Application"),
                "workingDaysAllowed", dmnIntegerValue(2),
                "taskId", dmnStringValue("processApplication")
            ));

        Request request = Request.create(Request.HttpMethod.GET, "Invalid Authrozation",
            new HashMap<>(), null, new RequestTemplate());

        when(camundaClient.evaluateDmn(
            eq(BEARER_SERVICE_TOKEN),
            anyString(),
            anyString(),
            eq(evaluateDmnRequest)
        )).thenThrow(new UnAuthorizedException("Not Authorized"));

        mockMvc.perform(
                post("/workflow/decision-definition/key/getTask_IA_asylum/tenant-id/ia/evaluate")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(asJsonString(evaluateDmnRequest))
            ).andExpect(status().isUnauthorized())
            .andExpect(content().contentType("application/json"))
            .andReturn();
    }

    @ParameterizedTest
    @MethodSource("scenarioProvider")
    void sendMessage(Scenario scenario) throws Exception {
        mockMvc.perform(
            post(WORKFLOW_MESSAGE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(scenario.sendMessageRequest))
        ).andExpect(status().isNoContent()).andReturn();

        verify(camundaClient).sendMessage(
            eq(BEARER_SERVICE_TOKEN),
            captor.capture()
        );
        SendMessageRequest actualSendMessageRequest = captor.getValue();

        assertThat(actualSendMessageRequest).isEqualTo(scenario.expectedSendMessageRequest);
    }

    private static Stream<Scenario> scenarioProvider() {

        /*
         Scenario1: When messageName is other than createTaskMessage
                    Then message is sent to Camunda with no dueDate
         */

        SendMessageRequest sendMessageRequest1 = new SendMessageRequest(
            "some other message",
            Map.of(
                "name", dmnStringValue("some name"),
                "jurisdiction", dmnStringValue("ia"),
                "caseType", dmnStringValue("asylum"),
                "taskId", dmnStringValue("some taskId"),
                "caseId", dmnStringValue("some caseId")
            ),
            null,
            false
        );

        SendMessageRequest expectedSendMessageRequest1 = new SendMessageRequest(
            "some other message",
            Map.of(
                "name", dmnStringValue("some name"),
                "jurisdiction", dmnStringValue("ia"),
                "caseType", dmnStringValue("asylum"),
                "taskId", dmnStringValue("some taskId"),
                "caseId", dmnStringValue("some caseId")
            ),
            null,
            false
        );

        Scenario messageIsOtherThanCreateTaskThenDueTaskIsNotSet = Scenario.builder()
            .sendMessageRequest(sendMessageRequest1)
            .expectedSendMessageRequest(expectedSendMessageRequest1)
            .build();

        return Stream.of(
            messageIsOtherThanCreateTaskThenDueTaskIsNotSet
        );
    }

    @Builder
    private static class Scenario {
        private final SendMessageRequest sendMessageRequest;
        private final SendMessageRequest expectedSendMessageRequest;
    }

    private List<Map<String, DmnValue<?>>> getEvalResponse() {
        return List.of(Map.of(
            "name", dmnStringValue("processApplication"),
            "group", dmnStringValue("TCW"),
            "workingDaysAllowed", dmnIntegerValue(5),
            "taskId", dmnStringValue("task name")
            )
        );
    }

}
