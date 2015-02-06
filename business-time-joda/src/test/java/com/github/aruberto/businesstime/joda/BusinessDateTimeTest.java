package com.github.aruberto.businesstime.joda;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BusinessDateTimeTest {

  @Test
  public void plusBusinessMillis_Add3Millis_DateTime3MillisLater() {
    DateTime start = new DateTime(2014, 12, 11, 12, 0, 0, 0);
    DateTime expected = new DateTime(2014, 12, 11, 12, 0, 0, 3);
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 12:00:00:000 plus 3 millis should return Thursday 12:00:00:003",
                 expected,
                 businessStart.plusMillis(3).toDateTime());
  }

  @Test
  public void plusBusinessMillis_Add3MillisEndOfDay_DateTime3MillisLaterNextDay() {
    DateTime start = new DateTime(2014, 12, 11, 16, 59, 59, 999);
    DateTime expected = new DateTime(2014, 12, 12, 9, 0, 0, 2);
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 16:59:59:999 plus 3 millis should return Friday 9:00:00:002",
                 expected,
                 businessStart.plusMillis(3).toDateTime());
  }

  @Test
  public void toDateTime_InBusinessHours_SameTime() {
    DateTime expected = new DateTime(2014, 12, 12, 11, 34, 56, 756);
    BusinessDateTime businessTime = new BusinessDateTime(expected);

    assertEquals("Friday 11:34:56:756 should equal Friday 11:34:56:756",
                 expected,
                 businessTime.toDateTime());
  }

  @Test
  public void toDateTime_AfterBusinessHours_EndOfDay() {
    DateTime start = new DateTime(2014, 12, 12, 18, 34, 56, 756);
    DateTime expected = new DateTime(2014, 12, 12, 17, 0, 0, 0);
    BusinessDateTime businessTime = new BusinessDateTime(start);

    assertEquals("Friday 18:34:56:756 should equal Friday 17:00:00:000",
                 expected,
                 businessTime.toDateTime());
  }

  @Test
  public void toDateTime_BeforeBusinessHours_EndOfPreviousDay() {
    DateTime start = new DateTime(2014, 12, 12, 5, 34, 56, 756);
    DateTime expected = new DateTime(2014, 12, 11, 17, 0, 0, 0);
    BusinessDateTime businessTime = new BusinessDateTime(start);

    assertEquals("Friday 5:34:56:756 should equal Thursday 17:00:00:000",
                 expected,
                 businessTime.toDateTime());
  }
}
