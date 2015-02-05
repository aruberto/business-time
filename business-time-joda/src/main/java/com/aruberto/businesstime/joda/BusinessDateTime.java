package com.aruberto.businesstime.joda;

import net.objectlab.kit.datecalc.common.DateCalculator;
import net.objectlab.kit.datecalc.common.DefaultHolidayCalendar;
import net.objectlab.kit.datecalc.common.HolidayCalendar;
import net.objectlab.kit.datecalc.common.HolidayHandlerType;
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
 *   BusinessDateTime represents an exact point on the time-line, where time-line:
 *   <ul>
 *     <li>is limited to the precision of milliseconds</li>
 *     <li>only includes points that fall between start and end of business day</li>
 *   </ul>
 * </p>
 *
 * @author Antonio Ruberto
 * @since 0.1
 */
public class BusinessDateTime extends AbstractDateTime implements ReadableDateTime, Serializable {

  private static final long serialVersionUID = -4950683087656123855L;

  private static final LocalTime BUSINESS_DAY_START = new LocalTime(9, 0, 0, 0);
  private static final LocalTime BUSINESS_DAY_END = new LocalTime(17, 0, 0, 0);
  private static final int MILLIS_PER_SECOND = 1000;
  private static final int MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
  private static final int MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
  private static final int MILLIS_PER_DAY =
      BUSINESS_DAY_END.getMillisOfDay() - BUSINESS_DAY_START.getMillisOfDay();
  private static final String HOLIDAY_KEY = "CUSTOM";

  private DateTime dateTime;
  private LocalDateKitCalculatorsFactory calculatorFactory;

  public BusinessDateTime(long instant, DateTimeZone zone, Set<LocalDate> holidays) {
    this(new DateTime(instant, zone), holidays);
  }

  public BusinessDateTime(DateTimeZone zone, Set<LocalDate> holidays) {
    this(new DateTime(System.currentTimeMillis(), zone), holidays);
  }

  public BusinessDateTime(long instant, DateTimeZone zone) {
    this(instant, zone, Collections.<LocalDate>emptySet());
  }

  public BusinessDateTime(DateTimeZone zone) {
    this(zone, Collections.<LocalDate>emptySet());
  }

  public BusinessDateTime(long instant, Set<LocalDate> holidays) {
    this(new DateTime(instant), holidays);
  }

  public BusinessDateTime(Set<LocalDate> holidays) {
    this(new DateTime(System.currentTimeMillis()), holidays);
  }

  public BusinessDateTime(long instant) {
    this(instant, Collections.<LocalDate>emptySet());
  }

  public BusinessDateTime() {
    this(System.currentTimeMillis());
  }

  public BusinessDateTime(DateTime dateTime) {
    this(dateTime, Collections.<LocalDate>emptySet());
  }

  public BusinessDateTime(DateTime dateTime, Set<LocalDate> holidays) {
    this.calculatorFactory = new LocalDateKitCalculatorsFactory();
    this.calculatorFactory
        .registerHolidays(HOLIDAY_KEY, new DefaultHolidayCalendar<LocalDate>(holidays));
    this.dateTime = getAdjustedDateTime(dateTime);
  }

  private BusinessDateTime(DateTime dateTime, LocalDateKitCalculatorsFactory calculatorFactory) {
    this.calculatorFactory = calculatorFactory;
    this.dateTime = getAdjustedDateTime(dateTime);
  }

  @Override
  public Chronology getChronology() {
    return dateTime.getChronology();
  }

  @Override
  public long getMillis() {
    return plusBusinessDays(dateTime, 0, HolidayHandlerType.BACKWARD).getMillis();
  }

  /**
   * Return a BusinessDateTime that is a certain number of business days into future
   *
   * @param days Number of business days to move forward
   * @return New BusinessDateTime <code>days</code> business days in the future
   */
  public BusinessDateTime plusBusinessDays(long days) {
    if (days == 0) {
      return this;
    } else {
      long millis = days * MILLIS_PER_DAY;
      return plusBusinessMillis(millis);
    }
  }

  /**
   * Return a BusinessDateTime that is a certain number of business days into future
   *
   * @param days Number of business days to move forward
   * @return New BusinessDateTime <code>days</code> business days in the future
   */
  public BusinessDateTime plusBusinessDays(double days) {
    long millis = Math.round(days * MILLIS_PER_DAY);
    return plusBusinessMillis(millis);
  }

  /**
   * Return a BusinessDateTime that is a certain number of business hours into future
   *
   * @param hours Number of business hours to move forward
   * @return New BusinessDateTime <code>hours</code> business hours in the future
   */
  public BusinessDateTime plusBusinessHours(long hours) {
    if (hours == 0) {
      return this;
    } else {
      long millis = hours * MILLIS_PER_HOUR;
      return plusBusinessMillis(millis);
    }
  }

  /**
   * Return a BusinessDateTime that is a certain number of business hours into future
   *
   * @param hours Number of business hours to move forward
   * @return New BusinessDateTime <code>hours</code> business hours in the future
   */
  public BusinessDateTime plusBusinessHours(double hours) {
    long millis = Math.round(hours * MILLIS_PER_HOUR);
    return plusBusinessMillis(millis);
  }

  /**
   * Return a BusinessDateTime that is a certain number of business minutes into future
   *
   * @param minutes Number of business minutes to move forward
   * @return New BusinessDateTime <code>minutes</code> business minutes in the future
   */
  public BusinessDateTime plusBusinessMinutes(long minutes) {
    if (minutes == 0) {
      return this;
    } else {
      long millis = minutes * MILLIS_PER_MINUTE;
      return plusBusinessMillis(millis);
    }
  }

  /**
   * Return a BusinessDateTime that is a certain number of business minutes into future
   *
   * @param minutes Number of business minutes to move forward
   * @return New BusinessDateTime <code>minutes</code> business minutes in the future
   */
  public BusinessDateTime plusBusinessMinutes(double minutes) {
    long millis = Math.round(minutes * MILLIS_PER_MINUTE);
    return plusBusinessMillis(millis);
  }

  /**
   * Return a BusinessDateTime that is a certain number of business seconds into future
   *
   * @param seconds Number of business seconds to move forward
   * @return New BusinessDateTime <code>seconds</code> business seconds in the future
   */
  public BusinessDateTime plusBusinessSeconds(long seconds) {
    if (seconds == 0) {
      return this;
    } else {
      long millis = seconds * MILLIS_PER_SECOND;
      return plusBusinessMillis(millis);
    }
  }

  /**
   * Return a BusinessDateTime that is a certain number of business seconds into future
   *
   * @param seconds Number of business seconds to move forward
   * @return New BusinessDateTime <code>seconds</code> business seconds in the future
   */
  public BusinessDateTime plusBusinessSeconds(double seconds) {
    long millis = Math.round(seconds * MILLIS_PER_SECOND);
    return plusBusinessMillis(millis);
  }

  /**
   * Return a BusinessDateTime that is a certain number of business millis into future
   *
   * @param millis Number of business millis to move forward
   * @return New BusinessDateTime <code>minutes</code> business millis in the future
   */
  public BusinessDateTime plusBusinessMillis(long millis) {
    if (millis == 0L) {
      return this;
    } else {
      // Step 1 - Add provided millis to current millis elapsed in the day
      long totalMillis = millis;
      DateTime startOfDay = dateTime.withMillisOfDay(BUSINESS_DAY_START.getMillisOfDay());
      HolidayCalendar<LocalDate> holidays = calculatorFactory.getHolidayCalendar(HOLIDAY_KEY);
      if (!holidays.isHoliday(dateTime.toLocalDate()) && dateTime.isAfter(startOfDay)) {
        totalMillis += dateTime.getMillis() - startOfDay.getMillis();
      }

      // Step 2 - Calculate how many business days and millis to move forward
      long businessDays = totalMillis / MILLIS_PER_DAY;
      long remainingMillis = totalMillis % MILLIS_PER_DAY;

      // Step 3 - Move forward business days then just add remaining millis
      String holidayHandlerType =
          totalMillis > 0 ? HolidayHandlerType.FORWARD : HolidayHandlerType.BACKWARD;
      DateTime newDateTime =
          plusBusinessDays(startOfDay, businessDays, holidayHandlerType).plus(remainingMillis);
      return new BusinessDateTime(newDateTime, calculatorFactory);
    }
  }

  public BusinessDateTime withMillis(long var1) {
    return var1 == dateTime.getMillis() ? this :
           new BusinessDateTime(new DateTime(var1, this.getChronology()), calculatorFactory);
  }

  public BusinessDateTime withEra(int var1) {
    return this.withMillis(this.getChronology().era().set(this.getMillis(), var1));
  }

  public BusinessDateTime withCenturyOfEra(int var1) {
    return this.withMillis(this.getChronology().centuryOfEra().set(this.getMillis(), var1));
  }

  public BusinessDateTime withYearOfEra(int var1) {
    return this.withMillis(this.getChronology().yearOfEra().set(this.getMillis(), var1));
  }

  public BusinessDateTime withYearOfCentury(int var1) {
    return this.withMillis(this.getChronology().yearOfCentury().set(this.getMillis(), var1));
  }

  public BusinessDateTime withYear(int var1) {
    return this.withMillis(this.getChronology().year().set(this.getMillis(), var1));
  }

  public BusinessDateTime withWeekyear(int var1) {
    return this.withMillis(this.getChronology().weekyear().set(this.getMillis(), var1));
  }

  public BusinessDateTime withMonthOfYear(int var1) {
    return this.withMillis(this.getChronology().monthOfYear().set(this.getMillis(), var1));
  }

  public BusinessDateTime withWeekOfWeekyear(int var1) {
    return this.withMillis(this.getChronology().weekOfWeekyear().set(this.getMillis(), var1));
  }

  public BusinessDateTime withDayOfYear(int var1) {
    return this.withMillis(this.getChronology().dayOfYear().set(this.getMillis(), var1));
  }

  public BusinessDateTime withDayOfMonth(int var1) {
    return this.withMillis(this.getChronology().dayOfMonth().set(this.getMillis(), var1));
  }

  public BusinessDateTime withDayOfWeek(int var1) {
    return this.withMillis(this.getChronology().dayOfWeek().set(this.getMillis(), var1));
  }

  public BusinessDateTime withHourOfDay(int var1) {
    return this.withMillis(this.getChronology().hourOfDay().set(this.getMillis(), var1));
  }

  public BusinessDateTime withMinuteOfHour(int var1) {
    return this.withMillis(this.getChronology().minuteOfHour().set(this.getMillis(), var1));
  }

  public BusinessDateTime withSecondOfMinute(int var1) {
    return this.withMillis(this.getChronology().secondOfMinute().set(this.getMillis(), var1));
  }

  public BusinessDateTime withMillisOfSecond(int var1) {
    return this.withMillis(this.getChronology().millisOfSecond().set(this.getMillis(), var1));
  }

  public BusinessDateTime withMillisOfDay(int var1) {
    return this.withMillis(this.getChronology().millisOfDay().set(this.getMillis(), var1));
  }

  private DateTime getAdjustedDateTime(DateTime dateTime) {
    DateTime newDateTime = dateTime;
    if (newDateTime.toLocalTime().isBefore(BUSINESS_DAY_START)) {
      newDateTime = plusBusinessDays(newDateTime, -1, HolidayHandlerType.BACKWARD)
          .withMillisOfDay(BUSINESS_DAY_END.getMillisOfDay());
    } else if (newDateTime.toLocalTime().isAfter(BUSINESS_DAY_END)) {
      newDateTime = newDateTime.withMillisOfDay(BUSINESS_DAY_END.getMillisOfDay());
    }
    return newDateTime;
  }

  private DateTime plusBusinessDays(DateTime startTime, long days, String holidayHandlerType) {
    HolidayCalendar<LocalDate> holidays = calculatorFactory.getHolidayCalendar(HOLIDAY_KEY);
    LocalDate startDate = startTime.toLocalDate();

    DateCalculator<LocalDate> calc =
        calculatorFactory.getDateCalculator(HOLIDAY_KEY, holidayHandlerType);
    calc.setStartDate(startDate);
    if (days != 0) {
      // hopefully never move by more than 2^31-1 days
      calc = calc.moveByBusinessDays((int) days);
    }

    LocalDate newDate = calc.getCurrentBusinessDate();
    LocalTime endTime = startTime.toLocalTime();
    if (holidays.isHoliday(startDate)) {
      if (newDate.isAfter(startDate)) {
        endTime = BUSINESS_DAY_START;
      } else {
        endTime = BUSINESS_DAY_END;
      }
    }

    return calc.getCurrentBusinessDate().toDateTime(endTime, startTime.getZone());
  }
}
