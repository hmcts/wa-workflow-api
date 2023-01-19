package uk.gov.hmcts.reform.waworkflowapi.services;

import io.restassured.http.Header;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static uk.gov.hmcts.reform.waworkflowapi.config.ServiceTokenGeneratorConfiguration.SERVICE_AUTHORIZATION;

@Slf4j
@Service
public class AuthorizationHeadersProvider {

    private final Map<String, String> tokens = new ConcurrentHashMap<>();

    @Autowired
    private AuthTokenGenerator serviceAuthTokenGenerator;

    public Header getAuthorizationHeaders() {

        String generatedServiceToken = serviceAuthTokenGenerator.generate();
        log.info("RWA-2044-getAuthorizationHeaders-generatedServiceToken:{}", generatedServiceToken);

        String serviceToken = tokens.computeIfAbsent(
            SERVICE_AUTHORIZATION, user -> generatedServiceToken);

        log.info("RWA-2044-getAuthorizationHeaders-serviceToken:{}", generatedServiceToken);

        return new Header(SERVICE_AUTHORIZATION, serviceToken);
    }

}
