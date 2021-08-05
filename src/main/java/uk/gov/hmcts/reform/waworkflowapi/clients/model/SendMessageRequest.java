package uk.gov.hmcts.reform.waworkflowapi.clients.model;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SendMessageRequest {
    private String messageName;
    private Map<String, DmnValue<?>> processVariables;
    private Map<String, DmnValue<?>> correlationKeys;
    private boolean all;

    public SendMessageRequest(String messageName,
                              Map<String, DmnValue<?>> processVariables,
                              Map<String, DmnValue<?>> correlationKeys,
                              boolean all) {
        this.messageName = messageName;
        this.processVariables = processVariables;
        this.correlationKeys = correlationKeys;
        this.all = all;
    }

    public String getMessageName() {
        return messageName;
    }


    public Map<String, DmnValue<?>> getProcessVariables() {
        return processVariables;
    }

    public Map<String, DmnValue<?>> getCorrelationKeys() {
        return correlationKeys;
    }

    public boolean isAll() {
        return all;
    }
}
