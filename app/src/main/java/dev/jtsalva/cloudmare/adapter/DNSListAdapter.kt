package dev.jtsalva.cloudmare.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.jtsalva.cloudmare.*
import dev.jtsalva.cloudmare.api.dns.DNSRecord
import java.io.InvalidObjectException

class DNSListAdapter(
    private val context: Context,
    private val domainId: String,
    private val domainName: String,
    private val records: MutableList<DNSRecord>
) : RecyclerView.Adapter<DNSListAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "DNSListAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.dns_list_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: called")

        val record = records[position]

        val typeText = record.type
        val nameText = record.name.substringBefore(".$domainName")
        val contentText = record.content

        holder.apply {
            // TODO: standardise string length ans size setting
            type.text = if (typeText.length > 22) "${typeText.subSequence(0, 20)}..." else typeText
            name.text = if (nameText == domainName) "@" else nameText
            content.text = if (contentText.length > 22) "${contentText.subSequence(0, 20)}..." else contentText
        }

        holder.itemView.setOnClickListener {
            if (context is Activity) context.startActivityWithExtrasForResult(
                DNSRecordActivity::class.java,  DNSListActivity.Request.EDIT_RECORD.code,
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