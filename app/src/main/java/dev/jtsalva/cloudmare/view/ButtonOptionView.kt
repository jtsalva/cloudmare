package dev.jtsalva.cloudmare.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import dev.jtsalva.cloudmare.R
import kotlinx.android.synthetic.main.button_option_view.view.*

class ButtonOptionView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    var title: String?
        get() = title_text_view.text.toString()
        set(value) { title_text_view.text = value }

    var info: String?
        get() = info_text_view.text.toString()
        set(value) { info_text_view.text = value }

    init {
        inflate(context, R.layout.button_option_view, this)

        context.obtainStyledAttributes(attrs, R.styleable.ButtonOptionView).apply {
            title = getString(R.styleable.ButtonOptionView_title)
            info = getString(R.styleable.ButtonOptionView_info)
        }.recycle()
    }

}