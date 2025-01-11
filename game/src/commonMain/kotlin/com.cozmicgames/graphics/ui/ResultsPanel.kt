package com.cozmicgames.graphics.ui

import com.cozmicgames.utils.FightResults
import kotlin.time.Duration

class ResultsPanel(private val results: FightResults) {
    enum class ResultState {
        NONE,
        RETRY_EASY,
        RETRY_NORMAL,
        RETRY_HARD,
        RETURN
    }

    var x = 0.0f
    var y = 0.0f
    var width = 0.0f
    var height = 0.0f

    fun renderAndGetResultState(delta: Duration): ResultState {


        return ResultState.NONE
    }
}