package dev.jtsalva.cloudmare

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import dev.jtsalva.cloudmare.api.tokens.TokenRequest
import dev.jtsalva.cloudmare.api.user.UserRequest
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : CloudMareActivity() {

    companion object {
        private const val API_KEY_LENGTH = 37
        private const val API_TOKEN_LENGTH = 40
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLayout(R.layout.activity_user)
        setToolbarTitle(R.string.title_user_activity)

        api_key_or_token_input.addTextChangedListener(object : TextWatcher {
            private val defaultApiKeyOrTokenLabel = getString(R.string.user_api_key_or_token_label)
            private val apiKeyLabel = getString(R.string.user_api_key_label)
            private val apiTokenLabel = getString(R.string.user_api_token_label)

            private fun setEmailGroupVisibility(visible: Boolean) {
                val visibility = if (visible) View.VISIBLE else View.GONE

                email_label.visibility = visibility
                email_input.visibility = visibility
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Blank
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Blank
            }

            override fun afterTextChanged(s: Editable?) {
                if (s == null) return

                when (s.length) {
                    API_KEY_LENGTH -> {
                        api_key_label.text = apiKeyLabel
                        setEmailGroupVisibility(true)
                    }

                    API_TOKEN_LENGTH -> {
                        api_key_label.text = apiTokenLabel
                        if (email_label.visibility == View.VISIBLE) setEmailGroupVisibility(false)
                    }

                    else -> {
                        api_key_label.text = defaultApiKeyOrTokenLabel
                        if (email_label.visibility == View.VISIBLE) setEmailGroupVisibility(false)
                    }
                }
            }
        })

        email_input.setOnKeyListener(::onEnter)
        api_key_or_token_input.setOnKeyListener(::onEnter)
        save_button.setOnClickListener(::onSave)
        help_button.setOnClickListener(::onHelp)
    }

    override fun onStart() {
        super.onStart()

        email_input.setText(Auth.email)
        api_key_or_token_input.setText(
            if (Auth.usingApiKey) Auth.apiKey
            else Auth.apiToken
        )
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onEnter(view: View, keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            onSave()
            return true
        }
        return false
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onSave(view: View? = null) {
        dialog.loading(title = "Validating…")

        api_key_or_token_input.text.toString().let { text ->
            if (text.length == API_KEY_LENGTH) {
                Auth.apiKey = text
                Auth.apiToken = ""
                Auth.email = email_input.text.toString()
            } else {
                Auth.apiKey = ""
                Auth.apiToken = text
                Auth.email = ""
            }
        }
        Auth.save(this)

        launch {
            val response =
                if (Auth.usingApiKey) UserRequest(this).getDetails()
                else TokenRequest(this).verify()

            if (response.success) finish()
            else dialog.error(message = response.firstErrorMessage)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onHelp(view: View? = null) {
        help_button.visibility = View.GONE
        info.visibility = View.VISIBLE
        user_required_token_permissions.visibility = View.VISIBLE
        user_additional_info.visibility = View.VISIBLE
    }
}
