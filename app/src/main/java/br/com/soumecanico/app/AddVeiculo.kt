@file:Suppress("DEPRECATION")

package br.com.soumecanico.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import br.com.soumecanico.app.view.MainActivity
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_add_veiculo.*
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class AddVeiculo : AppCompatActivity() {

    internal lateinit var queue: RequestQueue
    internal var bundle: Bundle? = null

    //variaveis a guardar no modo edição
    internal lateinit var dt_cadastro: String
    internal lateinit var id: String
    internal var editar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_veiculo)


        queue = Volley.newRequestQueue(this)


        btncadastrar.setOnClickListener {
            if (testForm()!!) {


                var url = getString(R.string.webservice) + "addVeiculo.php"
                if (editar) {
                    url = getString(R.string.webservice) + "updateVeiculo.php"
                }

                val stringRequest = object : StringRequest(Method.POST,
                        url, Response.Listener { response ->

                    try {

                        Toast.makeText(this@AddVeiculo, response, Toast.LENGTH_LONG).show()
                        val i = Intent(this@AddVeiculo, MainActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(i)

                    } catch (e: Exception) {
                        Toast.makeText(this@AddVeiculo, "Problemas na comunicação com o servidor.", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()

                    }
                }, Response.ErrorListener {


                    Toast.makeText(this@AddVeiculo,
                            "Problema na comunicação com o servidor!",
                            Toast.LENGTH_LONG).show()
                }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val params = HashMap<String, String>()
                        if (editar) {
                            params["PATH"] = "updateVeiculo"
                        } else {
                            params["PATH"] = "addVeiculo"
                        }
                        params["PLACA"] = etplaca.text.toString().trim { it <= ' ' }
                        params["MARCA"] = etmarca.text.toString().trim { it <= ' ' }
                        params["MODELO"] = etmodelo.text.toString().trim { it <= ' ' }
                        params["COR"] = etcor.text.toString().trim { it <= ' ' }
                        params["ANO"] = etano.text.toString().trim { it <= ' ' }
                        params["SERVICO"] = etservico.text.toString().trim { it <= ' ' }
                        if (editar) {
                            params["ID"] = id
                            //params.put("DT_CADASTRO", dt_cadastro);
                        }

                        return params
                    }
                }

                queue.add(stringRequest)
            }
        }

        try {
            bundle = intent.extras
            if (bundle!!.getString("editar")!!.equals("editar", ignoreCase = true)) {

                editar = true

                etplaca.setText(bundle!!.getString("placa"), TextView.BufferType.EDITABLE)
                etmarca.setText(bundle!!.getString("marca"), TextView.BufferType.EDITABLE)
                etmodelo.setText(bundle!!.getString("modelo"), TextView.BufferType.EDITABLE)
                etcor.setText(bundle!!.getString("cor"), TextView.BufferType.EDITABLE)
                etano.setText(bundle!!.getString("ano"), TextView.BufferType.EDITABLE)
                etservico.setText(bundle!!.getString("servico"), TextView.BufferType.EDITABLE)

                id = bundle!!.getString("id")
                dt_cadastro = bundle!!.getString("dt_cadastro")

            }

        } catch (e: Exception) {
        }

    }

    fun testForm(): Boolean? {
        if      (etplaca.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)
                || etmarca.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)
                || etmodelo.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)
                || etcor.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)
                || etano.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)
                || etservico.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)) {
            Toast.makeText(this, "Preencha todo o formulário.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
