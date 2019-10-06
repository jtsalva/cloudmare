package dev.jtsalva.cloudmare

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSetting
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSettingRequest
import dev.jtsalva.cloudmare.databinding.ActivitySslBindingImpl
import dev.jtsalva.cloudmare.viewmodel.SSLViewModel
import kotlinx.android.synthetic.main.activity_ssl.*

class SSLActivity : CloudMareActivity(), SwipeRefreshable {

    private lateinit var domain: Zone

    private lateinit var binding: ActivitySslBindingImpl

    private lateinit var viewModel: SSLViewModel

    val sslModeAdapter by lazy {
        ArrayAdapter.createFromResource(
            this,
            R.array.entries_ssl_settings,
            R.layout.spinner_item
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        domain = intent.getParcelableExtra("domain")!!

        viewModel = SSLViewModel(this, domain)
        binding = setLayoutBinding(R.layout.activity_ssl)

        setToolbarTitle("${domain.name} | SSL / TLS")
    }

    override fun onStart() {
        super.onStart()

        render()
    }

    override fun onSwipeRefresh() {
        super.onSwipeRefresh()

        viewModel.isFinishedInitializing = false
    }

    private inline fun List<ZoneSetting>.oneWithId(id: String) = find { it.id == id }!!

    private inline fun List<ZoneSetting>.valueAsBoolean(id: String) =
        oneWithId(id).value as String == "on"

    override fun render() = launch {
        val response = ZoneSettingRequest(this).list(domain.id)
        if (response.failure || response.result == null)
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)

        else response.result.let { settings ->
            viewModel.sslMode = settings.oneWithId(ZoneSetting.ID_SSL).value as String

            sslModeAdapter.let { adapter ->
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

                ssl_mode_spinner.apply {
                    setAdapter(adapter)

                    val currentSslMode = viewModel.run { sslModeTranslator.getReadable(sslMode) }
                    setSelection(adapter.getPosition(currentSslMode))

                    onItemSelectedListener = viewModel
                }
            }

            viewModel.apply {
                alwaysUseHttps =
                    settings.valueAsBoolean(ZoneSetting.ID_ALWAYS_USE_HTTPS)
                opportunisticEncryption =
                    settings.valueAsBoolean(ZoneSetting.ID_OPPORTUNISTIC_ENCRYPTION)
                opportunisticOnion =
                    settings.valueAsBoolean(ZoneSetting.ID_OPPORTUNISTIC_ONION)
                automaticHttpsRewrites =
                    settings.valueAsBoolean(ZoneSetting.ID_AUTOMATIC_HTTPS_REWRITES)
            }

            binding.viewModel = viewModel

            viewModel.isFinishedInitializing = true
            swipe_refresh.visibility = View.VISIBLE
        }

        showProgressBar = false
    }

}
