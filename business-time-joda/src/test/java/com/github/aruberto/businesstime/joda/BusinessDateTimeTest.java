package com.github.aruberto.businesstime.joda;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class BusinessDateTimeTest {

  @Test
  public void toDateTime_InBusinessHours_SameTime() {
    DateTime expected = new DateTime(2014, 12, 12, 11, 34, 56, 756);
    BusinessDateTime businessTime = new BusinessDateTime(expected);

    assertEquals("Friday 11:34:56:756 should equal Friday 11:34:56:756",
                 expected,
                 businessTime.toDateTime());
  }

  @Test
  public void toDateTime_AfterBusinessHours_StartNextDay() {
    DateTime start = new DateTime(2014, 12, 11, 18, 34, 56, 756);
    DateTime expected = new DateTime(2014, 12, 12, 9, 0, 0, 0);
    BusinessDateTime businessTime = new BusinessDateTime(start);

    assertEquals("Thursday 18:34:56:756 should equal Friday 9:00:00:000",
                 expected,
                 businessTime.toDateTime());
  }

  @Test
  public void toDateTime_BeforeBusinessHours_StartOfDay() {
    DateTime start = new DateTime(2014, 12, 12, 5, 34, 56, 756);
    DateTime expected = new DateTime(2014, 12, 12, 9, 0, 0, 0);
    BusinessDateTime businessTime = new BusinessDateTime(start);

    assertEquals("Friday 5:34:56:756 should equal Friday 9:00:00:000",
                 expected,
                 businessTime.toDateTime());
  }

  @Test
  public void toDateTime_AfterBusinessHoursFriday_StartMonday() {
    DateTime start = new DateTime(2014, 12, 12, 18, 34, 56, 756);
    DateTime expected = new DateTime(2014, 12, 15, 9, 0, 0, 0);
    BusinessDateTime businessTime = new BusinessDateTime(start);

    assertEquals("Friday 18:34:56:756 should equal Monday 9:00:00:000",
                 expected,
                 businessTime.toDateTime());
  }

  @Test
  public void toDateTime_Weekend_StartMonday() {
    DateTime start = new DateTime(2014, 12, 13, 14, 34, 56, 756);
    DateTime expected = new DateTime(2014, 12, 15, 9, 0, 0, 0);
    BusinessDateTime businessTime = new BusinessDateTime(start);

    assertEquals("Saturday 14:34:56:756 should equal Monday 9:00:00:000",
                 expected,
                 businessTime.toDateTime());
  }

  @Test
  public void toDateTime_Holiday_StartNextWorkingDay() {
    DateTime start = new DateTime(2014, 12, 10, 14, 34, 56, 756);
    DateTime expected = new DateTime(2014, 12, 12, 9, 0, 0, 0);
    Set<LocalDate> holidays = new HashSet<LocalDate>() {{
      add(new LocalDate(2014, 12, 10));
      add(new LocalDate(2014, 12, 11));
    }};
    BusinessDateTime businessTime = new BusinessDateTime(start, holidays);

    assertEquals("Holiday Wednesday + Thursday should equal Friday 9:00:00:000",
                 expected,
                 businessTime.toDateTime());
  }

  @Test
  public void plusMillis_3Millis_3MillisLater() {
    DateTime start = new DateTime(2014, 12, 11, 12, 0, 0, 0);
    DateTime expected = new DateTime(2014, 12, 11, 12, 0, 0, 3);
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 12:00:00:000 plus 3 millis should return Thursday 12:00:00:003",
                 expected,
                 businessStart.plusMillis(3).toDateTime());
  }

  @Test
  public void plusMillis_3MillisEndOfDay_3MillisLaterNextDay() {
    DateTime start = new DateTime(2014, 12, 11, 16, 59, 59, 999);
    DateTime expected = new DateTime(2014, 12, 12, 9, 0, 0, 2);
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 16:59:59:999 plus 3 millis should return Friday 9:00:00:002",
                 expected,
                 businessStart.plusMillis(3).toDateTime());
  }

  @Test
  public void plusMillis_3Millis3MillisBeforeEndOfDay_EndOfDay() {
    DateTime start = new DateTime(2014, 12, 11, 16, 59, 59, 997);
    DateTime expected = new DateTime(2014, 12, 11, 17, 0, 0, 0);
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 16:59:59:997 plus 3 millis should return Thursday 17:00:00:000",
                 expected,
                 businessStart.plusMillis(3).toDateTime());
  }

  @Test
  public void minusMillis_3Millis_3MillisBefore() {
    DateTime start = new DateTime(2014, 12, 11, 12, 0, 0, 3);
    DateTime expected = new DateTime(2014, 12, 11, 12, 0, 0, 0);
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 12:00:00:003 minus 3 millis should return Thursday 12:00:00:000",
                 expected,
                 businessStart.minusMillis(3).toDateTime());
  }

  @Test
  public void minusMillis_3MillisBeginOfDay_3MillisBeforePreviousDay() {
    DateTime start = new DateTime(2014, 12, 12, 9, 0, 0, 2);
    DateTime expected = new DateTime(2014, 12, 11, 16, 59, 59, 999);
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Friday 9:00:00:002 minus 3 millis should return Thursday 16:59:59:999",
                 expected,
                 businessStart.minusMillis(3).toDateTime());
  }

  @Test
  public void minusMillis_3Millis3MillisAfterStartOfDay_StartOfDay() {
    DateTime start = new DateTime(2014, 12, 12, 9, 0, 0, 3);
    DateTime expected = new DateTime(2014, 12, 12, 9, 0, 0, 0);
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Friday 9:00:00:003 minus 3 millis should return Friday 9:00:00:000",
                 expected,
                 businessStart.minusMillis(3).toDateTime());
  }
}
