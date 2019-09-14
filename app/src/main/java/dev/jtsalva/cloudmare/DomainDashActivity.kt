package dev.jtsalva.cloudmare

import android.os.Bundle
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentMode
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentModeRequest
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevel
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevelRequest
import dev.jtsalva.cloudmare.databinding.ActivityDomainDashBinding
import dev.jtsalva.cloudmare.viewmodel.DomainDashViewModel
import kotlinx.android.synthetic.main.activity_domain_dash.*

class DomainDashActivity : CloudMareActivity(), SwipeRefreshable {

    private lateinit var domain: Zone

    private lateinit var binding: ActivityDomainDashBinding

    private lateinit var viewModel: DomainDashViewModel

    private val initialized: Boolean get() = ::viewModel.isInitialized

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        domain = intent.getParcelableExtra("domain")!!

        launch {
            viewModel = DomainDashViewModel(this, domain.id)
            binding = setLayoutBinding(R.layout.activity_domain_dash)
            binding.viewModel = viewModel

            setToolbarTitle(domain.name)
            setOnClickListeners()
        }
    }

    override fun onStart() {
        super.onStart()

        render()
    }

    override fun render() = launch {
        val securityLevelRequest = SecurityLevelRequest(this)
        val developmentModeRequest = DevelopmentModeRequest(this)

        launch {
            developmentModeRequest.get(domain.id).let { response ->
                if (response.failure || response.result == null) {
                    dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)
                    securityLevelRequest.cancelAll(Request.GET)
                } else viewModel.apply {
                    initDevelopmentModeEnabled(response.result.value == DevelopmentMode.ON)
                }
            }
        }

        securityLevelRequest.get(domain.id).let { response ->
            if (response.failure || response.result == null) {
                dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)
                developmentModeRequest.cancelAll(Request.GET)
            } else viewModel.apply {
                initUnderAttackModeEnabled(response.result.value == SecurityLevel.UNDER_ATTACK)
            }
        }
    }

    private fun setOnClickListeners() {
        dns_item.setOnClickListener {
            startActivityWithExtras(DNSListActivity::class,
                    "domain" to domain
            )
        }
    }
}
