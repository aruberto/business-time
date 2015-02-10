package com.github.aruberto.businesstime.joda;

import net.objectlab.kit.datecalc.common.WorkingWeek;

import java.io.Serializable;

class JodaWorkingWeek
    extends net.objectlab.kit.datecalc.joda.JodaWorkingWeek
    implements Serializable {

  public JodaWorkingWeek(WorkingWeek ww) {
    super(ww);
  }
}
