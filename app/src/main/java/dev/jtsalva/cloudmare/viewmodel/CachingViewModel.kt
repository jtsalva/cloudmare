package dev.jtsalva.cloudmare.viewmodel

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.BaseObservable
import dev.jtsalva.cloudmare.CachingActivity
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSetting
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSettingRequest
import timber.log.Timber

class CachingViewModel(
    private val activity: CachingActivity,
    private val domain: Zone
) : BaseObservable(), AdapterView.OnItemSelectedListener {

    private val cacheLevelSpinner by lazy {
        activity.findViewById<Spinner>(R.id.cache_level_spinner)
    }

    var isFinishedInitializing = false

    val cacheLevelTranslator = ZoneSetting.CacheLevelTranslator(activity)

    var cacheLevel = ZoneSetting.CACHE_LEVEL_BASIC
        set(value) {
            val oldValue = field

            if (value != field && isFinishedInitializing)
                ZoneSettingRequest(activity).launch {
                    cacheLevelSpinner.isEnabled = false

                    val response = update(domain.id, ZoneSetting(ZoneSetting.ID_CACHE_LEVEL, value))

                    if (response.success) field = value
                    else {
                        field = oldValue

                        cacheLevelSpinner.setSelection(
                            activity.cacheLevelAdapter.getPosition(
                                cacheLevelTranslator.getReadable(oldValue)
                            )
                        )

                        activity.dialog.error(
                            title = "Can't set cache level",
                            message = response.firstErrorMessage
                        )
                    }

                    cacheLevelSpinner.isEnabled = true
                }
            else {
                field = value
                cacheLevelSpinner.isEnabled = true
            }
        }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        val selectedItem = parent.getItemAtPosition(pos)

        when (parent.id) {
            R.id.cache_level_spinner -> {
                cacheLevel = cacheLevelTranslator.getId(selectedItem.toString())
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        Timber.d("Nothing selected")
    }

}