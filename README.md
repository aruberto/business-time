# business-time

A library that wraps the use of [ObjectLab Kit's date calculator](http://objectlabkit.sourceforge.net/) in a date time object.

The goal of this project is to provide a set of thread safe and immutable date time classes that perform operations in business time.

## How it works

Assume business hours are from 9am to 5pm.

1) When time added/subtracted doesn't overflow outside business hours, business datetime behaves just like regular datetime

    Thursday 3:30pm + 1 business hour = Thursday 4:30pm

2) However when time added/subtracted does overflow outside business hours, non-business time is skipped over

    Thursday 3:30pm + 2 business hours = Friday 9:30am

In second case, since there are only 1.5 business hours remaining on Thursday, the remaining 0.5 hours to add are continued on Friday.

## Projects

### business-time-joda

Provides BusinessDateTime class which extends [org.joda.time.base.AbstractDateTime](http://joda-time.sourceforge.net/apidocs/org/joda/time/base/AbstractDateTime.html) and adds following additional operations:

#### BusinessDateTime plusMillis(long millis)
    Return a new BusinessDateTime that is current business time plus the additional business millis specified

### business-time-jsr310

Sorry, still in the works ...
