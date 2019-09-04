package dev.jtsalva.cloudmare

import java.lang.Exception

interface SwipeRefreshable {
    fun render(): Any = throw Exception("Stub!")

    fun onSwipeRefresh(): Any = render()
}