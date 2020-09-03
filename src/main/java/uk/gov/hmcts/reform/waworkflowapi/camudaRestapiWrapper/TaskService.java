package uk.gov.hmcts.reform.waworkflowapi.camudaRestapiWrapper;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class TaskService {

//    private static final String PROXY_HOST = "proxyout.reform.hmcts.net";
//    private static final int PROXY_PORT = 8080;
    private static final String URL = "http://camunda-api-aat.service.core-compute-aat.internal/engine-rest";
    public String getTaskByID(String id)  {
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(URL+"/task/"+id);
//        HostConfiguration config = client.getHostConfiguration();
//        config.setProxy(PROXY_HOST, PROXY_PORT);

//        AuthScope authScope = new AuthScope(PROXY_HOST, PROXY_PORT);
//        client.getState().setProxyCredentials(authScope, null);
        try {
            client.executeMethod(method);

            if (method.getStatusCode() == HttpStatus.SC_OK) {
                return method.getResponseBodyAsString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
        return null;
    }
}
