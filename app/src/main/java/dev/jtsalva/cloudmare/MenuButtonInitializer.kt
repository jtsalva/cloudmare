package dev.jtsalva.cloudmare

import android.view.Menu
import android.view.MenuItem

class MenuButtonInitializer {
    private fun Int.toMenuItem(menu: Menu): MenuItem = menu.findItem(this)

    companion object {
        const val USER_ACTIVITY_ACTION = R.id.action_user_activity
        const val SORT_BY_ACTION = R.id.action_sort_by
        const val SAVE_ACTION = R.id.action_save
        const val DELETE_ACTION = R.id.action_delete
        const val ADD_ACTION = R.id.action_add
    }

    private val itemsToInflate = mutableListOf<Int>()

    fun onInflateSetVisible(vararg buttonIds: Int) =
        itemsToInflate.addAll(buttonIds.asList())

    fun inflate(menu: Menu) = itemsToInflate.forEach { buttonId ->
        buttonId.toMenuItem(menu).isVisible = true
    }

}