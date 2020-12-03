package br.com.soumecanico.app.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import br.com.soumecanico.app.R
import br.com.soumecanico.app.model.Veiculo
import java.util.*


class VeiculoListAdapter(private var activity: Activity, private var items: ArrayList<Veiculo>): BaseAdapter() {

    private class ViewHolder(row: View?) {
        var txtMarca: TextView? = null
        var txtCadastro: TextView? = null
        var txtServico: TextView? = null
        var txtPlaca: TextView? = null
        var txtCor: TextView? = null
        var txtAno: TextView? = null
        var txtModelo: TextView? = null


        init {
            this.txtMarca = row?.findViewById(R.id.textMarca)
            this.txtCadastro = row?.findViewById(R.id.textDt_cadastro)
            this.txtServico = row?.findViewById(R.id.textDesc)
            this.txtPlaca = row?.findViewById(R.id.textPlaca)
            this.txtCor = row?.findViewById(R.id.textCor)
            this.txtAno = row?.findViewById(R.id.textAno)
            this.txtModelo = row?.findViewById(R.id.textModelo)
        }
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.item_veiculo, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val veiculo = items[position]
        viewHolder.txtMarca?.text = veiculo.marca
        viewHolder.txtCadastro?.text = veiculo.dt_cadastro
        viewHolder.txtServico?.text = veiculo.servico
        viewHolder.txtPlaca?.text = veiculo.placa
        viewHolder.txtCor?.text = veiculo.cor
        viewHolder.txtAno?.text = veiculo.ano
        viewHolder.txtModelo?.text = veiculo.modelo

        return view as View
    }

    override fun getItem(i: Int): Veiculo {
        return items[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

}