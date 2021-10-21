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

import com.ferelin.shared.toStrPrice
import org.junit.Assert
import org.junit.Test

class UtilsTest {

    @Test
    fun toStrPrice() {
        val result1 = 5000.0.toStrPrice()
        Assert.assertEquals("$5 000", result1)

        val result2 = 51345.25.toStrPrice()
        Assert.assertEquals("$51 345.25", result2)

        val result3 = 1000000.0.toStrPrice()
        Assert.assertEquals("$1 000 000", result3)

        val result4 = 13.21.toStrPrice()
        Assert.assertEquals("$13.21", result4)

        val result5 = 0.0.toStrPrice()
        Assert.assertEquals("$0", result5)

        val result6 = 10000.54.toStrPrice()
        Assert.assertEquals("$10 000.54", result6)

        val result7 = 99999.999999.toStrPrice()
        Assert.assertEquals("$99 999.99", result7)

        val result8 = 1000.0.toStrPrice()
        Assert.assertEquals("$1 000", result8)

        val result9 = 12345.678912.toStrPrice()
        Assert.assertEquals("$12 345.67", result9)

        val result10 = 100.0.toStrPrice()
        Assert.assertEquals("$100", result10)

        val result11 = 1.0.toStrPrice()
        Assert.assertEquals("$1", result11)
    }
}