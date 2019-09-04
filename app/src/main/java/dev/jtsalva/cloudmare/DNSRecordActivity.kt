package dev.jtsalva.cloudmare

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import dev.jtsalva.cloudmare.api.dns.DNSRecord
import dev.jtsalva.cloudmare.api.dns.DNSRecordRequest
import dev.jtsalva.cloudmare.databinding.ActivityDnsRecordBinding
import dev.jtsalva.cloudmare.viewmodel.DNSRecordViewModel
import kotlinx.android.synthetic.main.activity_dns_record.*
import timber.log.Timber

class DNSRecordActivity : CloudMareActivity() {

    private lateinit var domainId: String

    private lateinit var domainName: String

    private lateinit var dnsRecordId: String

    private val isNewRecord: Boolean get() = dnsRecordId.isEmpty()

    private lateinit var binding: ActivityDnsRecordBinding

    private lateinit var viewModel: DNSRecordViewModel

    private val initialized: Boolean get() = ::viewModel.isInitialized

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

    companion object Result {
        const val CHANGES_MADE = 0
        const val CREATED = 1
        const val DELETED = 2
    }

    override fun onBackPressed() {
        if (initialized && viewModel.dataHasChanged)
            dialog.confirm(message = "Changes will be lost", positive = "Yes, go back") { confirmed ->
                if (confirmed) super.onBackPressed()
            }

        else super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_save -> {
            saveRecord()
            true
        }

        R.id.action_delete -> {
            deleteRecord()
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
    }

    override fun onResume() {
        super.onResume()

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

        when (type) {
            DNSRecord.A -> {
                setContentHint("IPv4 address")
                setPriorityVisibility(false)
                setProxiedVisibility(true)
            }

            DNSRecord.AAAA -> {
                setContentHint("IPv6 address")
                setPriorityVisibility(false)
                setProxiedVisibility(true)
            }

            DNSRecord.CNAME -> {
                setContentHint("Domain name")
                setPriorityVisibility(false)
                setProxiedVisibility(true)
            }

            DNSRecord.MX -> {
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

        ttl_spinner.apply {
            isEnabled = !proxied
            isClickable = !proxied
        }
        if (proxied) {
            val ttlString = DNSRecord.Ttl.AUTOMATIC.toString(this@DNSRecordActivity)
            ttl_spinner.setSelection(dnsRecordTtlAdapter.getPosition(ttlString))
        }
    }

    private fun renderForm() = launch {
        val data: DNSRecord =
            if (isNewRecord) DNSRecord.default.apply { zoneName = domainName }
            else DNSRecordRequest(this).get(domainId, dnsRecordId).let { response ->
                response.result ?: DNSRecord.default.also {
                    dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onResume)
                }
            }

        Timber.d("DNSRecord: $data")

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

            ttl_spinner.apply {
                setAdapter(adapter)
                setSelection(adapter.getPosition(ttlString))
                onItemSelectedListener = viewModel
            }
        }
    }

    private fun deleteRecord() {
        if (isNewRecord) finish()

        else dialog.confirm(positive = "Yes delete") { confirmed ->
            if (confirmed) launch {
                dialog.loading(title = "Deleting...")

                val response = DNSRecordRequest(this).delete(domainId, dnsRecordId)

                if (response.success) {
                    setResult(DELETED, intent)
                    finish()
                }
                else dialog.error(message = response.firstErrorMessage)
            }
        }
    }

    private fun saveRecord() = launch {
        Timber.d("viewModel.data: ${viewModel.data}")
        dialog.loading(title = "Saving...")

        val response = DNSRecordRequest(this).run {
            if (isNewRecord) create(domainId, viewModel.data)
            else update(domainId, viewModel.data)
        }

        if (response.failure || response.result == null)
            dialog.error(message = response.firstErrorMessage, positive = "Understood").also {
                Timber.e("Could not save DNS Record: ${response.errors}")
                return@launch
            }
        else if (isNewRecord)
            setResult(CREATED, Intent().putExtras(
                "dns_record_id" to response.result.id
            ))
        else setResult(CHANGES_MADE, intent)

        finish()
    }
}
