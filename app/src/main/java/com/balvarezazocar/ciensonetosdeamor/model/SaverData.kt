package com.balvarezazocar.ciensonetosdeamor.model

import androidx.compose.foundation.lazy.LazyListState
import kotlinx.coroutines.CoroutineScope

class SaverData{
    var listSize:Int
        set(value) {
            this.listSize = value
        }
    get() = this.listSize
    // We save the scrolling position with this state
    var scrollState:LazyListState
        set(value) {
            this.scrollState = value
        }
    get() = this.scrollState
    // We save the coroutine scope where our animated scroll will be executed
    var coroutineScope:CoroutineScope
    set(value) {
        this.coroutineScope = value
    }
    get() = this.coroutineScope

}