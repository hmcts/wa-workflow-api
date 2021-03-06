package uk.gov.hmcts.reform.waworkflowapi.controllers.advice;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

class ErrorMessageTest {

    @Test
    void should_set_properties() {

        ErrorMessage testErrorMessage = new ErrorMessage(
            new Exception("some message"),
            HttpStatus.INTERNAL_SERVER_ERROR,
            Timestamp.valueOf(LocalDateTime.now())
        );

        assertEquals("Internal Server Error", testErrorMessage.getError());
        assertEquals("some message", testErrorMessage.getMessage());
        assertEquals(500, testErrorMessage.getStatus());
        assertNotNull(testErrorMessage.getTimestamp());
    }

    @Test
    void should_throw_exceptions_if_args_not_set() {

        assertThatThrownBy(() -> new ErrorMessage(
            null,
            HttpStatus.INTERNAL_SERVER_ERROR,
            Timestamp.valueOf(LocalDateTime.now())
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Exception must not be null")
            .hasNoCause();

        assertThatThrownBy(() -> new ErrorMessage(
            new Exception("some message"),
            null,
            Timestamp.valueOf(LocalDateTime.now())
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("HttpStatus error must not be null")
            .hasNoCause();


        assertThatThrownBy(() -> new ErrorMessage(
            new Exception("some message"),
            HttpStatus.INTERNAL_SERVER_ERROR,
            null
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Timestamp must not be null")
            .hasNoCause();

    }

}
