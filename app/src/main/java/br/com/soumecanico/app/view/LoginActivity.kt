@file:Suppress("DEPRECATION")

package br.com.soumecanico.app.view

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button

import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import br.com.soumecanico.app.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {


    private val TAG = "LoginActivity"

    private var email: String? = null
    private var password: String? = null

    private var tvForgotPassword: TextView? = null
    private var etemail: EditText? = null
    private var etpassword: EditText? = null
    private var btlogin: Button? = null
    private var btCreateAccount: Button? = null
    private var mProgressBar: ProgressDialog? = null
    private var mAuth: FirebaseAuth? = null


    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColorTo(R.color.colorPrimary)
        }

        this.initialise()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun Window.setStatusBarColorTo(color: Int) {
        this.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        this.statusBarColor = ContextCompat.getColor(baseContext, color)
    }

    private fun initialise() {
        this.tvForgotPassword = findViewById<View>(R.id.tv_forgot_password) as TextView
        this.etemail = findViewById<View>(R.id.et_email) as EditText
        this.etpassword = findViewById<View>(R.id.et_password) as EditText
        this.btlogin = findViewById<View>(R.id.bt_login) as Button
        this.btCreateAccount = findViewById<View>(R.id.bt_sign_up) as Button
        this.mProgressBar = ProgressDialog(this)

        this.mAuth = FirebaseAuth.getInstance()

        this.tvForgotPassword!!
                .setOnClickListener { startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java)) }
        this.btCreateAccount!!
                .setOnClickListener { startActivity(Intent(this@LoginActivity, CreateAccountActivity::class.java)) }
        this.btlogin!!
                .setOnClickListener { loginUser() }

    }

    private fun loginUser() {
        email = etemail?.text.toString()
        password = etpassword?.text.toString()

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mProgressBar!!.setMessage("Verificando o Usuário")
            mProgressBar!!.show()

            Log.d(TAG, "Login do Usuário.")

            mAuth!!.signInWithEmailAndPassword(email!!, password!!).addOnCompleteListener(this) { task ->

                mProgressBar!!.hide()

                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:sucess")
                    updateUI()
                } else {
                    Log.e(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this@LoginActivity, "Email ou senha incorretos.", Toast.LENGTH_SHORT).show()
                }

            }
        } else {
            Toast.makeText(this, "Entre com mais detalhes", Toast.LENGTH_SHORT).show()
        }

    }

        private fun updateUI() {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
    }
}
