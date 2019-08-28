package dev.jtsalva.cloudmare

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import dev.jtsalva.cloudmare.api.user.UserRequest
import kotlinx.android.synthetic.main.activity_user.*
import timber.log.Timber

class UserActivity : CloudMareActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLayout(R.layout.activity_user)
        setToolbarTitle(R.string.title_user_activity)

        Auth.load(this)
        if (Auth.isSet) {
            email_input.setText(Auth.email)
            api_key_input.setText(Auth.apiKey)
        }

        email_input.setOnKeyListener(::onEnter)
        api_key_input.setOnKeyListener(::onEnter)
        done_button.setOnClickListener(::onDone)
    }

    override fun onBackPressed() = onDone()

    private fun onEnter(view: View, keyCode: Int, event: KeyEvent): Boolean
    {
        Timber.d("event action: ${event.action}")
        Timber.d("key code: $keyCode")

        if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            onDone()
            return true
        }
        return false
    }

    private fun onDone(view: View? = null) {
        Auth.email = email_input.text.toString()
        Auth.apiKey = api_key_input.text.toString()
        Auth.save(this)

        val validationDialog = Dialog(this).validating()

        launch {
            val response = UserRequest(this).getDetails()
            validationDialog.dismiss()

            if (response.success) finish()
            else dialog.
                error(message = response.firstErrorMessage)
        }
    }

}
