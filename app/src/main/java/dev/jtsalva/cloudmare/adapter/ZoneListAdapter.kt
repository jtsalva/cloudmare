package dev.jtsalva.cloudmare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.jtsalva.cloudmare.ZoneDashActivity
import dev.jtsalva.cloudmare.ZoneListActivity
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.startActivityWithExtras

class ZoneListAdapter(
    private val activity: ZoneListActivity,
    private val zones: MutableList<Zone>
) : RecyclerView.Adapter<ZoneListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.zone_list_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val zone = zones[position]

        holder.name.text = zone.name.fit(75)
        holder.itemView.setOnClickListener {
            activity.startActivityWithExtras(ZoneDashActivity::class,
                "zone" to zone
            )
        }
    }

    override fun getItemCount(): Int = zones.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.name)

    }

}