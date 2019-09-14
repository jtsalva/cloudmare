package dev.jtsalva.cloudmare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.jtsalva.cloudmare.DNSListActivity
import dev.jtsalva.cloudmare.DNSRecordActivity
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.dns.DNSRecord
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.startActivityWithExtrasForResult

class DNSListAdapter(
    private val activity: DNSListActivity,
    private val domain: Zone,
    private val records: MutableList<DNSRecord>
) : RecyclerView.Adapter<DNSListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.dns_list_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]

        holder.apply {
            type.text = fitText(record.type)
            name.text = record.name.substringBefore(".${domain.name}").let { name ->
                if (name == domain.name) "@" else fitText(name)
            }
            content.text = fitText(record.content)

        }

        holder.itemView.setOnClickListener {
            activity.startActivityWithExtrasForResult(
                DNSRecordActivity::class,  DNSListActivity.EDIT_RECORD,
                    "domain" to domain,
                    "dns_record" to record
            )
        }
    }

    override fun getItemCount(): Int = records.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val type: TextView = itemView.findViewById(R.id.type)
        val name: TextView = itemView.findViewById(R.id.name)
        val content: TextView = itemView.findViewById(R.id.content)

    }

}