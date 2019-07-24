package dev.jtsalva.cloudmare.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import dev.jtsalva.cloudmare.BR
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.dns.DNSRecord
import dev.jtsalva.cloudmare.api.dns.DNSRecord.Ttl
import dev.jtsalva.cloudmare.api.dns.DNSRecordRequest
import dev.jtsalva.cloudmare.api.dns.DNSRecordResponse
import java.security.InvalidParameterException

class DNSRecordViewModel(
    private val context: Context,
    private val domainId: String,
    private val domainName: String,
    private val data: DNSRecord
) : BaseObservable(), AdapterView.OnItemSelectedListener {

    companion object {
        private const val TAG = "DNSRecordViewModel"
    }

    suspend fun updateRequest(): DNSRecordResponse =
        DNSRecordRequest(context).update(domainId, data)

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        when (parent.id) {
            R.id.type_spinner -> data.type = parent.getItemAtPosition(pos) as String

            R.id.ttl_spinner -> {
                data.ttl = when (parent.getItemAtPosition(pos) as String) {
                    Ttl.AUTOMATIC.toString(context) -> Ttl.AUTOMATIC.toInt()

                    Ttl.TWO_MINUTES.toString(context) -> Ttl.TWO_MINUTES.toInt()
                    Ttl.FIVE_MINUTES.toString(context) -> Ttl.FIVE_MINUTES.toInt()
                    Ttl.TEN_MINUTES.toString(context) -> Ttl.TEN_MINUTES.toInt()
                    Ttl.FIFTEEN_MINUTES.toString(context) -> Ttl.FIFTEEN_MINUTES.toInt()
                    Ttl.THIRTY_MINUTES.toString(context) -> Ttl.THIRTY_MINUTES.toInt()

                    Ttl.ONE_HOURS.toString(context) -> Ttl.ONE_HOURS.toInt()
                    Ttl.TWO_HOURS.toString(context) -> Ttl.TWO_HOURS.toInt()
                    Ttl.FIVE_HOURS.toString(context) -> Ttl.FIVE_HOURS.toInt()
                    Ttl.TWELVE_HOURS.toString(context) -> Ttl.TWELVE_HOURS.toInt()

                    Ttl.ONE_DAYS.toString(context) -> Ttl.ONE_DAYS.toInt()

                    else -> throw InvalidParameterException("Ttl set by user is not possible")
                }
            }
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        Log.d(TAG, "Nothing selected")
    }

    @Bindable
    fun getType(): String = "${data.type} Record"

    @Bindable
    fun getName(): String = if (data.name == domainName) "@" else data.name.substringBefore(".$domainName")

    fun setName(value: String) {
        Log.d(TAG, "intercepted set name $value")

        data.name = "$value.$domainName"
        notifyPropertyChanged(BR.name)
    }

    @Bindable
    fun getContent(): String = data.content

    fun setContent(value: String) {
        Log.d(TAG, "intercepted set content")

        data.content = value
        notifyPropertyChanged(BR.content)
    }

    @Bindable
    fun getProxied(): Boolean = data.proxied

    fun setProxied(value: Boolean) {
        Log.d(TAG, "intercepted set proxied")

        data.proxied = value
        notifyPropertyChanged(BR.proxied)
    }

}