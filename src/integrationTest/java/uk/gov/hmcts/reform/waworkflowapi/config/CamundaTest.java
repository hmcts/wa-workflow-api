package uk.gov.hmcts.reform.waworkflowapi.config;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CamundaTest {

    public static final String URL = "http://camunda-api-aat.service.core-compute-aat.internal";
    HttpClient client = new HttpClient();

    @Test
    public void camundaHealthTest() throws IOException {
        HttpMethod method = new GetMethod(URL + "/health");
        client.executeMethod(method);
        assertEquals(method.getStatusCode(),HttpStatus.SC_OK,"");
        method.releaseConnection();
    }

    @Test
    public void camundaVersionTest() throws IOException {
        HttpMethod method = new GetMethod(URL + "/engine-rest/version");
        client.executeMethod(method);
        assertEquals(method.getResponseBodyAsString(),"{\"version\":\"7.13.3-ee\"}","");
        assertEquals(method.getStatusCode(),HttpStatus.SC_OK,"");
        method.releaseConnection();
    }

    @Test
    public void camundaProcessDefinitionTest() throws IOException {
        HttpMethod method = new GetMethod(URL + "/engine-rest/decision-definition");
        client.executeMethod(method);
        assertEquals(method.getStatusCode(),HttpStatus.SC_OK,"");
        method.releaseConnection();
    }
}
