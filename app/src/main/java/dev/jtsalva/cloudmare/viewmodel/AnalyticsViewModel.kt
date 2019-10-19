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
        const val CATEGORY_REQUESTS = "requests"
        const val CATEGORY_BANDWIDTH = "bandwidth"
        const val CATEGORY_THREATS = "threats"
        const val CATEGORY_PAGEVIEWS = "pageviews"

        // In minutes
        const val TIME_PERIOD_ONE_DAY = -1440
        const val TIME_PERIOD_ONE_WEEK = -10080
        const val TIME_PERIOD_ONE_MONTH = -40320
    }

    private val categoryTranslator = activity.run {
        object {
            val idToReadable = mapOf(
                CATEGORY_REQUESTS to getString(R.string.analytics_dashboard_category_requests),
                CATEGORY_BANDWIDTH to getString(R.string.analytics_dashboard_category_bandwidth),
                CATEGORY_THREATS to getString(R.string.analytics_dashboard_category_threats),
                CATEGORY_PAGEVIEWS to getString(R.string.analytics_dashboard_category_pageviews)
            )

            fun getReadable(id: String): String =
                idToReadable.getValue(id)

            fun getId(readable: String): String =
                idToReadable.filterValues { it == readable }.keys.first()
        }
    }

    private val timePeriodTranslator = activity.run {
        object {
            val valueToReadable = mapOf(
                TIME_PERIOD_ONE_DAY to getString(R.string.analytics_dashboard_time_period_one_day),
                TIME_PERIOD_ONE_WEEK to getString(R.string.analytics_dashboard_time_period_one_week),
                TIME_PERIOD_ONE_MONTH to getString(R.string.analytics_dashboard_time_period_one_month)
            )

            fun getReadable(value: Int): String =
                valueToReadable.getValue(value)

            fun getValue(readable: String): Int =
                valueToReadable.filterValues { it == readable }.keys.first()
        }
    }

    var category: String = CATEGORY_REQUESTS
        set(value) {
            if (value != field) {
                field = value
                activity.render()
            }
        }

    var timePeriod: Int = TIME_PERIOD_ONE_WEEK
        set(value) {
            if (value != field) {
                field = value
                activity.render()
            }
        }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        val selectedItem = parent.getItemAtPosition(pos)

        when (parent.id) {
            R.id.category_spinner -> {
                category = categoryTranslator.getId(selectedItem.toString())
            }

            R.id.time_period_spinner -> {
                timePeriod = timePeriodTranslator.getValue(selectedItem.toString())
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        Timber.d("Nothing selected")
    }

}