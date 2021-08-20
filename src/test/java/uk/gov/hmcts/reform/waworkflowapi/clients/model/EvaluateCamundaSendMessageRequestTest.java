package uk.gov.hmcts.reform.waworkflowapi.clients.model;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.junit4.SpringRunner;
import pl.pojo.tester.api.assertion.Method;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;
import static uk.gov.hmcts.reform.waworkflowapi.clients.model.DmnValue.dmnStringValue;

@RunWith(SpringRunner.class)
@JsonTest
class EvaluateCamundaSendMessageRequestTest {

    @Autowired
    private JacksonTester<CamundaSendMessageRequest> jacksonTester;

    @Test
    void isWellImplemented() {

        final Class<?> classUnderTest = CamundaSendMessageRequest.class;

        assertPojoMethodsFor(classUnderTest)
            .testing(Method.GETTER)
            .testing(Method.CONSTRUCTOR)
            .testing(Method.EQUALS)
            .testing(Method.HASH_CODE)
            .testing(Method.TO_STRING)
            .areWellImplemented();

    }

    @Test
    void serialize_as_expected() throws IOException {

        JsonContent<CamundaSendMessageRequest> evaluateCamundaSendMessageRequestAsJson =
            jacksonTester.write(buildSendMessageRequest("2021-08-12T15:52:35.00000"));

        assertThat(evaluateCamundaSendMessageRequestAsJson).isEqualToJson("camunda-send-message-request.json");
    }

    private CamundaSendMessageRequest buildSendMessageRequest(String dueDate) {

        Map<String, DmnValue<?>> variables = Map.of(
            "idempotencyKey", dmnStringValue("a23422af-e92d-49d6-b591-be81f73e2dcf"),
            "group", dmnStringValue("TCW"),
            "jurisdiction", dmnStringValue("ia"),
            "caseType", dmnStringValue("asylum"),
            "taskId", dmnStringValue("processApplication"),
            "caseId", dmnStringValue("b675a81b-cea9-432f-9d63-9279973d0391"),
            "delayUntil", dmnStringValue(dueDate),
            "dueDate", dmnStringValue(dueDate)
        );

        SendMessageRequest sendMessageRequest =  CamundaSendMessageRequest.builder().all(false).correlationKeys(null)
            .messageName("createTaskMessage").processVariables(variables).build();

        return new CamundaSendMessageRequest(sendMessageRequest);
    }
}
