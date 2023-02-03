package uk.gov.hmcts.reform.waworkflowapi.clients.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;


@EqualsAndHashCode
@ToString
public class SendMessageRequest {
    private final String messageName;
    private final Map<String, DmnValue<?>> processVariables;
    private final Map<String, DmnValue<?>> correlationKeys;
    private final boolean all;
    private final String tenantId;

    @JsonCreator
    public SendMessageRequest(@JsonProperty("messageName") String messageName,
                              @JsonProperty("processVariables") Map<String, DmnValue<?>> processVariables,
                              @JsonProperty("correlationKeys") Map<String, DmnValue<?>> correlationKeys,
                              @JsonProperty("all") boolean all,
                              @JsonProperty("tenantId") String tenantId) {
        this.messageName = messageName;
        this.processVariables = processVariables;
        this.correlationKeys = correlationKeys;
        this.all = all;
        this.tenantId = tenantId;
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

    public String getTenantId() {
        return tenantId;
    }
}
