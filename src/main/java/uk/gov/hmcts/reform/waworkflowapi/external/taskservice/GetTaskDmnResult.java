package uk.gov.hmcts.reform.waworkflowapi.external.taskservice;

import java.util.Objects;

public class GetTaskDmnResult {
    private DmnValue taskId;
    private DmnValue group;

    private GetTaskDmnResult() {
    }

    public GetTaskDmnResult(DmnValue taskId, DmnValue group) {
        this.taskId = taskId;
        this.group = group;
    }

    public DmnValue getTaskId() {
        return taskId;
    }

    public DmnValue getGroup() {
        return group;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        GetTaskDmnResult that = (GetTaskDmnResult) object;
        return Objects.equals(taskId, that.taskId)
               && Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }

    @Override
    public String toString() {
        return "GetTaskDmnResult{"
               + "taskId=" + taskId
               + ", group=" + group
               + '}';
    }
}
