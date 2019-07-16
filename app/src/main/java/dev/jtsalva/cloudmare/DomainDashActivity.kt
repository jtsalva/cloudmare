package dev.jtsalva.cloudmare

import android.content.Intent
import android.os.Bundle
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentMode
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentModeRequest
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevel
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevelRequest
import dev.jtsalva.cloudmare.databinding.ActivityDomainDashBinding
import dev.jtsalva.cloudmare.viewmodel.DomainDashViewModel
import kotlinx.android.synthetic.main.activity_domain_dash.dns_item

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

        viewModel = DomainDashViewModel(this, domainId)

        SecurityLevelRequest(this).get(domainId) {
            if (it?.result == null) return@get

            viewModel.apply {
                setUnderAttackModeEnabled(it.result.value == SecurityLevel.Value.UNDER_ATTACK.toString())
            }
        }

        DevelopmentModeRequest(this).get(domainId) {
            if (it?.result == null) return@get

            viewModel.apply {
                setDevelopmentModeEnabled(it.result.value == DevelopmentMode.Value.ON.toString())
            }
        }

        binding.viewModel = viewModel

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        dns_item.setOnClickListener {
            startActivity(
                Intent(this, DNSListActivity::class.java).putStringExtras(
                    "domain_id", domainId,
                    "domain_name", domainName
                )
            )
        }
    }
}
