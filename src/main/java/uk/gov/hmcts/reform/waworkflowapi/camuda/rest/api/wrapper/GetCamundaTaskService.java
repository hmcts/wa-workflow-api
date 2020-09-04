package uk.gov.hmcts.reform.waworkflowapi.camuda.rest.api.wrapper;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;

@Service
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public class GetCamundaTaskService {

    private static final Logger logger = LoggerFactory.getLogger(GetCamundaTaskService.class);

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
            logger.error(MessageFormat.format("Exception: {0}", e));
        } finally {
            method.releaseConnection();
        }
        return null;
    }
}
