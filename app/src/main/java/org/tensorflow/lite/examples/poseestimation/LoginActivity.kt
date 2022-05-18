package org.tensorflow.lite.examples.poseestimation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val nextIntent = Intent(this, MainActivity::class.java)
        login_btn.setOnClickListener {
            startActivity(nextIntent)
        }
        val i = Intent(this, SignUpActivity::class.java)
        signUp_btn.setOnClickListener {
            startActivity(i)
        }
    }
}