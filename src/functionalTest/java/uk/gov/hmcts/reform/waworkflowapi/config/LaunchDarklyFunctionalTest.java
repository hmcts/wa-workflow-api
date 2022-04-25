package uk.gov.hmcts.reform.waworkflowapi.config;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.waworkflowapi.SpringBootFunctionalBaseTest;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.reform.waworkflowapi.config.features.FeatureFlag.NON_EXISTENT_KEY;
import static uk.gov.hmcts.reform.waworkflowapi.config.features.FeatureFlag.RELEASE_2_CFT_TASK_WARNING;
import static uk.gov.hmcts.reform.waworkflowapi.config.features.FeatureFlag.TEST_KEY;

public class LaunchDarklyFunctionalTest extends SpringBootFunctionalBaseTest {

    @Autowired
    private LaunchDarklyFeatureFlagProvider featureFlagProvider;

    @Test
    public void should_hit_launch_darkly_and_return_true() {
        boolean launchDarklyFeature = featureFlagProvider.getBooleanValue(TEST_KEY);
        assertThat(launchDarklyFeature, is(true));
    }

    @Test
    public void should_hit_launch_darkly_with_non_existent_key_and_return_default_value_for_boolean() {
        boolean launchDarklyFeature = featureFlagProvider.getBooleanValue(NON_EXISTENT_KEY);
        assertThat(launchDarklyFeature, is(false));
    }

    @Test
    public void should_hit_launch_darkly_for_privileged_access_feature_and_return_either_true_or_false() {
        boolean launchDarklyFeature = featureFlagProvider.getBooleanValue(RELEASE_2_CFT_TASK_WARNING);
        assertThat(launchDarklyFeature, either(equalTo(true)).or(equalTo(false)));
    }

}
