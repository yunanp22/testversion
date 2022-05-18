package org.tensorflow.lite.examples.poseestimation

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_signup.*

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        signUp_btn_cancel.setOnClickListener{
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        signUp_btn_submit.setOnClickListener{
            if (signup_email.text.toString().equals("") ||
                signup_nickname.text.toString().equals("") ||
                signup_password.text.toString().equals("") ||
                signup_password_check.text.toString().equals("")){
                Toast.makeText(this, "필수 입력항목이 비어있습니다.", Toast.LENGTH_SHORT).show()
            } else {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }
}