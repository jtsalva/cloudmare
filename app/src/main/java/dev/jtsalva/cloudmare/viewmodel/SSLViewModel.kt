package dev.jtsalva.cloudmare.viewmodel

import android.view.View
import android.widget.AdapterView
import androidx.databinding.BaseObservable
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.SSLActivity
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevel
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSetting
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSettingsRequest
import timber.log.Timber
import java.lang.Exception

class SSLViewModel(
    private val activity: SSLActivity,
    private val domain: Zone
) : BaseObservable(), AdapterView.OnItemSelectedListener {

    var sslMode = ZoneSetting.SSL_MODE_OFF
        set(value) {
            if (value != field)
                ZoneSettingsRequest(activity).launch {
                    val response = update(domain.id, ZoneSetting("ssl", value))

                    if (response.success) field = value
                    else activity.dialog.
                        error(title = "Can't set ssl mode", message = response.firstErrorMessage)
                }
        }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        when (parent.id) {
            R.id.ssl_mode_spinner -> with (activity) {
                sslMode = when (parent.getItemAtPosition(pos)) {
                    getString(R.string.ssl_off) -> ZoneSetting.SSL_MODE_OFF
                    getString(R.string.ssl_flexible) -> ZoneSetting.SSL_MODE_FLEXIBLE
                    getString(R.string.ssl_full) -> ZoneSetting.SSL_MODE_FULL
                    getString(R.string.ssl_full_strict) -> ZoneSetting.SSL_MODE_STRICT

                    else -> throw Exception("onItemSelected: ssl mode not possible")
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        Timber.d("Nothing selected")
    }

}