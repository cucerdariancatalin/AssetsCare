package com.gsabr.assetscare

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.fasterxml.jackson.module.kotlin.*
import kotlinx.android.synthetic.main.activity_dados.*
import kotlinx.android.synthetic.main.activity_revisao.*
import kotlinx.android.synthetic.main.activity_revisao.tv_numero_patrimonio
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess


class RevisaoActivity : AppCompatActivity()
{
    var date = Calendar.getInstance().time
    var dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    var pecaUtilizada = ""
    var qtdPecasUtilizadas: Number? = null
    private var requestQueue: RequestQueue? = null
    var nomeTecnico: String = ""
    var numOS: String = ""
    var status_os = ""
    var pos_os = ""
    var jsonobjpreos = JSONObject()
    var jsonobj = JSONObject()
    var msgNomeNaoEncontrado = "MATRÍCULA INVÁLIDA"
    var tipo_os = ""
    var detalhes = ""
    var codFunc = ""
    var baseUrl = "http://192.168.110.239:8080/api_manutencao/api/"
    val urlPecas = baseUrl + "peca"
    val getOsUrl = baseUrl + "os"
    val getNomeTecnicoUrl = baseUrl + "tecnico/"
    val postUrl = baseUrl + "pre_os"
    val putOsUrl = baseUrl + "gravar_os"


    //lateinit var patrimonio: String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revisao)
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) //Somente retrato

        var patrimonio = intent.getStringExtra("patrimonio")
        var codeEquipe = intent.getStringExtra("codequip")
        var localizacao = intent.getStringExtra("localizacao")
        var codFilial = intent.getStringExtra("codfilial")
        //detalhesDaRevisao.putExtra("codfilial", codFilial)
        var dataInicioOs = dateTimeFormat.format(date)
        number_picker?.minValue = 0
        number_picker?.maxValue = 30
        number_picker.wrapSelectorWheel = true
        number_picker.setOnValueChangedListener { _, _, newVal ->

            qtdPecasUtilizadas = newVal
            //Toast.makeText(this@RevisaoActivity, newVal.toString(), Toast.LENGTH_SHORT).show()
        }


        tv_data_os.text = dataInicioOs
        tv_loja.text = localizacao
        tv_numero_patrimonio.text = patrimonio


        objectRequestPecas(urlPecas)
        getNumOs(getOsUrl)
        ///



        ///...
        //tv_codfunc_descricao.text = nomeTecnico

        btn_buscar_tecnico.setOnClickListener {

            codFunc = et_codfunc.text.toString()

            val queue = Volley.newRequestQueue(this)
            val request = JsonObjectRequest(
                Request.Method.GET, getNomeTecnicoUrl + codFunc, null,
                { response ->

                        nomeTecnico = response.get("nome").toString().uppercase(Locale.getDefault())

                        if (nomeTecnico == "NOTFOUND"){

                            tv_codfunc_descricao.text = msgNomeNaoEncontrado
                            tv_codfunc_descricao.setTextColor(ContextCompat.getColor(this, R.color.colorRed))
                        }else{

                            tv_codfunc_descricao.text = nomeTecnico
                            tv_codfunc_descricao.setTextColor(ContextCompat.getColor(this, R.color.colorCyan))
                            //tv_codfunc_descricao.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightGreen))
                        }
                }
            ) { error -> Toast.makeText(this, "Insira sua MATRÍCULA!!!", Toast.LENGTH_SHORT).show() }
            queue.add(request)

        }

        //...

        btn_enviar_os.setOnClickListener {

            var id = rg_tipos_os.checkedRadioButtonId
            when(id){
                R.id.rd_descricao_tipo1 -> tipo_os = "P"
                R.id.rd_descricao_tipo2 -> tipo_os = "C"
            }

            id = rg_os_status.checkedRadioButtonId
            when(id){
                R.id.rd_conclusao_sim -> status_os = "F" //Aberta ou Pendente
                R.id.rd_conclusao_nao -> status_os = "A" //Fechada
            }

            id = rg_pos_os.checkedRadioButtonId
            when(id){
                R.id.rd_atende -> pos_os = "atende"
                R.id.rd_atende_restrito -> pos_os = "restricao"
                R.id.rd_nao_atende -> pos_os = "nao_atende"
            }

            var verifyEt = et_detalhes.text.toString()
            if (verifyEt.isEmpty()){
                detalhes = "Sem detalhes"
            }else{
                detalhes = verifyEt
            }


            jsonobj.put("num_os", numOS)
            jsonobj.put("codequip", codeEquipe)
            jsonobj.put("num_patrimonio", patrimonio)
            jsonobj.put("tipo_os", tipo_os)
            jsonobj.put("cod_pecautilizada", pecaUtilizada)
            jsonobj.put("codfuncmov", codFunc)
            jsonobj.put("detalhes", detalhes)
            jsonobj.put("status_os", status_os)
            jsonobj.put("pos_os", pos_os)
            jsonobj.put("qtd_pecas_utilizadas", qtdPecasUtilizadas)
            jsonobj.put("codfilial", codFilial)

            //REQUEST
            putOs()
            //dados_titulo_revisao.text = jsonobj.toString()
            //Toast.makeText(applicationContext, "O resquest foi executado!", Toast.LENGTH_SHORT).show()

            isFinal(true)
        }

        btn_voltar.setOnClickListener {
            val intent = Intent(this@RevisaoActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    //Obtendo lista de peças
    fun objectRequestPecas(url: String) {

        val queue = Volley.newRequestQueue(this@RevisaoActivity)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->

                var arrayListPecas = arrayListOf<String>()
                arrayListPecas.add("00 ; NENHUMA")
                for (i in 0..response.length() - 1) {

                    val pecaJsonObject = response.getJSONObject(i)
                    val codProd = pecaJsonObject.getString("codprod")
                    val descricao = pecaJsonObject.getString("descricao").toUpperCase()
                    arrayListPecas.add("${codProd}; ${descricao}")
                }

                val searchmethod =
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayListPecas)
                searchmethod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                spinnerPecasSearch.setTitle("Selecione a peça:")
                spinnerPecasSearch.setPositiveButton("OK")
                spinnerPecasSearch.adapter = searchmethod
                spinnerPecasSearch.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View, position: Int, id: Long
                        ) {
                            pecaUtilizada = arrayListPecas[position].substringBefore(";")
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // TODO
                        }
                    }
                //progress_bar_pecas.view

                Toast.makeText(
                    applicationContext,
                    "OS inicializada!",
                    Toast.LENGTH_SHORT
                ).show()
            },
            {
                Toast.makeText(
                    applicationContext,
                    "Erro, verifique o acesso à rede e tente novamente!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        queue.cache.clear()
        queue.add(jsonArrayRequest)

    }
    //...

    //Pegar num os
    fun getNumOs(url: String){

        isLoading(true)

        val que = Volley.newRequestQueue(this@RevisaoActivity)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->

                //var arrayListOs = arrayListOf<String>()
                for (i in 0..response.length() - 1) {

                    val osJsonObject = response.getJSONObject(i)
                    numOS = osJsonObject.getString("sequencia")

                }
                tv_num_os.text = numOS
                jsonobjpreos.put("num_preos", numOS)
                jsonobjpreos.put("num_patrimonio", tv_numero_patrimonio.text)
                //jsonobjpreos.put("num_patrimonio", patrimonio)
                jsonobjpreos.put("cliente", tv_loja.text)

                postPreOs()
                Toast.makeText( applicationContext,"Conexão realizada, OS iniciada!", Toast.LENGTH_SHORT).show()
            },
            {
                Toast.makeText( applicationContext,"Erro ao obter número de OS!", Toast.LENGTH_SHORT).show()
            }
        )
        que.cache.clear()
        que.add(jsonArrayRequest)
        isLoading(false)
    }
    //...

    //Enviar pré ordem de serviço
    fun postPreOs(){

        //isLoading(true)

        val que = Volley.newRequestQueue(this@RevisaoActivity)
        val TAG = "MyTag"
        val req = JsonObjectRequest(
            Request.Method.POST, postUrl, jsonobjpreos,{

                Toast.makeText(applicationContext, "Pré ordem Enviada", Toast.LENGTH_SHORT).show()
            }, {
                //Toast.makeText(applicationContext, "...", Toast.LENGTH_SHORT).show()
            })
        req.tag = TAG
        que.cache.clear()
        que?.add(req)

        isLoading(false)
    }
    //...

    //Atualizar OS
    fun putOs(){

        isLoading(true)
        //val TAG = "MyTag"
        val que = Volley.newRequestQueue(this@RevisaoActivity)
        val req = JsonObjectRequest(

            Request.Method.PUT, putOsUrl, jsonobj, {

                Toast.makeText(applicationContext, "OS enviada!", Toast.LENGTH_SHORT).show()
            },{
                //Toast.makeText(applicationContext, "OS enviada com alguns erros!", Toast.LENGTH_SHORT).show()

            })
        //req.tag = TAG
        que.cache.clear()
        que?.add(req)
        isLoading(false)
    }

    //Tela de carregamento
    fun isLoading(answer: Boolean){
        if(answer){
            tb_main.visibility = View.GONE
            progress_bar.visibility = View.VISIBLE
        }else{
            Handler(Looper.getMainLooper()).postDelayed({
                tb_main.visibility = View.VISIBLE
                progress_bar.visibility = View.GONE
            }, 1000)
        }
    }
    //...

    fun isFinal(answer: Boolean){
        if(answer){
            sv_dados_revisao.visibility = View.GONE
            iv_os_enviada.visibility = View.VISIBLE
            tv_os_enviada.visibility = View.VISIBLE
            btn_voltar.visibility = View.VISIBLE
            //tv_os_enviada.text= jsonobj.toString()
        }
    }
}