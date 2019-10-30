package dev.jtsalva.cloudmare

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.api.zonesettings.PurgeCacheRequest
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSetting
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSettingRequest
import dev.jtsalva.cloudmare.databinding.ActivityCachingBindingImpl
import dev.jtsalva.cloudmare.viewmodel.CachingViewModel
import kotlinx.android.synthetic.main.activity_caching.*

class CachingActivity : CloudMareActivity(), SwipeRefreshable {

    private lateinit var domain: Zone

    private lateinit var binding: ActivityCachingBindingImpl

    private lateinit var viewModel: CachingViewModel

    val cacheLevelAdapter by lazy {
        ArrayAdapter.createFromResource(
            this,
            R.array.entries_cache_level,
            R.layout.spinner_item
        )
    }

    val browserCacheTtlAdapter by lazy {
        ArrayAdapter.createFromResource(
            this,
            R.array.entries_browser_cache_ttl,
            R.layout.spinner_item
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        domain = intent.getParcelableExtra("domain")!!

        viewModel = CachingViewModel(this, domain)
        binding = setLayoutBinding(R.layout.activity_caching)

        setToolbarTitle("${domain.name} | Caching")

        setOnClickListeners()
    }

    override fun onStart() {
        super.onStart()

        render()
    }

    override fun onSwipeRefresh() {
        super.onSwipeRefresh()

        viewModel.isFinishedInitializing = false
    }

    override fun render() = launch {
        val response = ZoneSettingRequest(this).list(domain.id)
        if (response.failure || response.result == null)
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)

        else response.result.let { settings ->
            viewModel.cacheLevel = settings.valueAsString(ZoneSetting.ID_CACHE_LEVEL)
            viewModel.browserCacheTtl = settings.valueAsDouble(ZoneSetting.ID_BROWSER_CACHE_TTL).toInt()

            cacheLevelAdapter.let { adapter ->
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

                cache_level_spinner.apply {
                    setAdapter(adapter)

                    val currentCacheLevel = viewModel.run { cacheLevelTranslator.getReadable(cacheLevel) }
                    setSelection(adapter.getPosition(currentCacheLevel))

                    onItemSelectedListener = viewModel
                }
            }

            browserCacheTtlAdapter.let { adapter ->
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

                browser_cache_ttl_spinner.apply {
                    setAdapter(adapter)

                    val currentBrowserCacheTtl = viewModel.run { ttlTranslator.getReadable(browserCacheTtl) }
                    setSelection(adapter.getPosition(currentBrowserCacheTtl))

                    onItemSelectedListener = viewModel
                }
            }

            viewModel.alwaysOnline = settings.valueAsBoolean(ZoneSetting.ID_ALWAYS_ONLINE)

            binding.viewModel = viewModel

            viewModel.isFinishedInitializing = true
            caching_view_group.visibility = View.VISIBLE
        }

        showProgressBar = false
    }

    private fun setOnClickListeners() {
        purge_everything_item.setOnClickListener {
            handlePurgeEverything()
        }
    }

    private fun handlePurgeEverything() {
        dialog.confirm(message = "This will purge all cache") { confirmed ->
            if (confirmed) {
                dialog.loading(title = "Purging...")
                PurgeCacheRequest(this).launch {
                    val response = purgeAll(domain.id)
                    if (response.success) Dialog.dismissOpenDialog(hashCode())
                    else dialog.error(
                        title = "Couldn't purge cache",
                        message = response.firstErrorMessage,
                        positive = "Okay"
                    )
                }
            }
        }
    }
}
