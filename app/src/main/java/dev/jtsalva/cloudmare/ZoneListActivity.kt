package dev.jtsalva.cloudmare

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jtsalva.cloudmare.adapter.ZoneListAdapter
import dev.jtsalva.cloudmare.api.IdTranslator
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.api.zone.ZoneRequest
import kotlinx.android.synthetic.main.activity_zone_list.*
import timber.log.Timber

class ZoneListActivity : CloudMareActivity(), SwipeRefreshable {

    private lateinit var zones: MutableList<Zone>

    private val initialized get() = zone_list.adapter is ZoneListAdapter

    private val paginationListener by PaginationListener.lazy(this, R.id.zone_list) { pageNumber ->
        ZoneRequest(this@ZoneListActivity).launch {
            list(pageNumber).run {
                if (failure || result == null)
                    dialog.error(message = firstErrorMessage)
                else if (result.isNotEmpty()) result.let { nextPage ->
                    val positionStart = zones.size
                    zones.addAll(nextPage)
                    zone_list.adapter?.notifyItemRangeChanged(positionStart, zones.size)
                } else reachedLastPage()
            }

            finishedFetchingPage()
        }
    }

    companion object {
        private const val CONTACT_URL = "https://cloudmare.jtsalva.dev/contact"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_user_activity -> {
            startActivity(UserActivity::class)
            true
        }

        R.id.action_theme -> {
            selectTheme()
            true
        }

        R.id.action_contact -> {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(CONTACT_URL)
                )
            )
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        menuButtonInitializer.onInflateSetVisible(
            R.id.action_contact,
            R.id.action_theme,
            R.id.action_user_activity
        )

        setLayout(R.layout.activity_zone_list)
        setToolbarTitle(R.string.title_zone_list_activity)
    }

    override fun onStart() {
        super.onStart()

        updateTheme()

        if (Auth.isNotSet) startActivity(UserActivity::class)
        else launch {
            val validityResponse = Auth.testValidity(this)

            if (validityResponse.failure) {
                if (validityResponse.isLocalError)
                    dialog.error(
                        message = validityResponse.firstErrorMessage,
                        onAcknowledge = ::onStart
                    )
                else dialog.error(
                    title = "Invalid authorization",
                    message = "Re-enter your credentials",
                    positive = "Go to login"
                ) { startActivity(UserActivity::class) }
            }
            else render()
        }
    }

    override fun onSwipeRefresh() {
        super.onSwipeRefresh()
        if (initialized) paginationListener.resetPage()
    }

    override fun render() = ZoneRequest(this).launch {
        val response = list()
        if (response.failure || response.result == null)
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)

        else (response.result as MutableList<Zone>).also { result ->
            if (!initialized) zone_list.apply {
                zones = result

                adapter = ZoneListAdapter(this@ZoneListActivity, zones)
                layoutManager = LinearLayoutManager(this@ZoneListActivity)
                addOnScrollListener(paginationListener)
            } else if (result != zones) zones.apply {
                paginationListener.resetPage()
                zone_list.layoutManager!!.scrollToPosition(0)

                clear()
                addAll(result)

                zone_list.adapter!!.notifyDataSetChanged()
            }

            showNonFoundMessage = result.isEmpty()
        }

        showProgressBar = false

        swipeRefreshLayout.isRefreshing = false
    }

    private fun updateTheme() {
        Settings.load(this)

        if (AppCompatDelegate.getDefaultNightMode() != Settings.theme)
            AppCompatDelegate.setDefaultNightMode(Settings.theme)
    }

    private fun selectTheme() {
        Settings.load(this)

        val supportsNightFollowSystem = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

        val themeTranslator =
            IdTranslator(
                if (supportsNightFollowSystem)
                    mapOf(
                        AppCompatDelegate.MODE_NIGHT_NO to getString(R.string.settings_theme_light),
                        AppCompatDelegate.MODE_NIGHT_YES to getString(R.string.settings_theme_dark),
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM to getString(R.string.settings_theme_system_default)
                    )
                else
                    mapOf(
                        AppCompatDelegate.MODE_NIGHT_NO to getString(R.string.settings_theme_light),
                        AppCompatDelegate.MODE_NIGHT_YES to getString(R.string.settings_theme_dark),
                        AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY to getString(R.string.settings_theme_battery_saver)
                    )
            )

        val entriesId =
            if (supportsNightFollowSystem)
                R.array.entries_settings_theme_android_ten_and_above
            else
                R.array.entries_settings_theme_android_nine_and_below

        dialog.multiChoice(
            title = "Theme",
            resId = entriesId,
            initialSelection = themeTranslator.indexOfId(Settings.theme)
        ) { _, _, text ->
            val newTheme = themeTranslator.getId(text.toString())
            if (newTheme != Settings.theme) {
                Settings.theme = newTheme
                Settings.save(this)
                updateTheme()
            }
        }
    }
}
