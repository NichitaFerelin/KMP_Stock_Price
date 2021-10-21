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

package com.ferelin.utilsTests

import com.ferelin.domain.utils.AppDate
import org.junit.Assert
import org.junit.Test

class AppDateTest {

    @Test
    fun toTimeMillisForRequest() {
        val result1 = AppDate.toTimeMillisForRequest(1000000L)
        Assert.assertEquals(1000L, result1)

        val result2 = AppDate.toTimeMillisForRequest(123456789L)
        Assert.assertEquals(123456L, result2)

        val result3 = AppDate.toTimeMillisForRequest(10L)
        Assert.assertEquals(0L, result3)

        val result4 = AppDate.toTimeMillisForRequest(9999999L)
        Assert.assertEquals(9999, result4)
    }
}