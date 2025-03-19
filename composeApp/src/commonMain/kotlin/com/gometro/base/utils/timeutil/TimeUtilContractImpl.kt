package com.gometro.base.utils.timeutil

import com.gometro.base.featurecontracts.TimeUtilsContract
import com.gometro.constants.AppConstants
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.until

class TimeUtilContractImpl(): TimeUtilsContract {
    override fun getDateFromEpochTime(timeStamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timeStamp)
        val actualDate = instant.toLocalDateTime(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID)).date
        val customFormat = LocalDate.Format {
            dayOfMonth(); chars("/"); monthNumber(); chars("/"); year()
        }
        return actualDate.format(customFormat)
    }

    override fun getEpochTimeFromDate(dateString: String): Long {
        val dateFormat = LocalDate.Format {
            dayOfMonth()
            chars("/")
            monthNumber()
            chars("/")
            year()
        }

        val current = Clock.System
            .now()
            .toLocalDateTime(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID))

        return LocalDate.parse(dateString, dateFormat)
            .atTime(hour = current.hour, minute = current.minute)
            .toInstant(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID))
            .toEpochMilliseconds()
    }

    override fun getDateInMediumFormat(timeStamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timeStamp)
        val actualDate = instant.toLocalDateTime(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID)).date
        val customFormat = LocalDate.Format {
            dayOfMonth(); char(' '); monthName(MonthNames.ENGLISH_ABBREVIATED); char(' '); year()
        }
        return actualDate.format(customFormat)
    }

    override fun getDateAndAMPMTime(timeStamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timeStamp)
        val actualDate = instant.toLocalDateTime(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID))
        val customFormat = LocalDateTime.Format {
            amPmHour(); char(':'); minute(); char(' ');  amPmMarker(am = "AM", pm = "PM"); chars(", "); dayOfMonth(); char(' '); monthName(MonthNames.ENGLISH_ABBREVIATED); char(' '); year()
        }
        return actualDate.format(customFormat)
    }

    override fun getDateAndTimeWithoutYear(timeStamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timeStamp)
        val actualDate = instant.toLocalDateTime(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID))
        val customFormat = LocalDateTime.Format {
            amPmHour(); char(':'); minute(); char(' ');  amPmMarker(am = "AM", pm = "PM"); chars(", "); dayOfMonth(); char(' '); monthName(MonthNames.ENGLISH_ABBREVIATED);
        }
        return actualDate.format(customFormat)
    }

    override fun getDayAndDateWithoutYear(timeStamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timeStamp)
        val actualDate = instant.toLocalDateTime(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID))
        val customFormat = LocalDateTime.Format {
            dayOfWeek(DayOfWeekNames.ENGLISH_FULL); chars(", "); dayOfMonth(); char(' '); monthName(MonthNames.ENGLISH_ABBREVIATED);
        }
        return actualDate.format(customFormat)
    }

    override fun getTimeFromSeconds(seconds: Long): String {
        val instant = Instant.fromEpochMilliseconds(seconds*1000L)
        val actualDate = instant.toLocalDateTime(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID)).time
        val customFormat = LocalTime.Format {
            amPmHour(); char(':'); minute(); char(' ');  amPmMarker(am = "AM", pm = "PM")
        }
        return actualDate.format(customFormat)
    }

    override fun getTimeFromMillis(millis: Long): String {
        val instant = Instant.fromEpochMilliseconds(millis)
        val actualDate = instant.toLocalDateTime(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID)).time
        val customFormat = LocalTime.Format {
            amPmHour(); char(':'); minute(); char(' ');  amPmMarker(am = "AM", pm = "PM")
        }
        return actualDate.format(customFormat)
    }

    override fun getDateAnd24hrTime(timeStamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timeStamp)
        val actualDate = instant.toLocalDateTime(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID))
        val customFormat = LocalDateTime.Format {
            hour(); char(':'); minute(); char(':'); second(); chars(", "); dayOfMonth(); char(' '); monthName(MonthNames.ENGLISH_ABBREVIATED); char(' '); year()
        }
        return actualDate.format(customFormat)
    }

    override fun getSecondsElapsedSinceFirstOfCurrentMonth(timeStamp: Long): Long {
        val currentInstant = Clock.System.now()
        val currentLocalDateTime = currentInstant.toLocalDateTime(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID))
        val currentDate = currentLocalDateTime.date
        val startOfMonth = LocalDateTime(currentDate.year, currentDate.monthNumber, 1, 0, 0).toInstant(TimeZone.currentSystemDefault())
        val duration = currentLocalDateTime.toInstant(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID)) - startOfMonth
        return duration.inWholeSeconds
    }

    override fun hasXDaysPassedTillNow(dateTime: Long, numberOfDays: Int): Boolean {
        return Instant.fromEpochMilliseconds(dateTime).until(Clock.System.now(), DateTimeUnit.HOUR) > numberOfDays*24
    }

    override fun getCurrentWeekDayName(): String {
        val instant = Clock.System.now()
        return instant.toLocalDateTime(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID)).dayOfWeek.name
    }

    override fun getCurrentWeekDayNumber(): Int {
        val instant = Clock.System.now()
        return instant.toLocalDateTime(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID)).dayOfWeek.ordinal+1
    }

    override fun getSecondsSinceTodayMidnight(): Long {
        val currentInstant = Clock.System.now()
        val currentLocalDateTime = currentInstant.toLocalDateTime(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID))
        val currentDate = currentLocalDateTime.date
        val startOfDay = LocalDateTime(currentDate.year, currentDate.monthNumber, currentDate.dayOfMonth, 0, 0).toInstant(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID))
        val duration = currentLocalDateTime.toInstant(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID)) - startOfDay
        return duration.inWholeSeconds
    }

    override fun getHourOfDay(time: Long): Int {
        val instant = Instant.fromEpochMilliseconds(time)
        return instant.toLocalDateTime(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID)).time.hour
    }

    override fun getTimeInFullFormat(millis: Long): String {
        val instant = Instant.fromEpochMilliseconds(millis)
        val time = instant.toLocalDateTime(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID)).time
        val hour = time.hour
        val minutes = time.minute
        val seconds = time.second
        return if (hour != 0) {
            "$hour hr $minutes min $seconds sec"
        } else if (minutes != 0) {
            "$minutes min $seconds sec"
        } else {
            "$seconds sec"
        }
    }

    override fun getGMTTimeUsingSecondsFromMidnight(seconds: Long): String {
        val currentInstant = Clock.System.now()
        val currentDateTime = currentInstant.toLocalDateTime(TimeZone.of("UTC")).date
        val midnight = LocalDateTime(currentDateTime.year, currentDateTime.monthNumber, currentDateTime.dayOfMonth, 0, 0).toInstant(TimeZone.of("GMT"))
        val requiredTimeOfDay = midnight.plus(seconds.toDuration(DurationUnit.SECONDS)).toLocalDateTime(TimeZone.of("GMT")).time
        val customFormat = LocalTime.Format {
            amPmHour(); char(':'); minute(); char(' ');  amPmMarker(am = "AM", pm = "PM")
        }
        return requiredTimeOfDay.format(customFormat)
    }

    override fun getSecondsBetweenMidnightToXDaysFromNow(numberOfDays: Int): Long {
        val currentInstant = Clock.System.now()
        val currentDate = currentInstant.toLocalDateTime(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID)).date
        val startOfToday = LocalDateTime(currentDate.year, currentDate.monthNumber, currentDate.dayOfMonth, 0, 0).toInstant(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID))
        val startOfXthDay = LocalDateTime(currentDate.year, currentDate.monthNumber, currentDate.dayOfMonth + numberOfDays, 0, 0).toInstant(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID))
        val duration = startOfXthDay - startOfToday
        return duration.inWholeSeconds
    }

    override fun getTimeSinceMidnight(timezoneId: String?): Int {
        val currentInstant = Clock.System.now()
        val timezoneToUse = timezoneId ?: AppConstants.DEFAULT_TIMEZONE_ID
        val currentTime = currentInstant.toLocalDateTime(TimeZone.of(timezoneToUse)).time
        return with(currentTime) {
            (hour * 60 + minute) * 60 + second
        }
    }

    override fun getCurrentDay(timezoneId: String, selectedTimeStamp: Long?): String {
        val time = selectedTimeStamp ?: Clock.System.now().toEpochMilliseconds()
        val selectedInstant = Instant.fromEpochMilliseconds(time)
        val selectedDate = selectedInstant.toLocalDateTime(TimeZone.of(timezoneId)).date

        return selectedDate.dayOfWeek.name.lowercase()
    }

    override fun getWeekIndexForTime(timezoneId: String, selectedTimeStamp: Long?): Int {
        val time = selectedTimeStamp ?: Clock.System.now().toEpochMilliseconds()
        val selectedInstant = Instant.fromEpochMilliseconds(time)
        val selectedDate = selectedInstant.toLocalDateTime(TimeZone.of(timezoneId)).date

        return selectedDate.dayOfWeek.isoDayNumber
    }

    override fun getInMinutes(timeInMs: Long): String {
        val sec = (timeInMs / 1000).toInt()
        var min = (timeInMs / 1000 / 60).toInt()
        return if (min == 0) {
            "$sec sec"
        } else if (min > 60) {
            val hour = min / 60
            min %= 60
            if (min == 0) {
                hour.toString() + "hr "
            } else {
                hour.toString() + "hr " + min + "min"
            }
        } else {
            min = (timeInMs * 1.0 / 1000 / 60).roundToInt()
            "$min min"

        }
    }

    override fun resolveTimeInMillis(
        year: Int?,
        monthNumber: Int?,
        dayOfMonth: Int?,
        hour: Int?,
        minute: Int?,
        second: Int?,
        nanosecond: Int?
    ): Long {
        val currentInstance = Clock.System.now()
        val currentDateTime = currentInstance.toLocalDateTime(TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID))
        val expectedDateTime = LocalDateTime(
            year = year ?: currentDateTime.year,
            monthNumber = monthNumber ?: currentDateTime.monthNumber,
            dayOfMonth = dayOfMonth ?: currentDateTime.dayOfMonth,
            hour = hour ?: currentDateTime.hour,
            minute = minute ?: currentDateTime.minute,
            second = second ?: currentDateTime.second
        )

        return expectedDateTime.toInstant(
            TimeZone.of(AppConstants.DEFAULT_TIMEZONE_ID)
        ).toEpochMilliseconds()
    }

    override fun getEtaStringInMinsFromSecs(etaInSec: Long): String {
        if (etaInSec < 60) return "1 min"
        return getInMinutes(etaInSec * 1000)
    }
}