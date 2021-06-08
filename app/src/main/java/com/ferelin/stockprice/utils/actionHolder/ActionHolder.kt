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

package com.ferelin.stockprice.utils.actionHolder

/**
 * [ActionHolder] can hold an action and key.
 * Directly used to repeat actions at real-time database (that was invoked at local database).
 * Example:
 *      Local: Remove A1, Add A2, Remove A3 -> [ ActionHolder(Remove, a1) , ActionHolder(Add, A2)...]
 *      Than at real-time database -> list.forEach { when(action) -> add or remove from real-time db }
 */
class ActionHolder<T>(val actionType: ActionType, val key: T)