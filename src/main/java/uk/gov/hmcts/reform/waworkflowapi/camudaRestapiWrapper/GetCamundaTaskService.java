package uk.gov.hmcts.reform.waworkflowapi.camudaRestapiWrapper;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GetCamundaTaskService {


    private static final String URL = "http://camunda-api-aat.service.core-compute-aat.internal/engine-rest";

    public String getTaskByID(String id) {
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(URL + "/task/" + id);

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
