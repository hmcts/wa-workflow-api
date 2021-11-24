package uk.gov.hmcts.reform.waworkflowapi.clients.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@EqualsAndHashCode
@ToString
public class AddProcessVariableRequest {
    private final Map<String, CamundaValue<String>> modifications;

    public AddProcessVariableRequest(Map<String, CamundaValue<String>> modifications) {
        this.modifications = modifications;
    }

    public Map<String, CamundaValue<String>> getModifications() {
        return modifications;
    }
}
