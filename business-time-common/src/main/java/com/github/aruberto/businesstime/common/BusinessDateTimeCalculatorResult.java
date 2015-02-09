package com.github.aruberto.businesstime.common;

/**
 * Represent date and time generated from business date time calculations
 *
 * @param <E> the type of date object
 */
public class BusinessDateTimeCalculatorResult<E> {

  protected E endDate;
  protected long nanosOfDay;

  public BusinessDateTimeCalculatorResult(E endDate, long nanosOfDay) {
    this.endDate = endDate;
    this.nanosOfDay = nanosOfDay;
  }

  public E getEndDate() {
    return endDate;
  }

  public long getNanosOfDay() {
    return nanosOfDay;
  }
}
