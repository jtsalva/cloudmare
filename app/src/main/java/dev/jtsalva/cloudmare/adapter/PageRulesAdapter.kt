package dev.jtsalva.cloudmare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.jtsalva.cloudmare.PageRulesActivity
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.pagerules.PageRule
import dev.jtsalva.cloudmare.api.pagerules.PageRuleRequest
import dev.jtsalva.cloudmare.api.zone.Zone

class PageRulesAdapter(
    private val activity: PageRulesActivity,
    private val domain: Zone,
    private val pageRules: MutableList<PageRule>
) : RecyclerView.Adapter<PageRulesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.page_rules_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pageRule = pageRules[position]

        holder.apply {
            priority.text = pageRule.priority.toString()

            status.isChecked = pageRule.status == PageRule.ACTIVE

            status.setOnClickListener {
                pageRule.status = if (status.isChecked) PageRule.ACTIVE else PageRule.DISABLED

                PageRuleRequest(activity).launch {
                    update(domain.id, pageRule).let { response ->
                        if (response.failure) {
                            status.toggle()
                            activity.dialog.error(message = response.firstErrorMessage)
                        }
                    }
                }
            }

            target.text = pageRule.targets[0].constraint.value.fit(maxLength = 28)

            info.text = pageRule.actions[0].run {
                "$id: $value".fit(maxLength = 28)
            }
        }

        // TODO: set onclick listener
    }

    override fun getItemCount(): Int = pageRules.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val priority: TextView = itemView.findViewById(R.id.priority)
        val status: Switch = itemView.findViewById(R.id.status_switch)
        val target: TextView = itemView.findViewById(R.id.target)
        val info: TextView = itemView.findViewById(R.id.info)

    }

}