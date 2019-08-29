package dev.jtsalva.cloudmare

import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet

class Dialog(private val activity: CloudMareActivity) {
    private val bottomSheet: MaterialDialog = MaterialDialog(activity, BottomSheet(LayoutMode.WRAP_CONTENT))

    companion object {

        private val openDialogs = mutableMapOf<Int, MaterialDialog>()

        private fun setOpenDialog(activityHash: Int, dialog: MaterialDialog) {
            openDialogs[activityHash]?.dismiss()
            openDialogs[activityHash] = dialog
        }

        fun dismissOpenDialog(activityHash: Int) {
            openDialogs[activityHash]?.dismiss()
            openDialogs.remove(activityHash)
        }

    }

    fun dismiss() = bottomSheet.dismiss()

    fun error(title: String = "Oops",
              message: String = "Something went wrong",
              positive: String = "Try again",
              onAcknowledge: () -> Unit = {}) =
        setOpenDialog(activity.hashCode(), bottomSheet.show {
            title(text = title)
            message(text = message)
            positiveButton(text = positive) { onAcknowledge() }
        })

    fun confirm(title: String = "Are you sure?",
                message: String = "",
                positive: String = "Yes",
                negative: String = "Cancel",
                onResult: (confirmed: Boolean) -> Unit) =
        setOpenDialog(activity.hashCode(), bottomSheet.show {
            title(text = title)
            if (message != "") message(text = message)

            positiveButton(text = positive) { onResult(true) }

            negativeButton(text = negative) { onResult(false) }
        })

    fun loading(title: String = "Loading...",
                message: String = ""): Dialog {
        setOpenDialog(activity.hashCode(), bottomSheet.show {
            cancelable(false)
            setCanceledOnTouchOutside(false)

            title(text = title)
            if (message != "") message(text = message)
        })

        return this
    }
}