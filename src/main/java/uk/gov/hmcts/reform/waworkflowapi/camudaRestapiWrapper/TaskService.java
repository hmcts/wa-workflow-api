package uk.gov.hmcts.reform.waworkflowapi.camudaRestapiWrapper;

import com.microsoft.applicationinsights.core.dependencies.http.HttpResponse;
import com.microsoft.applicationinsights.core.dependencies.http.client.HttpClient;
import com.microsoft.applicationinsights.core.dependencies.http.client.methods.HttpGet;
import com.microsoft.applicationinsights.core.dependencies.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TaskService {

    private static final String URL = "http://camunda-api-demo.service.core-compute-demo.internal/engine-rest";

    public void getTaskByID(String id) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(new HttpGet(URL+"/task/"+id));
        System.out.println(response);
    }
}
