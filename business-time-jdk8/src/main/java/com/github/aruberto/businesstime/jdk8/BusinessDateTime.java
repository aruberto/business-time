package com.github.aruberto.businesstime.jdk8;

import com.github.aruberto.businesstime.common.BusinessDateTimeCalculator;
import com.github.aruberto.businesstime.common.BusinessDateTimeCalculatorResult;
import com.github.aruberto.businesstime.common.Constants;

import net.objectlab.kit.datecalc.common.DateCalculator;
import net.objectlab.kit.datecalc.common.DefaultHolidayCalendar;
import net.objectlab.kit.datecalc.common.HolidayHandlerType;
import net.objectlab.kit.datecalc.common.WorkingWeek;
import net.objectlab.kit.datecalc.jdk8.LocalDateKitCalculatorsFactory;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.Collections;
import java.util.Set;

/**
 * BusinessDateTime is an unmodifiable datetime class representing a datetime.
 * <p>
 * BusinessDateTime represents an exact point on the time-line, where time-line:
 * <ul>
 * <li>is limited to the precision of nanoseconds</li>
 * <li>only includes points that fall between start and end of business day</li>
 * </ul>
 *
 * @author Antonio Ruberto
 */
public final class BusinessDateTime
    implements Temporal, ChronoZonedDateTime<LocalDate>, Serializable {

  private static final long serialVersionUID = -7158714391819702156L;

  private static final LocalTime DEFAULT_BUSINESS_DAY_START = LocalTime.of(9, 0, 0, 0);
  private static final LocalTime DEFAULT_BUSINESS_DAY_END = LocalTime.of(17, 0, 0, 0);

  private final ZonedDateTime dateTime;
  private final Set<LocalDate> holidays;
  private final LocalTime dayStartTime;
  private final LocalTime dayEndTime;
  private final Jdk8WorkingWeek workingWeek;

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
  public BusinessDateTime(ZonedDateTime dateTime,
                          LocalTime dayStartTime,
                          LocalTime dayEndTime,
                          Set<LocalDate> holidays,
                          WorkingWeek workingWeek) {
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
      workingWeek = WorkingWeek.DEFAULT;
    }
    if (dayEndTime.equals(dayStartTime) || dayEndTime.isBefore(dayStartTime)) {
      throw new IllegalArgumentException("business day end time must be after start time");
    }
    this.dateTime = dateTime;
    this.dayStartTime = dayStartTime;
    this.dayEndTime = dayEndTime;
    this.holidays = holidays;
    this.workingWeek = new Jdk8WorkingWeek(workingWeek);
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
  public BusinessDateTime(ZonedDateTime dateTime,
                          LocalTime dayStartTime,
                          LocalTime dayEndTime,
                          Set<LocalDate> holidays) {
    this(dateTime, dayStartTime, dayEndTime, holidays, null);
  }

  /**
   * Constructs an instance of BusinessDateTime at current time in system time zone
   * with business day starting at {@code dayStartTime}, business day ending at {@code dayEndTime},
   * holiday list of {@code holidays} and working week of Monday to Friday
   *
   * @param dayStartTime business day start time, null means 9am
   * @param dayEndTime business day end time, null means 5pm
   * @param holidays holidays, null means no holidays
   */
  public BusinessDateTime(LocalTime dayStartTime, LocalTime dayEndTime, Set<LocalDate> holidays) {
    this(ZonedDateTime.now(), dayStartTime, dayEndTime, holidays, null);
  }

  /**
   * Constructs an instance of BusinessDateTime at point of time of {@code dateTime}
   * with business day starting at 9am, business day ending at 5pm,
   * holiday list of {@code holidays} and working week of Monday to Friday
   *
   * @param dateTime date time, null means current time with default time zone
   * @param holidays holidays, null means no holidays
   */
  public BusinessDateTime(ZonedDateTime dateTime, Set<LocalDate> holidays) {
    this(dateTime, null, null, holidays, null);
  }

  /**
   * Constructs an instance of BusinessDateTime at current time in system time zone
   * with business day starting at 9am, business day ending at 5pm,
   * holiday list of {@code holidays} and working week of Monday to Friday
   *
   * @param holidays holidays, null means no holidays
   */
  public BusinessDateTime(Set<LocalDate> holidays) {
    this(ZonedDateTime.now(), null, null, holidays, null);
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
  public BusinessDateTime(ZonedDateTime dateTime, LocalTime dayStartTime, LocalTime dayEndTime) {
    this(dateTime, dayStartTime, dayEndTime, null, null);
  }

  /**
   * Constructs an instance of BusinessDateTime at current time in system time zone
   * with business day starting at {@code dayStartTime}, business day ending at {@code dayEndTime},
   * no holidays and working week of Monday to Friday
   *
   * @param dayStartTime business day start time, null means 9am
   * @param dayEndTime business day end time, null means 5pm
   */
  public BusinessDateTime(LocalTime dayStartTime, LocalTime dayEndTime) {
    this(ZonedDateTime.now(), dayStartTime, dayEndTime, null, null);
  }

  /**
   * Constructs an instance of BusinessDateTime at point of time of {@code dateTime}
   * with business day starting at 9am, business day ending at 5pm,
   * no holidays and working week of Monday to Friday
   *
   * @param dateTime date time, null means current time with default time zone
   */
  public BusinessDateTime(ZonedDateTime dateTime) {
    this(dateTime, null, null, null, null);
  }

  /**
   * Constructs an instance of BusinessDateTime at current time in system time zone,
   * with business day starting at 9am, business day ending at 5pm,
   * no holidays and working week of Monday to Friday
   */
  public BusinessDateTime() {
    this(ZonedDateTime.now(), null, null, null, null);
  }

  private DateCalculator<LocalDate> getDateCalculator() {
    return new LocalDateKitCalculatorsFactory()
        .registerHolidays(Constants.HOLIDAY_KEY, new DefaultHolidayCalendar<>(holidays))
        .getDateCalculator(Constants.HOLIDAY_KEY, HolidayHandlerType.FORWARD_UNLESS_MOVING_BACK)
        .setWorkingWeek(workingWeek);
  }

  private BusinessDateTime fromResult(BusinessDateTimeCalculatorResult<LocalDate> result) {
    ZonedDateTime endDateTime = result.getEndDate()
        .atStartOfDay(dateTime.getZone())
        .plusNanos(result.getNanosOfDay());
    return new BusinessDateTime(endDateTime, dayStartTime, dayEndTime, holidays, workingWeek);
  }

  private BusinessDateTime move(long unitsToMove, long unitFactor) {
    DateCalculator<LocalDate> calc = getDateCalculator();

    BusinessDateTimeCalculator<LocalDate> businessCalc = new BusinessDateTimeCalculator<>();
    BusinessDateTimeCalculatorResult<LocalDate> result = businessCalc.move(
        dateTime.toLocalDate(),
        dateTime.toLocalTime().toNanoOfDay(),
        unitsToMove,
        unitFactor,
        dayStartTime.toNanoOfDay(),
        dayEndTime.toNanoOfDay(),
        calc);

    return fromResult(result);
  }

  private BusinessDateTime moveDays(int days) {
    DateCalculator<LocalDate> calc = getDateCalculator();

    BusinessDateTimeCalculator<LocalDate> businessCalc = new BusinessDateTimeCalculator<>();
    BusinessDateTimeCalculatorResult<LocalDate> result = businessCalc.moveDays(
        dateTime.toLocalDate(),
        dateTime.toLocalTime().toNanoOfDay(),
        days,
        dayStartTime.toNanoOfDay(),
        dayEndTime.toNanoOfDay(),
        calc);

    return fromResult(result);
  }

  @Override
  public ChronoLocalDateTime<LocalDate> toLocalDateTime() {
    return move(0, Constants.NANOS_PER_NANO).dateTime.toLocalDateTime();
  }

  @Override
  public ZoneOffset getOffset() {
    return dateTime.getOffset();
  }

  @Override
  public ZoneId getZone() {
    return dateTime.getZone();
  }

  @Override
  public ChronoZonedDateTime<LocalDate> withEarlierOffsetAtOverlap() {
    return new BusinessDateTime(
        dateTime.withEarlierOffsetAtOverlap(),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek
    );
  }

  @Override
  public ChronoZonedDateTime<LocalDate> withLaterOffsetAtOverlap() {
    return new BusinessDateTime(
        dateTime.withLaterOffsetAtOverlap(),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek
    );
  }

  @Override
  public ChronoZonedDateTime<LocalDate> withZoneSameLocal(ZoneId zone) {
    return new BusinessDateTime(
        dateTime.withZoneSameLocal(zone),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek
    );
  }

  @Override
  public ChronoZonedDateTime<LocalDate> withZoneSameInstant(ZoneId zone) {
    return new BusinessDateTime(
        dateTime.withZoneSameInstant(zone),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek
    );
  }

  @Override
  public ChronoZonedDateTime<LocalDate> with(TemporalField field, long newValue) {
    return new BusinessDateTime(
        dateTime.with(field, newValue),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek
    );
  }

  @Override
  public ChronoZonedDateTime<LocalDate> plus(long amountToAdd, TemporalUnit unit) {
    if (unit instanceof ChronoUnit) {
      ChronoUnit f = (ChronoUnit) unit;
      switch (f) {
        case NANOS: return move(amountToAdd, Constants.NANOS_PER_NANO);
        case MICROS: return move(amountToAdd, Constants.NANOS_PER_MICRO);
        case MILLIS: return move(amountToAdd, Constants.NANOS_PER_MILLI);
        case SECONDS: return move(amountToAdd, Constants.NANOS_PER_SECOND);
        case MINUTES: return move(amountToAdd, Constants.NANOS_PER_MINUTE);
        case HOURS: return move(amountToAdd, Constants.NANOS_PER_HOUR);
        case HALF_DAYS: return moveDays((int)(amountToAdd / 2));
        case DAYS: return moveDays((int)amountToAdd);
      }
    }
    return new BusinessDateTime(
        dateTime.plus(amountToAdd, unit),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek
    );
  }

  @Override
  public long until(Temporal endExclusive, TemporalUnit unit) {
    return move(0, Constants.NANOS_PER_NANO).dateTime.until(endExclusive, unit);
  }

  @Override
  public boolean isSupported(TemporalField field) {
    return dateTime.isSupported(field);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof BusinessDateTime) {
      BusinessDateTime other = (BusinessDateTime) obj;
      return move(0, Constants.NANOS_PER_NANO).dateTime
          .equals(other.move(0, Constants.NANOS_PER_NANO).dateTime);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return move(0, Constants.NANOS_PER_NANO).dateTime.hashCode();
  }

  @Override
  public String toString() {
    return move(0, Constants.NANOS_PER_NANO).dateTime.toString();
  }

  public ZonedDateTime toZonedDateTime() {
    return move(0, Constants.NANOS_PER_NANO).dateTime;
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the specified period in years added.
   *
   * @param years the years to add, may be negative
   * @return a {@code BusinessDateTime} based on this date-time with the years added, not null
   * @throws java.time.DateTimeException if the result exceeds the supported date range
   */
  public BusinessDateTime plusYears(long years) {
    if (years == 0) {
      return this;
    } else {
      return new BusinessDateTime(
          dateTime.plusYears(years),
          dayStartTime,
          dayEndTime,
          holidays,
          workingWeek
      );
    }
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the specified period in years subtracted.
   *
   * @param years the years to subtract, may be negative
   * @return a {@code BusinessDateTime} based on this date-time with the years subtracted, not null
   * @throws java.time.DateTimeException if the result exceeds the supported date range
   */
  public BusinessDateTime minusYears(long years) {
    if (years == 0) {
      return this;
    } else {
      return new BusinessDateTime(
          dateTime.minusYears(years),
          dayStartTime,
          dayEndTime,
          holidays,
          workingWeek
      );
    }
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the specified period in months added.
   *
   * @param months the months to add, may be negative
   * @return a {@code ZonedDateTime} based on this date-time with the months added, not null
   * @throws java.time.DateTimeException if the result exceeds the supported date range
   */
  public BusinessDateTime plusMonths(long months) {
    if (months == 0) {
      return this;
    } else {
      return new BusinessDateTime(
          dateTime.plusMonths(months),
          dayStartTime,
          dayEndTime,
          holidays,
          workingWeek
      );
    }
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the specified period in months subtracted.
   *
   * @param months the months to subtract, may be negative
   * @return a {@code BusinessDateTime} based on this date-time with the months subtracted, not null
   * @throws java.time.DateTimeException if the result exceeds the supported date range
   */
  public BusinessDateTime minusMonths(long months) {
    if (months == 0) {
      return this;
    } else {
      return new BusinessDateTime(
          dateTime.minusMonths(months),
          dayStartTime,
          dayEndTime,
          holidays,
          workingWeek
      );
    }
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the specified period in weeks added.
   *
   * @param weeks the weeks to add, may be negative
   * @return a {@code BusinessDateTime} based on this date-time with the weeks added, not null
   * @throws java.time.DateTimeException if the result exceeds the supported date range
   */
  public BusinessDateTime plusWeeks(long weeks) {
    if (weeks == 0) {
      return this;
    } else {
      return new BusinessDateTime(
          dateTime.plusWeeks(weeks),
          dayStartTime,
          dayEndTime,
          holidays,
          workingWeek
      );
    }
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the specified period in weeks subtracted.
   *
   * @param weeks the weeks to subtract, may be negative
   * @return a {@code BusinessDateTime} based on this date-time with the weeks subtracted, not null
   * @throws java.time.DateTimeException if the result exceeds the supported date range
   */
  public BusinessDateTime minusWeeks(long weeks) {
    if (weeks == 0) {
      return this;
    } else {
      return new BusinessDateTime(
          dateTime.minusWeeks(weeks),
          dayStartTime,
          dayEndTime,
          holidays,
          workingWeek
      );
    }
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the specified period in days added.
   *
   * @param days the days to add, may be negative
   * @return a {@code BusinessDateTime} based on this date-time with the days added, not null
   */
  public BusinessDateTime plusDays(long days) {
    if (days == 0) {
      return this;
    } else {
      return moveDays((int) days);
    }
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the specified period in days subtracted.
   *
   * @param days the days to subtract, may be negative
   * @return a {@code BusinessDateTime} based on this date-time with the days subtracted, not null
   */
  public BusinessDateTime minusDays(long days) {
    if (days == 0) {
      return this;
    } else {
      return moveDays((int) -days);
    }
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the specified period in hours added.
   *
   * @param hours the hours to add, may be negative
   * @return a {@code BusinessDateTime} based on this date-time with the hours added, not null
   */
  public BusinessDateTime plusHours(long hours) {
    if (hours == 0) {
      return this;
    } else {
      return move(hours, Constants.NANOS_PER_HOUR);
    }
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the specified period in hours subtracted.
   *
   * @param hours the hours to subtract, may be negative
   * @return a {@code BusinessDateTime} based on this date-time with the hours subtracted, not null
   */
  public BusinessDateTime minusHours(long hours) {
    if (hours == 0) {
      return this;
    } else {
      return move(-hours, Constants.NANOS_PER_HOUR);
    }
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the specified period in minutes added.
   *
   * @param minutes the minutes to add, may be negative
   * @return a {@code BusinessDateTime} based on this date-time with the minutes added, not null
   */
  public BusinessDateTime plusMinutes(long minutes) {
    if (minutes == 0) {
      return this;
    } else {
      return move(minutes, Constants.NANOS_PER_MINUTE);
    }
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the specified period in minutes
   * subtracted.
   *
   * @param minutes the minutes to subtract, may be negative
   * @return a {@code BusinessDateTime} based on this date-time with the minutes
   *         subtracted, not null
   */
  public BusinessDateTime minusMinutes(long minutes) {
    if (minutes == 0) {
      return this;
    } else {
      return move(-minutes, Constants.NANOS_PER_MINUTE);
    }
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the specified period in seconds added.
   *
   * @param seconds the seconds to add, may be negative
   * @return a {@code BusinessDateTime} based on this date-time with the seconds added, not null
   */
  public BusinessDateTime plusSeconds(long seconds) {
    if (seconds == 0) {
      return this;
    } else {
      return move(seconds, Constants.NANOS_PER_SECOND);
    }
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the specified period in seconds
   * subtracted.
   *
   * @param seconds the seconds to subtract, may be negative
   * @return a {@code BusinessDateTime} based on this date-time with the seconds
   *         subtracted, not null
   */
  public BusinessDateTime minusSeconds(long seconds) {
    if (seconds == 0) {
      return this;
    } else {
      return move(-seconds, Constants.NANOS_PER_SECOND);
    }
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the specified period in nanoseconds added.
   *
   * @param nanos the nanos to add, may be negative
   * @return a {@code BusinessDateTime} based on this date-time with the nanoseconds added, not null
   */
  public BusinessDateTime plusNanos(long nanos) {
    if (nanos == 0) {
      return this;
    } else {
      return move(nanos, Constants.NANOS_PER_NANO);
    }
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the specified period in nanoseconds
   * subtracted.
   *
   * @param nanos the nanos to subtract, may be negative
   * @return a {@code BusinessDateTime} based on this date-time with the nanoseconds
   *         subtracted, not null
   */
  public BusinessDateTime minusNanos(long nanos) {
    if (nanos == 0) {
      return this;
    } else {
      return move(-nanos, Constants.NANOS_PER_NANO);
    }
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the year value altered.
   *
   * @param year the year to set in the result, from MIN_YEAR to MAX_YEAR
   * @return a {@code BusinessDateTime} based on this date-time with the requested year, not null
   * @throws java.time.DateTimeException if the year value is invalid
   */
  public BusinessDateTime withYear(int year) {
    return new BusinessDateTime(
        dateTime.withYear(year),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek
    );
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the month-of-year value altered.
   *
   * @param month the month-of-year to set in the result, from 1 (January) to 12 (December)
   * @return a {@code BusinessDateTime} based on this date-time with the requested month, not null
   * @throws java.time.DateTimeException if the month-of-year value is invalid
   */
  public BusinessDateTime withMonth(int month) {
    return new BusinessDateTime(
        dateTime.withMonth(month),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek
    );
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the day-of-month value altered.
   *
   * @param dayOfMonth the day-of-month to set in the result, from 1 to 28-31
   * @return a {@code BusinessDateTime} based on this date-time with the requested day, not null
   * @throws java.time.DateTimeException if the day-of-month value is invalid,
   *         or if the day-of-month is invalid for the month-year
   */
  public BusinessDateTime withDayOfMonth(int dayOfMonth) {
    return new BusinessDateTime(
        dateTime.withDayOfMonth(dayOfMonth),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek
    );
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the day-of-year altered.
   *
   * @param dayOfYear the day-of-year to set in the result, from 1 to 365-366
   * @return a {@code BusinessDateTime} based on this date with the requested day, not null
   * @throws java.time.DateTimeException if the day-of-year value is invalid,
   *         or if the day-of-year is invalid for the year
   */
  public BusinessDateTime withDayOfYear(int dayOfYear) {
    return new BusinessDateTime(
        dateTime.withDayOfYear(dayOfYear),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek
    );
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the hour-of-day value altered.
   *
   * @param hour the hour-of-day to set in the result, from 0 to 23
   * @return a {@code BusinessDateTime} based on this date-time with the requested hour, not null
   * @throws java.time.DateTimeException if the hour value is invalid
   */
  public BusinessDateTime withHour(int hour) {
    return new BusinessDateTime(
        dateTime.withHour(hour),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek
    );
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the minute-of-hour value altered.
   *
   * @param minute the minute-of-hour to set in the result, from 0 to 59
   * @return a {@code BusinessDateTime} based on this date-time with the requested minute, not null
   * @throws java.time.DateTimeException if the minute value is invalid
   */
  public BusinessDateTime withMinute(int minute) {
    return new BusinessDateTime(
        dateTime.withMinute(minute),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek
    );
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the second-of-minute value altered.
   *
   * @param second the second-of-minute to set in the result, from 0 to 59
   * @return a {@code BusinessDateTime} based on this date-time with the requested second, not null
   * @throws java.time.DateTimeException if the second value is invalid
   */
  public BusinessDateTime withSecond(int second) {
    return new BusinessDateTime(
        dateTime.withSecond(second),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek
    );
  }

  /**
   * Returns a copy of this {@code BusinessDateTime} with the nano-of-second value altered.
   *
   * @param nanoOfSecond the nano-of-second to set in the result, from 0 to 999,999,999
   * @return a {@code BusinessDateTime} based on this date-time with the requested
   *         nanosecond, not null
   * @throws java.time.DateTimeException if the nano value is invalid
   */
  public BusinessDateTime withNano(int nanoOfSecond) {
    return new BusinessDateTime(
        dateTime.withNano(nanoOfSecond),
        dayStartTime,
        dayEndTime,
        holidays,
        workingWeek
    );
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
  public BusinessDateTime withWorkingWeek(WorkingWeek week) {
    return new BusinessDateTime(
        dateTime,
        dayStartTime,
        dayEndTime,
        holidays,
        week);
  }
}
