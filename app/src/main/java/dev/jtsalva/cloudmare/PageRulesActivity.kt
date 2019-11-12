package dev.jtsalva.cloudmare

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jtsalva.cloudmare.adapter.PageRulesAdapter
import dev.jtsalva.cloudmare.api.pagerules.PageRule
import dev.jtsalva.cloudmare.api.pagerules.PageRuleRequest
import dev.jtsalva.cloudmare.api.zone.Zone
import kotlinx.android.synthetic.main.activity_page_rules.*

class PageRulesActivity : CloudMareActivity(), SwipeRefreshable {

    private lateinit var domain: Zone

    private lateinit var pageRules: MutableList<PageRule>

    private val initialized: Boolean get() = page_rules.adapter is PageRulesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        domain = intent.getParcelableExtra("domain")!!

//        TODO: add ability to create page rules
//        showAddMenuButton = true

        setLayout(R.layout.activity_page_rules)
        setToolbarTitle("${domain.name} | Page Rules")
    }

    override fun onStart() {
        super.onStart()

        render()
    }

    override fun render() = PageRuleRequest(this).launch {
        val response = list(domain.id)
        if (response.failure || response.result == null)
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)

        else (response.result as MutableList<PageRule>).also { result ->
            if (!initialized) page_rules.apply {
                pageRules = result

                adapter = PageRulesAdapter(this@PageRulesActivity, domain, pageRules)
                layoutManager = LinearLayoutManager(this@PageRulesActivity)
            } else if (result != pageRules) pageRules.apply {
                clear()
                addAll(result)

                page_rules.adapter!!.notifyDataSetChanged()
            }

            showNonFoundMessage = result.isEmpty()
        }

        showProgressBar = false

        swipeRefreshLayout.isRefreshing = false
    }
}
