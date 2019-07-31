package dev.jtsalva.cloudmare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jtsalva.cloudmare.adapter.DNSListAdapter
import dev.jtsalva.cloudmare.api.dns.DNSRecord
import dev.jtsalva.cloudmare.api.dns.DNSRecordRequest
import kotlinx.android.synthetic.main.activity_dns_list.*

class DNSListActivity : CloudMareActivity() {

    override val TAG = "DNSListActivity"

    private lateinit var domainId: String

    private lateinit var domainName: String

    private lateinit var records: MutableList<DNSRecord>

    enum class Request(
        val code: Int
    ) {
        EDIT_RECORD(0),
        CREATE_RECORD(1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) {
            Log.d(TAG, "onActivityResult: null data")
            return
        }

        when (resultCode) {
            DNSRecordActivity.Result.CHANGES_MADE.code -> launch {
                val position = data.getIntExtra("position", -1)
                val dnsRecordId = data.getStringExtra("dns_record_id") ?: records[position].id

                val response = DNSRecordRequest(this).get(domainId, dnsRecordId)
                if (response.failure || response.result == null) {
                    Log.e(TAG, "can't fetch DNS Record")
                    return@launch
                }

                records[position] = response.result
                dns_list.adapter?.notifyItemChanged(position) ?: Log.e(TAG, "Can't notify change")
            }

            DNSRecordActivity.Result.CREATED.code -> launch {
                val dnsRecordId = data.getStringExtra("dns_record_id") ?: throw Exception("dns_record_id must be set")

                val response = DNSRecordRequest(this).get(domainId, dnsRecordId)
                if (response.failure || response.result == null) {
                    Log.e(TAG, "can't fetch DNS Record")
                    return@launch
                }

                records.add(0, response.result)
                dns_list.adapter?.notifyItemInserted(0) ?: Log.e(TAG, "Can't notify creation")
                dns_list.layoutManager?.scrollToPosition(0) ?: Log.e(TAG, "Can't scroll to top")
            }

            DNSRecordActivity.Result.DELETED.code -> {
                val position = data.getIntExtra("position", -1)

                records.removeAt(position)
                dns_list.adapter?.let { adapter ->
                    adapter.notifyItemRemoved(position)
                    adapter.notifyItemRangeChanged(position, records.size)
                } ?: Log.e(TAG, "Can't notify delete")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_add -> {
            Log.d(TAG, "add action clicked")
            startActivityWithExtrasForResult(DNSRecordActivity::class.java, Request.CREATE_RECORD.code,
                "domain_id" to domainId,
                "domain_name" to domainName
            )
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with (intent) {
            domainId = getStringExtra("domain_id") ?: ""
            domainName = getStringExtra("domain_name") ?: ""
        }

        showAddMenuButton = true

        setLayout(R.layout.activity_dns_list)
        setToolbarTitle("$domainName | DNS Records")

        renderList()
    }

    private fun renderList() = launch {
        val response = DNSRecordRequest(this).list(domainId)
        if (response.failure || response.result == null) {
            Log.e(TAG, "can't list DNS Records")
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::recreate)
        }

        Log.d(TAG, response.result.toString())

        records = response.result as MutableList<DNSRecord>

        dns_list.adapter = DNSListAdapter(this, domainId, domainName, records)
        dns_list.layoutManager = LinearLayoutManager(this)
    }


}
