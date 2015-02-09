package com.github.aruberto.businesstime.common;

import net.objectlab.kit.datecalc.common.DateCalculator;

/**
 * Common date calculation methods
 *
 * @param <E> the type of date object
 */
public class BusinessDateTimeCalculator<E> {

  /**
   * Use {@code calc} to move {@code startDate} at time {@code startTimeMillisOfDay} by
   * {@code unitsToMove} units.
   *
   * @param startDate starting date
   * @param startTimeNanosOfDay starting time as nanos since midnight
   * @param unitsToMove the amount of units to move, may be negative or positive
   * @param unitFactor the number of nanos in each unit
   * @param dayStartNanosOfDay business day start time as nanos since midnight
   * @param dayEndNanosOfDay business day end time as nanos since midnight
   * @param calc {@link net.objectlab.kit.datecalc.common.DateCalculator} to use to adjust days
   * @return date and nanos of day after moving by specified millis
   */
  public BusinessDateTimeCalculatorResult<E> move(E startDate,
                                                  long startTimeNanosOfDay,
                                                  long unitsToMove,
                                                  long unitFactor,
                                                  long dayStartNanosOfDay,
                                                  long dayEndNanosOfDay,
                                                  DateCalculator<E> calc) {
    long unitsPerDay = (dayEndNanosOfDay - dayStartNanosOfDay) / unitFactor;
    boolean moveForward = unitsToMove >= 0;
    long unitsToMoveAbs = Math.abs(unitsToMove);
    int days = (int) ((unitsToMoveAbs - 1) / unitsPerDay);
    long unitsRemaining = (unitsToMoveAbs - 1) % unitsPerDay + 1;

    return moveByDaysAndNanos(startDate,
                              startTimeNanosOfDay,
                              moveForward,
                              days,
                              unitsRemaining * unitFactor,
                              dayStartNanosOfDay,
                              dayEndNanosOfDay,
                              calc);
  }

  /**
   * Use {@code calc} to move {@code startDate} at time {@code startTimeMillisOfDay} by
   * {@code unitsToMove} units.
   *
   * @param startDate starting date
   * @param startTimeNanosOfDay starting time as nanos since midnight
   * @param days the amount of days to move, may be negative or positive
   * @param dayStartNanosOfDay business day start time as nanos since midnight
   * @param dayEndNanosOfDay business day end time as nanos since midnight
   * @param calc {@link net.objectlab.kit.datecalc.common.DateCalculator} to use to adjust days
   * @return date and nanos of day after moving by specified millis
   */
  public BusinessDateTimeCalculatorResult<E> moveDays(E startDate,
                                                      long startTimeNanosOfDay,
                                                      int days,
                                                      long dayStartNanosOfDay,
                                                      long dayEndNanosOfDay,
                                                      DateCalculator<E> calc) {
    return moveByDaysAndNanos(startDate,
                              startTimeNanosOfDay,
                              days >= 0,
                              Math.abs(days),
                              0,
                              dayStartNanosOfDay,
                              dayEndNanosOfDay,
                              calc);
  }

  /**
   * Use {@code calc} to move {@code startDate} at time {@code startTimeNanosOfDay} by
   * {@code daysToMove} days and {@code nanosToMove} nanos.
   *
   * @param startDate starting date
   * @param startTimeNanosOfDay starting time as nanos since midnight
   * @param moveForward whether adding or subtracting time
   * @param daysToMove days to move, must be greater than or equal to 0
   * @param nanosToMove the amount of nanos to move, must be greater than or equal to 0
   * @param dayStartNanosOfDay business day start time as nanos since midnight
   * @param dayEndNanosOfDay business day end time as nanos since midnight
   * @param calc {@link net.objectlab.kit.datecalc.common.DateCalculator} to use to adjust days
   * @return date and nanos of day after moving by specified days and nanos
   */
  private BusinessDateTimeCalculatorResult<E> moveByDaysAndNanos(E startDate,
                                                                 long startTimeNanosOfDay,
                                                                 boolean moveForward,
                                                                 int daysToMove,
                                                                 long nanosToMove,
                                                                 long dayStartNanosOfDay,
                                                                 long dayEndNanosOfDay,
                                                                 DateCalculator<E> calc) {
    long nanosPerDay = dayEndNanosOfDay - dayStartNanosOfDay;
    boolean isWorkingDay = !calc.isNonWorkingDay(startDate);
    boolean isStartBeforeBusinessDay = startTimeNanosOfDay < dayStartNanosOfDay;
    boolean isStartAfterBusinessDay = startTimeNanosOfDay > dayEndNanosOfDay;

    int days = daysToMove;
    long nanosOfDay = 0;
    long totalNanos = nanosToMove;

    if (isWorkingDay) {
      if (moveForward) {
        // Adjust days/millis to be in reference to current business day at business hours start
        if (isStartAfterBusinessDay) {
          // If start time is after business hours, then current business date time
          // is actually next day at business hour start
          days++;
        } else {
          // Add millis elapsed in current business day
          totalNanos += Math.max(0, startTimeNanosOfDay - dayStartNanosOfDay);
        }
      } else {
        // Adjust days/millis to be in reference to current business day at business hours end
        if (isStartBeforeBusinessDay) {
          // If start time is before business hours, then current business date time
          // is actually next day at business start time
          days++;
        } else {
          // Subtract millis remaining in day
          totalNanos += Math.max(0, dayEndNanosOfDay - startTimeNanosOfDay);
        }
      }
    }

    // Calculate the number of days and millisOfDay to move.
    // millisOfDay should end up being <= millisPerDay.
    days += (int) ((totalNanos - 1) / nanosPerDay);
    nanosOfDay += (totalNanos - 1) % nanosPerDay + 1;

    if (!moveForward) {
      days = -days;
      nanosOfDay = -nanosOfDay;
    }

    // Use DateCalculator to calculate new business day
    calc.setStartDate(startDate);
    if (days != 0) {
      calc = calc.moveByBusinessDays(days);
    }
    E endDate = calc.getCurrentBusinessDate();

    // When millisOfDay is positive, time was added and reference time is business hour start
    // When millisOfDay is negative, time was subtracted and reference time is business hour end
    long endTimeNanosOfDay = nanosOfDay >= 0 ? dayStartNanosOfDay : dayEndNanosOfDay;
    endTimeNanosOfDay += nanosOfDay;

    return new BusinessDateTimeCalculatorResult<E>(endDate, endTimeNanosOfDay);
  }
}
