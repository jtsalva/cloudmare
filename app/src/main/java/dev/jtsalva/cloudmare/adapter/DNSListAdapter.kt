package dev.jtsalva.cloudmare.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.jtsalva.cloudmare.DNSListActivity
import dev.jtsalva.cloudmare.DNSRecordActivity
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.dns.DNSRecord
import dev.jtsalva.cloudmare.startActivityWithExtrasForResult
import timber.log.Timber
import java.io.InvalidObjectException

class DNSListAdapter(
    private val context: Context,
    private val domainId: String,
    private val domainName: String,
    private val records: MutableList<DNSRecord>
) : RecyclerView.Adapter<DNSListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.dns_list_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Timber.d("onBindViewHolder: called")

        val record = records[position]

        holder.apply {
            type.text = fitText(record.type)
            name.text = record.name.substringBefore(".$domainName").let { name ->
                if (name == domainName) "@" else fitText(name)
            }
            content.text = fitText(record.content)

        }

        holder.itemView.setOnClickListener {
            if (context is Activity) context.startActivityWithExtrasForResult(
                DNSRecordActivity::class.java,  DNSListActivity.EDIT_RECORD,
                    "domain_id" to domainId,
                    "domain_name" to domainName,
                    "dns_record_id" to record.id,
                    "position" to position
            ) else throw InvalidObjectException("context should be subclass of Activity")
        }
    }

    override fun getItemCount(): Int = records.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val type: TextView = itemView.findViewById(R.id.type)
        val name: TextView = itemView.findViewById(R.id.name)
        val content: TextView = itemView.findViewById(R.id.content)

    }

}