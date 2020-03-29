package dev.jtsalva.cloudmare.viewmodel

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import dev.jtsalva.cloudmare.BR
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.ZoneDashActivity
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentModeRequest
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevelRequest
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSetting
import dev.jtsalva.cloudmare.view.SwitchOptionView

class ZoneDashViewModel(
    private val activity: ZoneDashActivity,
    private val zone: Zone
) : BaseObservable() {

    private val underAttackModeSwitch by lazy {
        activity.findViewById<SwitchOptionView>(R.id.under_attack_mode_item)
    }

    private val developmentModeSwitch by lazy {
        activity.findViewById<SwitchOptionView>(R.id.development_mode_item)
    }

    var underAttackModeInitialized = false
    var developmentModeInitialized = false

    @Bindable var underAttackModeEnabled = false
        set(value) {
            underAttackModeSwitch.switchIsEnabled = false

            val newSecurityLevelValue =
                if (value) ZoneSetting.SECURITY_LEVEL_UNDER_ATTACK
                else ZoneSetting.SECURITY_LEVEL_MEDIUM

            if (value != field && underAttackModeInitialized)
                SecurityLevelRequest(activity).launch {
                    val response = update(zone.id, newSecurityLevelValue)

                    underAttackModeSwitch.switchIsEnabled = true

                    if (response.success) field = value
                    else {
                        field = !value

                        activity.dialog.error(
                            title = "Can't set under attack mode",
                            message = response.firstErrorMessage,
                            onAcknowledge = activity::onStart)
                    }

                    @Suppress("UNRESOLVED_REFERENCE")
                    notifyPropertyChanged(BR.underAttackModeEnabled)
                }

            else {
                field = value
                underAttackModeSwitch.switchIsEnabled = true

                @Suppress("UNRESOLVED_REFERENCE")
                notifyPropertyChanged(BR.underAttackModeEnabled)
            }
        }

    @Bindable var developmentModeEnabled = false
        set(value) {
            developmentModeSwitch.switchIsEnabled = false

            val newDevelopmentModeValue =
                if (value) ZoneSetting.VALUE_ON
                else ZoneSetting.VALUE_OFF

            if (value != field && developmentModeInitialized)
                DevelopmentModeRequest(activity).launch {
                    val response = update(zone.id, newDevelopmentModeValue)

                    developmentModeSwitch.switchIsEnabled = true

                    if (response.success) field = value
                    else {
                        field = !value

                        activity.dialog.error(title = "Can't set development mode", message = response.firstErrorMessage)
                    }

                    @Suppress("UNRESOLVED_REFERENCE")
                    notifyPropertyChanged(BR.developmentModeEnabled)
                }

            else {
                field = value
                developmentModeSwitch.switchIsEnabled = true

                @Suppress("UNRESOLVED_REFERENCE")
                notifyPropertyChanged(BR.developmentModeEnabled)
            }
        }
}
