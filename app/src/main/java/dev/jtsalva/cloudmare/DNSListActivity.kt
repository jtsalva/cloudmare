package dev.jtsalva.cloudmare

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jtsalva.cloudmare.adapter.DNSListAdapter
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.dns.DNSRecord
import dev.jtsalva.cloudmare.api.dns.DNSRecordRequest
import dev.jtsalva.cloudmare.api.zone.Zone
import kotlinx.android.synthetic.main.activity_dns_list.*
import timber.log.Timber

class DNSListActivity : CloudMareActivity(), SwipeRefreshable {

    private lateinit var domain: Zone

    private lateinit var records: MutableList<DNSRecord>

    private var searchQuery: String? = null
        set(value) {
            if (value != field) {
                field = value
                render()
            }
        }

    private var sortBy = DNSRecord.SORT_BY_TYPE
        set(value) {
            if (value != field) {
                showProgressBar = true
                field = value
                paginationListener.resetPage()
                render()
            }
        }

    private var fromActivityResult = false
        get() {
            val current = field
            field = false
            return current
        }

    private val sortByTranslator by lazy {
        object {

            val idToReadable = mapOf(
                DNSRecord.SORT_BY_TYPE to getString(R.string.dns_list_sort_by_type),
                DNSRecord.SORT_BY_NAME to getString(R.string.dns_list_sort_by_name),
                DNSRecord.SORT_BY_CONTENT to getString(R.string.dns_list_sort_by_content),
                DNSRecord.SORT_BY_TTL to getString(R.string.dns_list_sort_by_ttl),
                DNSRecord.SORT_BY_PROXIED to getString(R.string.dns_list_sort_by_proxied)
            )

            fun indexOfValue(value: String): Int =
                idToReadable.run {
                    var index = 0
                    forEach {
                        if (it.key == value) return index
                        index += 1
                    }

                    return -1
                }

            fun getValue(readable: String): String =
                idToReadable.filterValues { it == readable }.keys.first()

        }
    }

    private val direction get() =
        if (sortBy == DNSRecord.SORT_BY_PROXIED) Request.DIRECTION_DESCENDING
        else Request.DIRECTION_ASCENDING

    private val initialized: Boolean get() = dns_list.adapter is DNSListAdapter

    private val paginationListener by lazy {
        object : PaginationListener(this, dns_list.layoutManager as LinearLayoutManager) {

            override fun fetchNextPage(pageNumber: Int) =
                DNSRecordRequest(this@DNSListActivity).launch {
                    list(
                        domain.id,
                        pageNumber = pageNumber,
                        order = sortBy,
                        direction = direction,
                        contains = searchQuery).run {
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

    companion object {
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

                showNonFoundMessage = false
            }

            DNSRecordActivity.DELETED -> {
                val dnsRecord = data.getParcelableExtra<DNSRecord>("dns_record")!!
                val position = records.indexOfFirst { it.id == dnsRecord.id }

                records.removeAt(position)
                dns_list.adapter!!.run {
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, records.size)
                }

                showNonFoundMessage = records.isEmpty()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_add -> {
            startActivityWithExtrasForResult(DNSRecordActivity::class, CREATE_RECORD,
                "domain" to domain
            )
            true
        }

        R.id.action_sort_by -> {
            val selectedItemIndex = sortByTranslator.indexOfValue(sortBy)
            dialog.multiChoice(
                title = "Sort By",
                resId = R.array.entries_dns_list_sort_by,
                initialSelection = selectedItemIndex) { _, _, text ->
                sortBy = sortByTranslator.getValue(text)
            }
            true
        }

        R.id.action_search -> {
            search_edit_text.apply {
                if (visibility == View.VISIBLE) {
                    visibility = View.GONE
                    search_edit_text.setText("")
                    searchQuery = null
                }
                else visibility = View.VISIBLE
            }

            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        domain = intent.getParcelableExtra("domain")!!

        menuButtonInitializer.onInflateSetVisible(
            R.id.action_search,
            R.id.action_sort_by,
            R.id.action_add
        )

        setLayout(R.layout.activity_dns_list)
        setToolbarTitle("${domain.name} | DNS Records")

        search_edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Blank
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Blank
            }

            override fun afterTextChanged(s: Editable?) {
                if (s == null) return

                searchQuery = s.toString()
            }
        })
    }

    override fun onStart() {
        super.onStart()

        if (!fromActivityResult) render()
    }

    override fun render() = DNSRecordRequest(this).launch {
        cancelAll(Request.LIST)
        val response = list(
            domain.id,
            order = sortBy,
            direction = direction,
            contains = searchQuery
        )
        if (response.failure || response.result == null)
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)

        else (response.result as MutableList<DNSRecord>).also { result ->
            if (!initialized) dns_list.apply {
                records = result

                adapter = DNSListAdapter(this@DNSListActivity, domain, records)
                layoutManager = LinearLayoutManager(this@DNSListActivity)
                addOnScrollListener(paginationListener)
            } else if (result != records) records.apply {
                paginationListener.resetPage()
                dns_list.layoutManager!!.scrollToPosition(0)

                clear()
                addAll(0, result)

                dns_list.adapter!!.notifyDataSetChanged()
            }

            showNonFoundMessage = result.isEmpty()
        }

        showProgressBar = false

        swipeRefreshLayout.isRefreshing = false
    }


}
