/*
 * Copyright 2021 Leah Nichita
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

package com.ferelin

import com.ferelin.remote.utils.buildProfitString
import com.ferelin.remote.utils.toBasicMillisTime
import org.junit.Assert
import org.junit.Test

class UtilsTest {

    @Test
    fun toBasicMillisTime() {
        val actual_1 = 10L.toBasicMillisTime()
        val expected_1 = 10000L
        Assert.assertEquals(expected_1, actual_1)

        val actual_2 = 123L.toBasicMillisTime()
        val expected_2 = 123000L
        Assert.assertEquals(expected_2, actual_2)

        val actual_3 = 104789L.toBasicMillisTime()
        val expected_3 = 104789000L
        Assert.assertEquals(expected_3, actual_3)

        val actual_4 = 9876543L.toBasicMillisTime()
        val expected_4 = 9876543000L
        Assert.assertEquals(expected_4, actual_4)
    }

    @Test
    fun buildProfitString() {
        val actual = buildProfitString(100.0, 50.0)
        val expected = "+$50.0 (50,0%)"
        Assert.assertEquals(expected, actual)
    }
}