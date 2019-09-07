package dev.jtsalva.cloudmare.viewmodel

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import dev.jtsalva.cloudmare.BR
import dev.jtsalva.cloudmare.DomainDashActivity
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentMode
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentModeRequest
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevel
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevelRequest
import timber.log.Timber

class DomainDashViewModel(
    private val activity: DomainDashActivity,
    private val domainId: String
) : BaseObservable() {

    private val data = object {
        var underAttackModeEnabled = false
        var developmentModeEnabled = false
    }

    var underAttackModeEnabled: Boolean
        @Bindable get() = data.underAttackModeEnabled
        set(value) {
            activity.launch {
                val newSecurityLevelValue =
                    if (value) SecurityLevel.UNDER_ATTACK
                    else SecurityLevel.MEDIUM

                if (value != data.underAttackModeEnabled)
                    SecurityLevelRequest(activity).set(domainId, newSecurityLevelValue).let { response ->
                        if (response.success) {
                            data.underAttackModeEnabled = value

                            Timber.d("Changed security level")
                        }
                        else {
                            data.underAttackModeEnabled = !value

                            activity.dialog.error(
                                title = "Can't set under attack mode",
                                message = response.firstErrorMessage,
                                onAcknowledge = activity::onStart)
                            Timber.e("Failed to change security level")
                        }

                        @Suppress("UNRESOLVED_REFERENCE")
                        notifyPropertyChanged(BR.underAttackModeEnabled)
                    }
            }
        }

    var developmentModeEnabled: Boolean
        @Bindable get() = data.developmentModeEnabled
        set(value) {
            activity.launch {
                val newDevelopmentModeValue =
                    if (value) DevelopmentMode.ON
                    else DevelopmentMode.OFF

                if (value != data.developmentModeEnabled)
                    DevelopmentModeRequest(activity).set(domainId, newDevelopmentModeValue).let { response ->
                        if (response.success) {
                            data.developmentModeEnabled = value

                            Timber.d("Changed development mode")
                        } else {
                            data.developmentModeEnabled = !value

                            activity.dialog.error(title = "Can't set development mode", message = response.firstErrorMessage)
                            Timber.e("Failed to change development mode")
                        }

                        @Suppress("UNRESOLVED_REFERENCE")
                        notifyPropertyChanged(BR.developmentModeEnabled)
                    }
            }
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