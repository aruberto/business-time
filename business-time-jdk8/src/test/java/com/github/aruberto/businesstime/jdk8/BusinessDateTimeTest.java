package com.github.aruberto.businesstime.jdk8;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class BusinessDateTimeTest {

  @Test
  public void toZonedDateTime_InBusinessHours_SameTime() {
    ZonedDateTime expected =
        ZonedDateTime.of(2014, 12, 12, 11, 34, 56, 756, ZoneId.systemDefault());
    BusinessDateTime businessTime = new BusinessDateTime(expected);

    assertEquals("Friday 11:34:56.756 should equal Friday 11:34:56.756",
                 expected,
                 businessTime.toZonedDateTime());
  }

  @Test
  public void toZonedDateTime_AfterBusinessHours_StartNextDay() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 11, 18, 34, 56, 756, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 12, 9, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessTime = new BusinessDateTime(start);

    assertEquals("Thursday 18:34:56.756 should equal Friday 9:00:00.0",
                 expected,
                 businessTime.toZonedDateTime());
  }

  @Test
  public void toZonedDateTime_BeforeBusinessHours_StartOfDay() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 12, 5, 34, 56, 756, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 12, 9, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessTime = new BusinessDateTime(start);

    assertEquals("Friday 5:34:56.756 should equal Friday 9:00:00.0",
                 expected,
                 businessTime.toZonedDateTime());
  }

  @Test
  public void toZonedDateTime_AfterBusinessHoursFriday_StartMonday() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 12, 18, 34, 56, 756, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 15, 9, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessTime = new BusinessDateTime(start);

    assertEquals("Friday 18:34:56.756 should equal Monday 9:00:00.0",
                 expected,
                 businessTime.toZonedDateTime());
  }

  @Test
  public void toZonedDateTime_Weekend_StartMonday() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 13, 14, 34, 56, 756, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 15, 9, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessTime = new BusinessDateTime(start);

    assertEquals("Saturday 14:34:56.756 should equal Monday 9:00:00.0",
                 expected,
                 businessTime.toZonedDateTime());
  }

  @Test
  public void toZonedDateTime_Holiday_StartNextWorkingDay() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 10, 14, 34, 56, 756, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 12, 9, 0, 0, 0, ZoneId.systemDefault());
    Set<LocalDate> holidays = new HashSet<LocalDate>() {{
      add(LocalDate.of(2014, 12, 10));
      add(LocalDate.of(2014, 12, 11));
    }};
    BusinessDateTime businessTime = new BusinessDateTime(start, holidays);

    assertEquals("Holiday Wednesday + Thursday should equal Friday 9:00:00.0",
                 expected,
                 businessTime.toZonedDateTime());
  }

  @Test
  public void plusNanos_3Nanos_3NanosLater() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 11, 12, 0, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 11, 12, 0, 0, 3, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 12:00:00.0 plus 3 Nanos should return Thursday 12:00:00.3",
                 expected,
                 businessStart.plusNanos(3).toZonedDateTime());
  }

  @Test
  public void plusNanos_3NanosEndOfDay_3NanosLaterNextDay() {
    ZonedDateTime start =
        ZonedDateTime.of(2014, 12, 11, 16, 59, 59, 999999999, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 12, 9, 0, 0, 2, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 16:59:59.999999999 plus 3 Nanos should return Friday 9:00:00.2",
                 expected,
                 businessStart.plusNanos(3).toZonedDateTime());
  }

  @Test
  public void plusNanos_3Nanos3NanosBeforeEndOfDay_EndOfDay() {
    ZonedDateTime start =
        ZonedDateTime.of(2014, 12, 11, 16, 59, 59, 999999997, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 11, 17, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 16:59:59.999999997 plus 3 Nanos should return Thursday 17:00:00.0",
                 expected,
                 businessStart.plusNanos(3).toZonedDateTime());
  }

  @Test
  public void minusNanos_3Nanos_3NanosBefore() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 11, 12, 0, 0, 3, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 11, 12, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 12:00:00.3 minus 3 Nanos should return Thursday 12:00:00.0",
                 expected,
                 businessStart.minusNanos(3).toZonedDateTime());
  }

  @Test
  public void minusNanos_3NanosBeginOfDay_3NanosBeforePreviousDay() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 12, 9, 0, 0, 2, ZoneId.systemDefault());
    ZonedDateTime expected =
        ZonedDateTime.of(2014, 12, 11, 16, 59, 59, 999999999, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Friday 9:00:00.2 minus 3 Nanos should return Thursday 16:59:59.999999999",
                 expected,
                 businessStart.minusNanos(3).toZonedDateTime());
  }

  @Test
  public void minusNanos_3Nanos3NanosAfterStartOfDay_StartOfDay() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 12, 9, 0, 0, 3, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 12, 9, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Friday 9:00:00.3 minus 3 Nanos should return Friday 9:00:00.0",
                 expected,
                 businessStart.minusNanos(3).toZonedDateTime());
  }

  @Test
  public void plusSeconds_3Seconds_3SecondsLater() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 11, 12, 0, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 11, 12, 0, 3, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 12:00:00.0 plus 3 Seconds should return Thursday 12:00:03.0",
                 expected,
                 businessStart.plusSeconds(3).toZonedDateTime());
  }

  @Test
  public void plusSeconds_3SecondsEndOfDay_3SecondsLaterNextDay() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 11, 16, 59, 59, 999, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 12, 9, 0, 2, 999, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 16:59:59.999 plus 3 Seconds should return Friday 9:00:02.999",
                 expected,
                 businessStart.plusSeconds(3).toZonedDateTime());
  }

  @Test
  public void plusSeconds_3Seconds3SecondsBeforeEndOfDay_EndOfDay() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 11, 16, 59, 57, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 11, 17, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 16:59:57.0 plus 3 Seconds should return Thursday 17:00:00.0",
                 expected,
                 businessStart.plusSeconds(3).toZonedDateTime());
  }

  @Test
  public void minusSeconds_3Seconds_3SecondsBefore() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 11, 12, 0, 3, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 11, 12, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 12:00:03.0 minus 3 Seconds should return Thursday 12:00:00.0",
                 expected,
                 businessStart.minusSeconds(3).toZonedDateTime());
  }

  @Test
  public void minusSeconds_3SecondsBeginOfDay_3SecondsBeforePreviousDay() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 12, 9, 0, 2, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 11, 16, 59, 59, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Friday 9:00:02.0 minus 3 Seconds should return Thursday 16:59:59.0",
                 expected,
                 businessStart.minusSeconds(3).toZonedDateTime());
  }

  @Test
  public void minusSeconds_3Seconds3SecondsAfterStartOfDay_StartOfDay() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 12, 9, 0, 3, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 12, 9, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Friday 9:00:03.0 minus 3 Seconds should return Friday 9:00:00.0",
                 expected,
                 businessStart.minusSeconds(3).toZonedDateTime());
  }

  @Test
  public void plusMinutes_3Minutes_3MinutesLater() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 11, 12, 0, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 11, 12, 3, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 12:00:00.0 plus 3 Minutes should return Thursday 12:03:00.0",
                 expected,
                 businessStart.plusMinutes(3).toZonedDateTime());
  }

  @Test
  public void plusMinutes_3MinutesEndOfDay_3MinutesLaterNextDay() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 11, 16, 59, 59, 999, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 12, 9, 2, 59, 999, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 16:59:59.999 plus 3 Minutes should return Friday 9:03:59.999",
                 expected,
                 businessStart.plusMinutes(3).toZonedDateTime());
  }

  @Test
  public void plusMinutes_3Minutes3MinutesBeforeEndOfDay_EndOfDay() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 11, 16, 57, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 11, 17, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 16:57:00.0 plus 3 Minutes should return Thursday 17:00:00.0",
                 expected,
                 businessStart.plusMinutes(3).toZonedDateTime());
  }

  @Test
  public void minusMinutes_3Minutes_3MinutesBefore() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 11, 12, 3, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 11, 12, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 12:03:00.0 minus 3 Minutes should return Thursday 12:00:00.0",
                 expected,
                 businessStart.minusMinutes(3).toZonedDateTime());
  }

  @Test
  public void minusMinutes_3MinutesBeginOfDay_3MinutesBeforePreviousDay() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 12, 9, 2, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 11, 16, 59, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Friday 9:02:00.0 minus 3 Minutes should return Thursday 16:59:00.0",
                 expected,
                 businessStart.minusMinutes(3).toZonedDateTime());
  }

  @Test
  public void minusMinutes_3Minutes3MinutesAfterStartOfDay_StartOfDay() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 12, 9, 3, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 12, 9, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Friday 9:03:00.0 minus 3 Minutes should return Friday 9:00:00.0",
                 expected,
                 businessStart.minusMinutes(3).toZonedDateTime());
  }

  @Test
  public void plusHours_3Hours_3HoursLater() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 11, 12, 0, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 11, 15, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 12:00:00.0 plus 3 Hours should return Thursday 15:00:00.0",
                 expected,
                 businessStart.plusHours(3).toZonedDateTime());
  }

  @Test
  public void plusHours_3HoursEndOfDay_3HoursLaterNextDay() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 11, 16, 59, 59, 999, ZoneId.systemDefault());
    ZonedDateTime expected =
        ZonedDateTime.of(2014, 12, 12, 11, 59, 59, 999, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 16:59:59.999 plus 3 Hours should return Friday 11:59:59.999",
                 expected,
                 businessStart.plusHours(3).toZonedDateTime());
  }

  @Test
  public void plusHours_3Hours3HoursBeforeEndOfDay_EndOfDay() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 11, 14, 0, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 11, 17, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 14:00:00.0 plus 3 Hours should return Thursday 17:00:00.0",
                 expected,
                 businessStart.plusHours(3).toZonedDateTime());
  }

  @Test
  public void minusHours_3Hours_3HoursBefore() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 11, 15, 0, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 11, 12, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 15:00:00.0 minus 3 Hours should return Thursday 12:00:00.0",
                 expected,
                 businessStart.minusHours(3).toZonedDateTime());
  }

  @Test
  public void minusHours_3HoursBeginOfDay_3HoursBeforePreviousDay() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 12, 11, 0, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 11, 16, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Friday 11:00:00.0 minus 3 Hours should return Thursday 16:00:00.0",
                 expected,
                 businessStart.minusHours(3).toZonedDateTime());
  }

  @Test
  public void minusHours_3Hours3HoursAfterStartOfDay_StartOfDay() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 12, 12, 0, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 12, 9, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Friday 12:00:00.0 minus 3 Hours should return Friday 9:00:00.0",
                 expected,
                 businessStart.minusHours(3).toZonedDateTime());
  }

  @Test
  public void plusDays_3Days_3DaysLater() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 8, 12, 0, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 11, 12, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Monday 12:00:00.0 plus 3 Days should return Thursday 12:00:00.0",
                 expected,
                 businessStart.plusDays(3).toZonedDateTime());
  }

  @Test
  public void plusDays_3DaysEndOfWeek_3DaysLaterNextWeek() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 11, 12, 0, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 16, 12, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 12:00:00.0 plus 3 Days should return Tuesday 12:00:00.0",
                 expected,
                 businessStart.plusDays(3).toZonedDateTime());
  }

  @Test
  public void minusDays_3Days_3DaysBefore() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 11, 12, 0, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 8, 12, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 12:00:00.0 minus 3 Days should return Monday 12:00:00.0",
                 expected,
                 businessStart.minusDays(3).toZonedDateTime());
  }

  @Test
  public void minusDays_3DaysBeginOfWeek_3DaysBeforePreviousWeek() {
    ZonedDateTime start = ZonedDateTime.of(2014, 12, 16, 12, 0, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2014, 12, 11, 12, 0, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Tuesday 12:00:00.0 minus 3 Days should return Thursday 12:00:00.0",
                 expected,
                 businessStart.minusDays(3).toZonedDateTime());
  }

  @Test
  public void plusMinutes_Weekend_DayAfterWeekend() {
    ZonedDateTime start = ZonedDateTime.of(2017, 6, 24, 0, 0, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2017, 6, 26, 9, 35, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Saturday 00:00:00.0 plus 35 minutes should return Monday 9:35:00.0",
                 expected,
                 businessStart.plusMinutes(35).toZonedDateTime());
  }

  @Test
  public void plusMinutes_Weekend_2DayAfterWeekend() {
    ZonedDateTime start = ZonedDateTime.of(2017, 6, 24, 0, 0, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2017, 6, 27, 9, 35, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Saturday 00:00:00.0 minus 515 minutes should return Tuesday 9:35:00.0",
                 expected,
                 businessStart.plusMinutes(515).toZonedDateTime());
  }

  @Test
  public void minusMinutes_Weekend_DayBeforeWeekend() {
    ZonedDateTime start = ZonedDateTime.of(2017, 6, 24, 0, 0, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2017, 6, 23, 16, 25, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Saturday 00:00:00.0 minus 35 minutes should return Friday 16:25:00.0",
                 expected,
                 businessStart.minusMinutes(35).toZonedDateTime());
  }

  @Test
  public void minusMinutes_Weekend_2DayBeforeWeekend() {
    ZonedDateTime start = ZonedDateTime.of(2017, 6, 24, 0, 0, 0, 0, ZoneId.systemDefault());
    ZonedDateTime expected = ZonedDateTime.of(2017, 6, 22, 16, 25, 0, 0, ZoneId.systemDefault());
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Saturday 00:00:00.0 minus 515 minutes should return Thursday 16:25:00.0",
                 expected,
                 businessStart.minusMinutes(515).toZonedDateTime());
  }

  @Test
  public void readWriteObject_CurrentTime_CurrentTime() throws Exception {
    BusinessDateTime dateTime = new BusinessDateTime();

    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    ObjectOutputStream inputStream = new ObjectOutputStream(byteStream);
    inputStream.writeObject(dateTime);
    inputStream.close();

    ObjectInputStream outputStream =
        new ObjectInputStream(new ByteArrayInputStream(byteStream.toByteArray()));
    Object o  = outputStream.readObject();

    assertEquals("Serialize and de-serialize should return itself", dateTime, o);
  }
}
