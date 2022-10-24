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

import static java.util.Calendar.DAY_OF_MONTH
import static java.util.Calendar.DAY_OF_YEAR
import static java.util.Calendar.HOUR_OF_DAY
import static java.util.Calendar.MONTH
import static java.util.Calendar.MINUTE
import static java.util.Calendar.SECOND
import static java.util.Calendar.YEAR

// print current date and time
println new Date()
println Calendar.instance.time
// Thu Sep 15 16:57:56 AEST 2022
// Thu Sep 15 16:57:56 AEST 2022

// day of current month/year
println Calendar.instance[DAY_OF_YEAR]
println Calendar.instance[DAY_OF_MONTH]
// 258
// 15

var now = Calendar.instance

// print/extract today's year, month and day
(_E, Y, M, _WY, _WM, D) = now
println "Today is $Y ${M+1} $D"
println SV(Y, M, D)

(Y, M, D) = now[YEAR, MONTH, DAY_OF_MONTH]
println SV(Y, M, D)
// Y=2022, M=8, D=17
// Y=2022, M=8, D=17

// other ways to format
println now.format("'Today is 'YYYY-MM-dd")
printf 'Today is %1$tY-%1$tm-%1$te%n', now

// print/extract current time components
println now.format("'The time is 'HH:mm:ss")
printf 'The time is %1$tH:%1$tM:%1$tS\n', now

(H, M, S) = now[HOUR_OF_DAY, MINUTE, SECOND]
println SV(H, M, S)
printf 'The time is %02d:%02d:%02d\n', H, M, S
// The time is 12:08:50
// The time is 12:08:50
// Y=2022, M=8, D=17
// The time is 12:08:50

// one min after midnight
Calendar.instance.with {
    clear()
    set(MINUTE, 1)
    println format('HH:mm')
    println format('hh:mm a')
    println format('K:mm a')
}
//00:01
//12:01 am
//0:01 am

// one min before midnight
Calendar.instance.with {
    clear()
    set(hourOfDay: 23, minute: 59)
    println format('HH:mm')
    println format('hh:mm a')
    println format('K:mm a')
}
//23:59
//11:59 pm
//11:59 pm

var breakfast = Date.parse('hh:mm', '07:30')
var lunch = Calendar.instance.tap {
    clear()
    set(hourOfDay: 12, minute: 30)
}
assert lunch[HOUR_OF_DAY, MINUTE] == [12, 30]
var dinner = lunch.clone().tap { it[HOUR_OF_DAY] += 7 }
assert dinner == lunch.copyWith(hourOfDay: 19)
assert dinner[HOUR_OF_DAY] - lunch[HOUR_OF_DAY] == 7
assert breakfast.before(lunch.time)   // Java API
assert lunch < dinner                 // Groovy shorthand
assert dinner.format('hh:mm a') == '07:30 pm'
assert dinner.format('k:mm') == '19:30'
assert dinner.time.timeString == '7:30:00 pm'

var halloween22 = Date.parse('yyyy-MMM-dd', '2022-Oct-31')
var halloween23 = Date.parse('dd/MM/yyyy', '31/10/2023')
assert halloween22 + 365 == halloween23
var melbourneCup22 = new GregorianCalendar(2022, 10, 1).time
assert halloween22 < melbourneCup22
assert melbourneCup22 - halloween22 == 1
assert melbourneCup22[DAY_OF_YEAR] - halloween22[DAY_OF_YEAR] == 1
assert melbourneCup22 == halloween22.copyWith(month: 10, date: 1)
var days = []
halloween22.upto(melbourneCup22) {days << it.format('EEEEE') }
assert days == ['Monday', 'Tuesday']
var hols = halloween22..melbourneCup22
assert hols.size() == 2

var melbourneCupLunch = new GregorianCalendar(2022, 10, 1, 12, 30).time
assert melbourneCupLunch.timeString == '12:30:00 pm'              // Locale specific
assert melbourneCupLunch.dateString == '1/11/22'                  // Locale specific
assert melbourneCupLunch.dateTimeString == '1/11/22, 12:30:00 pm' // Locale specific
assert melbourneCupLunch.clearTime() == melbourneCup22

var aet = TimeZone.getTimeZone('Australia/Sydney')
assert aet.displayName == 'Australian Eastern Standard Time'
assert aet.observesDaylightTime()
var melbourneCupBreakfast = new GregorianCalendar(aet).tap {
    set(year: 2022, month: 10, date: 1, hourOfDay: 7, minute: 30)
}
var pst = TimeZone.getTimeZone('America/Los_Angeles')
assert pst.displayName == 'Pacific Standard Time'
var meanwhileInLA = new GregorianCalendar(pst).tap {
    setTimeInMillis(melbourneCupBreakfast.timeInMillis)
}
assert meanwhileInLA.time.format('MMM dd', pst) == halloween22.format('MMM dd')
assert aet.rawOffset / 3600000 - pst.rawOffset / 3600000 == 18
