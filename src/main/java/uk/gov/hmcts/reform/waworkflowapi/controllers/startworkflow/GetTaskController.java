package uk.gov.hmcts.reform.waworkflowapi.controllers.startworkflow;

import com.microsoft.applicationinsights.core.dependencies.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.waworkflowapi.camuda.rest.api.wrapper.GetCamundaTaskService;
import uk.gov.hmcts.reform.waworkflowapi.models.Task;

import java.io.IOException;


@RestController
@SuppressWarnings("PMD.UnnecessaryLocalBeforeReturn")
public class GetTaskController {

    @Autowired
    private GetCamundaTaskService taskService;


    @GetMapping(path = "/task/{id}", produces = { MediaType.APPLICATION_JSON_VALUE})
    public Task getTask(@PathVariable("id") String id) throws IOException {
        Gson response = new Gson();
        return response.fromJson(taskService.getTaskByID(id), Task.class);
    }
}
