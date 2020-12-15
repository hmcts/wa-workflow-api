package uk.gov.hmcts.reform.waworkflowapi.external.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.waworkflowapi.external.model.DmnValue;
import uk.gov.hmcts.reform.waworkflowapi.external.model.EvaluateDmnRequest;

import java.util.List;
import java.util.Map;

@Component
public class EvaluateDmnService {
    private final TaskClientService taskClientService;

    @Autowired
    public EvaluateDmnService(TaskClientService taskClientService) {
        this.taskClientService = taskClientService;
    }

    public List<Map<String, DmnValue<?>>> evaluateDmn(EvaluateDmnRequest evaluateDmnRequest, String key) {
        return taskClientService.evaluate(evaluateDmnRequest, key);
    }

}

