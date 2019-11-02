package dev.jtsalva.cloudmare.viewmodel

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import dev.jtsalva.cloudmare.BR
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.SSLActivity
import dev.jtsalva.cloudmare.api.toApiValue
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSetting
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSettingRequest
import dev.jtsalva.cloudmare.view.SwitchOptionView
import timber.log.Timber

class SSLViewModel(
    private val activity: SSLActivity,
    private val domain: Zone
) : BaseObservable(), AdapterView.OnItemSelectedListener {

    private val sslModeSpinner by lazy {
        activity.findViewById<Spinner>(R.id.ssl_mode_spinner)
    }

    private val alwaysUseHttpsSwitch by lazy {
        activity.findViewById<SwitchOptionView>(R.id.always_use_https_item)
    }

    private val opportunisticEncryptionSwitch by lazy {
        activity.findViewById<SwitchOptionView>(R.id.opportunistic_encryption_item)
    }

    private val opportunisticOnionSwitch by lazy {
        activity.findViewById<SwitchOptionView>(R.id.opportunistic_onion_item)
    }

    private val automaticHttpsRewritesSwitch by lazy {
        activity.findViewById<SwitchOptionView>(R.id.automatic_https_rewrites_item)
    }

    var isFinishedInitializing = false

    val sslModeTranslator = ZoneSetting.SSLModeTranslator(activity)

    var sslMode = ZoneSetting.SSL_MODE_OFF
        set(value) {
            val oldValue = field

            if (value != field && isFinishedInitializing)
                ZoneSettingRequest(activity).launch {
                    sslModeSpinner.isEnabled = false

                    val response = update(domain.id, ZoneSetting.ssl(value))

                    if (response.success) field = value
                    else {
                        field = oldValue

                        sslModeSpinner.setSelection(
                            activity.sslModeAdapter.getPosition(
                                sslModeTranslator.getReadable(oldValue)
                            )
                        )

                        activity.dialog.error(
                            title = "Can't set ssl mode",
                            message = response.firstErrorMessage)
                    }

                    sslModeSpinner.isEnabled = true
                }
            else {
                field = value
                sslModeSpinner.isEnabled = true
            }
        }

    @Bindable var alwaysUseHttps = false
        set(value) {
            if (value != field && isFinishedInitializing)
                ZoneSettingRequest(activity).launch {
                    alwaysUseHttpsSwitch.switchIsEnabled = false

                    val response = update(
                        domain.id,
                        ZoneSetting.alwaysUseHttps(value.toApiValue()))

                    if (response.success) field = value
                    else {
                        field = !value

                        activity.dialog.error(
                            title = "Can't set always use HTTPS",
                            message = response.firstErrorMessage)
                    }

                    alwaysUseHttpsSwitch.switchIsEnabled = true

                    @Suppress("UNRESOLVED_REFERENCE")
                    notifyPropertyChanged(BR.alwaysUseHttps)
                }
            else {
                field = value
                alwaysUseHttpsSwitch.switchIsEnabled = true

                @Suppress("UNRESOLVED_REFERENCE")
                notifyPropertyChanged(BR.alwaysUseHttps)
            }
        }

    @Bindable var opportunisticEncryption = false
        set(value) {
            if (value != field && isFinishedInitializing)
                ZoneSettingRequest(activity).launch {
                    opportunisticEncryptionSwitch.switchIsEnabled = false

                    val response = update(
                        domain.id,
                        ZoneSetting.opportunisticEncryption(value.toApiValue()))

                    if (response.success) field = value
                    else {
                        field = !value

                        activity.dialog.error(
                            title = "Can't set opportunistic encryption",
                            message = response.firstErrorMessage)
                    }

                    opportunisticEncryptionSwitch.switchIsEnabled = true

                    @Suppress("UNRESOLVED_REFERENCE")
                    notifyPropertyChanged(BR.opportunisticEncryption)
                }
            else {
                field = value
                opportunisticEncryptionSwitch.switchIsEnabled = true

                @Suppress("UNRESOLVED_REFERENCE")
                notifyPropertyChanged(BR.opportunisticEncryption)
            }
        }

    @Bindable var opportunisticOnion = false
        set(value) {
            if (value != field && isFinishedInitializing)
                ZoneSettingRequest(activity).launch {
                    opportunisticOnionSwitch.switchIsEnabled = false

                    val response = update(
                        domain.id,
                        ZoneSetting.opportunisticOnion(value.toApiValue()))

                    if (response.success) field = value
                    else {
                        field = !value

                        activity.dialog.error(
                            title = "Can't set onion routing",
                            message = response.firstErrorMessage)
                    }

                    opportunisticOnionSwitch.switchIsEnabled = true

                    @Suppress("UNRESOLVED_REFERENCE")
                    notifyPropertyChanged(BR.opportunisticOnion)
                }
            else {
                field = value
                opportunisticOnionSwitch.switchIsEnabled = true

                @Suppress("UNRESOLVED_REFERENCE")
                notifyPropertyChanged(BR.opportunisticOnion)
            }
        }

    @Bindable var automaticHttpsRewrites = false
        set(value) {
            if (value != field && isFinishedInitializing)
                ZoneSettingRequest(activity).launch {
                    automaticHttpsRewritesSwitch.switchIsEnabled = false

                    val response = update(
                        domain.id,
                        ZoneSetting.automaticHttpsRewrites(value.toApiValue()))

                    if (response.success) field = value
                    else {
                        field = !value

                        activity.dialog.error(
                            title = "Can't set automatic HTTPS rewrites",
                            message = response.firstErrorMessage)
                    }

                    automaticHttpsRewritesSwitch.switchIsEnabled = true

                    @Suppress("UNRESOLVED_REFERENCE")
                    notifyPropertyChanged(BR.automaticHttpsRewrites)
                }
            else {
                field = value
                automaticHttpsRewritesSwitch.switchIsEnabled = true

                @Suppress("UNRESOLVED_REFERENCE")
                notifyPropertyChanged(BR.automaticHttpsRewrites)
            }
        }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        val selectedItem = parent.getItemAtPosition(pos)

        when (parent.id) {
            R.id.ssl_mode_spinner -> {
                sslMode = sslModeTranslator.getId(selectedItem.toString())
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        Timber.d("Nothing selected")
    }

}