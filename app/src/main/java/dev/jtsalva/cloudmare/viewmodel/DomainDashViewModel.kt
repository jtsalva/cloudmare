package dev.jtsalva.cloudmare.viewmodel

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import dev.jtsalva.cloudmare.BR
import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.Dialog
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentMode
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentModeRequest
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevel
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevelRequest

class DomainDashViewModel(
    private val context: CloudMareActivity,
    private val domainId: String
) : BaseObservable() {

    companion object {
        private const val TAG = "DomainDashViewModel"
    }

    private val data = object {
        var underAttackModeEnabled = false
        var developmentModeEnabled = false
    }

    @Bindable
    fun getUnderAttackModeEnabled(): Boolean {
        return data.underAttackModeEnabled
    }

    fun setUnderAttackModeEnabledNoSync(value: Boolean) {
        data.underAttackModeEnabled = value
        notifyPropertyChanged(BR.underAttackModeEnabled)
    }

    fun setUnderAttackModeEnabled(value: Boolean) = context.launch {
        val newSecurityLevelValue =
            if (value) SecurityLevel.Value.UNDER_ATTACK
            else SecurityLevel.Value.MEDIUM

        val response = SecurityLevelRequest(context).set(domainId, newSecurityLevelValue)
        if (response.success) Log.d(TAG, "Changed security level")
        else {
            Dialog(context).error(title = "Can't set under attack mode", message = response.firstErrorMessage)
            Log.e(TAG, "Failed to change security level")
        }

        data.underAttackModeEnabled = value
        notifyPropertyChanged(BR.underAttackModeEnabled)
    }

    @Bindable
    fun getDevelopmentModeEnabled(): Boolean = data.developmentModeEnabled

    fun setDevelopmentModeEnabledNoSync(value: Boolean) {
        data.developmentModeEnabled = value
        notifyPropertyChanged(BR.developmentModeEnabled)
    }

    fun setDevelopmentModeEnabled(value: Boolean) = context.launch {
        val newDevelopmentModeValue =
            if (value) DevelopmentMode.Value.ON
            else DevelopmentMode.Value.OFF

        val response = DevelopmentModeRequest(context).set(domainId, newDevelopmentModeValue)
        if (response.success) Log.d(TAG, "Changed development mode")
        else {
            Dialog(context).error(title = "Can't set development mode", message = response.firstErrorMessage)
            Log.e(TAG, "Failed to change development mode")
        }

        data.developmentModeEnabled = value
        notifyPropertyChanged(BR.developmentModeEnabled)
    }

}