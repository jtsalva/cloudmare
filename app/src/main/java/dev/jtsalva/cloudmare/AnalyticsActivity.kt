package dev.jtsalva.cloudmare

import android.os.Bundle
import com.github.mikephil.charting.charts.LineChart
import dev.jtsalva.cloudmare.api.analytics.AnalyticsDashboardRequest
import dev.jtsalva.cloudmare.api.zone.Zone
import kotlinx.android.synthetic.main.activity_analytics.analytics_chart
import timber.log.Timber

class AnalyticsActivity : CloudMareActivity(), SwipeRefreshable {

    private lateinit var domain: Zone

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        domain = intent.getParcelableExtra("domain")!!

        setLayout(R.layout.activity_analytics)
        setToolbarTitle("${domain.name} | Analytics")
    }

    override fun onStart() {
        super.onStart()

        render()
    }

    override fun render() = AnalyticsDashboardRequest(this).launch {
        val response = get(domain.id)
        if (response.failure || response.result == null)
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)

        Timber.d(response.result.toString())
    }
}
