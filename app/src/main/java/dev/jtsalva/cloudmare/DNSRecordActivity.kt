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
            viewModel.updateRequest { response ->
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

        renderForm()
    }

    private fun renderForm() = DNSRecordRequest(this).get(domainId, dnsRecordId) { response ->
        if (response.failure || response.result == null) {
            Log.e(TAG, "can't fetch DNS Record")
            return@get
        }

        viewModel = DNSRecordViewModel(this, domainId, domainName, response.result)

        binding.viewModel = viewModel

        ArrayAdapter.createFromResource(
            this,
            R.array.entries_dns_record_type,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

            // TODO: if request code edit
            if (true) {
//                type_spinner.isEnabled = false
//                type_spinner.isClickable = false
            }

            type_spinner.adapter = adapter
            type_spinner.setSelection(adapter.getPosition(response.result.type))
            type_spinner.onItemSelectedListener = viewModel
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.entries_dns_record_ttl,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

            if (response.result.proxied) {
                ttl_spinner.isEnabled = false
                ttl_spinner.isClickable = false
            }

            val ttlString = DNSRecord.Ttl.getFromValue(response.result.ttl).toString(this)

            Log.d(TAG, "Ttl String: $ttlString")

            ttl_spinner.adapter = adapter
            ttl_spinner.setSelection(adapter.getPosition(ttlString))
            ttl_spinner.onItemSelectedListener = viewModel
        }
    }
}
