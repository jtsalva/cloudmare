package dev.jtsalva.cloudmare

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dev.jtsalva.cloudmare.api.analytics.AnalyticsDashboard
import dev.jtsalva.cloudmare.api.analytics.AnalyticsDashboardRequest
import dev.jtsalva.cloudmare.api.toDate
import dev.jtsalva.cloudmare.api.toDateHourAsString
import dev.jtsalva.cloudmare.api.toDateMonthAsString
import dev.jtsalva.cloudmare.api.toDateWeekDayAsString
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.databinding.ActivityAnalyticsBinding
import dev.jtsalva.cloudmare.viewmodel.AnalyticsViewModel
import kotlinx.android.synthetic.main.activity_analytics.*

class AnalyticsActivity : CloudMareActivity(), SwipeRefreshable {

    class DayAxisValueFormatter : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String =
            value.toDateWeekDayAsString()
    }

    class HourAxisValueFormatter : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String =
            value.toDateHourAsString()
    }

    class MonthAxisValueFormatter : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String =
            value.toDateMonthAsString()
    }

    class YAxisValueFormatter : LargeValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String =
            if (value >= 1000) super.getAxisLabel(value, axis)
            else TWO_DECIMAL_PLACES_FORMATTER.format(value)
    }

    data class CacheItem(
        val analyticsDashboard: AnalyticsDashboard,
        var lines: MutableMap<String, LineData> = mutableMapOf()
    )

    companion object {
        private const val AXIS_LABEL_TEXT_SIZE = 15f
        private const val LEGEND_TEXT_SIZE = 18f
        private const val LINE_WIDTH = 4f
        private const val X_AXIS_LABEL_ROTATION = -45f
        private const val MAX_NUM_OF_X_AXIS_LABELS = 7

        private const val TWO_DECIMAL_PLACES_FORMATTER = "%.2f"

        private const val BYTE = 1
        private const val KILOBYTE = BYTE * 1000
        private const val MEGABYTE = KILOBYTE * 1000
        private const val GIGABYTE = MEGABYTE * 1000

        private val LAST_DAY = -900 downTo -4319
        private val LAST_WEEK = -4320 downTo -10080
        private val LAST_MONTH = -10081 downTo -525600
    }

    private fun customXAxis(
        count: Int,
        formatter: ValueFormatter,
        forceLabelCount: Boolean = true
    ): XAxis.() -> Unit = {
            position = XAxis.XAxisPosition.BOTTOM
            textSize = AXIS_LABEL_TEXT_SIZE
            valueFormatter = formatter
            labelRotationAngle = X_AXIS_LABEL_ROTATION
            setDrawGridLines(false)
            setDrawAxisLine(false)
            setLabelCount(
                if (count <= MAX_NUM_OF_X_AXIS_LABELS) count else MAX_NUM_OF_X_AXIS_LABELS,
                forceLabelCount
            )
            textColor = labelColor
        }

    private val customYAxis: YAxis.() -> Unit = {
        textSize = AXIS_LABEL_TEXT_SIZE
        valueFormatter = YAxisValueFormatter()
        setDrawGridLines(true)
        setDrawAxisLine(false)
        setDrawGridLinesBehindData(false)
        textColor = labelColor
    }

    private fun customChart(
        count: Int,
        formatter: ValueFormatter
    ): LineChart.() -> Unit = {
        isAutoScaleMinMaxEnabled = true

        setTouchEnabled(false)
        description.isEnabled = false
        axisRight.isEnabled = false

        legend.textSize = LEGEND_TEXT_SIZE

        setExtraOffsets(0f, 0f, 0f, 10f)

        xAxis.apply(customXAxis(count, formatter))
        axisLeft.apply(customYAxis)

        legend.textColor = labelColor
    }

    private fun customLineDataSet(lineColor: Int): LineDataSet.() -> Unit = {
        setColors(intArrayOf(lineColor), this@AnalyticsActivity)
        lineWidth = LINE_WIDTH
        setDrawValues(false)
        setDrawCircles(false)
    }

    @SuppressLint("InflateParams")
    private fun totalsTemplate(label: String, value: String): View =
        layoutInflater.inflate(R.layout.analytics_dashboard_totals_item, null).apply {
            val labelTextView = findViewById<TextView>(R.id.label)
            val valueTextView = findViewById<TextView>(R.id.value)

            labelTextView.text = label
            valueTextView.text = value
        }

    private fun drawTotals(vararg totals: Pair<String, Float>, units: String = "") =
        totals.forEach { total ->
            val value = TWO_DECIMAL_PLACES_FORMATTER.format(total.second)
            totals_table.addView(totalsTemplate(total.first, "$value $units"))
        }

    private fun autoFormatter() = when (viewModel.timePeriod) {
        in LAST_DAY -> HourAxisValueFormatter()
        in LAST_WEEK -> DayAxisValueFormatter()
        in LAST_MONTH -> MonthAxisValueFormatter()

        else -> throw Exception("Can't auto assign formatter given viewModel.timePeriod")
    }

    private val labelColor by lazy {
        val darkModes =
            Configuration.UI_MODE_NIGHT_YES..(Configuration.UI_MODE_NIGHT_YES + 1)
        getColor(
            if (resources.configuration.uiMode in darkModes) R.color.colorWhite
            else R.color.colorBlack
        )
    }

    private lateinit var zone: Zone

    private lateinit var binding: ActivityAnalyticsBinding

    private lateinit var viewModel: AnalyticsViewModel

    private val categoryAdapter by lazy {
        ArrayAdapter.createFromResource(
            this,
            R.array.entries_analytics_dashboard_categories,
            R.layout.spinner_item
        )
    }

    val timePeriodAdapter by lazy {
        ArrayAdapter.createFromResource(
            this,
            R.array.entries_analytics_dashboard_time_periods,
            R.layout.spinner_item
        )
    }

    val cache = mutableMapOf<Int, CacheItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        zone = intent.getParcelableExtra("zone")!!

        viewModel = AnalyticsViewModel(this, zone)
        binding = setLayoutBinding(R.layout.activity_analytics)
        binding.viewModel = viewModel

        setToolbarTitle("${zone.name} | Analytics")

        launch {
            categoryAdapter.let { adapter ->
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

                category_spinner.apply {
                    setAdapter(adapter)
                    onItemSelectedListener = viewModel
                }
            }

            timePeriodAdapter.let { adapter ->
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

                time_period_spinner.apply {
                    setAdapter(adapter)
                    onItemSelectedListener = viewModel
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        render()
    }

    override fun onSwipeRefresh() {
        super.onSwipeRefresh()

        viewModel.disableSpinners()
        cache.clear()
    }

    override fun render() = AnalyticsDashboardRequest(this).launch {
        val response = get(zone.id, since = viewModel.timePeriod)
        if (response.failure || response.result == null)
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)

        else {
            cache[viewModel.timePeriod] = CacheItem(analyticsDashboard = response.result)
            drawGraph()
        }

        swipeRefreshLayout.isRefreshing = false
        viewModel.enableSpinners()
    }

    fun drawGraph() {
        totals_table.removeAllViews()

        when (viewModel.category) {
            AnalyticsViewModel.CATEGORY_REQUESTS -> drawRequests()
            AnalyticsViewModel.CATEGORY_BANDWIDTH -> drawBandwidth()
            AnalyticsViewModel.CATEGORY_THREATS -> drawThreats()
            AnalyticsViewModel.CATEGORY_PAGEVIEWS -> drawPageviews()

            else -> throw Exception("Selected category doesn't exist")
        }

        analytics_chart.invalidate()
        analytics_view_group.visibility = View.VISIBLE
        showProgressBar = false
    }

    private fun drawRequests() = with(cache.getValue(viewModel.timePeriod)) {
        drawTotals(
            "All" to analyticsDashboard.totals.requests.all.toFloat(),
            "Cached" to analyticsDashboard.totals.requests.cached.toFloat(),
            "Uncached" to analyticsDashboard.totals.requests.uncached.toFloat()
        )

        if (!lines.containsKey(AnalyticsViewModel.CATEGORY_REQUESTS)) {
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

            dataSets.apply {
                add(
                    LineDataSet(all, "All").apply(
                        customLineDataSet(R.color.colorPrimary)
                    )
                )

                add(
                    LineDataSet(cached, "Cached").apply(
                        customLineDataSet(R.color.colorBlue)
                    )
                )

                add(
                    LineDataSet(uncached, "Uncached").apply(
                        customLineDataSet(R.color.colorRed)
                    )
                )
            }

            lines[AnalyticsViewModel.CATEGORY_REQUESTS] = LineData(dataSets)
        }

        val requestsLineData = lines.getValue(AnalyticsViewModel.CATEGORY_REQUESTS)
        y_axis_title.text = getString(R.string.analytics_axis_title_requests)
        analytics_chart.apply(customChart(requestsLineData.entryCount, autoFormatter()))
        analytics_chart.data = requestsLineData
    }

    private fun drawBandwidth() = with(cache.getValue(viewModel.timePeriod)) {
        var displayUnit = BYTE to "B"
        val dataPoints = analyticsDashboard.totals.bandwidth.run { arrayOf(all, cached, uncached) }
        val units = mapOf(KILOBYTE to "KB", MEGABYTE to "MB", GIGABYTE to "GB")

        units.forEach { unit ->
            dataPoints.forEach { if (it >= unit.key) displayUnit = unit.key to unit.value }
        }

        drawTotals(
            "All" to analyticsDashboard.totals.bandwidth.all.toFloat() / displayUnit.first,
            "Cached" to analyticsDashboard.totals.bandwidth.cached.toFloat() / displayUnit.first,
            "Uncached" to analyticsDashboard.totals.bandwidth.uncached.toFloat() / displayUnit.first,
            units = displayUnit.second
        )

        if (!lines.containsKey(AnalyticsViewModel.CATEGORY_BANDWIDTH)) {
            val all = ArrayList<Entry>()
            val cached = ArrayList<Entry>()
            val uncached = ArrayList<Entry>()
            val dataSets = ArrayList<ILineDataSet>()

            analyticsDashboard.timeSeries.forEach { data ->
                val time = data.since.toDate().time.toFloat()
                all.add(Entry(time, data.bandwidth.all.toFloat() / displayUnit.first))
                cached.add(Entry(time, data.bandwidth.cached.toFloat() / displayUnit.first))
                uncached.add(Entry(time, data.bandwidth.uncached.toFloat() / displayUnit.first))
            }

            dataSets.apply {
                add(
                    LineDataSet(all, "All").apply(
                        customLineDataSet(R.color.colorPrimary)
                    )
                )

                add(
                    LineDataSet(cached, "Cached").apply(
                        customLineDataSet(R.color.colorBlue)
                    )
                )

                add(
                    LineDataSet(uncached, "Uncached").apply(
                        customLineDataSet(R.color.colorRed)
                    )
                )
            }

            lines[AnalyticsViewModel.CATEGORY_BANDWIDTH] = LineData(dataSets)
        }

        val bandwidthLineData = lines.getValue(AnalyticsViewModel.CATEGORY_BANDWIDTH)
        y_axis_title.text = getString(R.string.analytics_axis_title_bandwidth, displayUnit.second)
        analytics_chart.apply(customChart(bandwidthLineData.entryCount, autoFormatter()))
        analytics_chart.data = bandwidthLineData
    }

    private fun drawThreats() = with(cache.getValue(viewModel.timePeriod)) {
        drawTotals("Threats" to analyticsDashboard.totals.threats.all.toFloat())

        if (!lines.containsKey(AnalyticsViewModel.CATEGORY_THREATS)) {
            val all = ArrayList<Entry>()
            val dataSets = ArrayList<ILineDataSet>()

            analyticsDashboard.timeSeries.forEach { data ->
                val time = data.since.toDate().time.toFloat()
                all.add(Entry(time, data.threats.all.toFloat()))
            }

            dataSets.add(
                LineDataSet(all, "All").apply(
                    customLineDataSet(R.color.colorPrimary)
                )
            )

            lines[AnalyticsViewModel.CATEGORY_THREATS] = LineData(dataSets)
        }

        val threatsLineData = lines.getValue(AnalyticsViewModel.CATEGORY_THREATS)
        y_axis_title.text = getString(R.string.analytics_axis_title_threats)
        analytics_chart.apply(customChart(threatsLineData.entryCount, autoFormatter()))
        analytics_chart.data = threatsLineData
    }

    private fun drawPageviews() = with(cache.getValue(viewModel.timePeriod)) {
        drawTotals("Pageviews" to analyticsDashboard.totals.pageviews.all.toFloat())

        if (!lines.containsKey(AnalyticsViewModel.CATEGORY_PAGEVIEWS)) {
            val all = ArrayList<Entry>()
            val dataSets = ArrayList<ILineDataSet>()

            analyticsDashboard.timeSeries.forEach { data ->
                val time = data.since.toDate().time.toFloat()
                all.add(Entry(time, data.pageviews.all.toFloat()))
            }

            dataSets.add(
                LineDataSet(all, "All").apply(
                    customLineDataSet(R.color.colorPrimary)
                )
            )

            lines[AnalyticsViewModel.CATEGORY_PAGEVIEWS] = LineData(dataSets)
        }

        val pageviewsLineData = lines.getValue(AnalyticsViewModel.CATEGORY_PAGEVIEWS)
        y_axis_title.text = getString(R.string.analytics_axis_title_pageviews)
        analytics_chart.apply(customChart(pageviewsLineData.entryCount, autoFormatter()))
        analytics_chart.data = pageviewsLineData
    }
}
