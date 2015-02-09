package com.github.aruberto.businesstime.jdk8;

import com.github.aruberto.businesstime.common.BusinessDateTimeCalculator;
import com.github.aruberto.businesstime.common.BusinessDateTimeCalculatorResult;
import com.github.aruberto.businesstime.common.Constants;

import net.objectlab.kit.datecalc.common.DateCalculator;
import net.objectlab.kit.datecalc.common.DefaultHolidayCalendar;
import net.objectlab.kit.datecalc.common.HolidayHandlerType;
import net.objectlab.kit.datecalc.common.WorkingWeek;
import net.objectlab.kit.datecalc.jdk8.Jdk8WorkingWeek;
import net.objectlab.kit.datecalc.jdk8.LocalDateKitCalculatorsFactory;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.Collections;
import java.util.Set;

public final class BusinessDateTime
    implements Temporal, ChronoZonedDateTime<LocalDate>, Serializable {

  private static final long serialVersionUID = -4192005765453687618L;

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

    BusinessDateTimeCalculator<LocalDate> businessCalc =
        new BusinessDateTimeCalculator<LocalDate>();
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

    BusinessDateTimeCalculator<LocalDate> businessCalc =
        new BusinessDateTimeCalculator<LocalDate>();
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
    /*
    if (unit instanceof ChronoUnit) {
      ChronoUnit f = (ChronoUnit) unit;
      switch (f) {
        case NANOS: return plusNanos(amountToAdd);
        case MICROS: return plusDays(amountToAdd / MICROS_PER_DAY).plusNanos((amountToAdd % MICROS_PER_DAY) * 1000);
        case MILLIS: return plusDays(amountToAdd / MILLIS_PER_DAY).plusNanos((amountToAdd % MILLIS_PER_DAY) * 1000_000);
        case SECONDS: return plusSeconds(amountToAdd);
        case MINUTES: return plusMinutes(amountToAdd);
        case HOURS: return plusHours(amountToAdd);
        case HALF_DAYS: return plusDays(amountToAdd / 256).plusHours((amountToAdd % 256) * 12);  // no overflow (256 is multiple of 2)
      }
      return with(date.plus(amountToAdd, unit), time);
    }
    */
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
}
