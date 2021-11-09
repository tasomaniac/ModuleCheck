/*
 * Copyright (C) 2021 Rick Busarow
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package modulecheck.specs.util

public val DEFAULT_GRADLE_VERSION: String = System
  .getProperty("modulecheck.gradleVersion", "7.3-rc-1")
  /*
  * The GitHub Actions test matrix parses "7.0" into an Int and passes in a command line argument of "7".
  * That version doesn't resolve.  So if the String doesn't contain a period, just append ".0"
  */
  .let { prop ->
    if (prop.contains('.')) prop else "$prop.0"
  }
public val DEFAULT_KOTLIN_VERSION: String =
  System.getProperty("modulecheck.kotlinVersion", "1.5.30")
public val DEFAULT_AGP_VERSION: String =
  System.getProperty("modulecheck.agpVersion", "7.0.3")
