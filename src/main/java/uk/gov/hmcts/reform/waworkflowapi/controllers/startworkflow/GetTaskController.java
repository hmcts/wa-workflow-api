package uk.gov.hmcts.reform.waworkflowapi.controllers.startworkflow;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.waworkflowapi.camudaRestapiWrapper.TaskService;

@RestController
public class GetTaskController {

    @Autowired
    TaskService taskService;

    private final Logger log = LoggerFactory.getLogger(GetTaskController.class);

    @GetMapping(path = "/task")
    @ApiOperation("Gets a task by ID")
    @ApiResponses({@ApiResponse(code = 204, message = "No task was found")})
    public String getTask()  {
//        log.info("In here b");
//        log.info(id);
//        taskService.getTaskByID(id);
        return "Done";
    }
}
