package com.gsabr.assetscare

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.fasterxml.jackson.module.kotlin.*
import kotlinx.android.synthetic.main.activity_dados.*
import kotlinx.android.synthetic.main.activity_revisao.*
import kotlinx.android.synthetic.main.activity_revisao.tv_numero_patrimonio
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class RevisaoActivity : AppCompatActivity()
{
    //Constantes
    private val baseUrl = "http://192.168.110.239:8080/api_manutencao/api/"
    private val urlPecas = baseUrl + "peca"
    private val getOsUrl = baseUrl + "os"
    private val getNomeTecnicoUrl = baseUrl + "tecnico/"
    private val postUrl = baseUrl + "pre_os"
    private val putOsUrl = baseUrl + "gravar_os"
    private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private val date = Calendar.getInstance().time
    private val msgNomeNaoEncontrado = "MATRÍCULA INVÁLIDA"
    private val run = Run()

    //Variáveis
    private var arrayListPecas = arrayListOf<String>("00; NENHUMA")
    private var jsonobjpreos = JSONObject()
    private var jsonobj = JSONObject()
    private var pecaUtilizada = ""
    private var qtdPecasUtilizadas = 0
    private var nomeTecnico = ""
    private var numOS = ""
    private var status_os = ""
    private var pos_os = ""
    private var tipo_os = ""
    private var detalhes = ""
    private var codFunc = ""
    private var isValidCodFunc = false

    @SuppressLint("SetTextI18n")
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

        //
        btn_buscar_tecnico.setOnClickListener {

            tv_codfunc_descricao.visibility = View.INVISIBLE
            progress_bar_tecnico.visibility = View.VISIBLE

            codFunc = et_codfunc.text.toString()

            val queue = Volley.newRequestQueue(this)
            val request = JsonObjectRequest(
                Request.Method.GET, getNomeTecnicoUrl + codFunc, null,
                { response ->

                        nomeTecnico = response.get("nome").toString().uppercase(Locale.getDefault())

                        if (nomeTecnico == "NOTFOUND"){

                            isValidCodFunc = false
                            //run.hideViewDelay(tv_codfunc_descricao, 200)
                            tv_codfunc_descricao.text = msgNomeNaoEncontrado
                            tv_codfunc_descricao.setTextColor(ContextCompat.getColor(this, R.color.colorRed))
                            progress_bar_tecnico.visibility = View.INVISIBLE
                            tv_codfunc_descricao.visibility = View.VISIBLE
                        }else{
                            //run.hideViewDelay(tv_codfunc_descricao, 200)
                            tv_codfunc_descricao.text = nomeTecnico
                            tv_codfunc_descricao.setTextColor(ContextCompat.getColor(this, R.color.colorCyan))
                            hideSoftKeyboard(et_codfunc)
                            et_codfunc.clearFocus()
                            isValidCodFunc = true
                            progress_bar_tecnico.visibility = View.INVISIBLE
                            tv_codfunc_descricao.visibility = View.VISIBLE
                        }
                }
            ) {
                Toast.makeText(this, "Insira sua MATRÍCULA!!!", Toast.LENGTH_SHORT).show()
                progress_bar_tecnico.visibility = View.INVISIBLE
                tv_codfunc_descricao.visibility = View.VISIBLE
                tv_codfunc_descricao.text = "PREENCHA O CAMPO COM SUA MATRÍCULA"
                tv_codfunc_descricao.setTextColor(ContextCompat.getColor(this, R.color.colorRed))

            }
            queue.add(request)

            //run.hideViewDelay(tv_codfunc_descricao, 325)


        }

        btn_enviar_os.setOnClickListener {

            if(pecaUtilizada != "00" && (qtdPecasUtilizadas as Int) < 1){
                Toast.makeText(this@RevisaoActivity, "A quantidade de peças parece incorreta, quantidade atual: $qtdPecasUtilizadas", Toast.LENGTH_SHORT).show()

                run.hideViewDelay(btn_enviar_os, 2500)

                return@setOnClickListener
            }

            if (pecaUtilizada == "00" && (qtdPecasUtilizadas as Int)> 0){
                Toast.makeText(this@RevisaoActivity, "Nenhuma peça foi selecionada a quantidade deveria ser 0, Peça: NENHUMA, Qtd: $qtdPecasUtilizadas", Toast.LENGTH_SHORT).show()

                run.hideViewDelay(btn_enviar_os, 2500)

                return@setOnClickListener
            }

            if (isValidCodFunc) {

                var id = rg_tipos_os.checkedRadioButtonId
                when (id) {
                    R.id.rd_descricao_tipo1 -> tipo_os = "P"
                    R.id.rd_descricao_tipo2 -> tipo_os = "C"
                }

                id = rg_os_status.checkedRadioButtonId
                when (id) {
                    R.id.rd_conclusao_sim -> status_os = "F" //Fechada
                    R.id.rd_conclusao_nao -> status_os = "A" //Aberta ou Pendente
                }

                id = rg_pos_os.checkedRadioButtonId
                when (id) {
                    R.id.rd_atende -> pos_os = "atende"
                    R.id.rd_atende_restrito -> pos_os = "restricao"
                    R.id.rd_nao_atende -> pos_os = "nao_atende"
                }

                val verifyEt = et_detalhes.text.toString()
                detalhes = if (verifyEt.isEmpty()) {
                    "Sem detalhes"
                } else {
                    verifyEt
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
                isFinal(true)
            }else{
                Toast.makeText(this@RevisaoActivity, "Verifique sua MATRÍCULA e tente novamente!", Toast.LENGTH_SHORT).show()
                run.hideViewDelay(btn_enviar_os, 2500)
            }
        }

        btn_voltar.setOnClickListener {
            val intent = Intent(this@RevisaoActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    //Obtendo lista de peças
    private fun objectRequestPecas(url: String) {

        val queue = Volley.newRequestQueue(this@RevisaoActivity)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->

//                var arrayListPecas = arrayListOf<String>()
                //arrayListPecas.add("00 ; NENHUMA")
                for (i in 0 until response.length()) {

                    val pecaJsonObject = response.getJSONObject(i)
                    val codProd = pecaJsonObject.getString("codprod")
                    val descricao = pecaJsonObject.getString("descricao")
                        .uppercase(Locale.getDefault())
                    arrayListPecas.add("$codProd; $descricao")
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

                Toast.makeText(
                    applicationContext,
                    "Lista de peças carregada!",
                    Toast.LENGTH_SHORT
                ).show()
            },
            {
                Toast.makeText(this , "Erro, verifique o acesso à rede e tente novamente!", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(jsonArrayRequest)
        queue.cache.clear()
    }
    //...

    //Pegar num os
    private fun getNumOs(url: String){

        isLoading(true)

        val que = Volley.newRequestQueue(this@RevisaoActivity)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->

                //var arrayListOs = arrayListOf<String>()
                for (i in 0 until response.length()) {

                    val osJsonObject = response.getJSONObject(i)
                    numOS = osJsonObject.getString("sequencia")

                }
                tv_num_os.text = numOS
                jsonobjpreos.put("num_preos", numOS)
                jsonobjpreos.put("num_patrimonio", tv_numero_patrimonio.text)
                //jsonobjpreos.put("num_patrimonio", patrimonio)
                jsonobjpreos.put("cliente", tv_loja.text)

                postPreOs()
                Toast.makeText( applicationContext,"OS iniciada!", Toast.LENGTH_SHORT).show()
            },
            {
                Toast.makeText( applicationContext,"Erro ao obter número de OS!", Toast.LENGTH_SHORT).show()
            }
        )
        que.cache.clear()
        que.add(jsonArrayRequest)
        isLoading(false)
    }

    private fun postPreOs(){
        isLoading(true)
        val que = Volley.newRequestQueue(this@RevisaoActivity)
        val req = JsonObjectRequest(
            Request.Method.POST, postUrl, jsonobjpreos,{response->

            }, { })
        que.add(req)
        que.cache.clear()
        isLoading(false)
    }

    private fun putOs(){
        val que = Volley.newRequestQueue(this@RevisaoActivity)
        val req = JsonObjectRequest(

            Request.Method.PUT, putOsUrl, jsonobj, {

                Toast.makeText(applicationContext, "OS enviada!", Toast.LENGTH_SHORT).show()
            },{
                //Toast.makeText(applicationContext, "OS enviada com alguns erros!", Toast.LENGTH_SHORT).show()

            })
        que.add(req)
        que.cache.clear()
    }

    //Tela de carregamento
    private fun isLoading(answer: Boolean){
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

    @SuppressLint("SetTextI18n")
    private fun isFinal(answer: Boolean){
        if(answer){
            sv_dados_revisao.visibility = View.GONE
            iv_os_enviada.visibility = View.VISIBLE
            tv_os_enviada.visibility = View.VISIBLE
            btn_voltar.visibility = View.VISIBLE
            tv_os_enviada.text=  "OS $numOS ENVIADA!"
        }
    }

     fun isValid(value: Boolean): Boolean {
         return value
    }

    private fun showSoftKeyboard(view: View) {

        if (view.requestFocus()) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideSoftKeyboard(view: View){
        if(view.requestFocus()){
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }



//    fun hideViewDelay(view: View, duration: Long){
//        view.visibility = View.INVISIBLE
//        Handler(Looper.getMainLooper()).postDelayed({
//            view.visibility = View.VISIBLE
//        }, duration)
//    }
    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({

            showSoftKeyboard(et_codfunc)
        }, 2000)
    }
}

