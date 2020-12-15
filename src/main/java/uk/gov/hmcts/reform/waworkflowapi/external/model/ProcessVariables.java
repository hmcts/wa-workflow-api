package uk.gov.hmcts.reform.waworkflowapi.external.model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static uk.gov.hmcts.reform.waworkflowapi.external.model.DmnValue.dmnStringValue;

public class ProcessVariables {
    private final DmnValue<String> jurisdiction;
    private final DmnValue<String> caseType;
    private final DmnValue<String> caseId;
    private final DmnValue<String> taskId;
    private final DmnValue<String> group;
    private final DmnValue<String> dueDate;
    private final DmnValue<String> name;

    public ProcessVariables(
        String jurisdiction,
        String caseType,
        String caseId,
        String taskId,
        String group,
        ZonedDateTime dueDate,
        String name
    ) {
        this.jurisdiction = dmnStringValue(jurisdiction);
        this.caseType = dmnStringValue(caseType);
        this.caseId = dmnStringValue(caseId);
        this.taskId = dmnStringValue(taskId);
        this.group = dmnStringValue(group);
        this.dueDate = dmnStringValue(dueDate.format(DateTimeFormatter.ISO_INSTANT));
        this.name = dmnStringValue(name);
    }

    public DmnValue<String> getJurisdiction() {
        return jurisdiction;
    }

    public DmnValue<String> getCaseType() {
        return caseType;
    }

    public DmnValue<String> getCaseId() {
        return caseId;
    }

    public DmnValue<String> getTaskId() {
        return taskId;
    }

    public DmnValue<String> getGroup() {
        return group;
    }

    public DmnValue<String> getDueDate() {
        return dueDate;
    }

    public DmnValue<String> getName() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ProcessVariables that = (ProcessVariables) object;
        return Objects.equals(jurisdiction, that.jurisdiction)
               && Objects.equals(caseType, that.caseType)
               && Objects.equals(caseId, that.caseId)
               && Objects.equals(taskId, that.taskId)
               && Objects.equals(group, that.group)
               && Objects.equals(dueDate, that.dueDate)
               && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseId, taskId);
    }

    @Override
    public String toString() {
        return "ProcessVariables{"
               + "jurisdiction=" + jurisdiction
               + ", caseType=" + caseType
               + ", caseId=" + caseId
               + ", taskId=" + taskId
               + ", group=" + group
               + ", dueDate=" + dueDate
               + ", name=" + name
               + '}';
    }
}
