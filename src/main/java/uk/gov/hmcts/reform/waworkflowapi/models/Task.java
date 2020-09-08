package uk.gov.hmcts.reform.waworkflowapi.models;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings({"PMD.TooManyFields", "PMD.ShortClassName", "PMD.UnnecessaryConstructor", "PMD.UncommentedEmptyConstructor"})
@Getter
@Setter
public class Task {

    private String id;
    private String name;
    private String assignee;
    private String created;
    private String due;
    private String followUp;
    private String delegationState;
    private String description;
    private String executionId;
    private String owner;
    private String parentTaskId;
    private int priority;
    private String processDefinitionId;
    private String processInstanceId;
    private String taskDefinitionKey;
    private String caseExecutionId;
    private String caseInstanceId;
    private String caseDefinitionId;
    private boolean suspended;
    private String formKey;
    private String tenantId;

    public Task() {
    }
}
