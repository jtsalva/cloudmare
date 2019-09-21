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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        domain = intent.getParcelableExtra("domain")!!

        showAddMenuButton = true

        setLayout(R.layout.activity_page_rules)
        setToolbarTitle("${domain.name} | Page Rules")
    }

    override fun onStart() {
        super.onStart()

        render()
    }

    override fun render() = launch {
        val response = PageRuleRequest(this).list(domain.id)
        if (response.failure || response.result == null)
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)

        else (response.result as MutableList<PageRule>).let { result ->
            pageRules = result

            page_rules.apply {
                adapter = PageRulesAdapter(this@PageRulesActivity, domain, pageRules)
                layoutManager = LinearLayoutManager(this@PageRulesActivity)
                // TODO: add pagination listener
            }
        }

        showProgressBar = false
    }

}
