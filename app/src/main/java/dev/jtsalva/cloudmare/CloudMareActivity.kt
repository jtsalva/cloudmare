package dev.jtsalva.cloudmare

import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class CloudMareActivity : AppCompatActivity() {

    protected open val TAG = "CloudMareActivity"

    protected var showSettingsMenuButton = false
    protected var showSaveMenuButton = false
    protected var showDeleteMenuButton = false
    protected var showAddMenuButton = false

    protected fun setLayout(contentViewResId: Int) {
        setContentView(contentViewResId)
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    protected fun <T : ViewDataBinding> setLayoutBinding(contentViewResId: Int): T {
        val binding: T = DataBindingUtil.setContentView(this, contentViewResId)
        setSupportActionBar(findViewById(R.id.toolbar))

        return binding
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_settings -> {
            Log.d(TAG, "Settings clicked")

            startActivity(
                Intent(this, AppSettingsActivity::class.java)
            )

            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        with(menu) {
            menuInflater.inflate(R.menu.main_menu, this)

            if (showSettingsMenuButton) findItem(R.id.action_settings).isVisible = true

            if (showSaveMenuButton) findItem(R.id.action_save).isVisible = true

            if (showDeleteMenuButton) findItem(R.id.action_delete).isVisible = true

            if (showAddMenuButton) findItem(R.id.action_add).isVisible = true
        }

        return true
    }

    protected fun setToolbarTitle(title: String) = supportActionBar?.setTitle(title) ?: Log.e(TAG, "Can't set title")
    protected fun setToolbarTitle(resId: Int) = supportActionBar?.setTitle(resId) ?: Log.e(TAG, "Can't set title")
}
