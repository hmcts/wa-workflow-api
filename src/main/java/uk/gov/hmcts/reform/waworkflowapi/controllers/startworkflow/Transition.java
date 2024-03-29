package uk.gov.hmcts.reform.waworkflowapi.controllers.startworkflow;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

public class Transition {

    @Schema(
        example = "caseCreated",
        required = true,
        description = "The state the case was in before the event fired"
    )
    private final String preState;
    @Schema(
        example = "submitCaseEvent",
        required = true,
        description = "The event that triggered the transition"
    )
    private final String eventId;
    @Schema(
        example = "caseSubmitted",
        required = true,
        description = "The state the case was in after the event fired"
    )
    private final String postState;

    public Transition(String preState, String eventId, String postState) {
        this.preState = preState;
        this.eventId = eventId;
        this.postState = postState;
    }

    public String getPreState() {
        return preState;
    }

    public String getEventId() {
        return eventId;
    }

    public String getPostState() {
        return postState;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Transition that = (Transition) object;
        return Objects.equals(preState, that.preState)
            && Objects.equals(eventId, that.eventId)
            && Objects.equals(postState, that.postState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(preState, eventId, postState);
    }

    @Override
    public String toString() {
        return "Transition{"
            + "preState='" + preState + '\''
            + ", eventId='" + eventId + '\''
            + ", postState='" + postState + '\''
            + '}';
    }
}
