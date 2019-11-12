package dev.jtsalva.cloudmare

import android.os.Bundle
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentModeRequest
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevelRequest
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSetting
import dev.jtsalva.cloudmare.databinding.ActivityDomainDashBinding
import dev.jtsalva.cloudmare.viewmodel.DomainDashViewModel
import kotlinx.android.synthetic.main.activity_domain_dash.*

class DomainDashActivity : CloudMareActivity(), SwipeRefreshable {

    private lateinit var domain: Zone

    private lateinit var binding: ActivityDomainDashBinding

    private lateinit var viewModel: DomainDashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        domain = intent.getParcelableExtra("domain")!!

        viewModel = DomainDashViewModel(this, domain)
        binding = setLayoutBinding(R.layout.activity_domain_dash)
        binding.viewModel = viewModel

        launch {
            setToolbarTitle(domain.name)
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
            get(domain.id).let { response ->
                if (response.failure || response.result == null) {
                    dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)
                    developmentModeRequest.cancelAll(Request.GET)
                } else viewModel.apply {
                    viewModel.underAttackModeEnabled = response.result.value.toString() == ZoneSetting.SECURITY_LEVEL_UNDER_ATTACK
                    viewModel.underAttackModeInitialized = true
                }
            }
        }

        developmentModeRequest.launch {
            get(domain.id).let { response ->
                if (response.failure || response.result == null) {
                    dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)
                    securityLevelRequest.cancelAll(Request.GET)
                } else viewModel.apply {
                    viewModel.developmentModeEnabled = response.result.value.toString() == ZoneSetting.VALUE_ON
                    viewModel.developmentModeInitialized = true
                }
            }
        }

        swipeRefreshLayout.isRefreshing = false
    }

    private fun setOnClickListeners() {
        analytics_item.setOnClickListener {
            startActivityWithExtras(AnalyticsActivity::class,
                "domain" to domain
            )
        }

        caching_item.setOnClickListener {
            startActivityWithExtras(CachingActivity::class,
                "domain" to domain
            )
        }

        dns_item.setOnClickListener {
            startActivityWithExtras(DNSListActivity::class,
                "domain" to domain
            )
        }

        page_rules_item.setOnClickListener {
            startActivityWithExtras(PageRulesActivity::class,
                "domain" to domain)
        }

        ssl_item.setOnClickListener {
            startActivityWithExtras(SSLActivity::class,
                "domain" to domain)
        }
    }
}
