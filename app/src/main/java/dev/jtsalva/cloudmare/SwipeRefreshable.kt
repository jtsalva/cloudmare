package dev.jtsalva.cloudmare

interface SwipeRefreshable {
    fun render(): Any = throw Exception("Stub!")

    fun onSwipeRefresh(): Any = render()
}