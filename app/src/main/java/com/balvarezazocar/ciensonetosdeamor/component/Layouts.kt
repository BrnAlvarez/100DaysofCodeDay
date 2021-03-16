package com.balvarezazocar.ciensonetosdeamor.component

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.Dp

//Layouts
fun Modifier.firstBaseLineToTop(
    firstBaselineToTop: Dp
) = Modifier.layout{
        measurable, constraints ->
    val placeable = measurable.measure(constraints)
    // Check the composable has a first baseline
    check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
    val firstBaseline = placeable[FirstBaseline]
    // Height of the composable with padding - first baseline
    val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
    val height = placeable.height + placeableY
    layout(placeable.width,height){
        placeable.placeRelative(0, placeableY)
    }
}
@Composable
fun Customlayouts(
    modifier : Modifier = Modifier,
    content: @Composable ()-> Unit
){

}