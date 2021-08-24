package uk.gov.hmcts.reform.waworkflowapi.clients.model;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.experimental.SuperBuilder;

@JsonNaming
@SuperBuilder(toBuilder = true)
public class CamundaSendMessageRequest extends SendMessageRequest {

}
