package uk.gov.hmcts.reform.waworkflowapi.controllers.startworkflow;

import com.microsoft.applicationinsights.core.dependencies.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.waworkflowapi.camudaRestapiWrapper.TaskService;
import uk.gov.hmcts.reform.waworkflowapi.models.ExUITaskAttributtes;
import uk.gov.hmcts.reform.waworkflowapi.models.Task;


@RestController
public class GetTaskController {

    @Autowired
    private TaskService taskService;

    private final Logger log = LoggerFactory.getLogger(GetTaskController.class);

    @GetMapping(path = "/task/{id}", produces = { MediaType.APPLICATION_JSON_VALUE})
    public ExUITaskAttributtes getTask(@PathVariable(value = "id", required = true) String id) {
        log.info(taskService.getTaskByID(id));
        Gson response = new Gson();
        Task task = response.fromJson(taskService.getTaskByID(id), Task.class);
        ExUITaskAttributtes exUITaskAttributtes = new ExUITaskAttributtes();
        exUITaskAttributtes.setCaseDefinitionId(task.getCaseDefinitionId());
        exUITaskAttributtes.setTaskName(task.getName());
        return exUITaskAttributtes;
    }
}
