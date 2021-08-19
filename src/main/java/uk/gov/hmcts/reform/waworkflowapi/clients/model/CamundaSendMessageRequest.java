package uk.gov.hmcts.reform.waworkflowapi.clients.model;

import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming
public class CamundaSendMessageRequest extends SendMessageRequest {

    public CamundaSendMessageRequest(SendMessageRequest request) {
        super(request.getMessageName(), request.getProcessVariables(), request.getCorrelationKeys(), request.isAll());
    }
}
