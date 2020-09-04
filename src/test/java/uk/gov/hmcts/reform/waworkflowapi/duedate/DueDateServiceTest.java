package uk.gov.hmcts.reform.waworkflowapi.duedate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.waworkflowapi.duedate.holidaydates.HolidayService;
import uk.gov.hmcts.reform.waworkflowapi.external.taskservice.TaskToCreate;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.waworkflowapi.external.taskservice.Task.PROCESS_APPLICATION;

@SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
class DueDateServiceTest {

    public static final String TCW_GROUP = "TCW";
    private FixedDateService dateService;
    private DueDateService underTest;
    private HolidayService holidayService;

    @BeforeEach
    void setUp() {
        dateService = new FixedDateService();
        holidayService = mock(HolidayService.class);
        underTest = new DueDateService(dateService, holidayService);
    }

    @Test
    void haveToSetEitherADueDateOrHaveWorkingDays() {
        assertThrows(IllegalStateException.class, () -> {
            underTest.calculateDueDate(
                null,
                new TaskToCreate(PROCESS_APPLICATION, TCW_GROUP)
            );
        });

    }

    @Test
    void ifADueDateIsAlreadySetDoNotCalculateANewOne() {
        ZonedDateTime providedDueDate = ZonedDateTime.now();
        ZonedDateTime calculatedDueDate = underTest.calculateDueDate(
            providedDueDate,
            new TaskToCreate(PROCESS_APPLICATION, TCW_GROUP)
        );

        assertThat(calculatedDueDate, is(providedDueDate));
    }

    @Test
    void calculateDueDateAllWorkingDays() {
        int leadTimeDays = 2;
        ZonedDateTime now = ZonedDateTime.of(2020, 9, 1, 1, 2, 3, 4, ZoneId.systemDefault());
        dateService.setCurrentDateTime(now);

        ZonedDateTime calculatedDueDate = underTest.calculateDueDate(
            null,
            new TaskToCreate(PROCESS_APPLICATION, TCW_GROUP, leadTimeDays)
        );

        assertThat(calculatedDueDate, is(now.plusDays(leadTimeDays)));
    }

    @Test
    void calculateDueDateWhenFallInAWeekend() {
        int leadTimeDays = 2;
        ZonedDateTime now = ZonedDateTime.of(2020, 9, 3, 1, 2, 3, 4, ZoneId.systemDefault());
        dateService.setCurrentDateTime(now);

        ZonedDateTime calculatedDueDate = underTest.calculateDueDate(
            null,
            new TaskToCreate(PROCESS_APPLICATION, TCW_GROUP, leadTimeDays)
        );

        ZonedDateTime theFollowingMonday = ZonedDateTime.of(2020, 9, 7, 1, 2, 3, 4, ZoneId.systemDefault());
        assertThat(calculatedDueDate, is(theFollowingMonday));
    }

    @Test
    void calculateDueDateWhenStraddlesAWeekend() {
        int leadTimeDays = 4;
        ZonedDateTime now = ZonedDateTime.of(2020, 9, 3, 1, 2, 3, 4, ZoneId.systemDefault());
        dateService.setCurrentDateTime(now);

        ZonedDateTime calculatedDueDate = underTest.calculateDueDate(
            null,
            new TaskToCreate(PROCESS_APPLICATION, TCW_GROUP, leadTimeDays)
        );

        ZonedDateTime theFollowingMonday = ZonedDateTime.of(2020, 9, 9, 1, 2, 3, 4, ZoneId.systemDefault());
        assertThat(calculatedDueDate, is(theFollowingMonday));
    }

    @Test
    void calculateDueDateWhichStraddlesMultipleWeekends() {
        int leadTimeDays = 10;
        ZonedDateTime now = ZonedDateTime.of(2020, 9, 3, 1, 2, 3, 4, ZoneId.systemDefault());
        dateService.setCurrentDateTime(now);

        ZonedDateTime calculatedDueDate = underTest.calculateDueDate(
            null,
            new TaskToCreate(PROCESS_APPLICATION, TCW_GROUP, leadTimeDays)
        );

        ZonedDateTime theFollowingMonday = ZonedDateTime.of(2020, 9, 17, 1, 2, 3, 4, ZoneId.systemDefault());
        assertThat(calculatedDueDate, is(theFollowingMonday));
    }

    @Test
    void calculateDueDateWhichFallsOnAWeekend() {
        int leadTimeDays = 10;
        ZonedDateTime now = ZonedDateTime.of(2020, 9, 3, 1, 2, 3, 4, ZoneId.systemDefault());
        dateService.setCurrentDateTime(now);

        ZonedDateTime calculatedDueDate = underTest.calculateDueDate(
            null,
            new TaskToCreate(PROCESS_APPLICATION, TCW_GROUP, leadTimeDays)
        );

        ZonedDateTime theFollowingMonday = ZonedDateTime.of(2020, 9, 17, 1, 2, 3, 4, ZoneId.systemDefault());
        assertThat(calculatedDueDate, is(theFollowingMonday));
    }

    @Test
    void calculateDueDateWhichFallsOnAHoliday() {
        int leadTimeDays = 2;
        ZonedDateTime startDay = ZonedDateTime.of(2020, 9, 1, 1, 2, 3, 4, ZoneId.systemDefault());
        dateService.setCurrentDateTime(startDay);
        when(holidayService.isHoliday(ZonedDateTime.of(2020, 9, 3, 1, 2, 3, 4, ZoneId.systemDefault()))).thenReturn(true);

        ZonedDateTime calculatedDueDate = underTest.calculateDueDate(
            null,
            new TaskToCreate(PROCESS_APPLICATION, TCW_GROUP, leadTimeDays)
        );

        ZonedDateTime theFollowingMonday = ZonedDateTime.of(2020, 9, 4, 1, 2, 3, 4, ZoneId.systemDefault());
        assertThat(calculatedDueDate, is(theFollowingMonday));
    }

    @Test
    void calculateDueDateWhichStraddlesAHoliday() {
        int leadTimeDays = 2;
        ZonedDateTime startDay = ZonedDateTime.of(2020, 9, 1, 1, 2, 3, 4, ZoneId.systemDefault());
        dateService.setCurrentDateTime(startDay);
        when(holidayService.isHoliday(startDay.plusDays(1))).thenReturn(true);

        ZonedDateTime calculatedDueDate = underTest.calculateDueDate(
            null,
            new TaskToCreate(PROCESS_APPLICATION, TCW_GROUP, leadTimeDays)
        );

        ZonedDateTime theFollowingMonday = ZonedDateTime.of(2020, 9, 4, 1, 2, 3, 4, ZoneId.systemDefault());
        assertThat(calculatedDueDate, is(theFollowingMonday));
    }
}
