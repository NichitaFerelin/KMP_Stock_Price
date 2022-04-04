package com.ferelin.core.coroutine

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

open class DispatchersProvider {
  open val Main: CoroutineContext = Dispatchers.Main
  open val IO: CoroutineContext = Dispatchers.IO
  open val Default: CoroutineContext = Dispatchers.Default
}