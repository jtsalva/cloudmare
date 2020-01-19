package dev.jtsalva.cloudmare

import android.os.Bundle
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentModeRequest
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevelRequest
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSetting
import dev.jtsalva.cloudmare.databinding.ActivityZoneDashBinding
import dev.jtsalva.cloudmare.viewmodel.ZoneDashViewModel
import kotlinx.android.synthetic.main.activity_zone_dash.*

class ZoneDashActivity : CloudMareActivity(), SwipeRefreshable {

    private lateinit var zone: Zone

    private lateinit var binding: ActivityZoneDashBinding

    private lateinit var viewModel: ZoneDashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        zone = intent.getParcelableExtra("zone")!!

        viewModel = ZoneDashViewModel(this, zone)
        binding = setLayoutBinding(R.layout.activity_zone_dash)
        binding.viewModel = viewModel

        launch {
            setToolbarTitle(zone.name)
            setOnClickListeners()
        }
    }

    override fun onStart() {
        super.onStart()

        render()
    }

    override fun onSwipeRefresh() {
        super.onSwipeRefresh()

        viewModel.apply {
            underAttackModeInitialized = false
            developmentModeInitialized = false
        }
    }

    override fun render() {
        val securityLevelRequest = SecurityLevelRequest(this)
        val developmentModeRequest = DevelopmentModeRequest(this)

        securityLevelRequest.launch {
            get(zone.id).let { response ->
                if (response.failure || response.result == null) {
                    dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)
                    developmentModeRequest.cancelAll(developmentModeRequest::get)
                } else viewModel.apply {
                    underAttackModeEnabled = response.result.value.toString() == ZoneSetting.SECURITY_LEVEL_UNDER_ATTACK
                    underAttackModeInitialized = true
                }
            }
        }

        developmentModeRequest.launch {
            get(zone.id).let { response ->
                if (response.failure || response.result == null) {
                    dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)
                    securityLevelRequest.cancelAll(securityLevelRequest::get)
                } else viewModel.apply {
                    developmentModeEnabled = response.result.value.toString() == ZoneSetting.VALUE_ON
                    developmentModeInitialized = true
                }
            }
        }

        swipeRefreshLayout.isRefreshing = false
    }

    private fun setOnClickListeners() {
        analytics_item.setOnClickListener {
            startActivityWithExtras(AnalyticsActivity::class,
                "zone" to zone
            )
        }

        caching_item.setOnClickListener {
            startActivityWithExtras(CachingActivity::class,
                "zone" to zone
            )
        }

        dns_item.setOnClickListener {
            startActivityWithExtras(DNSRecordListActivity::class,
                "zone" to zone
            )
        }

        network_item.setOnClickListener {
            startActivityWithExtras(NetworkActivity::class,
                "zone" to zone
            )
        }

        page_rules_item.setOnClickListener {
            startActivityWithExtras(PageRulesActivity::class,
                "zone" to zone)
        }

        ssl_item.setOnClickListener {
            startActivityWithExtras(SSLActivity::class,
                "zone" to zone)
        }
    }
}
