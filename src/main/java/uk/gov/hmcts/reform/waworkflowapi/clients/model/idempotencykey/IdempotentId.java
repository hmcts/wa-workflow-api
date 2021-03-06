package uk.gov.hmcts.reform.waworkflowapi.clients.model.idempotencykey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@EqualsAndHashCode
@ToString
public class IdempotentId implements Serializable {
    private static final long serialVersionUID = -8159479848340078451L;
    private String idempotencyKey;
    private String tenantId;

    public IdempotentId() {
        //needed for spring boot data JPA
    }

    public IdempotentId(String idempotencyKey, String tenantId) {
        this.idempotencyKey = idempotencyKey;
        this.tenantId = tenantId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getTenantId() {
        return tenantId;
    }
}
