package dev.jtsalva.cloudmare

import android.app.Activity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.SingleChoiceListener
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import timber.log.Timber

class Dialog(private val activity: CloudMareActivity) {
    companion object {

        private val openDialogs = mutableMapOf<Int, MaterialDialog>()

        private fun setOpenDialog(activity: Activity, materialFunc: MaterialDialog.() -> Unit) {
            if (activity.isFinishing) return

            val activityHash = activity.hashCode()
            openDialogs[activityHash]?.dismiss()
            openDialogs[activityHash] = MaterialDialog(activity).show(materialFunc)
        }

        fun dismissOpenDialog(activityHash: Int) {
            openDialogs[activityHash]?.dismiss()
            openDialogs.remove(activityHash)
        }
    }

    fun multiChoice(
        title: String = "Choose one",
        resId: Int = -1,
        initialSelection: Int = -1,
        onSelection: SingleChoiceListener
    ) = setOpenDialog(activity) {
            title(text = title)
            listItemsSingleChoice(
                resId,
                initialSelection = initialSelection,
                selection = onSelection
            )
        }

    fun error(
        title: String = "Oops",
        message: String = "Something went wrong",
        positive: String = "Try again",
        onAcknowledge: () -> Unit = {}
    ) = setOpenDialog(activity) {
            title(text = title)
            message(text = message)
            positiveButton(text = positive) { onAcknowledge() }
        }.also {
            Throwable().run {
                stackTrace[2].let { caller ->
                    Timber.e("${caller.className}: $message")
                }
            }
        }

    fun confirm(
        title: String = "Are you sure?",
        message: String = "",
        positive: String = "Yes",
        negative: String = "Cancel",
        onResult: (confirmed: Boolean) -> Unit
    ) = setOpenDialog(activity) {
            title(text = title)
            if (message.isNotBlank()) message(text = message)

            positiveButton(text = positive) { onResult(true) }
            negativeButton(text = negative) { onResult(false) }
        }

    fun loading(
        title: String = "Loading…",
        message: String = ""
    ) = setOpenDialog(activity) {
            cancelable(false)
            cancelOnTouchOutside(false)

            title(text = title)
            if (message.isNotBlank()) message(text = message)
        }
}
