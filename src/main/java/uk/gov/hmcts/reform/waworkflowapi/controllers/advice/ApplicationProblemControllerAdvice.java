package uk.gov.hmcts.reform.waworkflowapi.controllers.advice;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.validation.ValidationAdviceTrait;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import uk.gov.hmcts.reform.waworkflowapi.exceptions.CustomConstraintViolationException;
import uk.gov.hmcts.reform.waworkflowapi.exceptions.GenericForbiddenException;
import uk.gov.hmcts.reform.waworkflowapi.exceptions.GenericServerErrorException;

import java.net.URI;
import java.util.List;
import javax.validation.ConstraintViolationException;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.zalando.problem.Status.BAD_GATEWAY;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.SERVICE_UNAVAILABLE;

@Slf4j
@ControllerAdvice(basePackages = {
    "uk.gov.hmcts.reform.waworkflowapi.controllers"
})
@RequestMapping(produces = APPLICATION_PROBLEM_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.DataflowAnomalyAnalysis",
    "PMD.UseStringBufferForStringAppends", "PMD.LawOfDemeter"})
public class ApplicationProblemControllerAdvice implements ValidationAdviceTrait,
    ApplicationProblemControllerAdviceBase {

    public static final String EXCEPTION_OCCURRED = "Exception occurred: {}";

    @ExceptionHandler(FeignException.ServiceUnavailable.class)
    public ResponseEntity<ThrowableProblem> handleFeignServiceUnavailableException(FeignException ex) {
        log.error(EXCEPTION_OCCURRED, ex.getMessage(), ex);
        Status statusType = SERVICE_UNAVAILABLE; //503
        return createDownStreamErrorResponse(statusType);
    }

    @ExceptionHandler(FeignException.BadGateway.class)
    public ResponseEntity<ThrowableProblem> handleFeignBadGatewayException(FeignException ex) {
        log.error(EXCEPTION_OCCURRED, ex.getMessage(), ex);
        Status statusType = BAD_GATEWAY; //502
        return createDownStreamErrorResponse(statusType);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Problem> handleMessageNotReadable(HttpMessageNotReadableException exception) {

        Status statusType = BAD_REQUEST; //400
        URI type = URI.create("https://github.com/hmcts/wa-workflow-api/problem/bad-request");
        String title = "Bad Request";

        String errorMessage = extractErrors(exception);
        return ResponseEntity.status(statusType.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_PROBLEM_JSON_VALUE)
            .body(Problem.builder()
                .withType(type)
                .withTitle(title)
                .withDetail(errorMessage)
                .withStatus(statusType)
                .build());

    }

    @ExceptionHandler(CustomConstraintViolationException.class)
    public ResponseEntity<Problem> handleCustomConstraintViolation(CustomConstraintViolationException ex) {

        return ResponseEntity.status(ex.getStatus().getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_PROBLEM_JSON_VALUE)
            .body(new ConstraintViolationProblem(
                ex.getType(),
                ex.getStatus(),
                ex.getViolations())
            );
    }

    @Override
    public ResponseEntity<Problem> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                NativeWebRequest request) {

        Status status = BAD_REQUEST; //400
        URI type = URI.create("https://github.com/hmcts/wa-workflow-api/problem/constraint-validation");

        List<Violation> streamViolations = createViolations(ex.getBindingResult());

        List<Violation> violations = streamViolations.stream()
            // sorting to make tests deterministic
            .sorted(comparing(Violation::getField).thenComparing(Violation::getMessage))
            .collect(toList());

        return ResponseEntity.status(status.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_PROBLEM_JSON_VALUE)
            .body(new ConstraintViolationProblem(
                type,
                status,
                violations)
            );

    }

    @Override
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Problem> handleConstraintViolation(
        ConstraintViolationException ex,
        NativeWebRequest request) {
        Status status = BAD_REQUEST; //400
        URI type = URI.create("https://github.com/hmcts/wa-workflow-api/problem/constraint-validation");

        final List<Violation> violations = ex.getConstraintViolations().stream()
            .map(this::createViolation)
            .collect(toList());

        return ResponseEntity.status(status.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_PROBLEM_JSON_VALUE)
            .body(new ConstraintViolationProblem(
                type,
                status,
                violations)
            );
    }

    @ExceptionHandler({
        GenericForbiddenException.class,
        GenericServerErrorException.class
    })
    protected ResponseEntity<Problem> handleApplicationProblemExceptions(
        AbstractThrowableProblem ex
    ) {
        log.error(EXCEPTION_OCCURRED, ex.getMessage(), ex);
        return ResponseEntity.status(ex.getStatus().getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_PROBLEM_JSON_VALUE)
            .body(Problem.builder()
                .withType(ex.getType())
                .withTitle(ex.getTitle())
                .withDetail(ex.getMessage())
                .withStatus(ex.getStatus())
                .build());
    }

}

