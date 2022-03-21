package uk.gov.hmcts.reform.waworkflowapi.exceptions.enums;

public enum ErrorMessages {

    GENERIC_FORBIDDEN_ERROR(
        "The action could not be completed because the client/user had insufficient rights to a resource."),

    INITIATE_TASK_PROCESS_ERROR(
        "The action could not be completed because there was a problem when initiating the task."),

    DOWNSTREAM_DEPENDENCY_ERROR(
        "Downstream dependency did not respond as expected and the request could not be completed.");

    private final String detail;

    ErrorMessages(String detail) {
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

}
