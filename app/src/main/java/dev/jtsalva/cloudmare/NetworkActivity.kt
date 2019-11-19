package dev.jtsalva.cloudmare

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSetting
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSettingRequest
import dev.jtsalva.cloudmare.databinding.ActivityNetworkBindingImpl
import dev.jtsalva.cloudmare.viewmodel.NetworkViewModel
import kotlinx.android.synthetic.main.activity_network.*
import timber.log.Timber

class NetworkActivity : CloudMareActivity(), SwipeRefreshable {

    private lateinit var domain: Zone

    private lateinit var binding: ActivityNetworkBindingImpl

    private lateinit var viewModel: NetworkViewModel

    val pseudoIpv4Adapter by lazy {
        ArrayAdapter.createFromResource(
            this,
            R.array.entries_network_pseudo_ipv4,
            R.layout.spinner_item
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        domain = intent.getParcelableExtra("domain")!!

        viewModel = NetworkViewModel(this, domain)
        binding = setLayoutBinding(R.layout.activity_network)

        setToolbarTitle("${domain.name} | Network")
    }

    override fun onStart() {
        super.onStart()

        render()
    }

    override fun render() = launch {
        val response = ZoneSettingRequest(this).list(domain.id)
        if (response.failure || response.result == null)
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)

        else response.result.let { settings ->
            Timber.e(settings.toString())
            viewModel.pseudoIpv4 = settings.valueAsString(ZoneSetting.ID_PSEUDO_IPV4)

            pseudoIpv4Adapter.let { adapter ->
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

                pseudo_ipv4_spinner.apply {
                    setAdapter(adapter)

                    val currentSelection = viewModel.run { pseudoIpv4Translator.getReadable(pseudoIpv4) }
                    setSelection(adapter.getPosition(currentSelection))

                    onItemSelectedListener = viewModel
                }
            }

            viewModel.apply {
                ipv6Compatibility =
                    settings.valueAsBoolean(ZoneSetting.ID_IPV6)
                webSockets =
                    settings.valueAsBoolean(ZoneSetting.ID_WEBSOCKETS)
                ipGeolocation =
                    settings.valueAsBoolean(ZoneSetting.ID_IP_GELOCATION)
            }

            binding.viewModel = viewModel

            viewModel.isFinishedInitializing = true
            network_view_group.visibility = View.VISIBLE
        }

        showProgressBar = false

        swipeRefreshLayout.isRefreshing = false
    }
}
