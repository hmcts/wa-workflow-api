package uk.gov.hmcts.reform.waworkflowapi.clients.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
public class CamundaJob {
    private final String id;
    private final String jobDefinitionId;
    private final String processInstanceId;
    private final String processDefinitionId;
    private final String processDefinitionKey;
    private final String executionId;
    private final String exceptionMessage;
    private final String failedActivityId;
    private final Integer retries;
    private final String dueDate;
    private final Boolean suspended;
    private final Integer priority;
    private final String tenantId;
    private final String createTime;

    public CamundaJob(String id, String jobDefinitionId, String processInstanceId, String processDefinitionId,
                      String processDefinitionKey, String executionId, String exceptionMessage,
                      String failedActivityId, Integer retries, String dueDate, Boolean suspended, Integer priority,
                      String tenantId, String createTime) {
        this.id = id;
        this.jobDefinitionId = jobDefinitionId;
        this.processInstanceId = processInstanceId;
        this.processDefinitionId = processDefinitionId;
        this.processDefinitionKey = processDefinitionKey;
        this.executionId = executionId;
        this.exceptionMessage = exceptionMessage;
        this.failedActivityId = failedActivityId;
        this.retries = retries;
        this.dueDate = dueDate;
        this.suspended = suspended;
        this.priority = priority;
        this.tenantId = tenantId;
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public String getJobDefinitionId() {
        return jobDefinitionId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public String getFailedActivityId() {
        return failedActivityId;
    }

    public Integer getRetries() {
        return retries;
    }

    public String getDueDate() {
        return dueDate;
    }

    public Boolean getSuspended() {
        return suspended;
    }

    public Integer getPriority() {
        return priority;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getCreateTime() {
        return createTime;
    }
}
