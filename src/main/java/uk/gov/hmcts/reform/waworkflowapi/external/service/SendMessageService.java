package uk.gov.hmcts.reform.waworkflowapi.external.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.waworkflowapi.external.model.SendMessageRequest;

@Component
public class SendMessageService {
    private final TaskClientService taskClientService;

    @Autowired
    public SendMessageService(TaskClientService taskClientService) {
        this.taskClientService = taskClientService;
    }

    public void createMessage(SendMessageRequest sendMessageRequest) {
        taskClientService.sendMessage(sendMessageRequest);
    }

}
