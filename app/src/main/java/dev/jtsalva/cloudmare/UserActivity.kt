package dev.jtsalva.cloudmare

import android.os.Bundle
import dev.jtsalva.cloudmare.api.user.UserRequest
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : CloudMareActivity() {

    override val TAG = "UserActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLayout(R.layout.activity_user)
        setToolbarTitle(R.string.title_user_activity)

        Auth.load(this)
        if (Auth.isSet) {
            email_input.setText(Auth.email)
            api_key_input.setText(Auth.apiKey)
        }

        done_button.setOnClickListener {
            Auth.email = email_input.text.toString()
            Auth.apiKey = api_key_input.text.toString()
            Auth.save(this)

            launch {
                val response = UserRequest(this).getDetails()
                if (response.success) finish()
                else longToast(response.firstErrorMessage)
            }

        }
    }

}
