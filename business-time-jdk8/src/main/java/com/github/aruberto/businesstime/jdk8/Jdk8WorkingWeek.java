package com.github.aruberto.businesstime.jdk8;

import net.objectlab.kit.datecalc.common.WorkingWeek;

import java.io.Serializable;

public class Jdk8WorkingWeek
    extends net.objectlab.kit.datecalc.jdk8.Jdk8WorkingWeek
    implements Serializable {

  public Jdk8WorkingWeek(WorkingWeek ww) {
    super(ww);
  }
}
