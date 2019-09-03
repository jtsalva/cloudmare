package dev.jtsalva.cloudmare.view

import android.content.Context
import android.util.AttributeSet
import android.widget.CompoundButton
import androidx.constraintlayout.widget.ConstraintLayout
import dev.jtsalva.cloudmare.R
import kotlinx.android.synthetic.main.switch_option_view.view.*

class SwitchOptionView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    var title: String?
        get() = title_switch.text.toString()
        set(value) { title_switch.text = value }

    var info: String?
        get() = info_text_view.text.toString()
        set(value) { info_text_view.text = value }

    var checked: Boolean
        get() = title_switch.isChecked
        set(value) { title_switch.isChecked = value }

    init {
        inflate(context, R.layout.switch_option_view, this)

        context.obtainStyledAttributes(attrs, R.styleable.SwitchOptionView).apply {
            title = getString(R.styleable.SwitchOptionView_title)
            info = getString(R.styleable.SwitchOptionView_info)
            checked = getBoolean(R.styleable.SwitchOptionView_checked, false)
        }.recycle()
    }

    fun setOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener) =
        title_switch.setOnCheckedChangeListener(listener)

}