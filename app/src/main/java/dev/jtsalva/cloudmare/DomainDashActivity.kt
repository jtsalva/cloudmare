package dev.jtsalva.cloudmare

import android.os.Bundle
import android.util.Log
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentMode
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentModeRequest
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevel
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevelRequest
import dev.jtsalva.cloudmare.databinding.ActivityDomainDashBinding
import dev.jtsalva.cloudmare.viewmodel.DomainDashViewModel
import kotlinx.android.synthetic.main.activity_domain_dash.*

class DomainDashActivity : CloudMareActivity() {

    override val TAG = "DomainDashActivity"

    // TODO: properly handle null extras
    private lateinit var domainId: String

    private lateinit var domainName: String

    data class Category(
        val title: String,
        val info: String
    )

    private lateinit var binding: ActivityDomainDashBinding

    private lateinit var viewModel: DomainDashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        domainId = intent.getStringExtra("domain_id") ?: ""
        domainName = intent.getStringExtra("domain_name") ?: ""

        binding = setLayoutBinding(R.layout.activity_domain_dash)
        setToolbarTitle(domainName)

        renderDash()
    }

    private fun renderDash() = launch {
        viewModel = DomainDashViewModel(this, domainId)

        val securityLevelResponse = SecurityLevelRequest(this).get(domainId)
        val developmentMoveResponse = DevelopmentModeRequest(this).get(domainId)

        if (securityLevelResponse.failure || securityLevelResponse.result == null) {
            Log.e(TAG, "can't fetch security level")
            return@launch
        }

        if (developmentMoveResponse.failure || developmentMoveResponse.result == null) {
            Log.e(TAG, "can't fetch development mode")
            return@launch
        }

        viewModel.apply {
            setUnderAttackModeEnabled(securityLevelResponse.result.value == SecurityLevel.Value.UNDER_ATTACK.toString())
        }

        viewModel.apply {
            setDevelopmentModeEnabled(developmentMoveResponse.result.value == DevelopmentMode.Value.ON.toString())
        }

        binding.viewModel = viewModel

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        dns_item.setOnClickListener {
            startActivityWithExtras(DNSListActivity::class.java,
                    "domain_id" to domainId,
                    "domain_name" to domainName
            )
        }
    }
}
