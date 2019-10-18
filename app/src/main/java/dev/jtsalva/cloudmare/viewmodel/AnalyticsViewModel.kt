package dev.jtsalva.cloudmare.viewmodel

import android.view.View
import android.widget.AdapterView
import androidx.databinding.BaseObservable
import dev.jtsalva.cloudmare.AnalyticsActivity
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.zone.Zone
import timber.log.Timber

class AnalyticsViewModel(
    private val activity: AnalyticsActivity,
    private val domain: Zone
) : BaseObservable(), AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        val selectedItem = parent.getItemAtPosition(pos)

//        when (parent.id) {
//        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        Timber.d("Nothing selected")
    }

}