package dev.jtsalva.cloudmare

import android.os.Bundle
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentMode
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentModeRequest
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevel
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevelRequest
import dev.jtsalva.cloudmare.databinding.ActivityDomainDashBinding
import dev.jtsalva.cloudmare.viewmodel.DomainDashViewModel
import kotlinx.android.synthetic.main.activity_domain_dash.*
import timber.log.Timber

class DomainDashActivity : CloudMareActivity(), SwipeRefreshable {

    private lateinit var domainId: String

    private lateinit var domainName: String

    private lateinit var binding: ActivityDomainDashBinding

    private lateinit var viewModel: DomainDashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with (intent) {
            domainId = getStringExtra("domain_id") ?: ""
            domainName = getStringExtra("domain_name") ?: ""
        }

        renderDash()
    }

    override fun onSwipeRefresh() {
        renderDash(refresh = true)
    }

    private fun renderDash(refresh: Boolean = false) = launch {
        if (!refresh) launch {
            viewModel = DomainDashViewModel(this, domainId)
            binding = setLayoutBinding(R.layout.activity_domain_dash)
            binding.viewModel = viewModel

            setToolbarTitle(domainName)
            setOnClickListeners()
        }

        launch {
            DevelopmentModeRequest(this).get(domainId).let { response ->
                if (response.failure || response.result == null) {
                    Timber.e("can't fetch development mode")
                    dialog.error(message = response.firstErrorMessage, onAcknowledge = ::recreate)
                } else viewModel.apply {
                    initDevelopmentModeEnabled(response.result.value == DevelopmentMode.ON)
                }
            }
        }

        SecurityLevelRequest(this).get(domainId).let { response ->
            if (response.failure || response.result == null) {
                Timber.e("can't fetch security level")
                dialog.error(message = response.firstErrorMessage, onAcknowledge = ::recreate)
            } else viewModel.apply {
                initUnderAttackModeEnabled(response.result.value == SecurityLevel.UNDER_ATTACK)
            }
        }
    }

    private fun setOnClickListeners() {
        dns_item.setOnClickListener {
            startActivityWithExtras(DNSListActivity::class,
                    "domain_id" to domainId,
                    "domain_name" to domainName
            )
        }
    }
}
