package com.sdk.qrcodescanner.core

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.onFocusedBoundsChanged
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.runningFold
import kotlin.random.Random

class FocusedAreaEvent {
    var id: String by mutableStateOf("")
    var rect: Rect? by mutableStateOf(null)
    var spaceFromBottom: Float? by mutableStateOf(null)
}

class FocusedArea {
    var rect: Rect? = null
}

data class History<T>(val previous: T?, val current: T)

fun <T> Flow<T>.runningHistory(): Flow<History<T>> =
    runningFold(
        initial = null as (History<T>?),
        operation = { accumulator, new -> History(accumulator?.current, new) }
    ).filterNotNull()

data class ClickData(
    val unconsumed: Boolean = true,
    val offset: Offset = Offset.Zero
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImeAdaptiveColumn(
    scrollState: ScrollState = rememberScrollState(),
    scrollable: Boolean = true,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val screenHeight = LocalScreenSize.height
    val imeHeight by rememberUpdatedState(imeHeight())

    var clickData by remember { mutableStateOf(ClickData()) }
    val focusedAreaEvent = remember { FocusedAreaEvent() }
    val focusedArea = remember { FocusedArea() }
    LaunchedEffect(
        key1 = focusedAreaEvent.id
    ) {
        if (focusedAreaEvent.id.isNotEmpty()) {
            focusedAreaEvent.spaceFromBottom?.let { capturedBottom ->
                snapshotFlow { imeHeight }
                    .runningHistory()
                    .collectLatest { (prev, height) ->
                        val prevHeight = prev ?: 0
                        if (height > capturedBottom) {
                            if (prevHeight < capturedBottom) {
                                val difference = height - capturedBottom
                                scrollState.scrollBy(difference)
                            } else {
                                val difference = height - prevHeight
                                scrollState.scrollBy(difference.toFloat())
                            }
                        } else {
                            if (prevHeight > capturedBottom) {
                                val difference = prevHeight - capturedBottom
                                scrollState.scrollBy(-difference)
                            }
                        }
                    }
            }
        }
    }

    Column(
        modifier = modifier
            .onFocusedBoundsChanged { coordinates ->
                coordinates?.boundsInWindow()?.let {
                    focusedArea.rect = it
                    if (clickData.unconsumed && clickData.offset in it) {
                        focusedAreaEvent.run {
                            id = Random.nextInt().toString()
                            rect = it
                            spaceFromBottom = screenHeight - it.bottom
                        }
                        clickData = clickData.copy(unconsumed = false)
                    }
                }
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val event = awaitPointerEvent(PointerEventPass.Main)
                    if (event.type == PointerEventType.Press && imeHeight == 0) {
                        val offset = event.changes.firstOrNull()?.position ?: Offset.Zero
                        clickData = ClickData(
                            unconsumed = true,
                            offset = offset
                        )
                    }
                }
            }
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = horizontalPadding)
            .verticalScroll(scrollState, enabled = scrollable),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = content
    )
}

@Composable
fun imeHeight() = WindowInsets.ime.getBottom(LocalDensity.current)