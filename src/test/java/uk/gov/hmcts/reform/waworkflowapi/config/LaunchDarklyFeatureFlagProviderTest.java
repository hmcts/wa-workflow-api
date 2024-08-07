package uk.gov.hmcts.reform.waworkflowapi.config;

import com.launchdarkly.sdk.LDContext;
import com.launchdarkly.sdk.server.interfaces.LDClientInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.waworkflowapi.config.features.FeatureFlag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.waworkflowapi.config.features.FeatureFlag.TEST_KEY;

@ExtendWith(MockitoExtension.class)
class LaunchDarklyFeatureFlagProviderTest {

    @Mock
    private LDClientInterface ldClient;

    @InjectMocks
    private LaunchDarklyFeatureFlagProvider launchDarklyFeatureFlagProvider;

    private LDContext expectedLdContext;

    @BeforeEach
    void setup() {

        expectedLdContext = LDContext.builder("wa-workflow-api")
            .set("firstName", "Work Allocation")
            .set("lastName", "Workflow Api")
            .build();
    }

    @ParameterizedTest
    @CsvSource({
        "false, true, true",
        "false, false, false"
    })
    void getBooleanValue_return_expectedFlagValue(
        boolean defaultValue,
        boolean boolVariationReturn,
        boolean expectedFlagValue
    ) {
        when(ldClient.boolVariation(anyString(), any(LDContext.class), anyBoolean()))
            .thenReturn(boolVariationReturn);

        assertThat(launchDarklyFeatureFlagProvider.getBooleanValue(TEST_KEY))
            .isEqualTo(expectedFlagValue);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "NULL, featureFlag must not be null"}, nullValues = "NULL")
    void getBooleanValue_edge_case_scenarios(FeatureFlag featureFlag, String expectedMessage) {
        assertThatThrownBy(() -> launchDarklyFeatureFlagProvider.getBooleanValue(featureFlag))
            .isInstanceOf(NullPointerException.class)
            .hasMessage(expectedMessage);
    }

}
