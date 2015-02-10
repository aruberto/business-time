# business-time

A library that wraps the use of [ObjectLab Kit's date calculator](http://objectlabkit.sourceforge.net/) in a date time object.

The goal of this project is to provide a set of thread safe and immutable date time classes that perform operations in business time.

## How it works

Assume business hours are from 9am to 5pm.

1) When time added/subtracted doesn't overflow outside business hours, business datetime behaves just like regular datetime

`Thursday 3:30pm + 1 business hour = Thursday 4:30pm`

2) However when time added/subtracted does overflow outside business hours, non-business time is skipped over

`Thursday 3:30pm + 2 business hours = Friday 9:30am`

In second case, since there are only 1.5 business hours remaining on Thursday, the remaining 0.5 hours to add are continued on Friday.

## Projects

### [business-time-common](http://aruberto.github.io/business-time/javadoc/0.1.2/index.html?com/github/aruberto/businesstime/common/package-summary.html)

Common business time calculation methods. Dependency of following projects and not meant to be used directly.

### [business-time-joda](http://aruberto.github.io/business-time/javadoc/0.1.2/index.html?com/github/aruberto/businesstime/joda/package-summary.html)

Provides class BusinessDateTime which extends Joda's [AbstractDateTime](http://joda-time.sourceforge.net/apidocs/org/joda/time/base/AbstractDateTime.html).

#### Creating BusinessDateTime

The default constructor builds an instance of BusinessDateTime using the current system time, in the system time zone, 9-17 business hours, no holidays and Mon-Fri working week:

```java
new BusinessDateTime(); // current time
```

To create an instance of BusinessDateTime that is not current time, simply provide an instance of Joda's [DateTime](http://joda-time.sourceforge.net/apidocs/org/joda/time/DateTime.html):

```java
new BusinessDateTime(new DateTime(2014, 12, 11, 9, 15, 0, 0)); // Thurs Dec 11, 2014 @ 9:15 AM
```

If the time specified is outside business hours, BusinessDateTime becomes next possible business moment:

```java
new BusinessDateTime(new DateTime(2014, 12, 11, 8, 45, 0, 0)); // Thurs Dec 11, 2014 @ 9:00 AM since 8:45 AM is before business hours
new BusinessDateTime(new DateTime(2014, 12, 11, 17, 15, 0, 0)); // Fri Dec 12, 2014 @ 9:00 AM since 5:15 PM is after business hours
new BusinessDateTime(new DateTime(2014, 12, 13, 12, 0, 0, 0)); // Mon Dec 15, 2014 @ 9:00 AM since Dec 13 is weekend
```

If your business doesn't work standard 9-17 hours, provide business day start/end times as instances of Joda's [LocalTime](http://joda-time.sourceforge.net/apidocs/org/joda/time/LocalTime.html):

```java
new BusinessDateTime(new LocalTime(9, 30, 0, 0), new LocalTime(17, 30, 0, 0)); // current time with business hours of 9:30-17:30
```

To specify which days are holidays, provide a set of instances of Joda's [LocalDate](http://joda-time.sourceforge.net/apidocs/org/joda/time/LocalDate.html):

```java
Set<LocalDate> holidays = new HashSet<>();
holidays.add(new LocalDate(2014, 12, 25)); // Christmas
holidays.add(new LocalDate(2015, 1, 1)); // New Year
new BusinessDateTime(holidays); // current time with Christmas and New Year's as holidays
```

#### Manipulating BusinessDateTime

BusinessDateTime provides a set of plusX and minusX methods to add/subtract time where X is any of Millis/Seconds/Minutes/Hours/Days/Weeks/Months/Years. Since BusinessDateTime is immutable, all plusX/minusX methods return a new instance of BusinessDateTime:

```java
BusinessDateTime dateTime = new BusinessDateTime(new DateTime(2014, 12, 11, 15, 30, 0, 0)); // Thurs Dec 11, 2014 @ 3:30 PM
time.plusHours(1); // Thurs Dec 11, 2014 @ 4:30 PM
time.plusHours(2); // Fri Dec 12, 2014 @ 9:30 AM - remaining 30 min are added to next day
time.plusMinutes(30); // // Thurs Dec 11, 2014 @ 4:00 PM
time.minusMinutes(30); // // Thurs Dec 11, 2014 @ 3:00 PM
```

* note that Weeks/Months/Years methods simply delegate to Joda's plus/minus methods and perform no business time calculations

### [business-time-jdk8](http://aruberto.github.io/business-time/javadoc/0.1.2/index.html?com/github/aruberto/businesstime/jdk8/package-summary.html)

The default constructor builds an instance of BusinessDateTime using the current system time, in the system time zone, 9-17 business hours, no holidays and Mon-Fri working week:

```java
new BusinessDateTime(); // current time
```

To create an instance of BusinessDateTime that is not current time, simply provide an instance of JDK8's [ZonedDateTime](https://docs.oracle.com/javase/8/docs/api/java/time/ZonedDateTime.html):

```java
new BusinessDateTime(ZonedDateTime.of(2014, 12, 11, 9, 15, 0, 0, ZoneId.systemDefault())); // Thurs Dec 11, 2014 @ 9:15 AM
```

If the time specified is outside business hours, BusinessDateTime becomes next possible business moment:

```java
new BusinessDateTime(ZonedDateTime.of(2014, 12, 11, 8, 45, 0, 0, ZoneId.systemDefault())); // Thurs Dec 11, 2014 @ 9:00 AM since 8:45 AM is before business hours
new BusinessDateTime(ZonedDateTime.of(2014, 12, 11, 17, 15, 0, 0, ZoneId.systemDefault())); // Fri Dec 12, 2014 @ 9:00 AM since 5:15 PM is after business hours
new BusinessDateTime(ZonedDateTime.of(2014, 12, 13, 12, 0, 0, 0, ZoneId.systemDefault())); // Mon Dec 15, 2014 @ 9:00 AM since Dec 13 is weekend
```

If your business doesn't work standard 9-17 hours, provide business day start/end times as instances of JDK8's [LocalTime](https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html):

```java
new BusinessDateTime(LocalTime.of(9, 30, 0, 0), LocalTime.of(17, 30, 0, 0)); // current time with business hours of 9:30-17:30
```

To specify which days are holidays, provide a set of instances of JDK8's [LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html):

```java
Set<LocalDate> holidays = new HashSet<>();
holidays.add(LocalDate.of(2014, 12, 25)); // Christmas
holidays.add(LocalDate.of(2015, 1, 1)); // New Year
new BusinessDateTime(holidays); // current time with Christmas and New Year's as holidays
```

#### Manipulating BusinessDateTime

BusinessDateTime provides a set of plusX and minusX methods to add/subtract time where X is any of Nanos/Seconds/Minutes/Hours/Days/Weeks/Months/Years. Since BusinessDateTime is immutable, all plusX/minusX methods return a new instance of BusinessDateTime:

```java
BusinessDateTime dateTime = new BusinessDateTime(ZonedDateTime.of(2014, 12, 11, 15, 30, 0, 0, ZoneId.systemDefault())); // Thurs Dec 11, 2014 @ 3:30 PM
time.plusHours(1); // Thurs Dec 11, 2014 @ 4:30 PM
time.plusHours(2); // Fri Dec 12, 2014 @ 9:30 AM - remaining 30 min are added to next day
time.plusMinutes(30); // // Thurs Dec 11, 2014 @ 4:00 PM
time.minusMinutes(30); // // Thurs Dec 11, 2014 @ 3:00 PM
```

* note that Weeks/Months/Years methods simply delegate to JDK8's plus/minus methods and perform no business time calculations
