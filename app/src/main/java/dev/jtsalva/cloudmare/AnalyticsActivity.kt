package dev.jtsalva.cloudmare

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.setPadding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dev.jtsalva.cloudmare.api.analytics.AnalyticsDashboard
import dev.jtsalva.cloudmare.api.analytics.AnalyticsDashboardRequest
import dev.jtsalva.cloudmare.api.toDate
import dev.jtsalva.cloudmare.api.toDateWeekDayAsInt
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.databinding.ActivityAnalyticsBinding
import dev.jtsalva.cloudmare.viewmodel.AnalyticsViewModel
import kotlinx.android.synthetic.main.activity_analytics.*
import org.w3c.dom.Text

class AnalyticsActivity : CloudMareActivity(), SwipeRefreshable {

    class DayAxisValueFormatter : ValueFormatter() {

        private val daysOfWeek = mapOf(
            1 to "Sun",
            2 to "Mon",
            3 to "Tue",
            4 to "Wed",
            5 to "Thu",
            6 to "Fri",
            7 to "Sat"
        )

        override fun getAxisLabel(value: Float, axis: AxisBase?): String =
            daysOfWeek.getValue(value.toDateWeekDayAsInt())

    }

    companion object {
        private const val AXIS_LABEL_TEXT_SIZE = 15f
        private const val LINE_WIDTH = 4f
        private const val X_AXIS_LABEL_ROTATION = -45f

        private fun customXAxis(count: Int, forceLabelCount: Boolean = true): XAxis.() -> Unit =
            {
                position = XAxis.XAxisPosition.BOTTOM
                textSize = AXIS_LABEL_TEXT_SIZE
                valueFormatter = DayAxisValueFormatter()
                labelRotationAngle = X_AXIS_LABEL_ROTATION
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setLabelCount(count, forceLabelCount)
            }

        private val customYAxis: YAxis.() -> Unit = {
            textSize = AXIS_LABEL_TEXT_SIZE
            setDrawGridLines(true)
            setDrawAxisLine(false)
            setDrawGridLinesBehindData(false)
        }

        private fun customLineDataSet(context: Context): LineDataSet.() -> Unit = {
            setColors(intArrayOf(R.color.colorPrimary), context)
            lineWidth = LINE_WIDTH
            setDrawValues(false)
        }

        private fun customChart(count: Int): LineChart.() -> Unit = {
            isAutoScaleMinMaxEnabled = true

            setTouchEnabled(false)
            description.isEnabled = false
            axisRight.isEnabled = false

            xAxis.apply(customXAxis(count))
            axisLeft.apply(customYAxis)
        }
    }

    private fun totalsTemplate(label: String, value: Int): View =
        layoutInflater.inflate(R.layout.analytics_dashboard_totals_item, null).apply {
            val labelTextView = findViewById<TextView>(R.id.label)
            val valueTextView = findViewById<TextView>(R.id.value)

            labelTextView.text = label
            valueTextView.text = value.toString()
        }

    private lateinit var domain: Zone

    private lateinit var binding: ActivityAnalyticsBinding

    private lateinit var viewModel: AnalyticsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        domain = intent.getParcelableExtra("domain")!!

        viewModel = AnalyticsViewModel(this, domain)
        binding = setLayoutBinding(R.layout.activity_analytics)
        binding.viewModel = viewModel

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

        else response.result.let { analyticsDashboard ->
            when (viewModel.category) {
                AnalyticsViewModel.CATEGORY_REQUESTS -> drawRequests(analyticsDashboard)

                else -> throw Exception("Doesn't exist mate")
            }

            analytics_chart.invalidate()
        }
    }

    private fun drawRequests(analyticsDashboard: AnalyticsDashboard) {
        totals_table.addView(totalsTemplate("All", analyticsDashboard.totals.requests.all))
        totals_table.addView(totalsTemplate("Cached", analyticsDashboard.totals.requests.cached))
        totals_table.addView(totalsTemplate("Uncached", analyticsDashboard.totals.requests.uncached))

        val all = ArrayList<Entry>()
        val cached = ArrayList<Entry>()
        val uncached = ArrayList<Entry>()
        val dataSets = ArrayList<ILineDataSet>()

        analyticsDashboard.timeSeries.forEach { data ->
            val time = data.since.toDate().time.toFloat()
            all.add(Entry(time, data.requests.all.toFloat()))
            cached.add(Entry(time, data.requests.cached.toFloat()))
            uncached.add(Entry(time, data.requests.uncached.toFloat()))
        }

        y_axis_title.text = "Number of Requests"

        dataSets.add(
            LineDataSet(all, "All").apply(customLineDataSet(this@AnalyticsActivity))
        )

        dataSets.add(
            LineDataSet(cached, "Cached").apply(customLineDataSet(this@AnalyticsActivity))
        )

        dataSets.add(
            LineDataSet(uncached, "Uncached").apply(customLineDataSet(this@AnalyticsActivity))
        )

        analytics_chart.apply(customChart(all.size)).apply {
            data = LineData(dataSets)
        }
    }
}
