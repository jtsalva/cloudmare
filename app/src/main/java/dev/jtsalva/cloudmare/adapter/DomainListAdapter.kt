package dev.jtsalva.cloudmare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.jtsalva.cloudmare.DomainDashActivity
import dev.jtsalva.cloudmare.DomainListActivity
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.startActivityWithExtras
import timber.log.Timber

class DomainListAdapter(
    private val activity: DomainListActivity,
    private val domains: MutableList<Zone>
) : RecyclerView.Adapter<DomainListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.domain_list_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val domain = domains[position]

        holder.name.text = domain.name
        holder.itemView.setOnClickListener {
            activity.startActivityWithExtras(DomainDashActivity::class,
                "domain" to domain
            )
        }
    }

    override fun getItemCount(): Int = domains.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.name)

    }

}