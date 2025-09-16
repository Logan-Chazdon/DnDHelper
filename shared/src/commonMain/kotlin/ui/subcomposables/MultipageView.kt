package ui.subcomposables

import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import kotlinx.coroutines.launch

private enum class DraggablePosition {
    One,
    Two,
    Three,
    Four,
    Five,
    Six
}

private val pageSpacerSize = 25.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MultipageView(
    modifier: Modifier = Modifier,
    vararg pages: @Composable (Modifier) -> Unit,
) {
    // Don`t render the switcher if only one page is passed.
    if(pages.size == 1)  {
        pages[0].invoke(modifier)
    } else {
        val scope = rememberCoroutineScope()
        val density = LocalDensity.current

        val size = remember {
            mutableStateOf(0.dp)
        }


        val draggableState = remember {
            AnchoredDraggableState(
                initialValue = DraggablePosition.One,
                positionalThreshold = { distance: Float -> distance * 0.35f },
                velocityThreshold = { with(density) { 120.dp.toPx() } },
                snapAnimationSpec = FloatTweenSpec(),
                decayAnimationSpec = splineBasedDecay(density),
            )
        }

        LaunchedEffect(size.value) {
            val anchors = with(density) {
                DraggableAnchors {
                    pages.indices.forEach { index ->
                        DraggablePosition.entries[index] at (-1 * index.toFloat() * (size.value + if (index != 0) pageSpacerSize else 0.dp)).toPx()
                    }
                }
            }

            draggableState.updateAnchors(anchors)
        }



        Column(
            Modifier.onPlaced {
                with(density) {
                    size.value = it.size.width.toDp()
                }
            }.then(modifier),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                for (i in pages.indices) {
                    Pip(
                        isActive = i == draggableState.settledValue.ordinal,
                        onClick = {
                            scope.launch {
                                draggableState.animateTo(DraggablePosition.entries[i])
                            }
                        }
                    )
                }
            }

            Row(
                modifier = Modifier.
                    // Required for the pages to actually draw as anchoredDraggable() does not give additional space.
                horizontalScroll(rememberScrollState(), false)
                    .anchoredDraggable(
                        state = draggableState,
                        orientation = Orientation.Horizontal
                    ).offset { IntOffset(x = draggableState.offset.toInt(), y = 0) }

            ) {
                pages.forEachIndexed { index, page ->
                    page.invoke(
                        Modifier
                            .fillMaxHeight()
                            .width(size.value)
                    )

                    if (index != pages.size - 1) {
                        Spacer(
                            Modifier.width(pageSpacerSize)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun Pip(
    isActive: Boolean,
    onClick: () -> Unit
) {
    val activeColor = MaterialTheme.colors.secondary
    val nonActiveColor = MaterialTheme.colors.onBackground

    Canvas(
        modifier = Modifier
            .padding(5.dp)
            .size(15.dp)
            .clickable { onClick() }
    ) {
        drawCircle(
            color = if (isActive) activeColor else nonActiveColor,
            center = this.center,
            style = Fill
        )
    }
}

