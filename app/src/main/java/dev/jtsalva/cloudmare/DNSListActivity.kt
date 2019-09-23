package dev.jtsalva.cloudmare

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jtsalva.cloudmare.adapter.DNSListAdapter
import dev.jtsalva.cloudmare.api.dns.DNSRecord
import dev.jtsalva.cloudmare.api.dns.DNSRecordRequest
import dev.jtsalva.cloudmare.api.zone.Zone
import kotlinx.android.synthetic.main.activity_dns_list.*
import timber.log.Timber

class DNSListActivity : CloudMareActivity(), SwipeRefreshable {

    private lateinit var domain: Zone

    private lateinit var records: MutableList<DNSRecord>

    private var fromActivityResult = false
        get() {
            val current = field
            field = false
            return current
        }

    private val initialized: Boolean get() = dns_list.adapter is DNSListAdapter

    private val pagination by lazy {
        object : Pagination(this, dns_list) {

            override fun fetchNextPage(pageNumber: Int) = launch {
                DNSRecordRequest(this@DNSListActivity).
                    list(domain.id, pageNumber).run {
                    if (failure || result == null)
                        dialog.error(message = firstErrorMessage)
                    else if (result.isNotEmpty()) result.let { nextPage ->
                        val positionStart = records.size
                        records.addAll(nextPage)
                        dns_list.adapter?.notifyItemRangeInserted(positionStart, records.size)
                    } else reachedLastPage = true
                }

                fetchingNextPage = false
            }

        }
    }

    companion object Request {
        const val EDIT_RECORD = 0
        const val CREATE_RECORD = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        fromActivityResult = true

        if (data == null) {
            Timber.d("onActivityResult: null data")
            return
        }

        when (resultCode) {
            DNSRecordActivity.CHANGES_MADE -> {
                val dnsRecord = data.getParcelableExtra<DNSRecord>("dns_record")!!
                val position = records.indexOfFirst { it.id == dnsRecord.id }

                records[position] = dnsRecord
                dns_list.adapter!!.notifyItemChanged(position)
            }

            DNSRecordActivity.CREATED -> {
                val dnsRecord = data.getParcelableExtra<DNSRecord>("dns_record")!!

                records.add(0, dnsRecord)
                dns_list.adapter!!.notifyItemInserted(0)
                dns_list.layoutManager!!.scrollToPosition(0)
            }

            DNSRecordActivity.DELETED -> {
                val dnsRecord = data.getParcelableExtra<DNSRecord>("dns_record")!!
                val position = records.indexOfFirst { it.id == dnsRecord.id }

                records.removeAt(position)
                dns_list.adapter!!.run {
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, records.size)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_add -> {
            Timber.d("add action clicked")
            startActivityWithExtrasForResult(DNSRecordActivity::class, CREATE_RECORD,
                "domain" to domain
            )
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        domain = intent.getParcelableExtra("domain")!!

        showAddMenuButton = true

        setLayout(R.layout.activity_dns_list)
        setToolbarTitle("${domain.name} | DNS Records")
    }

    override fun onStart() {
        super.onStart()

        if (!fromActivityResult) render()
    }

    override fun onSwipeRefresh() {
        super.onSwipeRefresh()
        pagination.resetPage()
    }

    override fun render() = launch {
        val response = DNSRecordRequest(this).list(domain.id)
        if (response.failure || response.result == null)
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)

        else (response.result as MutableList<DNSRecord>).also { result ->
            if (!initialized) dns_list.apply {
                records = result

                adapter = DNSListAdapter(this@DNSListActivity, domain, records)
                layoutManager = LinearLayoutManager(this@DNSListActivity)
                addOnScrollListener(pagination)
            } else if (result != records) records.apply {
                clear()
                addAll(0, result)

                dns_list.adapter!!.notifyDataSetChanged()
            }
        }

        showProgressBar = false
    }


}
