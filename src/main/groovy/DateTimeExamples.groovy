/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

import static java.time.temporal.ChronoField.DAY_OF_MONTH
import static java.time.temporal.ChronoField.HOUR_OF_DAY
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR
import static java.time.temporal.ChronoField.MONTH_OF_YEAR
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE
import static java.time.temporal.ChronoField.YEAR

// print current date and time
println LocalDateTime.now()
println Instant.now()
// 2022-09-17T08:56:31.319728
// 2022-09-16T22:56:31.326728600Z

// day of current month/year
println LocalDateTime.now().dayOfYear
println LocalDateTime.now().dayOfMonth
// 258
// 15

var now = LocalDate.now() // or LocalDateTime

// print/extract today's year, month and day
println SV(now.year, now.monthValue, now.dayOfMonth)
//now.year=2022, now.monthValue=9, now.dayOfMonth=17

// alternate
(Y, M, D) = now[YEAR, MONTH_OF_YEAR, DAY_OF_MONTH]
println "Today is $Y $M $D"

// other ways to format
println now.format("'Today is 'YYYY-MM-dd")
printf 'Today is %1$tY-%1$tm-%1$te%n', now
//Today is 2022-09-17
//Today is 2022-09-17


// print/extract current time components
now = LocalTime.now() // or LocalDateTime
println SV(now.hour, now.minute, now.second)
(H, M, S) = now[HOUR_OF_DAY, MINUTE_OF_HOUR, SECOND_OF_MINUTE]
printf 'The time is %02d:%02d:%02d\n', H, M, S

// alternatives to print time
println now.format("'The time is 'HH:mm:ss")
printf 'The time is %1$tH:%1$tM:%1$tS\n', now
//The time is 13:04:01
//The time is 13:04:01
//now.hour=13, now.minute=4, now.second=1
//H=13, M=4, S=1
//The time is 13:04:01

// one min after midnight
LocalTime.of(0, 1).with {
    println format('HH:mm')
    println format('hh:mm a')
    println format('K:mm a')
}
//00:01
//12:01 am
//0:01 am

// one min before midnight
LocalTime.of(23, 59).with {
    println format('HH:mm')
    println format('hh:mm a')
    println format('K:mm a')
}
//23:59
//11:59 pm
//11:59 pm

var breakfast = LocalTime.of(7, 30)
var lunch = LocalTime.parse('12:30')
assert lunch == LocalTime.parse('12:30.00 pm', 'hh:mm.ss a')
lunch.with { assert hour == 12 && minute == 30 }
var dinner = lunch.plusHours(7)
assert dinner == lunch.plus(7, ChronoUnit.HOURS)
assert Duration.between(lunch, dinner).toHours() == 7
assert breakfast.isBefore(lunch)   // Java API
assert lunch < dinner              // Groovy shorthand
assert lunch in breakfast..dinner
assert dinner.format('hh:mm a') == '07:30 pm'
assert dinner.format('k:mm') == '19:30'
assert dinner.format(FormatStyle.MEDIUM) == '7:30:00 pm'
assert dinner.timeString == '19:30:00'

var halloween22 = LocalDate.of(2022, 10, 31)
var halloween23 = LocalDate.parse('2023-Oct-31', 'yyyy-LLL-dd')
assert halloween22 == halloween23 - 365
assert halloween23 == halloween22.plusYears(1)
var melbourneCup22 = LocalDate.of(2022, 11, 1)
assert halloween22 < melbourneCup22
assert melbourneCup22 - halloween22 == 1
assert Period.between(halloween22, melbourneCup22).days == 1
assert ChronoUnit.DAYS.between(halloween22, melbourneCup22) == 1L
var days = []
halloween22.upto(melbourneCup22) {days << "$it.dayOfWeek" }
assert days == ['MONDAY', 'TUESDAY']
var hols = halloween22..melbourneCup22
assert hols.size() == 2

var melbourneCupLunch = LocalDateTime.of(2022, 11, 1, 12, 30)
assert melbourneCupLunch.timeString == '12:30:00'
assert melbourneCupLunch.dateString == '2022-11-01'
assert melbourneCupLunch.dateTimeString == '2022-11-01T12:30:00'
assert melbourneCupLunch.toLocalDate() == melbourneCup22
assert melbourneCupLunch.toLocalTime() == lunch
assert melbourneCupLunch == melbourneCup22 << lunch

var aet = ZoneId.of('Australia/Sydney')
assert aet.fullName == 'Australian Eastern Time' && aet.shortName == 'AET'
assert aet.offset == ZoneOffset.of('+11:00')
var melbCupBreakfastInAU = ZonedDateTime.of(melbourneCup22, breakfast, aet)
var melbCupBreakfast = LocalDateTime.of(melbourneCup22, breakfast)
assert melbCupBreakfastInAU == melbCupBreakfast << aet
var pst = ZoneId.of('America/Los_Angeles')
assert pst.fullName == 'Pacific Time' && pst.shortName == 'GMT-08:00'
var meanwhileInLA = melbCupBreakfastInAU.withZoneSameInstant(pst)
assert halloween22 == meanwhileInLA.toLocalDate()
assert aet.offset.hours - pst.offset.hours == 18
