package com.eygraber.virtue.utils

import kotlin.coroutines.cancellation.CancellationException

@Suppress("RedundantSuspendModifier")
public suspend inline fun <R> runCatchingCoroutine(block: () -> R): Result<R> = try {
  Result.success(block())
}
catch(c: CancellationException) {
  throw c
}
catch(e: Throwable) {
  Result.failure(e)
}
