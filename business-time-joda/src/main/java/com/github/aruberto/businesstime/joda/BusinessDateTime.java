package com.github.aruberto.businesstime.joda;

import com.github.aruberto.businesstime.common.BusinessDateTimeCalculator;
import com.github.aruberto.businesstime.common.BusinessDateTimeCalculatorResult;
import com.github.aruberto.businesstime.common.Constants;

import net.objectlab.kit.datecalc.common.DateCalculator;
import net.objectlab.kit.datecalc.common.DefaultHolidayCalendar;
import net.objectlab.kit.datecalc.common.HolidayHandlerType;
import net.objectlab.kit.datecalc.joda.JodaWorkingWeek;
import net.objectlab.kit.datecalc.joda.LocalDateKitCalculatorsFactory;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.ReadableDateTime;
import org.joda.time.base.AbstractDateTime;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * BusinessDateTime is an unmodifiable datetime class representing a datetime.
 * <p>
 * BusinessDateTime represents an exact point on the time-line, where time-line:
 * <ul>
 * <li>is limited to the precision of milliseconds</li>
 * <li>only includes points that fall between start and end of business day</li>
 * </ul>
 *
 * @author Antonio Ruberto
 */
public final class BusinessDateTime
    extends AbstractDateTime
    implements ReadableDateTime, Serializable {

  private static final long serialVersionUID = -9186202461058242569L;

  private static final LocalTime DEFAULT_BUSINESS_DAY_START = new LocalTime(9, 0, 0, 0);
  private static final LocalTime DEFAULT_BUSINESS_DAY_END = new LocalTime(17, 0, 0, 0);

  private final DateTime dateTime;
  private final Set<LocalDate> holidays;
  private final LocalTime dayStartTime;
  private final LocalTime dayEndTime;
  private final JodaWorkingWeek workingWeek;

  /**
   * Constructs an instance of BusinessDateTime at point of time of {@code dateTime}
   * with business day starting at {@code dayStartTime}, business day ending at {@code dayEndTime},
   * holiday list of {@code holidays} and working week of {@code workingWeek}
   *
   * @param dateTime date time, null means current time with default time zone
   * @param dayStartTime business day start time, null means 9am
   * @param dayEndTime business day end time, null means 5pm
   * @param holidays holidays, null means no holidays
   * @param workingWeek the working week, null means Monday to Friday
   */
  public BusinessDateTime(DateTime dateTime,
                          LocalTime dayStartTime,
                          LocalTime dayEndTime,
                          Set<LocalDate> holidays,
                          JodaWorkingWeek workingWeek) {
    if (dateTime == null) {
      throw new NullPointerException("date time cannot be null");
    }
    if (dayStartTime == null) {
      dayStartTime = DEFAULT_BUSINESS_DAY_START;
    }
    if (dayEndTime == null) {
      dayEndTime = DEFAULT_BUSINESS_DAY_END;
    }
    if (holidays == null) {
      holidays = Collections.emptySet();
    }
    if (workingWeek == null) {
      workingWeek = JodaWorkingWeek.DEFAULT;
    }
    if (dayEndTime.isEqual(dayStartTime) || dayEndTime.isBefore(dayStartTime)) {
      throw new IllegalArgumentException("business day end time must be after start time");
    }
    this.dateTime = dateTime;
    this.dayStartTime = dayStartTime;
    this.dayEndTime = dayEndTime;
    this.holidays = holidays;
    this.workingWeek = workingWeek;
  }

  /**
   * Constructs an instance of BusinessDateTime at point of time of {@code dateTime}
   * with business day starting at {@code dayStartTime}, business day ending at {@code dayEndTime},
   * holiday list of {@code holidays} and working week of Monday to Friday
   *
   * @param dateTime date time, null means current time with default time zone
   * @param dayStartTime business day start time, null means 9am
   * @param dayEndTime business day end time, null means 5pm
   * @param holidays holidays, null means no holidays
   */
  public BusinessDateTime(DateTime dateTime,
                          LocalTime dayStartTime,
                          LocalTime dayEndTime,
                          Set<LocalDate> holidays) {
    this(dateTime, dayStartTime, dayEndTime, holidays, null);
  }

  /**
   * Constructs an instance of BusinessDateTime at point of time of {@code dateTime}
   * with business day starting at 9am, business day ending at 5pm,
   * holiday list of {@code holidays} and working week of Monday to Friday
   *
   * @param dateTime date time, null means current time with default time zone
   * @param holidays holidays, null means no holidays
   */
  public BusinessDateTime(DateTime dateTime, Set<LocalDate> holidays) {
    this(dateTime, null, null, holidays, null);
  }

  /**
   * Constructs an instance of BusinessDateTime at point of time of {@code dateTime}
   * with business day starting at {@code dayStartTime}, business day ending at {@code dayEndTime},
   * no holidays and working week of Monday to Friday
   *
   * @param dateTime date time, null means current time with default time zone
   * @param dayStartTime business day start time, null means 9am
   * @param dayEndTime business day end time, null means 5pm
   */
  public BusinessDateTime(DateTime dateTime, LocalTime dayStartTime, LocalTime dayEndTime) {
    this(dateTime, dayStartTime, dayEndTime, null, null);
  }

  /**
   * Constructs an instance of BusinessDateTime at point of time of {@code dateTime}
   * with business day starting at 9am, business day ending at 5pm,
   * no holidays and working week of Monday to Friday
   *
   * @param dateTime date time, null means current time with default time zone
   */
  public BusinessDateTime(DateTime dateTime) {
    this(dateTime, null, null, null, null);
  }

  /**
   * Constructs an instance of BusinessDateTime at current time in system time zone,
   * with business day starting at 9am, business day ending at 5pm,
   * no holidays and working week of Monday to Friday
   */
  public BusinessDateTime() {
    this(new DateTime(), null, null, null, null);
  }

  private BusinessDateTime moveByMillis(long millisToMove) {
    DateCalculator<LocalDate> calc = new LocalDateKitCalculatorsFactory()
        .registerHolidays(Constants.HOLIDAY_KEY, new DefaultHolidayCalendar<LocalDate>(holidays))
        .getDateCalculator(Constants.HOLIDAY_KEY, HolidayHandlerType.FORWARD_UNLESS_MOVING_BACK)
        .setWorkingWeek(workingWeek);

    BusinessDateTimeCalculator<LocalDate> businessCalc =
        new BusinessDateTimeCalculator<LocalDate>();
    BusinessDateTimeCalculatorResult<LocalDate> result = businessCalc.moveByMillis(
        dateTime.toLocalDate(),
        dateTime.toLocalTime().getMillisOfDay(),
        millisToMove,
        dayStartTime.getMillisOfDay(),
        dayEndTime.getMillisOfDay(),
        calc);

    LocalTime endTime = new LocalTime(0, 0, 0, 0).plusMillis(result.getMillisOfDay());
    DateTime endDateTime = result.getEndDate().toDateTime(endTime, dateTime.getZone());
    return new BusinessDateTime(endDateTime, dayStartTime, dayEndTime, holidays, workingWeek);
  }

  /**
   * Gets the chronology of the datetime.
   *
   * @return the Chronology that the datetime is using
   */
  public Chronology getChronology() {
    return dateTime.getChronology();
  }

  /**
   * Gets the milliseconds of the datetime instant from the Java epoch
   * of 1970-01-01T00:00:00Z.
   *
   * @return the number of milliseconds since 1970-01-01T00:00:00Z
   */
  public long getMillis() {
    return moveByMillis(0).dateTime.getMillis();
  }

  /**
   * Returns a copy of this business datetime plus {@code millis} millis.
   *
   * @param millis the amount of millis to add, may be negative
   * @return the new business datetime plus the increased millis
   */
  public BusinessDateTime plusMillis(int millis) {
    if (millis == 0) {
      return this;
    } else {
      return moveByMillis(millis);
    }
  }

  /**
   * Returns a copy of this business datetime minus {@code millis} millis.
   *
   * @param millis the amount of millis to subtract, may be negative
   * @return the new business datetime minus the decreased millis
   */
  public BusinessDateTime minusMillis(int millis) {
    if (millis == 0) {
      return this;
    } else {
      return moveByMillis(-millis);
    }
  }

  /**
   * Returns a copy of this business datetime plus {@code seconds} seconds.
   *
   * @param seconds the amount of seconds to add, may be negative
   * @return the new business datetime plus the increased seconds
   */
  public BusinessDateTime plusSeconds(int seconds) {
    if (seconds == 0) {
      return this;
    } else {
      return moveByMillis(seconds * Constants.MILLIS_PER_SECOND);
    }
  }

  /**
   * Returns a copy of this business datetime minus {@code seconds} seconds.
   *
   * @param seconds the amount of seconds to subtract, may be negative
   * @return the new business datetime minus the decreased seconds
   */
  public BusinessDateTime minusSeconds(int seconds) {
    if (seconds == 0) {
      return this;
    } else {
      return moveByMillis(-seconds * Constants.MILLIS_PER_SECOND);
    }
  }

  /**
   * Returns a copy of this business datetime plus {@code minutes} minutes.
   *
   * @param minutes the amount of minutes to add, may be negative
   * @return the new business datetime plus the increased minutes
   */
  public BusinessDateTime plusMinutes(int minutes) {
    if (minutes == 0) {
      return this;
    } else {
      return moveByMillis(minutes * Constants.MILLIS_PER_MINUTE);
    }
  }

  /**
   * Returns a copy of this business datetime minus {@code minutes} minutes.
   *
   * @param minutes the amount of minutes to subtract, may be negative
   * @return the new business datetime minus the decreased minutes
   */
  public BusinessDateTime minusMinutes(int minutes) {
    if (minutes == 0) {
      return this;
    } else {
      return moveByMillis(-minutes * Constants.MILLIS_PER_MINUTE);
    }
  }

  /**
   * Returns a copy of this business datetime plus {@code hours} hours.
   *
   * @param hours the amount of hours to add, may be negative
   * @return the new business datetime plus the increased hours
   */
  public BusinessDateTime plusHours(int hours) {
    if (hours == 0) {
      return this;
    } else {
      return moveByMillis(hours * Constants.MILLIS_PER_HOUR);
    }
  }

  /**
   * Returns a copy of this business datetime minus {@code hours} hours.
   *
   * @param hours the amount of hours to subtract, may be negative
   * @return the new business datetime minus the decreased hours
   */
  public BusinessDateTime minusHours(int hours) {
    if (hours == 0) {
      return this;
    } else {
      return moveByMillis(-hours * Constants.MILLIS_PER_HOUR);
    }
  }

  /**
   * Returns a copy of this business datetime plus {@code days} days.
   *
   * @param days the amount of days to add, may be negative
   * @return the new business datetime plus the increased days
   */
  public BusinessDateTime plusDays(int days) {
    if (days == 0) {
      return this;
    } else {
      long millisPerDay = dayEndTime.getMillisOfDay() - dayStartTime.getMillisOfDay();
      return moveByMillis(days * millisPerDay);
    }
  }

  /**
   * Returns a copy of this business datetime minus {@code days} days.
   *
   * @param days the amount of days to subtract, may be negative
   * @return the new business datetime minus the decreased days
   */
  public BusinessDateTime minusDays(int days) {
    if (days == 0) {
      return this;
    } else {
      long millisPerDay = dayEndTime.getMillisOfDay() - dayStartTime.getMillisOfDay();
      return moveByMillis(-days * millisPerDay);
    }
  }

  /**
   * Returns a copy of this business datetime plus {@code weeks} weeks.
   *
   * @param weeks the amount of weeks to add, may be negative
   * @return the new business datetime plus the increased weeks
   */
  public BusinessDateTime plusWeeks(int weeks) {
    if (weeks == 0) {
      return this;
    } else {
      return new BusinessDateTime(
          dateTime.plusWeeks(weeks),
          dayStartTime,
          dayEndTime,
          holidays,
          workingWeek);
    }
  }

  /**
   * Returns a copy of this business datetime minus {@code weeks} weeks.
   *
   * @param weeks the amount of weeks to subtract, may be negative
   * @return the new business datetime minus the decreased weeks
   */
  public BusinessDateTime minusWeeks(int weeks) {
    if (weeks == 0) {
      return this;
    } else {
      return new BusinessDateTime(
          dateTime.minusWeeks(weeks),
          dayStartTime,
          dayEndTime,
          holidays,
          workingWeek);
    }
  }

  /**
   * Returns a copy of this business datetime plus {@code months} months.
   *
   * @param months the amount of months to add, may be negative
   * @return the new business datetime plus the increased months
   */
  public BusinessDateTime plusMonths(int months) {
    if (months == 0) {
      return this;
    } else {
      return new BusinessDateTime(
          dateTime.plusMonths(months),
          dayStartTime,
          dayEndTime,
          holidays,
          workingWeek);
    }
  }

  /**
   * Returns a copy of this business datetime minus {@code months} months.
   *
   * @param months the amount of months to subtract, may be negative
   * @return the new business datetime minus the decreased months
   */
  public BusinessDateTime minusMonths(int months) {
    if (months == 0) {
      return this;
    } else {
      return new BusinessDateTime(
          dateTime.minusMonths(months),
          dayStartTime,
          dayEndTime,
          holidays,
          workingWeek);
    }
  }

  /**
   * Returns a copy of this business datetime plus {@code years} years.
   *
   * @param years the amount of years to add, may be negative
   * @return the new business datetime plus the increased years
   */
  public BusinessDateTime plusYears(int years) {
    if (years == 0) {
      return this;
    } else {
      return new BusinessDateTime(
          dateTime.plusYears(years),
          dayStartTime,
          dayEndTime,
          holidays,
          workingWeek);
    }
  }

  /**
   * Returns a copy of this business datetime minus {@code years} years.
   *
   * @param years the amount of years to subtract, may be negative
   * @return the new business datetime minus the decreased years
   */
  public BusinessDateTime minusYears(int years) {
    if (years == 0) {
      return this;
    } else {
      return new BusinessDateTime(
          dateTime.minusYears(years),
          dayStartTime,
          dayEndTime,
          holidays,
          workingWeek);
    }
  }

  /**
   * Returns a copy of this datetime with the era field updated.
   *
   * @param era the era to set
   * @return a copy of this object with the field set
   * @throws IllegalArgumentException if the value is invalid
   */
  public BusinessDateTime withEra(int era) {
    return new BusinessDateTime(
        dateTime.withEra(era),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the century of era field updated.
   *
   * @param centuryOfEra the century of era to set
   * @return a copy of this object with the field set
   * @throws IllegalArgumentException if the value is invalid
   */
  public BusinessDateTime withCenturyOfEra(int centuryOfEra) {
    return new BusinessDateTime(
        dateTime.withCenturyOfEra(centuryOfEra),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the year of era field updated.
   *
   * @param yearOfEra the year of era to set
   * @return a copy of this object with the field set
   * @throws IllegalArgumentException if the value is invalid
   */
  public BusinessDateTime withYearOfEra(int yearOfEra) {
    return new BusinessDateTime(
        dateTime.withYearOfEra(yearOfEra),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the year of century field updated.
   *
   * @param yearOfCentury the year of century to set
   * @return a copy of this object with the field set
   * @throws IllegalArgumentException if the value is invalid
   */
  public BusinessDateTime withYearOfCentury(int yearOfCentury) {
    return new BusinessDateTime(
        dateTime.withYearOfCentury(yearOfCentury),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the year field updated.
   *
   * @param year the year to set
   * @return a copy of this object with the field set
   * @throws IllegalArgumentException if the value is invalid
   */
  public BusinessDateTime withYear(int year) {
    return new BusinessDateTime(
        dateTime.withYear(year),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the week year field updated.
   *
   * @param weekYear the week year to set
   * @return a copy of this object with the field set
   * @throws IllegalArgumentException if the value is invalid
   */
  public BusinessDateTime withWeekyear(int weekYear) {
    return new BusinessDateTime(
        dateTime.withWeekyear(weekYear),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the month of year field updated.
   *
   * @param monthOfYear the month of year to set
   * @return a copy of this object with the field set
   * @throws IllegalArgumentException if the value is invalid
   */
  public BusinessDateTime withMonthOfYear(int monthOfYear) {
    return new BusinessDateTime(
        dateTime.withMonthOfYear(monthOfYear),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the week of week year field updated.
   *
   * @param weekOfWeekYear the week of week year to set
   * @return a copy of this object with the field set
   * @throws IllegalArgumentException if the value is invalid
   */
  public BusinessDateTime withWeekOfWeekyear(int weekOfWeekYear) {
    return new BusinessDateTime(
        dateTime.withWeekOfWeekyear(weekOfWeekYear),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the day of year field updated.
   *
   * @param dayOfYear the day of year to set
   * @return a copy of this object with the field set
   * @throws IllegalArgumentException if the value is invalid
   */
  public BusinessDateTime withDayOfYear(int dayOfYear) {
    return new BusinessDateTime(
        dateTime.withDayOfYear(dayOfYear),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the day of month field updated.
   *
   * @param dayOfMonth the day of month to set
   * @return a copy of this object with the field set
   * @throws IllegalArgumentException if the value is invalid
   */
  public BusinessDateTime withDayOfMonth(int dayOfMonth) {
    return new BusinessDateTime(
        dateTime.withDayOfMonth(dayOfMonth),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the day of week field updated.
   *
   * @param dayOfWeek the day of week to set
   * @return a copy of this object with the field set
   * @throws IllegalArgumentException if the value is invalid
   */
  public BusinessDateTime withDayOfWeek(int dayOfWeek) {
    return new BusinessDateTime(
        dateTime.withDayOfWeek(dayOfWeek),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the hour of day field updated.
   *
   * @param hour the hour of day to set
   * @return a copy of this object with the field set
   * @throws IllegalArgumentException if the value is invalid
   */
  public BusinessDateTime withHourOfDay(int hour) {
    return new BusinessDateTime(
        dateTime.withHourOfDay(hour),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the minute of hour updated.
   *
   * @param minute the minute of hour to set
   * @return a copy of this object with the field set
   * @throws IllegalArgumentException if the value is invalid
   */
  public BusinessDateTime withMinuteOfHour(int minute) {
    return new BusinessDateTime(
        dateTime.withMinuteOfHour(minute),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the second of minute field updated.
   *
   * @param second the second of minute to set
   * @return a copy of this object with the field set
   * @throws IllegalArgumentException if the value is invalid
   */
  public BusinessDateTime withSecondOfMinute(int second) {
    return new BusinessDateTime(
        dateTime.withSecondOfMinute(second),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the millis of second field updated.
   *
   * @param millis the millis of second to set
   * @return a copy of this object with the field set
   * @throws IllegalArgumentException if the value is invalid
   */
  public BusinessDateTime withMillisOfSecond(int millis) {
    return new BusinessDateTime(
        dateTime.withMillisOfSecond(millis),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the millis of day field updated.
   *
   * @param millis the millis of day to set
   * @return a copy of this object with the field set
   * @throws IllegalArgumentException if the value is invalid
   */
  public BusinessDateTime withMillisOfDay(int millis) {
    return new BusinessDateTime(
        dateTime.withMillisOfDay(millis),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the business day start time field updated.
   *
   * @param time the time of day to set
   * @return a copy of this object with the field set
   * @throws IllegalArgumentException if the value is invalid
   */
  public BusinessDateTime withBusinessDayStartTime(LocalTime time) {
    return new BusinessDateTime(
        dateTime,
        time,
        dayEndTime,
        holidays,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the business day end time field updated.
   *
   * @param time the time of day to set
   * @return a copy of this object with the field set
   * @throws IllegalArgumentException if the value is invalid
   */
  public BusinessDateTime withBusinessDayEndTime(LocalTime time) {
    return new BusinessDateTime(
        dateTime,
        dayStartTime,
        time,
        holidays,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the holidays field updated.
   *
   * @param dates the set of holidays to set
   * @return a copy of this object with the field set
   */
  public BusinessDateTime withHolidays(Set<LocalDate> dates) {
    return new BusinessDateTime(
        dateTime,
        dayStartTime,
        dayEndTime,
        dates,
        workingWeek);
  }

  /**
   * Returns a copy of this datetime with the working week field updated.
   *
   * @param week the week to set
   * @return a copy of this object with the field set
   */
  public BusinessDateTime withWorkingWeek(JodaWorkingWeek week) {
    return new BusinessDateTime(
        dateTime,
        dayStartTime,
        dayEndTime,
        holidays,
        week);
  }
}
