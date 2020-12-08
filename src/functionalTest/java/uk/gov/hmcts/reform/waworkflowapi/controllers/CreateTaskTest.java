package uk.gov.hmcts.reform.waworkflowapi.controllers;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.reform.waworkflowapi.SpringBootFunctionalBaseTest;
import uk.gov.hmcts.reform.waworkflowapi.api.CreateTaskRequest;
import uk.gov.hmcts.reform.waworkflowapi.utils.AuthorizationHeadersProvider;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static net.serenitybdd.rest.SerenityRest.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.waworkflowapi.api.CreateTaskRequestBuilder.aCreateTaskRequest;
import static uk.gov.hmcts.reform.waworkflowapi.api.CreateTaskRequestCreator.appealSubmittedCreateTaskRequest;
import static uk.gov.hmcts.reform.waworkflowapi.api.CreateTaskRequestCreator.requestRespondentEvidenceTaskRequest;
import static uk.gov.hmcts.reform.waworkflowapi.api.CreateTaskRequestCreator.unmappedCreateTaskRequest;
import static uk.gov.hmcts.reform.waworkflowapi.api.TransitionBuilder.aTransition;

public class CreateTaskTest extends SpringBootFunctionalBaseTest {


    @Value("${targets.instance}")
    private String testUrl;

    @Value("${targets.camunda}")
    private String camundaUrl;

    @Autowired
    private AuthorizationHeadersProvider authorizationHeadersProvider;

    private String caseId;

    private String serviceAuthorizationToken;

    @Before
    public void setUp() {
        caseId = UUID.randomUUID().toString();
        serviceAuthorizationToken =
            authorizationHeadersProvider
                .getAuthorizationHeaders()
                .getValue(SERVICE_AUTHORIZATION);
    }

    @Test
    public void should_not_allow_requests_without_valid_service_authorisation_and_return_403_response_code() {

        given()
            .relaxedHTTPSValidation()
            .contentType(APPLICATION_JSON_VALUE)
            .body(appealSubmittedCreateTaskRequest(caseId)).log().body()
            .baseUri(testUrl)
            .basePath("/tasks")
            .when()
            .post()
            .then()
            .statusCode(HttpStatus.FORBIDDEN_403);
    }

    @Test
    public void transition_creates_atask_with_default_due_date() {

        given()
            .relaxedHTTPSValidation()
            .header(SERVICE_AUTHORIZATION, serviceAuthorizationToken)
            .contentType(APPLICATION_JSON_VALUE)
            .body(appealSubmittedCreateTaskRequest(caseId)).log().body()
            .baseUri(testUrl)
            .basePath("/tasks")
            .when()
            .post()
            .then()
            .statusCode(HttpStatus.CREATED_201);

        String taskId = given()
            .header(SERVICE_AUTHORIZATION, serviceAuthorizationToken)
            .contentType(APPLICATION_JSON_VALUE)
            .baseUri(camundaUrl)
            .basePath("/task")
            .param("processVariables", "caseId_eq_" + caseId)
            .when()
            .get()
            .prettyPeek()
            .then()
            .body("size()", is(1))
            .body("[0].name", is("Process Application"))
            .body("[0].formKey", is("processApplication"))
            .extract()
            .path("[0].id");

        given()
            .header(SERVICE_AUTHORIZATION, serviceAuthorizationToken)
            .contentType(APPLICATION_JSON_VALUE)
            .baseUri(camundaUrl)
            .basePath("/task/" + taskId + "/identity-links?type=candidate")
            .when()
            .get()
            .prettyPeek()
            .then()
            .body("[0].groupId", is("TCW"));

        cleanUp(taskId, serviceAuthorizationToken);
    }

    @Test
    public void transition_creates_atask_with_due_date() {
        given()
            .relaxedHTTPSValidation()
            .header(SERVICE_AUTHORIZATION, serviceAuthorizationToken)
            .contentType(APPLICATION_JSON_VALUE)
            .body(requestRespondentEvidenceTaskRequest(caseId)).log().body()
            .baseUri(testUrl)
            .basePath("/tasks")
            .when()
            .post()
            .then()
            .statusCode(HttpStatus.CREATED_201);

        String taskId = given()
            .header(SERVICE_AUTHORIZATION, serviceAuthorizationToken)
            .contentType(APPLICATION_JSON_VALUE)
            .baseUri(camundaUrl)
            .basePath("/task")
            .param("processVariables", "caseId_eq_" + caseId)
            .when()
            .get()
            .prettyPeek()
            .then()
            .body("size()", is(1))
            .body("[0].name", is("Provide Respondent Evidence"))
            .body("[0].formKey", is("provideRespondentEvidence"))
            .extract()
            .path("[0].id");

        given()
            .header(SERVICE_AUTHORIZATION, serviceAuthorizationToken)
            .contentType(APPLICATION_JSON_VALUE)
            .baseUri(camundaUrl)
            .basePath("/task/" + taskId + "/identity-links?type=candidate")
            .when()
            .get()
            .prettyPeek()
            .then()
            .body("[0].groupId", is("external"));

        cleanUp(taskId, serviceAuthorizationToken);
    }

    @Test
    public void transition_does_not_creates_atask() {
        given()
            .relaxedHTTPSValidation()
            .header(SERVICE_AUTHORIZATION, serviceAuthorizationToken)
            .contentType(APPLICATION_JSON_VALUE)
            .body(unmappedCreateTaskRequest(caseId)).log().body()
            .baseUri(testUrl)
            .basePath("/tasks")
            .when()
            .post()
            .then()
            .statusCode(HttpStatus.NO_CONTENT_204);

        given()
            .header(SERVICE_AUTHORIZATION, serviceAuthorizationToken)
            .contentType(APPLICATION_JSON_VALUE)
            .baseUri(camundaUrl)
            .basePath("/task")
            .param("processVariables", "caseId_eq_" + caseId)
            .when()
            .get()
            .then()
            .body("size()", is(0));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void transition_create_overdue_task() {
        ZonedDateTime dueDate = ZonedDateTime.now();
        CreateTaskRequest createTaskRequest = aCreateTaskRequest()
            .withJurisdiction("IA")
            .withCaseType("Asylum")
            .withCaseId(caseId)
            .withTransition(
                aTransition()
                    .withPreState("appealSubmitted")
                    .withEventId("requestRespondentEvidence")
                    .withPostState("awaitingRespondentEvidence")
                    .build()
            )
            .withDueDate(dueDate)
            .build();
        given()
            .relaxedHTTPSValidation()
            .header(SERVICE_AUTHORIZATION, serviceAuthorizationToken)
            .contentType(APPLICATION_JSON_VALUE)
            .body(createTaskRequest).log().body()
            .baseUri(testUrl)
            .basePath("/tasks")
            .when()
            .post()
            .then()
            .statusCode(HttpStatus.CREATED_201);

        await().ignoreException(AssertionError.class).pollInterval(1, SECONDS).atMost(20, SECONDS).until(
            () -> {
                Object tasks = given()
                    .header(SERVICE_AUTHORIZATION, serviceAuthorizationToken)
                    .contentType(APPLICATION_JSON_VALUE)
                    .baseUri(camundaUrl)
                    .basePath("/task")
                    .param("processVariables", "caseId_eq_" + caseId)
                    .when()
                    .get()
                    .prettyPeek()
                    .then()
                    .body("size()", is(2))
                    .body("[0].name", is("Provide Respondent Evidence"))
                    .body("[0].formKey", is("provideRespondentEvidence"))
                    .body("[0].due", startsWith(dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                    .body("[1].name", is("Follow Up Overdue Respondent Evidence"))
                    .body("[1].formKey", is("followUpOverdueRespondentEvidence"))
                    .extract()
                    .path("id");

                ((List<String>) tasks).forEach(taskId -> cleanUp(taskId, serviceAuthorizationToken));

                return true;
            }
        );
    }
}
