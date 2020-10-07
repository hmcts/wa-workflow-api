package uk.gov.hmcts.reform.waworkflowapi.controllers;

import io.restassured.RestAssured;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.waworkflowapi.SpringBootFunctionalBaseTest;

import static org.assertj.core.api.Assertions.assertThat;

public class EndpointSecurityTest extends SpringBootFunctionalBaseTest {

    @Value("${targets.instance}")
    private String testUrl;

    @Before
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    public void should_allow_unauthenticated_requests_to_welcome_message_and_return_200_response_code() {

        String response =
            SerenityRest
                .given()
                .when()
                .get("/")
                .then()
                .statusCode(HttpStatus.OK.value())
                .and()
                .extract().body().asString();

        assertThat(response)
            .contains("Welcome");
    }

    @Test
    public void should_allow_unauthenticated_requests_to_health_check_and_return_200_response_code() {

        String response =
            SerenityRest
                .given()
                .when()
                .get("/health")
                .then()
                .statusCode(HttpStatus.OK.value())
                .and()
                .extract().body().asString();

        assertThat(response)
            .contains("UP");
    }

    @Test
    public void should_not_allow_requests_without_valid_service_authorisation_and_return_403_response_code() {

        String invalidServiceToken = "invalid";

        authenticatedEndpoints.forEach(endpoint ->
                                           SerenityRest
                                               .given()
                                               .header("ServiceAuthorization", invalidServiceToken)
                                               .when()
                                               .get(endpoint)
                                               .then()
                                               .statusCode(HttpStatus.FORBIDDEN.value())
        );
    }

}
