package dev.jtsalva.cloudmare.viewmodel

import android.content.Context
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import dev.jtsalva.cloudmare.BR
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentMode
import dev.jtsalva.cloudmare.api.zonesettings.DevelopmentModeRequest
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevel
import dev.jtsalva.cloudmare.api.zonesettings.SecurityLevelRequest

class DomainDashViewModel(
    private val context: Context,
    private val domainId: String
) : BaseObservable() {

    companion object {
        private const val TAG = "DomainDashViewModel"
    }

    data class Data(
        var underAttackModeEnabled: Boolean,
        var developmentModeEnabled: Boolean
    )

    private val data = Data(
        underAttackModeEnabled = false,
        developmentModeEnabled = false
    )

    // prevent sending updates until after initial loading
    // TODO: doesn't exactly fully work :(
    private var locked = true

    @Bindable
    fun getUnderAttackModeEnabled(): Boolean {
        if (locked) locked = false
        return data.underAttackModeEnabled
    }

    fun setUnderAttackModeEnabled(value: Boolean) {
        val newSecurityLevelValue =
            if (value) SecurityLevel.Value.UNDER_ATTACK
            else SecurityLevel.Value.MEDIUM

        if (!locked) SecurityLevelRequest(context).set(domainId, newSecurityLevelValue) {
            if (it?.success == true) Log.d(TAG, "Changed security level")
            else Log.e(TAG, "Failed to change security level")
        }

        data.underAttackModeEnabled = value
        notifyPropertyChanged(BR.underAttackModeEnabled)
    }

    @Bindable
    fun getDevelopmentModeEnabled(): Boolean = data.developmentModeEnabled

    fun setDevelopmentModeEnabled(value: Boolean) {
        val newDevelopmentModeValue =
            if (value) DevelopmentMode.Value.ON
            else DevelopmentMode.Value.OFF

        if (!locked) DevelopmentModeRequest(context).set(domainId, newDevelopmentModeValue) {
            if (it?.success == true) Log.d(TAG, "Changed development mode")
            else Log.e(TAG, "Failed to change development mode")
        }

        data.developmentModeEnabled = value
        notifyPropertyChanged(BR.developmentModeEnabled)
    }

}