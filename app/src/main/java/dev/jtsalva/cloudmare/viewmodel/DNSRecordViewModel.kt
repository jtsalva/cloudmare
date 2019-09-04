package dev.jtsalva.cloudmare.viewmodel

import android.view.View
import android.widget.AdapterView
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import dev.jtsalva.cloudmare.BR
import dev.jtsalva.cloudmare.DNSRecordActivity
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.dns.DNSRecord
import dev.jtsalva.cloudmare.api.dns.DNSRecord.Ttl
import timber.log.Timber
import java.security.InvalidParameterException

class DNSRecordViewModel(
    private val activity: DNSRecordActivity,
    private val domainId: String,
    private val domainName: String,
    val data: DNSRecord
) : BaseObservable(), AdapterView.OnItemSelectedListener {

    private val originalHash = data.hashCode()

    val dataHasChanged: Boolean get() = data.hashCode() != originalHash

    var name: String
        @Bindable get() = if (data.name == domainName) "@" else data.name.substringBefore(".$domainName")
        set(value) {
            data.name = if (value in setOf(domainName, "@")) domainName else "$value.$domainName"

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

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        when (parent.id) {
            R.id.type_spinner -> {
                data.type = parent.getItemAtPosition(pos) as String

                activity.customizeForm()
            }

            R.id.ttl_spinner -> {
                data.ttl = when (parent.getItemAtPosition(pos) as String) {
                    Ttl.AUTOMATIC.toString(activity) -> Ttl.AUTOMATIC.toInt()

                    Ttl.TWO_MINUTES.toString(activity) -> Ttl.TWO_MINUTES.toInt()
                    Ttl.FIVE_MINUTES.toString(activity) -> Ttl.FIVE_MINUTES.toInt()
                    Ttl.TEN_MINUTES.toString(activity) -> Ttl.TEN_MINUTES.toInt()
                    Ttl.FIFTEEN_MINUTES.toString(activity) -> Ttl.FIFTEEN_MINUTES.toInt()
                    Ttl.THIRTY_MINUTES.toString(activity) -> Ttl.THIRTY_MINUTES.toInt()

                    Ttl.ONE_HOURS.toString(activity) -> Ttl.ONE_HOURS.toInt()
                    Ttl.TWO_HOURS.toString(activity) -> Ttl.TWO_HOURS.toInt()
                    Ttl.FIVE_HOURS.toString(activity) -> Ttl.FIVE_HOURS.toInt()
                    Ttl.TWELVE_HOURS.toString(activity) -> Ttl.TWELVE_HOURS.toInt()

                    Ttl.ONE_DAYS.toString(activity) -> Ttl.ONE_DAYS.toInt()

                    else -> throw InvalidParameterException("Ttl is not possible")
                }
            }
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        Timber.d("Nothing selected")
    }

}