package dev.jtsalva.cloudmare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import dev.jtsalva.cloudmare.api.dns.DNSRecord
import dev.jtsalva.cloudmare.api.dns.DNSRecord.Type
import dev.jtsalva.cloudmare.api.dns.DNSRecordRequest
import dev.jtsalva.cloudmare.databinding.ActivityDnsRecordBinding
import dev.jtsalva.cloudmare.viewmodel.DNSRecordViewModel
import kotlinx.android.synthetic.main.activity_dns_record.*

class DNSRecordActivity : CloudMareActivity() {

    override val TAG = "DNSRecordActivity"

    private lateinit var domainId: String

    private lateinit var domainName: String

    private lateinit var dnsRecordId: String

    private val isNewRecord: Boolean get() = dnsRecordId.isEmpty()

    private lateinit var binding: ActivityDnsRecordBinding

    private lateinit var viewModel: DNSRecordViewModel

    private val dnsRecordTypeAdapter by lazy {
        ArrayAdapter.createFromResource(
            this,
            R.array.entries_dns_record_type,
            R.layout.spinner_item
        )
    }

    private val dnsRecordTtlAdapter by lazy {
        ArrayAdapter.createFromResource(
            this,
            R.array.entries_dns_record_ttl,
            R.layout.spinner_item
        )
    }

    enum class Result(
        val code: Int
    ) {
        CHANGES_MADE(0),
        CREATED(1),
        DELETED(2)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_save -> {
            saveRecord()

            true
        }

        R.id.action_delete -> {
            if (isNewRecord) finish()

            else dialog.confirm(positive = "Yes delete") { confirmed ->
                if (confirmed) launch {
                    val response = DNSRecordRequest(this).delete(domainId, dnsRecordId)

                    if (response.success) {
                        setResult(Result.DELETED.code, intent)
                        finish()
                    }
                    else dialog.error(message = response.firstErrorMessage)
                }
            }

            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with (intent) {
            domainId = getStringExtra("domain_id") ?: ""
            domainName = getStringExtra("domain_name") ?: ""
            dnsRecordId = getStringExtra("dns_record_id") ?: ""
        }

        showSaveMenuButton = true
        showDeleteMenuButton = true

        binding = setLayoutBinding(R.layout.activity_dns_record)
        setToolbarTitle("$domainName | ${if (isNewRecord) "Create" else "Edit"}")

        renderForm()
    }

    fun customizeForm() = with (viewModel.data) {
        fun setPriorityVisibility(isVisible: Boolean) {
            priority_label.isVisible = isVisible
            priority_edit_text.isVisible = isVisible
        }

        fun setProxiedVisibility(isVisible: Boolean) {
            proxied_switch.isVisible = isVisible
        }

        fun setContentHint(hint: String) = content_edit_text.apply { setHint(hint) }

        when (Type.fromString(type)) {
            Type.A -> {
                setContentHint("IPv4 address")
                setPriorityVisibility(false)
                setProxiedVisibility(true)
            }

            Type.AAAA -> {
                setContentHint("IPv6 address")
                setPriorityVisibility(false)
                setProxiedVisibility(true)
            }

            Type.CNAME -> {
                setContentHint("Domain name")
                setPriorityVisibility(false)
                setProxiedVisibility(true)
            }

            Type.MX -> {
                setContentHint("Content")
                setPriorityVisibility(true)
                setProxiedVisibility(false)
            }

            else -> {
                setContentHint("Content")
                setPriorityVisibility(false)
                setProxiedVisibility(false)
            }
        }

        ttl_spinner.isEnabled = !proxied
        ttl_spinner.isClickable = !proxied
        if (proxied) {
            val ttlString = DNSRecord.Ttl.AUTOMATIC.toString(this@DNSRecordActivity)
            ttl_spinner.setSelection(dnsRecordTtlAdapter.getPosition(ttlString))
        }
    }

    private fun renderForm() = launch {
        val data: DNSRecord =
            if (isNewRecord) DNSRecord(
                id = "",
                type = DNSRecord.Type.A.toString(),
                name = "",
                content = "",
                proxiable = true,
                proxied = false,
                ttl = DNSRecord.Ttl.AUTOMATIC.toInt(),
                locked = false,
                zoneId = domainId,
                zoneName = domainName
            ) else DNSRecordRequest(this).get(domainId, dnsRecordId).let { response ->
                response.result ?: dialog.error(message = response.firstErrorMessage, onAcknowledge = ::recreate).run { return@launch }
            }

        Log.d(TAG, "Data: $data")

        viewModel = DNSRecordViewModel(this, domainId, domainName, data)

        customizeForm()

        binding.viewModel = viewModel

        dns_record_form_group.isVisible = true

        dnsRecordTypeAdapter.let { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

            type_spinner.apply {
                isEnabled = isNewRecord
                isClickable = isNewRecord

                setAdapter(adapter)
                setSelection(adapter.getPosition(data.type))
                onItemSelectedListener = viewModel
            }
        }

        dnsRecordTtlAdapter.let { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

            val ttlString = DNSRecord.Ttl.fromValue(data.ttl).toString(this)

            Log.d(TAG, "Ttl String: $ttlString")

            ttl_spinner.apply {
                setAdapter(adapter)
                setSelection(adapter.getPosition(ttlString))
                onItemSelectedListener = viewModel
            }
        }
    }

    private fun saveRecord() = launch {
        Log.d(TAG, "viewModel.data: ${viewModel.data}")
        val response = DNSRecordRequest(this).run {
            if (isNewRecord) create(domainId, viewModel.data)
            else update(domainId, viewModel.data)
        }

        if (response.failure || response.result == null) {
            Log.e(TAG, "Could not save DNS Record: ${response.errors}")

            dialog.error(message = response.firstErrorMessage)
        } else {
            if (isNewRecord)
                setResult(Result.CREATED.code, Intent().putExtras(
                    "dns_record_id" to response.result.id
                ))
            else setResult(Result.CHANGES_MADE.code, intent)

            finish()
        }
    }
}
