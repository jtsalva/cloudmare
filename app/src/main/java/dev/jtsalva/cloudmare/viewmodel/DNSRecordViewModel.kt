package dev.jtsalva.cloudmare.viewmodel

import android.view.View
import android.widget.AdapterView
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import dev.jtsalva.cloudmare.BR
import dev.jtsalva.cloudmare.DNSRecordActivity
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.dns.DNSRecord
import dev.jtsalva.cloudmare.api.zone.Zone

class DNSRecordViewModel(
    private val activity: DNSRecordActivity,
    private val zone: Zone,
    val data: DNSRecord
) : BaseObservable(), AdapterView.OnItemSelectedListener {

    val ttlTranslator = DNSRecord.ttlTranslator(activity)

    private val originalHash = data.hashCode()

    val dataHasChanged: Boolean get() = data.hashCode() != originalHash

    var name: String
        @Bindable get() = if (data.name == zone.name) "@" else data.name.substringBefore(".${zone.name}")
        set(value) {
            data.name = if (value in setOf(zone.name, "@")) zone.name else "$value.${zone.name}"

            @Suppress("UNRESOLVED_REFERENCE")
            notifyPropertyChanged(BR.name)
        }

    var content: String
        @Bindable get() = data.content
        set(value) {
            data.content = value

            @Suppress("UNRESOLVED_REFERENCE")
            notifyPropertyChanged(BR.content)
        }

    var priority: String
        @Bindable get() = if (data.priority == null) "" else data.priority.toString()
        set(value) {
            data.priority = value.toIntOrNull()

            @Suppress("UNRESOLVED_REFERENCE")
            notifyPropertyChanged(BR.priority)
        }

    var proxied: Boolean
        @Bindable get() = data.proxied
        set(value) {
            data.proxied = value
            activity.customizeForm()

            @Suppress("UNRESOLVED_REFERENCE")
            notifyPropertyChanged(BR.proxied)
        }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        if (parent == null) return

        val selectedItem = parent.getItemAtPosition(pos)

        when (parent.id) {
            R.id.type_spinner -> {
                data.type = selectedItem as String

                activity.customizeForm()
            }

            R.id.ttl_spinner ->
                data.ttl = ttlTranslator.getId(selectedItem as String)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Intentionally blank
    }
}
