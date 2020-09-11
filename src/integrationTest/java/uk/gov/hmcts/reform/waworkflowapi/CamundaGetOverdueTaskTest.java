package uk.gov.hmcts.reform.waworkflowapi;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionRuleResult;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class CamundaGetOverdueTaskTest {

    private DmnEngine dmnEngine;

    @BeforeEach
    void setUp() {
        dmnEngine = DmnEngineConfiguration
            .createDefaultDmnEngineConfiguration()
            .buildEngine();
    }

    @DisplayName("Get overdue task")
    @ParameterizedTest(name = "\"{0}\" should go to \"{1}\"")
    @CsvSource({
        "provideRespondentEvidence, followUpOverdueRespondentEvidence, TCW",
        "provideCaseBuilding, followUpOverdueCaseBuilding, TCW",
        "provideReasonsForAppeal, followUpOverdueReasonsForAppeal, TCW",
        "provideClarifyingAnswers, followUpOverdueClarifyingAnswers, TCW",
        "provideCmaRequirements, followUpOverdueCmaRequirements, TCW",
        "provideRespondentReview, followUpOverdueRespondentReview, TCW",
        "provideHearingRequirements, followUpOverdueHearingRequirements, TCW"
    })
    void shouldGetOverdueTaskIdTest(String taskId, String overdueTaskId) {
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmn(taskId);

        DmnDecisionRuleResult singleResult = dmnDecisionTableResult.getSingleResult();

        assertThat(singleResult.getEntry("taskId"), is(overdueTaskId));
    }

    @DisplayName("transition unmapped")
    @Test
    void transitionUnmapped() {
        DmnDecisionTableResult dmnDecisionRuleResults = evaluateDmn("anything");

        assertThat(dmnDecisionRuleResults.isEmpty(), is(true));
    }

    private DmnDecisionTableResult evaluateDmn(String taskId) {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("getOverdueTask.dmn")) {
            DmnDecision decision = dmnEngine.parseDecision("getOverdueTask", inputStream);

            VariableMap variables = new VariableMapImpl();
            variables.putValue("taskId", taskId);

            return dmnEngine.evaluateDecisionTable(decision, variables);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
