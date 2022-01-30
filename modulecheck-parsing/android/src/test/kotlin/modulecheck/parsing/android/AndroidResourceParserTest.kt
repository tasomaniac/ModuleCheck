/*
 * Copyright (C) 2021-2022 Rick Busarow
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

package modulecheck.parsing.android

import modulecheck.testing.BaseTest
import modulecheck.utils.child
import modulecheck.utils.stripNonPrintableCharacters
import org.junit.jupiter.api.Test

internal class AndroidResourceParserTest : BaseTest() {

  @Test
  fun `what is happening`() {

    val text = """
      <?xml version="1.0" encoding="utf-8"?>
      <resources>
        <style name="NavigationBarItem.Badge" parent="None">
          <item name="android:width">48dp</item>
          <item name="android:height">48dp</item>
          <item name="baseStyle">@style/NavigationBarItem.Badge.Base</item>
        </style>

        <style name="NavigationBarItem.Badge.Base" parent="Widget.Noho.Notification.Base">
          <item name="android:left">2dp</item>
          <item name="android:top">3dp</item>
          <item name="android:width">40dp</item>
          <item name="android:height">40dp</item>
        </style>

        <style name="NavigationBarItem.Badge.WithoutDot.FilterOut" parent="Widget.Noho.Notification.FilterOut">
          <item name="android:left">25dp</item>
          <item name="android:top">1dp</item>
          <item name="android:width">18dp</item>
          <item name="android:height">18dp</item>
        </style>

        <style name="NavigationBarItem.Badge.FilterOut" parent="Widget.Noho.Notification.FilterOut">
          <item name="android:left">22dp</item>
          <item name="android:top">0dp</item>
          <item name="android:width">24dp</item>
          <item name="android:height">24dp</item>
        </style>
        ​
        <style name="NavigationBarItem.Badge.WithoutDot.Balloon" parent="Widget.Noho.Notification.Balloon">
          <item name="android:left">28dp</item>
          <item name="android:top">4dp</item>
          <item name="android:width">12dp</item>
          <item name="android:height">12dp</item>
        </style>
        ​
        <style name="NavigationBarItem.Badge.Balloon" parent="Widget.Noho.Notification.Balloon">
          <item name="android:left">25dp</item>
          <item name="android:top">2dp</item>
          <item name="android:width">18dp</item>
          <item name="android:height">18dp</item>
        </style>
        ​
        <style name="NavigationBarItem.Badge.Text" parent="None">
          <item name="android:textAppearance">@style/TextAppearance.NavigationBarItem.Badge.Balloon</item>
          <item name="android:x">33dp</item>
          <item name="android:y">10dp</item>
        </style>
        ​
        <style name="TextAppearance.NavigationBarItem.Badge.Balloon" parent="TextAppearance.Widget.Noho.Notification">
          <item name="android:textSize">12sp</item>
        </style>
      </resources>
    """.trimIndent()

    println(text.stripNonPrintableCharacters())

    testProjectDir.also { it.mkdirs() }
      .child("values.xml")
      .writeText(text)

    val parser = AndroidResourceParser().parseFile(testProjectDir)
  }
}
