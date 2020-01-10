package dev.jtsalva.cloudmare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import dev.jtsalva.cloudmare.DNSListActivity
import dev.jtsalva.cloudmare.DNSRecordActivity
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.dns.DNSRecord
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.startActivityWithExtrasForResult

class DNSListAdapter(
    private val activity: DNSListActivity,
    private val zone: Zone,
    private val records: MutableList<DNSRecord>
) : RecyclerView.Adapter<DNSListAdapter.ViewHolder>() {

    companion object {
        const val NO_BIAS = 0.5f
        const val UPSHIFT_BIAS = 0.3f
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.dns_list_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]

        holder.apply {
            type.text = record.type.fit()
            name.text = record.name.substringBefore(".${zone.name}").let { name ->
                if (name == zone.name) "@" else name.fit()
            }
            content.text = record.content.fit()
            val constraintSet = ConstraintSet().apply { clone(dnsListItem) }
            if (record.priority != null) {
                priority.text = record.priority.toString()
                constraintSet.setVerticalBias(R.id.type, UPSHIFT_BIAS)
                constraintSet.applyTo(dnsListItem)
            } else {
                priority.text = ""
                constraintSet.setVerticalBias(R.id.type, NO_BIAS)
                constraintSet.applyTo(dnsListItem)
            }
        }

        holder.itemView.setOnClickListener {
            activity.startActivityWithExtrasForResult(
                DNSRecordActivity::class,  DNSListActivity.EDIT_RECORD,
                    "zone" to zone,
                    "dns_record" to record
            )
        }
    }

    override fun getItemCount(): Int = records.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val dnsListItem: ConstraintLayout = itemView.findViewById(R.id.dns_list_item)
        val type: TextView = itemView.findViewById(R.id.type)
        val name: TextView = itemView.findViewById(R.id.name)
        val content: TextView = itemView.findViewById(R.id.content)
        val priority: TextView = itemView.findViewById(R.id.priority)

    }

}