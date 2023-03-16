package uk.gov.hmcts.reform.waworkflowapi.clients.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@EqualsAndHashCode
@ToString
public class CamundaProcessBody {

    private final Map<String, DmnValue<?>> variables;

    @JsonCreator
    public CamundaProcessBody(@JsonProperty("variables") Map<String, DmnValue<?>> variables) {
        this.variables = variables;
    }

    public Map<String, DmnValue<?>> getVariables() {
        return variables;
    }
}
