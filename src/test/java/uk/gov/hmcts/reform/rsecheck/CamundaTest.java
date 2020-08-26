package uk.gov.hmcts.reform.rsecheck;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CamundaTest {


    private static final String PROXY_HOST = "proxyout.reform.hmcts.net";
    private static final int PROXY_PORT = 8080;

    @Test
    public void CamundaTest() throws IOException {
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod("http://camunda-api-aat.service.core-compute-aat.internal/version");

        HostConfiguration config = client.getHostConfiguration();
        config.setProxy(PROXY_HOST, PROXY_PORT);


        AuthScope authScope = new AuthScope(PROXY_HOST, PROXY_PORT);
        client.getState().setProxyCredentials(authScope, null);
        client.executeMethod(method);
        assertEquals(method.getStatusCode(),HttpStatus.SC_NOT_FOUND);
        method.releaseConnection();
        }
}
