package uk.gov.hmcts.reform.waworkflowapi.duedate;

import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class RealDateService implements DateService {
    @Override
    public ZonedDateTime now() {
        return ZonedDateTime.now();
    }
}
