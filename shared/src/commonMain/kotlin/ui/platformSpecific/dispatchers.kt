package ui.platformSpecific

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Suppress("no_actual_for_expect")
expect val Dispatchers.IO : CoroutineDispatcher