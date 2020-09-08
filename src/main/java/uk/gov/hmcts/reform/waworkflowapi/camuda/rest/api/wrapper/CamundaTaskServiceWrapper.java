package uk.gov.hmcts.reform.waworkflowapi.camuda.rest.api.wrapper;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.hmcts.reform.waworkflowapi.models.Task;

@FeignClient(
    name = "tasks",
    url = "{camunda.url}"
)
@Service
public interface CamundaTaskServiceWrapper {

    @GetMapping("/task/{task-id}")
     Task getTask(@PathVariable("task-id") String id);
}
