package dev.jtsalva.cloudmare

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jtsalva.cloudmare.adapter.DomainListAdapter
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.api.zone.ZoneRequest
import kotlinx.android.synthetic.main.activity_domain_list.*
import timber.log.Timber

class DomainListActivity : CloudMareActivity(), SwipeRefreshable {

    private lateinit var domains: MutableList<Zone>

    private val initialized get() = domain_list.adapter is DomainListAdapter

    private val paginationListener by lazy {
        object : PaginationListener(this, domain_list.layoutManager as LinearLayoutManager) {

            override fun fetchNextPage(pageNumber: Int) =
                ZoneRequest(this@DomainListActivity).launch {
                    list(pageNumber).run {
                        if (failure || result == null)
                            dialog.error(message = firstErrorMessage)
                        else if (result.isNotEmpty()) result.let { nextPage ->
                            val positionStart = domains.size
                            domains.addAll(nextPage)
                            domain_list.adapter?.notifyItemRangeChanged(positionStart, domains.size)
                        } else reachedLastPage = true
                    }

                    fetchingNextPage = false
                }

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

        R.id.action_settings_activity -> {
            startActivity(SettingsActivity::class)
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
            R.id.action_settings_activity,
            R.id.action_user_activity
        )

        setLayout(R.layout.activity_domain_list)
        setToolbarTitle(R.string.title_domain_list_activity)
    }

    override fun onStart() {
        super.onStart()

        Settings.load(this)

        if (AppCompatDelegate.getDefaultNightMode() != Settings.theme)
            AppCompatDelegate.setDefaultNightMode(Settings.theme)

        if (Auth.notSet) startActivity(UserActivity::class)
        else render()
    }

    override fun onSwipeRefresh() {
        super.onSwipeRefresh()
        paginationListener.resetPage()
    }

    override fun render() = ZoneRequest(this).launch {
        val response = list()
        if (response.failure || response.result == null)
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)

        else (response.result as MutableList<Zone>).also { result ->
            if (!initialized) domain_list.apply {
                domains = result

                adapter = DomainListAdapter(this@DomainListActivity, domains)
                layoutManager = LinearLayoutManager(this@DomainListActivity)
                addOnScrollListener(paginationListener)
            } else if (result != domains) domains.apply {
                paginationListener.resetPage()
                domain_list.layoutManager!!.scrollToPosition(0)

                clear()
                addAll(result)

                domain_list.adapter!!.notifyDataSetChanged()
            }

            showNonFoundMessage = result.isEmpty()
        }

        showProgressBar = false

        swipeRefreshLayout.isRefreshing = false
    }
}
