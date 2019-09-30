package dev.jtsalva.cloudmare.viewmodel

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import dev.jtsalva.cloudmare.BR
import dev.jtsalva.cloudmare.DomainDashActivity
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentMode
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentModeRequest
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevel
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevelRequest
import dev.jtsalva.cloudmare.view.SwitchOptionView

class DomainDashViewModel(
    private val activity: DomainDashActivity,
    private val domainId: String
) : BaseObservable() {

    private val data = object {
        var underAttackModeEnabled = false
        var developmentModeEnabled = false
    }

    private val underAttackModeSwitch by lazy {
        activity.findViewById<SwitchOptionView>(R.id.under_attack_mode_item)
    }

    private val developmentModeSwitch by lazy {
        activity.findViewById<SwitchOptionView>(R.id.development_mode_item)
    }

    var underAttackModeEnabled: Boolean
        @Bindable get() = data.underAttackModeEnabled
        set(value) {
            underAttackModeSwitch.switchIsEnabled = false

            val newSecurityLevelValue =
                if (value) SecurityLevel.UNDER_ATTACK
                else SecurityLevel.MEDIUM

            if (value != data.underAttackModeEnabled)
                SecurityLevelRequest(activity).launch {
                    val response = update(domainId, newSecurityLevelValue)

                    underAttackModeSwitch.switchIsEnabled = true

                    if (response.success) data.underAttackModeEnabled = value
                    else {
                        data.underAttackModeEnabled = !value

                        activity.dialog.error(
                            title = "Can't set under attack mode",
                            message = response.firstErrorMessage,
                            onAcknowledge = activity::onStart)
                    }

                    @Suppress("UNRESOLVED_REFERENCE")
                    notifyPropertyChanged(BR.underAttackModeEnabled)
                }

            else underAttackModeSwitch.switchIsEnabled = true
        }

    var developmentModeEnabled: Boolean
        @Bindable get() = data.developmentModeEnabled
        set(value) {
            developmentModeSwitch.switchIsEnabled = false

            val newDevelopmentModeValue =
                if (value) DevelopmentMode.ON
                else DevelopmentMode.OFF

            if (value != data.developmentModeEnabled)
                DevelopmentModeRequest(activity).launch {
                    val response = update(domainId, newDevelopmentModeValue)

                    developmentModeSwitch.switchIsEnabled = true

                    if (response.success) data.developmentModeEnabled = value
                    else {
                        data.developmentModeEnabled = !value

                        activity.dialog.error(title = "Can't set development mode", message = response.firstErrorMessage)
                    }

                    @Suppress("UNRESOLVED_REFERENCE")
                    notifyPropertyChanged(BR.developmentModeEnabled)
                }

            else developmentModeSwitch.switchIsEnabled = true
        }

    fun initUnderAttackModeEnabled(value: Boolean) {
        data.underAttackModeEnabled = value

        @Suppress("UNRESOLVED_REFERENCE")
        notifyPropertyChanged(BR.underAttackModeEnabled)
    }

    fun initDevelopmentModeEnabled(value: Boolean) {
        data.developmentModeEnabled = value

        @Suppress("UNRESOLVED_REFERENCE")
        notifyPropertyChanged(BR.developmentModeEnabled)
    }

}