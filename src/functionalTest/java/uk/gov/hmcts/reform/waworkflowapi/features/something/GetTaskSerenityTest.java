package uk.gov.hmcts.reform.waworkflowapi.features.something;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.serenitybdd.rest.SerenityRest.given;

@RunWith(SpringIntegrationSerenityRunner.class)
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
public class GetTaskSerenityTest {

    private final String testUrl = System.getenv("TEST_URL") == null ? "http://localhost:8099" :  System.getenv("TEST_URL");

    @Test
    public void transitionGetsATask() {
        given()
            .relaxedHTTPSValidation()
            .contentType("application/json")
            .pathParam("id","025c59e3-dbe2-11ea-81e2-661816095024")
            .baseUri(testUrl)
            .when()
            .get("task/{id}")
            .then()
            .statusCode(HttpStatus.OK_200);
    }
}
