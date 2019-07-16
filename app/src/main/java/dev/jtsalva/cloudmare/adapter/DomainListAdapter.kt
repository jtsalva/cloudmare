package dev.jtsalva.cloudmare.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.jtsalva.cloudmare.DomainDashActivity
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.putStringExtras

class DomainListAdapter(
    private val context: Context,
    private val domains: MutableList<Pair<String, String>>
) : RecyclerView.Adapter<DomainListAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "DomainListAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.domain_list_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: called")

        holder.name.text = domains[position].second
        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(context, DomainDashActivity::class.java).putStringExtras(
                    "domain_id", domains[position].first,
                    "domain_name", domains[position].second
                )
            )
        }
    }

    override fun getItemCount(): Int = domains.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.name)

    }

}