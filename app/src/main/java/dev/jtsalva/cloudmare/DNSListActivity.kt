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
        EDIT_RECORD(0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) {
            Log.d(TAG, "onActivityResult: null data")
            return
        }

        when (requestCode) {
            Request.EDIT_RECORD.code -> {
                if (resultCode == DNSRecordActivity.Result.CHANGES_MADE.code) {
                    val position = data.getIntExtra("position", -1)
                    val dnsRecordId = data.getStringExtra("dns_record_id") ?: records[position].id

                    DNSRecordRequest(this).get(domainId, dnsRecordId) { response ->
                        if (response.failure || response.result == null) {
                            Log.e(TAG, "can't fetch DNS Record")
                            return@get
                        }

                        records[position] = response.result
                        dns_list.adapter?.notifyItemChanged(position) ?: Log.e(TAG, "Can't notify change")
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        // TODO: start activity to add record
        R.id.action_add -> {
            Log.d(TAG, "add action clicked")

            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        domainId = intent.getStringExtra("domain_id") ?: ""
        domainName = intent.getStringExtra("domain_name") ?: ""

        showAddMenuButton = true

        setLayout(R.layout.activity_dns_list)
        setToolbarTitle("$domainName | DNS Records")

        DNSRecordRequest(this).list(domainId) { response ->
            if (response.failure || response.result == null) {
                Log.e(TAG, "can't list DNS Records")
                return@list
            }

            records = response.result as MutableList<DNSRecord>

            dns_list.adapter = DNSListAdapter(this, domainId, domainName, records)
            dns_list.layoutManager = LinearLayoutManager(this)
        }
    }


}
