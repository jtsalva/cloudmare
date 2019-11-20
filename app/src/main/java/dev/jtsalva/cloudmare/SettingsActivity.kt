package dev.jtsalva.cloudmare

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import dev.jtsalva.cloudmare.api.IdTranslator
import dev.jtsalva.cloudmare.databinding.ActivitySettingsBinding
import dev.jtsalva.cloudmare.viewmodel.SettingsViewModel
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : CloudMareActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel = SettingsViewModel(this)

    private val themeSpinnerAdapter by lazy {
        val entries =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q)
                R.array.entries_settings_theme_android_ten_and_above
            else
                R.array.entries_settings_theme_android_nine_and_below

        ArrayAdapter.createFromResource(
            this,
            entries,
            R.layout.spinner_dropdown_item
        )
    }

    val themeTranslator by lazy {
        IdTranslator(
            mapOf(
                AppCompatDelegate.MODE_NIGHT_NO to getString(R.string.settings_theme_light),
                AppCompatDelegate.MODE_NIGHT_YES to getString(R.string.settings_theme_dark),
                AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY to getString(R.string.settings_theme_battery_saver),
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM to getString(R.string.settings_theme_system_default)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = setLayoutBinding(R.layout.activity_settings)
        setToolbarTitle("Preferences")
        binding.viewModel = viewModel

        viewModel.theme = themeTranslator.getReadable(Settings.theme)

        Settings.load(this)

        themeSpinnerAdapter.let { adapter ->
            theme_spinner.apply {
                setAdapter(adapter)
                setSelection(adapter.getPosition(viewModel.theme))
                onItemSelectedListener = viewModel
            }
        }
    }

}
