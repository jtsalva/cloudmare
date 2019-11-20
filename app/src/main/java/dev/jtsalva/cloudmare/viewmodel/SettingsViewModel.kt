package dev.jtsalva.cloudmare.viewmodel

import android.view.View
import android.widget.AdapterView
import androidx.databinding.BaseObservable
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.Settings
import dev.jtsalva.cloudmare.SettingsActivity
import timber.log.Timber

class SettingsViewModel(
    private val activity: SettingsActivity
) : BaseObservable(), AdapterView.OnItemSelectedListener {

    var theme: String = ""
        set(value) {
            if (value != field) {
                field = value
                Settings.theme = activity.themeTranslator.getId(field)
                Settings.save(activity)
            }
        }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val selectedItem = parent.getItemAtPosition(pos)

        when (parent.id) {
            R.id.theme_spinner -> {
                theme = selectedItem as String
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        Timber.d("Nothing selected")
    }
}