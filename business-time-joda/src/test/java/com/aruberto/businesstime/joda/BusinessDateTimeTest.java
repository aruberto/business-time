package com.aruberto.businesstime.joda;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BusinessDateTimeTest {

  @Test
  public void plusBusinessMillis_Add3Millis_DateTime3MillisLate() throws Exception {
    DateTime start = new DateTime(2014, 12, 11, 12, 0, 0, 0);
    DateTime expected = new DateTime(2014, 12, 11, 12, 0, 0, 3);
    BusinessDateTime businessStart = new BusinessDateTime(start);

    assertEquals("Thursday 12:00:00:0000 plus 3 millis should return Thursday 12:00:00:0003",
                 expected,
                 businessStart.plusBusinessMillis(3).toDateTime());
  }
}