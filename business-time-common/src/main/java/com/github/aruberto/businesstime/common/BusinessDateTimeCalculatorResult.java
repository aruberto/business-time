package com.github.aruberto.businesstime.common;

/**
 * Represent date and time generated from business date time calculations
 *
 * @param <E> the type of date object
 */
public class BusinessDateTimeCalculatorResult<E> {
  protected E endDate;
  protected int millisOfDay;

  public BusinessDateTimeCalculatorResult(E endDate, int millisOfDay) {
    this.endDate = endDate;
    this.millisOfDay = millisOfDay;
  }

  public E getEndDate() {
    return endDate;
  }

  public int getMillisOfDay() {
    return millisOfDay;
  }
}
