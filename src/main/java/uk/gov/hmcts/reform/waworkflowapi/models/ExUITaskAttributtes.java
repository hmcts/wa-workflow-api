package uk.gov.hmcts.reform.waworkflowapi.models;

public class ExUITaskAttributtes {

    private String taskName;
    private String caseDefinitionId;

    public ExUITaskAttributtes(){}

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setCaseDefinitionId(String caseDefinitionId) {
        this.caseDefinitionId = caseDefinitionId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getCaseDefinitionId() {
        return caseDefinitionId;
    }
}
