package dev.jtsalva.cloudmare

import android.os.Bundle
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

    private val sslModeAdapter by lazy {
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
        binding.viewModel = viewModel

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

            viewModel.isFinishedInitializing = true
        }
    }

}
