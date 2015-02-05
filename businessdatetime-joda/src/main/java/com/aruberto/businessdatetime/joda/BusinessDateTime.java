package com.aruberto.businessdatetime.joda;

import org.joda.time.Chronology;
import org.joda.time.ReadableDateTime;
import org.joda.time.base.AbstractDateTime;

import java.io.Serializable;

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

  public long getMillis() {
    return 0;
  }

  public Chronology getChronology() {
    return null;
  }
}
