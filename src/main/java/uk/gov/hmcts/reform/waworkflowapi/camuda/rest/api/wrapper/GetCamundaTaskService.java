package uk.gov.hmcts.reform.waworkflowapi.camuda.rest.api.wrapper;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@SuppressWarnings({"PMD.AvoidPrintStackTrace","PMD.AvoidThrowingNewInstanceOfSameException","PMD.PreserveStackTrace"})
public class GetCamundaTaskService {

    public String getTaskByID(String id) throws IOException {
        final String taskUrl = "http://camunda-api-aat.service.core-compute-aat.internal/engine-rest/task/" + id;
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(taskUrl);

        try {
            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK && !method.getResponseBodyAsString().equals("")) {
                return method.getResponseBodyAsString();
            }
        } catch (IOException e) {
            throw new IOException(e.toString());
        } finally {
            method.releaseConnection();
        }
        return null;
    }
}
