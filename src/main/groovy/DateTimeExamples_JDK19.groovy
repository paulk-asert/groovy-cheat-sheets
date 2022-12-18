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

import java.time.*
import static java.time.format.DateTimeFormatter.*
import static java.time.format.FormatStyle.SHORT
import static java.util.Locale.of as locale

var now = ZonedDateTime.now()
var columns = '%7s | %10s | %10s | %10s | %14s%n'
printf columns, 'locale', 'GDK', 'custom', 'local', 'both'
[locale('en', 'US'),
 locale('ro', 'RO'),
 locale('vi', 'VN')].each { locale ->
    Locale.default = locale
    var gdk = now.format('y-MM-dd')
    var custom = now.format(ofPattern('y-MM-dd'))
    var local = now.format(ofLocalizedDate(SHORT))
    var both = now.format(ofLocalizedPattern('yMM'))
    printf columns, locale, gdk, custom, local, both
}
/*
 locale |        GDK |     custom |      local |           both
  en_US | 2022-12-18 | 2022-12-18 |   12/18/22 |        12/2022
  ro_RO | 2022-12-18 | 2022-12-18 | 18.12.2022 |        12.2022
  vi_VN | 2022-12-18 | 2022-12-18 | 18/12/2022 | tháng 12, 2022
 */
