package dev.jtsalva.cloudmare

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import dev.jtsalva.cloudmare.api.dns.DNSRecord
import dev.jtsalva.cloudmare.api.dns.DNSRecordRequest
import dev.jtsalva.cloudmare.databinding.ActivityDnsRecordBinding
import dev.jtsalva.cloudmare.viewmodel.DNSRecordViewModel
import kotlinx.android.synthetic.main.activity_dns_record.*

class DNSRecordActivity : CloudMareActivity() {

    override val TAG = "DNSRecordActivity"

    private lateinit var domainId: String

    private lateinit var domainName: String

    private lateinit var dnsRecordId: String

    private lateinit var binding: ActivityDnsRecordBinding

    private lateinit var viewModel: DNSRecordViewModel

    enum class Result(
        val code: Int
    ) {
        CHANGES_MADE(0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_save -> {
            launch {
                val response = viewModel.updateRequest()
                if (response.failure || response.result == null) {
                    // TODO: tell user why unable to save
                    Log.e(TAG, "Could not save DNS Record: ${response.errors}")

                    longToast(response.firstErrorMessage)
                } else {
                    setResult(Result.CHANGES_MADE.code, intent)
                    finish()
                }
            }

            true
        }

        // TODO: delete dns record when clicked
        R.id.action_delete -> {
            Log.d(TAG, "Delete clicked")

            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        domainId = intent.getStringExtra("domain_id") ?: ""
        domainName = intent.getStringExtra("domain_name") ?: ""
        dnsRecordId = intent.getStringExtra("dns_record_id") ?: ""

        showSaveMenuButton = true
        showDeleteMenuButton = true

        binding = setLayoutBinding(R.layout.activity_dns_record)
        setToolbarTitle("$domainName | Edit DNS Record")

        renderForm(newRecord = dnsRecordId.isEmpty())
    }

    private fun renderForm(newRecord: Boolean) = launch {
        val data: DNSRecord
        if (!newRecord) {
            val response = DNSRecordRequest(this).get(domainId, dnsRecordId)

            if (response.failure || response.result == null) {
                Log.e(TAG, "can't fetch DNS Record")
                return@launch
            }

            data = response.result
        } else {
            data = DNSRecord(
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
            )
        }


        viewModel = DNSRecordViewModel(this, domainId, domainName, data)

        binding.viewModel = viewModel

        ArrayAdapter.createFromResource(
            this,
            R.array.entries_dns_record_type,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

            if (!newRecord) {
                type_spinner.isEnabled = false
                type_spinner.isClickable = false
            }

            type_spinner.adapter = adapter
            type_spinner.setSelection(adapter.getPosition(data.type))
            type_spinner.onItemSelectedListener = viewModel
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.entries_dns_record_ttl,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

            if (data.proxied) {
                ttl_spinner.isEnabled = false
                ttl_spinner.isClickable = false
            }

            val ttlString = DNSRecord.Ttl.getFromValue(data.ttl).toString(this)

            Log.d(TAG, "Ttl String: $ttlString")

            ttl_spinner.adapter = adapter
            ttl_spinner.setSelection(adapter.getPosition(ttlString))
            ttl_spinner.onItemSelectedListener = viewModel
        }
    }
}
