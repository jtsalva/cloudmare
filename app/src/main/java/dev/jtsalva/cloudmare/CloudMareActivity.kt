package dev.jtsalva.cloudmare

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.launch as coLaunch

abstract class CloudMareActivity : AppCompatActivity(), CoroutineScope {

    protected open val TAG = "CloudMareActivity"

    protected lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    protected var showUserActivityMenuButton = false
    protected var showSaveMenuButton = false
    protected var showDeleteMenuButton = false
    protected var showAddMenuButton = false

    val dialog: Dialog get() = Dialog(this)

    fun launch(block: suspend () -> Unit): Job = coLaunch { block() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        job = Job()
    }

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
    }

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
        R.id.action_user_activity -> {
            Log.d(TAG, "Settings clicked")
            startActivity(UserActivity::class.java)
                true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        with(menu) {
            menuInflater.inflate(R.menu.main_menu, this)

            if (showUserActivityMenuButton) findItem(R.id.action_user_activity).isVisible = true

            if (showSaveMenuButton) findItem(R.id.action_save).isVisible = true

            if (showDeleteMenuButton) findItem(R.id.action_delete).isVisible = true

            if (showAddMenuButton) findItem(R.id.action_add).isVisible = true
        }

        return true
    }

    protected fun setToolbarTitle(title: String) = supportActionBar?.setTitle(title) ?: Log.e(TAG, "Can't set title")
    protected fun setToolbarTitle(resId: Int) = supportActionBar?.setTitle(resId) ?: Log.e(TAG, "Can't set title")
}
