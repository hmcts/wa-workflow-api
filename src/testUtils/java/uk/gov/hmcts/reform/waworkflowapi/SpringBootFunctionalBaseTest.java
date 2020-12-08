package uk.gov.hmcts.reform.waworkflowapi;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static net.serenitybdd.rest.SerenityRest.given;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RunWith(SpringIntegrationSerenityRunner.class)
@SpringBootTest
@ActiveProfiles("functional")
public abstract class SpringBootFunctionalBaseTest {

    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    @Value("${targets.camunda}")
    protected String camundaUrl;

    public void cleanUp(String taskId, String token) {
        given()
            .header(SERVICE_AUTHORIZATION, token)
            .contentType(APPLICATION_JSON_VALUE)
            .baseUri(camundaUrl)
            .basePath("/task/" + taskId + "/complete")
            .when()
            .post();
    }
}
