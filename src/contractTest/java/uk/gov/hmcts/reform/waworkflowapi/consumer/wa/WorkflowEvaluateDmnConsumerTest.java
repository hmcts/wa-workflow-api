package uk.gov.hmcts.reform.waworkflowapi.consumer.wa;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.waworkflowapi.SpringBootContractBaseTest;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.EvaluateDmnRequest;

import java.util.Map;

import static uk.gov.hmcts.reform.waworkflowapi.clients.model.DmnValue.dmnStringValue;

@PactTestFor(providerName = "wa_workflow_api_evaluate_dmn", port = "8899")
public class WorkflowEvaluateDmnConsumerTest extends SpringBootContractBaseTest {

    private static final String WA_EVALUATE_DMN_URL = "/workflow/decision-definition/key/someKey/tenant-id/someTenant/evaluate";

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @PactTestFor(pactMethod = "evaluateDmn204")
    void evaluateDmn204Test(MockServer mockServer)  {
        SerenityRest
            .given()
            .headers(getHttpHeaders())
            .contentType(ContentType.JSON)
            .body(createMessage())
            .post(mockServer.getUrl() + WA_EVALUATE_DMN_URL)
            .then()
            .statusCode(204);
    }

    @Pact(provider = "wa_workflow_api_evaluate_dmn", consumer = "wa_workflow_api")
    public RequestResponsePact evaluateDmn204(PactDslWithProvider builder) {

        return builder
            .given("evaluate dmn")
            .uponReceiving("response to return")
            .path(WA_EVALUATE_DMN_URL)
            .method(HttpMethod.POST.toString())
            .body(createMessage(), String.valueOf(ContentType.JSON))
            .matchHeader(SERVICE_AUTHORIZATION, SERVICE_AUTH_TOKEN)
            .willRespondWith()
            .status(HttpStatus.NO_CONTENT.value())
            .toPact();
    }


    private String createMessage() {
        EvaluateDmnRequest evaluateDmnRequest = new EvaluateDmnRequest(
            Map.of(
                "name", dmnStringValue("some name"),
                "jurisdiction", dmnStringValue("WA"),
                "caseType", dmnStringValue("WaCaseType"),
                "taskId", dmnStringValue("some taskId"),
                "caseId", dmnStringValue("some caseId")
            )
        );
        try {
            return objectMapper.writeValueAsString(evaluateDmnRequest);
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
        }
        return "";
    }
}

