package uk.gov.hmcts.reform.waworkflowapi.controllers;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.waworkflowapi.SpringBootFunctionalBaseTest;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.DmnValue;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.EvaluateDmnRequest;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static uk.gov.hmcts.reform.waworkflowapi.config.ServiceTokenGeneratorConfiguration.SERVICE_AUTHORIZATION;

public class EvaluateDmnTest extends SpringBootFunctionalBaseTest {

    private static final String ENDPOINT_BEING_TESTED =
        "/workflow/decision-definition/key/%s/tenant-id/%s/evaluate";
    private Header authenticationHeaders;

    @Before
    public void setUp() {
        authenticationHeaders = authorizationHeadersProvider.getAuthorizationHeaders();
    }

    @Test
    public void should_not_allow_requests_without_valid_service_authorisation_and_return_401_response_code() {

        Response result = restApiActions.post(
            format(ENDPOINT_BEING_TESTED, WA_TASK_INITIATION_WA_ASYLUM, TENANT_ID_WA),
            null,
            null,
            new Headers(new Header(SERVICE_AUTHORIZATION, "invalidtoken"))
        );

        result.then().assertThat()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void should_evaluate_and_return_dmn_results() {

        EvaluateDmnRequest body = new EvaluateDmnRequest(
            Map.of(
                "eventId", DmnValue.dmnStringValue("uploadHomeOfficeBundle"),
                "postEventState", DmnValue.dmnStringValue("awaitingRespondentEvidence")
            ));

        Response result = restApiActions.post(
            format(ENDPOINT_BEING_TESTED, WA_TASK_INITIATION_WA_ASYLUM, TENANT_ID_WA),
            null,
            body,
            authenticationHeaders
        );

        result.then().assertThat()
            .statusCode(HttpStatus.OK.value())
            .and()
            .body("size()", equalTo(1))
            .body("results[0].name.value", equalTo("Review Respondent Evidence"))
            .body("results[0].workingDaysAllowed.value", equalTo(2))
            .body("results[0].taskId.value", equalTo("reviewRespondentEvidence"))
            .body("results[0].processCategories.value", equalTo("caseProgression"));

    }

    @Test
    public void should_evaluate_json_data_and_return_dmn_results() {

        Map<String, Object> appealMap = new HashMap<>();
        appealMap.put("appealType", "protection");
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("Data", appealMap);

        EvaluateDmnRequest body = new EvaluateDmnRequest(
            Map.of(
                "eventId", DmnValue.dmnStringValue("uploadHomeOfficeBundle"),
                "postEventState", DmnValue.dmnStringValue("awaitingRespondentEvidence"),
                "additionalData", DmnValue.dmnMapValue(dataMap)
            ));

        Response result = restApiActions.post(
            format(ENDPOINT_BEING_TESTED, WA_TASK_INITIATION_WA_ASYLUM, TENANT_ID_WA),
            null,
            body,
            authenticationHeaders
        );

        result.prettyPrint();

        result.then().assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("size()", equalTo(1))
            .body("results[0].name.value", equalTo("Review Respondent Evidence"))
            .body("results[0].workingDaysAllowed.value", equalTo(2))
            .body("results[0].taskId.value", equalTo("reviewRespondentEvidence"))
            .body("results[0].processCategories.value", equalTo("caseProgression"));

    }

    @Test
    public void should_evaluate_json_data_and_return_dmn_results_makeAnApplication() {

        Map<String, Object> appealMap = new HashMap<>();
        appealMap.put("lastModifiedApplication", Map.of(
            "type", "Adjourn",
            "decision", ""));
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("Data", appealMap);

        EvaluateDmnRequest body = new EvaluateDmnRequest(
            Map.of(
                "eventId", DmnValue.dmnStringValue("makeAnApplication"),
                "additionalData", DmnValue.dmnMapValue(dataMap)
            ));

        Response result = restApiActions.post(
            format(ENDPOINT_BEING_TESTED, WA_TASK_INITIATION_WA_ASYLUM, TENANT_ID_WA),
            null,
            body,
            authenticationHeaders
        );

        result.prettyPrint();

        result.then().assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("size()", equalTo(1))
            .body("results[0].name.value", equalTo("processDummyApplication"))
            .body("results[0].workingDaysAllowed.value", equalTo(2))
            .body("results[0].taskId.value", equalTo("processDummyApplication"))
            .body("results[0].processCategories.value", equalTo("caseProgression,followUpOverdue"));

    }

    @Test
    public void should_throw_an_error_when_dmn_table_does_not_exist() {

        EvaluateDmnRequest body = new EvaluateDmnRequest(
            Map.of(
                "eventId", DmnValue.dmnStringValue("submitAppeal"),
                "postEventState", DmnValue.dmnStringValue("appealSubmitted")
            ));

        Response result = restApiActions.post(
            format(ENDPOINT_BEING_TESTED, "non-existent", TENANT_ID_WA),
            body,
            authenticationHeaders
        );

        result.then().assertThat()
            .statusCode(HttpStatus.NOT_FOUND.value());

    }

    @Test
    public void should_not_allow_requests_without_valid_service_authorisation_and_return_401_response_code_for_wa() {

        Response result = restApiActions.post(
            format(ENDPOINT_BEING_TESTED, WA_TASK_INITIATION_WA_ASYLUM, TENANT_ID_WA),
            null,
            null,
            new Headers(new Header(SERVICE_AUTHORIZATION, "invalidtoken"))
        );

        result.then().assertThat()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void should_evaluate_and_return_dmn_results_for_wa() {

        EvaluateDmnRequest body = new EvaluateDmnRequest(
            Map.of(
                "eventId", DmnValue.dmnStringValue("submitCase"),
                "postEventState", DmnValue.dmnStringValue("caseUnderReview")
            ));

        Response result = restApiActions.post(
            format(ENDPOINT_BEING_TESTED, WA_TASK_INITIATION_WA_ASYLUM, TENANT_ID_WA),
            null,
            body,
            authenticationHeaders
        );

        result.then().assertThat()
            .statusCode(HttpStatus.OK.value())
            .and()
            .body("size()", equalTo(1))
            .body("results[0].name.value", equalTo("Review Appeal Skeleton Argument"))
            .body("results[0].workingDaysAllowed.value", equalTo(2))
            .body("results[0].taskId.value", equalTo("reviewAppealSkeletonArgument"))
            .body("results[0].processCategories.value", equalTo("caseProgression"));

    }

    @Test
    public void should_evaluate_json_data_and_return_dmn_results_for_wa() {

        Map<String, Object> appealMap = new HashMap<>();
        appealMap.put("appealType", "");
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("Data", appealMap);

        EvaluateDmnRequest body = new EvaluateDmnRequest(
            Map.of(
                "eventId", DmnValue.dmnStringValue("listCma"),
                "postEventState", DmnValue.dmnStringValue("cmaListed"),
                "additionalData", DmnValue.dmnMapValue(dataMap)
            ));

        Response result = restApiActions.post(
            format(ENDPOINT_BEING_TESTED, WA_TASK_INITIATION_WA_ASYLUM, TENANT_ID_WA),
            null,
            body,
            authenticationHeaders
        );

        result.then().assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("size()", equalTo(1))
            .body("results[0].name.value", equalTo("Attend Cma"))
            .body("results[0].workingDaysAllowed.value", equalTo(2))
            .body("results[0].taskId.value", equalTo("attendCma"))
            .body("results[0].taskType.value", equalTo("attendCma"))
            .body("results[0].processCategories.value", equalTo("caseProgression"));

    }


    @Test
    public void should_return_200_with_empty_list_when_event_id_does_not_exist() {

        EvaluateDmnRequest body = new EvaluateDmnRequest(
            Map.of(
                "eventId", DmnValue.dmnStringValue("invalidEventId"),
                "postEventState", DmnValue.dmnStringValue("appealSubmitted")
            ));

        Response result = restApiActions.post(
            format(ENDPOINT_BEING_TESTED, WA_TASK_INITIATION_WA_ASYLUM, TENANT_ID_WA),
            body,
            authenticationHeaders
        );

        result.then().assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("size()", equalTo(1))
            .body("results.size()", equalTo(0));

    }

    @Test
    public void should_return_200_with_empty_list_when_post_event_state_does_not_exist() {

        EvaluateDmnRequest body = new EvaluateDmnRequest(
            Map.of(
                "eventId", DmnValue.dmnStringValue("listCma"),
                "postEventState", DmnValue.dmnStringValue("invalidPostEventState")
            ));

        Response result = restApiActions.post(
            format(ENDPOINT_BEING_TESTED, WA_TASK_INITIATION_WA_ASYLUM, TENANT_ID_WA),
            body,
            authenticationHeaders
        );

        result.then().assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("size()", equalTo(1))
            .body("results.size()", equalTo(0));
    }

    @Test
    public void should_evaluate_and_return_dmn_results_without_space() {

        String taskAttributes = "{\"taskType\":\"reviewAppealSkeletonArgument\"}";

        EvaluateDmnRequest body = new EvaluateDmnRequest(
            Map.of(
                "taskAttributes", DmnValue.jsonValue(taskAttributes)
            ));

        Response result = restApiActions.post(
            format(ENDPOINT_BEING_TESTED, WA_TASK_PERMISSIONS_WA_ASYLUM, TENANT_ID_WA),
            null,
            body,
            authenticationHeaders
        );

        result.then().assertThat()
            .statusCode(HttpStatus.OK.value())
            .and()
            .body("size()", equalTo(1))
            .body("results.size()", equalTo(2))
            .body("results[0].name.value", equalTo("task-supervisor"))
            .body("results[0].value.value", equalTo("Read,Manage,Cancel,Assign,Unassign,Complete"))
            .body("results[1].name.value", equalTo("ctsc"))
            .body("results[1].value.value", equalTo("Read,Own,Cancel"));

    }

}
