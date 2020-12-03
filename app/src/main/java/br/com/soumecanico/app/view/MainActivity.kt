package br.com.soumecanico.app.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import br.com.soumecanico.app.AddVeiculo
import br.com.soumecanico.app.R
import br.com.soumecanico.app.R.string.webservice
import br.com.soumecanico.app.adapter.VeiculoListAdapter
import br.com.soumecanico.app.model.Veiculo
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.itextpdf.text.Document
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {


    internal var veiculosList: ArrayList<Veiculo> = ArrayList()

    internal lateinit var queue: RequestQueue

    internal lateinit var adapter: VeiculoListAdapter

    private var jsonParser: JsonParser? = null
    private var gson: Gson? = null
    private val STORAGE_CODE: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCenter.start(application, "f37dfdf8-2b89-43d6-9235-245177e79a7d",
                Analytics::class.java, Crashes::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnPdf.setOnClickListener {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permissions, STORAGE_CODE)
                } else {
                    savePdf()
                }
            } else {
                savePdf()
            }
        }

        jsonParser = JsonParser()
        gson = Gson()

        swipeRefresh.setOnRefreshListener { carregarLista() }

        btnAddVeiculo.setOnClickListener { v ->
            val i = Intent(v.context, AddVeiculo::class.java)
            v.context.startActivity(i)
        }

        queue = Volley.newRequestQueue(this)

        carregarLista()

    }

    private fun savePdf() {

        val mDoc = Document()
        val mFileName = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
        val mFilePatch = Environment.getExternalStorageDirectory().toString() + "/" + mFileName +".pdf"
        try {
            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePatch))

            mDoc.open()

            mDoc.pageSize = PageSize.A4


            val mText = (Request.Method.POST.toString(webservice) + "getAllVeiculos.php")

            mDoc.addAuthor("Sou Mecânico app")

            mDoc.add(Paragraph(mText))

            mDoc.close()

            Toast.makeText(this, "$mFileName.pdf\nis saved to \n$mFilePatch" , Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            STORAGE_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    savePdf()
                    Toast.makeText(this, "Salvo com Sucesso...", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this, "Permissão negada...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun carregarLista() {
        veiculosList.clear()
        val stringRequest = @SuppressLint("SetTextI18n")
        object : StringRequest(Method.POST,
                getString(webservice) + "getAllVeiculos.php", Response.Listener { response ->
            try {

                val mJson = jsonParser!!.parse(response) as JsonArray
                veiculosList = ArrayList()

                (0 until mJson.size())
                        .map {
                            gson!!.fromJson(mJson.get(it),
                                    Veiculo::class.java)
                        }
                        .forEach { veiculosList.add(it) }

                adapter = VeiculoListAdapter(this, veiculosList)

                veiculosListView?.adapter = adapter
                adapter.notifyDataSetChanged()

                errormessage.text = "Sem veículos cadastrados no momento."
                veiculosListView.emptyView = errormessage

                veiculosListView.setOnItemClickListener { _, view, position, _ ->
                    val i = Intent(view.context, VeiculoDetalheActivity::class.java)
                    i.putExtra("id", veiculosList[position].id)
                    view.context.startActivity(i)
                }


                swipeRefresh.isRefreshing = false

            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Problemas na comunicação com o servidor.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
                swipeRefresh.isRefreshing = false
            }
        }, Response.ErrorListener {

            swipeRefresh.isRefreshing = false
            Toast.makeText(this@MainActivity,
                    "Problema na comunicação com o servidor!",
                    Toast.LENGTH_LONG).show()
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["PATH"] = "getVeiculos"

                return params
            }
        }
        queue.add(stringRequest)

    }

}
