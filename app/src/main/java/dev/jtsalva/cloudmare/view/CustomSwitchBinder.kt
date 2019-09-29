package dev.jtsalva.cloudmare.view

import android.widget.CompoundButton
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.InverseBindingMethod
import androidx.databinding.InverseBindingMethods

@InverseBindingMethods(
    InverseBindingMethod(
        type = SwitchOptionView::class,
        attribute = "app:checked",
        method = "switchIsChecked"
    )
)

object CustomSwitchBinder {

    @JvmStatic
    @BindingAdapter(value = ["checkedAttrChanged"])
    fun setListener(switchOptionView: SwitchOptionView, listener: InverseBindingListener?) {
        if (listener != null)
            switchOptionView.setOnCheckedChangeListener(
                CompoundButton.OnCheckedChangeListener { _, _ ->
                    listener.onChange()
                }
            )
    }

    @JvmStatic
    @BindingAdapter("app:checked")
    fun setChecked(switchOptionView: SwitchOptionView, checked: Boolean) {
        if (checked != switchOptionView.switchIsChecked)
            switchOptionView.switchIsChecked = checked
    }

}