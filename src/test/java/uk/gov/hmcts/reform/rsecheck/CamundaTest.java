package uk.gov.hmcts.reform.rsecheck;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CamundaTest {

    @Test
    public void camundaHealthTest() throws IOException {
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod("http://camunda-api-aat.service.core-compute-aat.internal/health");
        client.executeMethod(method);
        assertEquals(method.getStatusCode(),HttpStatus.SC_OK,"");
        method.releaseConnection();
    }
}
