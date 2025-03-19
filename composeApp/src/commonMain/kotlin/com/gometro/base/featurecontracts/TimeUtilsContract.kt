package com.gometro.base.featurecontracts

interface TimeUtilsContract {

    /** Get date in dd/MM/yyyy format from epoch time in milliseconds.*/
    fun getDateFromEpochTime(timeStamp: Long): String

    /** Get epoch time in millis for date in dd/MM/yyyy format.*/
    fun getEpochTimeFromDate(dateString: String): Long

    /** Get date in dd MMM yyyy format from epoch time in milliseconds.*/
    fun getDateInMediumFormat(timeStamp: Long): String

    /** Get date in dd MMM yyyy format and time in hh:mm a format from epoch time in milliseconds.*/
    fun getDateAndAMPMTime(timeStamp: Long): String

    /** Get date in dd MMM format and time in hh:mm a format from epoch time in milliseconds.*/
    fun getDateAndTimeWithoutYear(timeStamp: Long): String

    /** Get day in short-hand and date in dd MMM format from epoch time in milliseconds.*/
    fun getDayAndDateWithoutYear(timeStamp: Long): String

    /** Get time in hh:mm a format from seconds.*/
    fun getTimeFromSeconds(seconds: Long): String

    /** Get time in hh:mm a format from milli-seconds.*/
    fun getTimeFromMillis(millis: Long): String

    /** Get time in hh:mm:ss, dd MMM yyyy format from epoch time-stamp.*/
    fun getDateAnd24hrTime(timeStamp: Long): String

    /** Get seconds elapsed from first of current month to the current moment */
    fun getSecondsElapsedSinceFirstOfCurrentMonth(timeStamp: Long): Long

    /** Returns a Boolean, returns true if difference in days between given date and current date is greater than the given number of days. */
    fun hasXDaysPassedTillNow(dateTime: Long, numberOfDays: Int): Boolean

    /** Returns current weekday name. Example - Mon, ..., Sun. */
    fun getCurrentWeekDayName(): String

    /** Returns current weekday number. Example - 1(Mon), ..., 7(Sun). */
    fun getCurrentWeekDayNumber(): Int

    /** Returns seconds passed from today's midnight */
    fun getSecondsSinceTodayMidnight(): Long

    /** Returns the hour of the current day, in 24hrs format. */
    fun getHourOfDay(time: Long): Int

    /** Returns time in full string format from milli-seconds. Example - 1 hr 1 min 1 sec. */
    fun getTimeInFullFormat(millis: Long): String

    /** Returns time in [hh:mm a] format from seconds passed since today's midnight with GMT timezone. */
    fun getGMTTimeUsingSecondsFromMidnight(seconds: Long): String

    /** Returns time in seconds from today till given number of days from today's midnight. */
    fun getSecondsBetweenMidnightToXDaysFromNow(numberOfDays: Int): Long


    fun getTimeSinceMidnight(timezoneId: String? = null): Int

    /** Returns day name for selected time stamp based on passed timezone id
     * @param selectedTimeStamp when null currentTimeStamp will be considered for calculation
     */
    fun getCurrentDay(
        timezoneId: String,
        selectedTimeStamp: Long? = null
    ): String

    /** Returns ISO 8601 day index (1 is monday, 7 is sunday) for selected time stamp based on passed timezone id
     * @param selectedTimeStamp when null currentTimeStamp will be considered for calculation
     */
    fun getWeekIndexForTime(
        timezoneId: String,
        selectedTimeStamp: Long? = null
    ): Int

    fun getInMinutes(timeInMs: Long): String

    fun resolveTimeInMillis(
        year: Int? = null,
        monthNumber: Int? = null,
        dayOfMonth: Int? = null,
        hour: Int? = null,
        minute: Int? = null,
        second: Int? = null,
        nanosecond: Int? = null
    ): Long

    fun getEtaStringInMinsFromSecs(etaInSec: Long): String

    companion object {
        const val HALF_HOUR_MILLIS = 30 * 60 * 1000L
        const val ONE_DAY_MILLIS = 86400000L
        const val ONE_DAY_MINUTES = 1440L
        const val TIMESTAMP_MILLIS_JAN_2022_START = 1640995200000L
        const val TEN_YEARS_MILLIS = 315360000000L

        const val HOURS_12_IN_MILLIS = 12 * 60 * 60 * 1000L
    }
}