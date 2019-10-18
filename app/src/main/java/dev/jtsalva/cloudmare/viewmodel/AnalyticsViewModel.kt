package dev.jtsalva.cloudmare.viewmodel

import android.view.View
import android.widget.AdapterView
import androidx.databinding.BaseObservable
import dev.jtsalva.cloudmare.AnalyticsActivity
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.zone.Zone
import timber.log.Timber

class AnalyticsViewModel(
    private val activity: AnalyticsActivity,
    private val domain: Zone
) : BaseObservable(), AdapterView.OnItemSelectedListener {

    companion object {
        private const val CATEGORY_REQUESTS = "requests"
        private const val CATEGORY_BANDWIDTH = "bandwidth"
        private const val CATEGORY_THREATS = "threats"
        private const val CATEGORY_PAGEVIEWS = "pageviews"
    }

    private val categoryTranslator = activity.run {
        object {
            val idToReadable = mapOf(
                CATEGORY_REQUESTS to getString(R.string.analytics_dashboard_category_requests),
                CATEGORY_BANDWIDTH to getString(R.string.analytics_dashboard_category_bandwidth),
                CATEGORY_THREATS to getString(R.string.analytics_dashboard_category_threats),
                CATEGORY_PAGEVIEWS to getString(R.string.analytics_dashboard_category_pageviews)
            )

            inline fun getReadable(value: String): String =
                idToReadable.getValue(value)

            inline fun getId(readable: String): String =
                idToReadable.filterValues { it == readable }.keys.first()
        }
    }

    var category: String = CATEGORY_REQUESTS
        set(value) {
            field = value

            TODO("stuff init")
        }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        val selectedItem = parent.getItemAtPosition(pos)

//        when (parent.id) {
//        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        Timber.d("Nothing selected")
    }

}