package dev.jtsalva.cloudmare

interface SwipeRefreshable {
    fun render(): Any

    fun onSwipeRefresh(): Any = render()
}
