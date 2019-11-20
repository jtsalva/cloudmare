package dev.jtsalva.cloudmare.viewmodel

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.BaseObservable
import dev.jtsalva.cloudmare.CachingActivity
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.dns.DNSRecord
import dev.jtsalva.cloudmare.api.toApiValue
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSetting
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSettingRequest
import dev.jtsalva.cloudmare.view.SwitchOptionView
import timber.log.Timber

class CachingViewModel(
    private val activity: CachingActivity,
    private val domain: Zone
) : BaseObservable(), AdapterView.OnItemSelectedListener {

    private val cacheLevelSpinner by lazy {
        activity.findViewById<Spinner>(R.id.cache_level_spinner)
    }

    private val browserCacheTtlSpinner by lazy {
        activity.findViewById<Spinner>(R.id.browser_cache_ttl_spinner)
    }

    private val alwaysOnlineSwitch by lazy {
        activity.findViewById<SwitchOptionView>(R.id.always_online_item)
    }

    var isFinishedInitializing = false

    val cacheLevelTranslator = ZoneSetting.cacheLevelTranslator(activity)
    val ttlTranslator = DNSRecord.ttlTranslator(activity)

    var cacheLevel = ZoneSetting.CACHE_LEVEL_BASIC
        set(value) {
            val oldValue = field

            if (value != field && isFinishedInitializing)
                ZoneSettingRequest(activity).launch {
                    cacheLevelSpinner.isEnabled = false

                    val response = update(domain.id, ZoneSetting.cacheLevel(value))

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

    var browserCacheTtl = DNSRecord.TTL_RESPECT_EXISTING_HEADERS
        set(value) {
            val oldValue = field

            if (value != field && isFinishedInitializing)
                ZoneSettingRequest(activity).launch {
                    browserCacheTtlSpinner.isEnabled = false

                    val response = update(domain.id, ZoneSetting.browserCacheTtl(value))
                    if (response.success) field = value
                    else {
                        field = oldValue

                        browserCacheTtlSpinner.setSelection(
                            activity.browserCacheTtlAdapter.getPosition(
                                ttlTranslator.getReadable(
                                    oldValue
                                )
                            )
                        )

                        activity.dialog.error(
                            title = "Can't set browser cache ttl",
                            message = response.firstErrorMessage
                        )
                    }

                    browserCacheTtlSpinner.isEnabled = true
                }
            else {
                field = value
                browserCacheTtlSpinner.isEnabled = true
            }
        }

    var alwaysOnline = false
        set(value) {
            if (value != field && isFinishedInitializing)
                ZoneSettingRequest(activity).launch {
                    alwaysOnlineSwitch.switchIsEnabled = false

                    val response = update(domain.id, ZoneSetting.alwaysOnline(value.toApiValue()))

                    if (response.success) field = value
                    else {
                        field = !value

                        activity.dialog.error(
                            title = "Can't set always online",
                            message = response.firstErrorMessage
                        )
                    }

                    alwaysOnlineSwitch.switchIsEnabled = true
                }
            else {
                field = value
                alwaysOnlineSwitch.switchIsEnabled = true
            }
        }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        val selectedItem = parent.getItemAtPosition(pos)

        when (parent.id) {
            R.id.cache_level_spinner -> {
                cacheLevel = cacheLevelTranslator.getId(selectedItem.toString())
            }
            R.id.browser_cache_ttl_spinner -> {
                browserCacheTtl = ttlTranslator.getId(selectedItem.toString())
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        Timber.d("Nothing selected")
    }

}