package dev.jtsalva.cloudmare

import android.view.Menu
import android.view.MenuItem

class MenuButtonInitializer {
    private fun Int.toMenuItem(menu: Menu): MenuItem = menu.findItem(this)

    private lateinit var itemsToInflate: IntArray

    fun onInflateSetVisible(vararg buttonIds: Int) {
        itemsToInflate = buttonIds
    }

    fun inflate(menu: Menu) {
        if (::itemsToInflate.isInitialized) itemsToInflate.forEach { buttonId ->
            buttonId.toMenuItem(menu).isVisible = true
        }
    }
}