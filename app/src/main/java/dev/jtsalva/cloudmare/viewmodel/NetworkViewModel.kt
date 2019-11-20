package dev.jtsalva.cloudmare.viewmodel

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import dev.jtsalva.cloudmare.BR
import dev.jtsalva.cloudmare.NetworkActivity
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.IdTranslator
import dev.jtsalva.cloudmare.api.toApiValue
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSetting
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSettingRequest
import dev.jtsalva.cloudmare.view.SwitchOptionView
import timber.log.Timber

class NetworkViewModel(
    private val activity: NetworkActivity,
    private val domain: Zone
) : BaseObservable(), AdapterView.OnItemSelectedListener {

    private val ipv6CompatibilitySwitch by lazy {
        activity.findViewById<SwitchOptionView>(R.id.ipv6_compatibility_item)
    }

    private val webSocketsSwitch by lazy {
        activity.findViewById<SwitchOptionView>(R.id.web_sockets_item)
    }

    private val pseudoIpv4Spinner by lazy {
        activity.findViewById<Spinner>(R.id.pseudo_ipv4_spinner)
    }

    private val ipGeolocationSwitch by lazy {
        activity.findViewById<SwitchOptionView>(R.id.ip_geolocation_item)
    }

    var isFinishedInitializing = false

    val pseudoIpv4Translator = ZoneSetting.pseudoIpv4Translator(activity)

    @Bindable var ipv6Compatibility = false
        set(value) {
            if (value != field && isFinishedInitializing)
                ZoneSettingRequest(activity).launch {
                    ipv6CompatibilitySwitch.switchIsEnabled = false

                    val response = update(
                        domain.id,
                        ZoneSetting.ipv6(value.toApiValue())
                    )

                    if (response.success) field = value
                    else {
                        field = !value

                        activity.dialog.error(
                            title = "Can't set IPv6 Compatibility",
                            message = response.firstErrorMessage
                        )
                    }

                    ipv6CompatibilitySwitch.switchIsEnabled = true

                    @Suppress("UNRESOLVED_REFERENCE")
                    notifyPropertyChanged(BR.ipv6Compatibility)
                }
            else {
                field = value
                ipv6CompatibilitySwitch.switchIsEnabled = true

                @Suppress("UNRESOLVED_REFERENCE")
                notifyPropertyChanged(BR.ipv6Compatibility)
            }
        }

    @Bindable var webSockets = false
        set(value) {
            if (value != field && isFinishedInitializing)
                ZoneSettingRequest(activity).launch {
                    webSocketsSwitch.switchIsEnabled = false

                    val response = update(
                        domain.id,
                        ZoneSetting.webSockets(value.toApiValue())
                    )

                    if (response.success) field = value
                    else {
                        field = !value

                        activity.dialog.error(
                            title = "Can't set Web Sockets",
                            message = response.firstErrorMessage
                        )
                    }

                    webSocketsSwitch.switchIsEnabled = true

                    @Suppress("UNRESOLVED_REFERENCE")
                    notifyPropertyChanged(BR.webSockets)
                }
            else {
                field = value
                webSocketsSwitch.switchIsEnabled = true

                @Suppress("UNRESOLVED_REFERENCE")
                notifyPropertyChanged(BR.webSockets)
            }
        }

    var pseudoIpv4 = ZoneSetting.PSEUDO_IPV4_OFF
        set(value) {
            val oldValue = field

            if (value != field && isFinishedInitializing)
                ZoneSettingRequest(activity).launch {
                    pseudoIpv4Spinner.isEnabled = false
                    val response = update(domain.id, ZoneSetting.pseudoIpv4(value))

                    if (response.success) field = value
                    else {
                        field = oldValue

                        pseudoIpv4Spinner.setSelection(
                            activity.pseudoIpv4Adapter.getPosition(
                                pseudoIpv4Translator.getReadable(oldValue)
                            )
                        )

                        activity.dialog.error(
                            title = "Can't set Pseudo IPv4",
                            message = response.firstErrorMessage
                        )
                    }

                    pseudoIpv4Spinner.isEnabled = true
                }
            else {
                field = value
                pseudoIpv4Spinner.isEnabled = true
            }
        }

    @Bindable var ipGeolocation = false
        set(value) {
            if (value != field && isFinishedInitializing)
                ZoneSettingRequest(activity).launch {
                    ipGeolocationSwitch.switchIsEnabled = false

                    val response = update(
                        domain.id,
                        ZoneSetting.ipGeolocation(value.toApiValue())
                    )

                    if (response.success) field = value
                    else {
                        field = !value

                        activity.dialog.error(
                            title = "Can't set IP Geolocation",
                            message = response.firstErrorMessage
                        )
                    }

                    ipGeolocationSwitch.switchIsEnabled = true

                    @Suppress("UNRESOLVED_REFERENCE")
                    notifyPropertyChanged(BR.ipGeolocation)
                }
            else {
                field = value
                ipGeolocationSwitch.switchIsEnabled = true

                @Suppress("UNRESOLVED_REFERENCE")
                notifyPropertyChanged(BR.ipGeolocation)
            }
        }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        val selectedItem = parent.getItemAtPosition(pos)

        when (parent.id) {
            R.id.pseudo_ipv4_spinner -> {
                pseudoIpv4 = pseudoIpv4Translator.getId(selectedItem.toString())
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        Timber.d("Nothing selected")
    }

}