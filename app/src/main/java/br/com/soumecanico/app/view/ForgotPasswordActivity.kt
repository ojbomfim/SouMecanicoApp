package br.com.soumecanico.app.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import br.com.soumecanico.app.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private val TAG = "ForgotPasswordActivity"

    private var etemail: EditText? = null
    private var btsubmit: Button? = null

    private var mAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        initialise()
    }

    private fun initialise(){
        etemail = findViewById<View>(R.id.et_email) as EditText
        btsubmit = findViewById<View>(R.id.bt_submit) as Button
        mAuth = FirebaseAuth.getInstance()
        btsubmit!!.setOnClickListener { sendPasswordResetEmail() }
    }

    private fun sendPasswordResetEmail(){
        val email = etemail?.text.toString()

        if (!TextUtils.isEmpty(email)){
            mAuth!!
                    .sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            val message = "Email Enviado."
                            Log.d(TAG, message)
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            updateUI()

                        } else {
                            Log.w(TAG, task.exception!!.message)
                            Toast.makeText(this, "Nenhum usuário encontrado com esse e-mail.", Toast.LENGTH_SHORT).show()
                        }
                    }
        } else {
            Toast.makeText(this, "Entre com um e-mail válido.", Toast.LENGTH_SHORT).show()
        }



    }

    private fun updateUI(){
        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}
