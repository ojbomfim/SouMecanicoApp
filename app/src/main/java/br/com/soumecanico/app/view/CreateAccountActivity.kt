@file:Suppress("DEPRECATION")

package br.com.soumecanico.app.view

import android.app.ProgressDialog
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")
class CreateAccountActivity : AppCompatActivity() {


    private var etfirstName: EditText? = null
    private var etlastName: EditText? = null
    private var etemail: EditText? = null
    private var etpassword: EditText? = null
    private var btsign_up: Button? = null
    private var mProgressBar: ProgressDialog? = null

    private var mDataBaseReference: DatabaseReference? = null
    private var mDataBase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private val TAG = "CreateAccountActivity"

    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        initialise()
    }
        private fun initialise(){
            etfirstName = findViewById<View>(R.id.et_firstName) as EditText
            etlastName = findViewById<View>(R.id.et_lastName) as EditText
            etemail = findViewById<View>(R.id.et_email) as EditText
            etpassword = findViewById<View>(R.id.et_password) as EditText
            btsign_up = findViewById<View>(R.id.bt_sign_up) as Button
            mProgressBar = ProgressDialog(this)

            mDataBase = FirebaseDatabase.getInstance()
            mDataBaseReference = mDataBase!!.reference.child("Users")
            mAuth = FirebaseAuth.getInstance()

            btsign_up!!.setOnClickListener { createNewAccount()}
        }

        private fun createNewAccount() {

            firstName = etfirstName?.text.toString()
            lastName = etlastName?.text.toString()
            email = etemail?.text.toString()
            password = etpassword?.text.toString()

            if (    !TextUtils.isEmpty(firstName) &&
                    !TextUtils.isEmpty(lastName) &&
                    !TextUtils.isEmpty(email) &&
                    !TextUtils.isEmpty(password)){

                Toast.makeText(this, "Informações preenchidas corretamente!", Toast.LENGTH_SHORT).show()

            }else {
                Toast.makeText(this, "Entre com mais detalhes", Toast.LENGTH_SHORT).show()
            }

            mProgressBar!!.setMessage("Registrando Usuário...")
            mProgressBar!!.show()

            mAuth!!.createUserWithEmailAndPassword(email!!, password!!).addOnCompleteListener(this){task ->
                    mProgressBar!!.hide()

                    if(task.isSuccessful){
                        Log.d(TAG, "createUserWithEmail:sucess")

                        val userId = mAuth!!.currentUser!!.uid

                        verifyEmail();

                        val currentUserDb = mDataBaseReference!!.child(userId)
                        currentUserDb.child("firstName").setValue(firstName)
                        currentUserDb.child("lastName").setValue(lastName)

                        updateUserInfoAndUI()

                    } else {
                        Log.w(TAG,"createUserWithEmail:failure" , task.exception)
                        Toast.makeText(this@CreateAccountActivity, "A autenticação falhou.",Toast.LENGTH_SHORT).show()
                    }

                    }

        }

    private fun updateUserInfoAndUI(){
        val intent = Intent(this@CreateAccountActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun verifyEmail(){
        val mUser = mAuth!!.currentUser;
        mUser!!.sendEmailVerification()
                .addOnCompleteListener(this){ task ->

            if (task.isSuccessful){
                Toast.makeText(this@CreateAccountActivity, "Verification email sent to" + mUser.getEmail(),
                        Toast.LENGTH_SHORT).show()
            } else {
                Log.e(TAG, "sendEmailVerification", task.exception)
                Toast.makeText(this@CreateAccountActivity, "Failed to send verification email.",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }
}
