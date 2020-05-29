package dev.jtsalva.cloudmare

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import dev.jtsalva.cloudmare.api.dns.DNSRecord
import dev.jtsalva.cloudmare.api.dns.DNSRecordRequest
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.databinding.ActivityDnsRecordBinding
import dev.jtsalva.cloudmare.viewmodel.DNSRecordViewModel
import kotlinx.android.synthetic.main.activity_dns_record.*

class DNSRecordActivity : CloudMareActivity() {

    private lateinit var zone: Zone

    private lateinit var dnsRecord: DNSRecord

    private val isNewRecord get() = dnsRecord.id.isEmpty()

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

        with(intent) {
            zone = getParcelableExtra("zone")!!

            dnsRecord = getParcelableExtra("dns_record")
                ?: DNSRecord.default.apply { zoneName = zone.name }
        }

        menuButtonInitializer.onInflateSetVisible(
            R.id.action_save,
            R.id.action_delete
        )

        binding = setLayoutBinding(R.layout.activity_dns_record)
        setToolbarTitle("${zone.name} | ${if (isNewRecord) "Create" else "Edit"}")
    }

    override fun onStart() {
        super.onStart()

        render()
    }

    fun customizeForm() = with(viewModel.data) {
        fun setValues(contentHint: String, isPriorityVisibile: Boolean, isProxiedVisible: Boolean) {
            content_edit_text.hint = contentHint
            priority_label.isVisible = isPriorityVisibile
            priority_edit_text.isVisible = isPriorityVisibile
            proxied_switch.isVisible = isProxiedVisible
        }

        when (type) {
            DNSRecord.TYPE_A -> setValues(
                contentHint = "Ipv4 address",
                isPriorityVisibile = false,
                isProxiedVisible = true
            )

            DNSRecord.TYPE_AAAA -> setValues(
                contentHint = "IPv6 address",
                isPriorityVisibile = false,
                isProxiedVisible = true
            )

            DNSRecord.TYPE_CNAME -> setValues(
                contentHint = "Domain name",
                isPriorityVisibile = false,
                isProxiedVisible = true
            )

            DNSRecord.TYPE_MX -> setValues(
                contentHint = "Content",
                isPriorityVisibile = true,
                isProxiedVisible = false
            )

            else -> setValues(
                contentHint = "Content",
                isPriorityVisibile = false,
                isProxiedVisible = false
            )
        }

        ttl_spinner.apply {
            isEnabled = !proxied
            isClickable = !proxied
        }
        if (proxied) {
            val ttlString = viewModel.ttlTranslator.getReadable(DNSRecord.TTL_AUTOMATIC)
            ttl_spinner.setSelection(dnsRecordTtlAdapter.getPosition(ttlString))
        }
    }

    private fun render() {
        viewModel = DNSRecordViewModel(this, zone, dnsRecord)

        customizeForm()

        binding.viewModel = viewModel

        dnsRecordTypeAdapter.let { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

            type_spinner.apply {
                isEnabled = isNewRecord
                isClickable = isNewRecord

                setAdapter(adapter)
                setSelection(adapter.getPosition(dnsRecord.type))
                onItemSelectedListener = viewModel
            }
        }

        dnsRecordTtlAdapter.let { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

            val ttlString = viewModel.ttlTranslator.getReadable(dnsRecord.ttl)

            ttl_spinner.apply {
                setAdapter(adapter)
                setSelection(adapter.getPosition(ttlString))
                onItemSelectedListener = viewModel
            }
        }

        dns_record_form_group.isVisible = true

        showProgressBar = false
    }

    private fun deleteRecord() {
        if (isNewRecord) finish()

        else dialog.confirm(positive = "Yes delete") { confirmed ->
            if (confirmed) DNSRecordRequest(this).launch {
                dialog.loading(title = "Deleting…")

                val response = delete(zone.id, dnsRecord.id)

                if (response.success) {
                    setResult(DELETED, Intent().putExtras(
                        "dns_record" to dnsRecord
                    ))
                    finish()
                } else dialog.error(message = response.firstErrorMessage)
            }
        }
    }

    private fun saveRecord() = DNSRecordRequest(this).launch {
        dialog.loading(title = "Saving…")

        val response =
            if (isNewRecord) create(zone.id, viewModel.data)
            else update(zone.id, viewModel.data)

        if (response.failure || response.result == null)
            dialog.error(message = response.firstErrorMessage, positive = "Okay")
        else {
            setResult(
                if (isNewRecord) CREATED else CHANGES_MADE,
                Intent().putExtras(
                    "dns_record" to response.result
                )
            )

            finish()
        }
    }
}
