package com.aruberto.businesstime.joda;

import net.objectlab.kit.datecalc.common.DateCalculator;
import net.objectlab.kit.datecalc.common.DefaultHolidayCalendar;
import net.objectlab.kit.datecalc.common.HolidayCalendar;
import net.objectlab.kit.datecalc.common.HolidayHandlerType;
import net.objectlab.kit.datecalc.common.WorkingWeek;
import net.objectlab.kit.datecalc.joda.JodaWorkingWeek;
import net.objectlab.kit.datecalc.joda.LocalDateKitCalculatorsFactory;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
public class BusinessDateTime extends AbstractDateTime implements ReadableDateTime, Serializable {

  private static final long serialVersionUID = -4950683087656123855L;

  private static final LocalTime DEFAULT_BUSINESS_DAY_START = new LocalTime(9, 0, 0, 0);
  private static final LocalTime DEFAULT_BUSINESS_DAY_END = new LocalTime(17, 0, 0, 0);
  private static final String HOLIDAY_KEY = "CUSTOM";

  private final DateTime dateTime;
  private final Set<LocalDate> holidays;
  private final LocalTime dayStartTime;
  private final LocalTime dayEndTime;
  private final WorkingWeek workingWeek;

  /**
   * Constructs an instance of BusinessDateTime at point of time of {@code dateTime}
   * with business day starting at {@code dayStartTime}, business day ending at {@code dayEndTime},
   * holiday list of {@code holidays} and working week of {@code workingWeek}
   *
   * @param dateTime  date time, null means current time with default time zone
   * @param dayStartTime  business day start time, null means 9am
   * @param dayEndTime  business day end time, null means 5pm
   * @param holidays  holidays, null means no holidays
   * @param workingWeek  the working week, null means Monday to Friday
   */
  public BusinessDateTime(DateTime dateTime,
                          LocalTime dayStartTime,
                          LocalTime dayEndTime,
                          Set<LocalDate> holidays,
                          WorkingWeek workingWeek) {
    if (dateTime == null) {
      throw new IllegalArgumentException("date time cannot be null");
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
    if (dayEndTime.isEqual(dayStartTime) ||
        dayEndTime.isBefore(dayStartTime)) {
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
   * @param dateTime  date time, null means current time with default time zone
   * @param dayStartTime  business day start time, null means 9am
   * @param dayEndTime  business day end time, null means 5pm
   * @param holidays  holidays, null means no holidays
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
   * @param dateTime  date time, null means current time with default time zone
   * @param holidays  holidays, null means no holidays
   */
  public BusinessDateTime(DateTime dateTime, Set<LocalDate> holidays) {
    this(dateTime, null, null, holidays, null);
  }

  /**
   * Constructs an instance of BusinessDateTime at point of time of {@code dateTime}
   * with business day starting at {@code dayStartTime}, business day ending at {@code dayEndTime},
   * no holidays and working week of Monday to Friday
   *
   * @param dateTime  date time, null means current time with default time zone
   * @param dayStartTime  business day start time, null means 9am
   * @param dayEndTime  business day end time, null means 5pm
   */
  public BusinessDateTime(DateTime dateTime, LocalTime dayStartTime, LocalTime dayEndTime) {
    this(dateTime, dayStartTime, dayEndTime, null, null);
  }

  /**
   * Constructs an instance of BusinessDateTime at point of time of {@code dateTime}
   * with business day starting at 9am, business day ending at 5pm,
   * no holidays and working week of Monday to Friday
   *
   * @param dateTime  date time, null means current time with default time zone
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

  protected static DateTime plusBusinessDays(DateTime startDateTime,
                                             LocalTime dayStartTime,
                                             LocalTime dayEndTime,
                                             Set<LocalDate> holidays,
                                             WorkingWeek workingWeek,
                                             long days) {
    LocalDateKitCalculatorsFactory calculatorFactory = new LocalDateKitCalculatorsFactory();
    HolidayCalendar<LocalDate> holidayCalendar = new DefaultHolidayCalendar<LocalDate>(holidays);
    calculatorFactory.registerHolidays(HOLIDAY_KEY, holidayCalendar);

    LocalDate startDate = startDateTime.toLocalDate();
    LocalTime startTime = startDateTime.toLocalTime();
    DateTimeZone zone = startDateTime.getZone();

    // When outside business hours, assume current time is previous business moment
    LocalTime endTime = startTime;
    if (endTime.isAfter(dayEndTime) ||
        endTime.isBefore(dayStartTime) ||
        holidays.contains(startDate) ||
        !workingWeek.isWorkingDay(startDate.toDate())) {
      // If current day is working day and current time is before business start of day,
      // need to roll back to previous day
      if (endTime.isBefore(dayStartTime) &&
          !holidays.contains(startDate) &&
          workingWeek.isWorkingDay(startDate.toDate())) {
        days--;
      }

      // Previous business moment is always at business end of day
      endTime = dayEndTime;
    }

    String holidayHandlerType = days > 0 ? HolidayHandlerType.FORWARD : HolidayHandlerType.BACKWARD;

    DateCalculator<LocalDate> calc =
        calculatorFactory.getDateCalculator(HOLIDAY_KEY, holidayHandlerType);
    calc.setWorkingWeek(workingWeek);
    calc.setStartDate(startDate);
    if (days != 0) {
      // hopefully never move by more than 2^31-1 days
      calc = calc.moveByBusinessDays((int) days);
    }

    return calc.getCurrentBusinessDate().toDateTime(endTime, zone);
  }

  /**
   * Gets the chronology of the datetime.
   *
   * @return  the Chronology that the datetime is using
   */
  @Override
  public Chronology getChronology() {
    return dateTime.getChronology();
  }

  /**
   * Gets the milliseconds of the datetime instant from the Java epoch
   * of 1970-01-01T00:00:00Z.
   *
   * @return  the number of milliseconds since 1970-01-01T00:00:00Z
   */
  @Override
  public long getMillis() {
    return plusBusinessDays(dateTime,
                            dayStartTime,
                            dayEndTime,
                            holidays,
                            workingWeek,
                            0).getMillis();
  }

  /**
   * Returns a copy of this business datetime plus {@code millis} millis.
   *
   * @param millis  the amount of millis to add, may be negative
   * @return  the new business datetime plus the increased millis
   */
  public BusinessDateTime plusMillis(long millis) {
    if (millis == 0L) {
      return this;
    } else {
      long millisPerDay = dayEndTime.getMillisOfDay() - dayStartTime.getMillisOfDay();

      // Step 1 - Add provided millis to current millis elapsed in the day (max of millisPerDay)
      long totalMillis = millis;
      DateTime startOfDay = dateTime.withMillisOfDay(dayStartTime.getMillisOfDay());
      if (!holidays.contains(dateTime.toLocalDate()) &&
          workingWeek.isWorkingDay(dateTime.toDate()) &&
          dateTime.isAfter(startOfDay)) {
        totalMillis += Math.min(dateTime.getMillis() - startOfDay.getMillis(), millisPerDay);
      }

      // Step 2 - Calculate how many business days and millis to move forward
      long businessDays = totalMillis / millisPerDay;
      long remainingMillis = totalMillis % millisPerDay;

      // Step 3 - Move forward business days then just add remaining millis
      DateTime newDateTime = plusBusinessDays(startOfDay,
                                              dayStartTime,
                                              dayEndTime,
                                              holidays,
                                              workingWeek,
                                              businessDays).plus(remainingMillis);
      return new BusinessDateTime(newDateTime, dayStartTime, dayEndTime, holidays, workingWeek);
    }
  }
}
