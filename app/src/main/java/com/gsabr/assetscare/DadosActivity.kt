package com.gsabr.assetscare
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_dados.*
import kotlinx.android.synthetic.main.activity_dados.tv_numero_patrimonio
import kotlinx.android.synthetic.main.activity_revisao.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import org.json.JSONStringer
import java.text.SimpleDateFormat
import java.util.*


class DadosActivity : AppCompatActivity() {

    var dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val jsonobj = JSONObject()
    var dadoAusente = "DADO AUSENTE OU NÃO INFORMADO!"

    lateinit var p: Patrimonio


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dados)
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) // Deixar layout somente na modo retrato

        val numPatrimonio = intent.getStringExtra("patrimonio")
        val r = Run()

        r.isLoading(true, arrayOf(ll_main_dados), arrayOf(progress_bar_dados))

        //            ll_main_dados.visibility = View.GONE
//            progress_bar_dados.visibility = View.VISIBLE

        jsonobj.put("", numPatrimonio)
        val que: RequestQueue = Volley.newRequestQueue(this)
        val url = "http://192.168.110.239:8080/api_manutencao/api/equipamento/${numPatrimonio}"
        val req = JsonObjectRequest(Request.Method.GET, url, jsonobj,
            { response ->

                p = Gson().fromJson(response.toString(), Patrimonio::class.java)

                tv_descricao_equipamento.text = p.toString()


                r.setViewText(arrayOf(
                    tv_numero_patrimonio,
                    tv_descricao_equipamento,
                    tv_fabricante,
                    tv_data_instalacao,
                    tv_data_revisao,
                    tv_responsavel,
                    tv_localizacao,
                    tv_custo
                ), arrayOf(
                    p.numpatrimonio,
                    p.descricao,
                    p.fabricante,
                    p.dtcadastro,
                    p.dtultmanutencao,
                    p.responsavel+" - "+p.nome_responsavel,
                    p.localizacao,
                    "R$" + p.custo
                ))


                //Toast.makeText(this, resp, Toast.LENGTH_SHORT).show()

            },{
                sv_dados.visibility = View.INVISIBLE
                iv_dados.visibility = View.GONE
                r.changeView(arrayOf(iv_erro_rede, tv_alerta_rede), View.VISIBLE)
                Toast.makeText(this, "Erro ao conectar com o servidor!", Toast.LENGTH_SHORT).show()
            })
        que.add(req)
        r.isLoading(false, arrayOf(ll_main_dados), arrayOf(progress_bar_dados))

        btn_voltar_main.setOnClickListener {
            val intentMain = Intent(this@DadosActivity, MainActivity::class.java)
            startActivity(intentMain)
        }
//
        btn_registrar_revisao.setOnClickListener {

            val detalhesDaRevisao = Intent(this@DadosActivity, RevisaoActivity::class.java)

            detalhesDaRevisao.putExtra("patrimonio", numPatrimonio)
            detalhesDaRevisao.putExtra("codfilial", p.codfilial)
            detalhesDaRevisao.putExtra("codequip", p.codequip)
            detalhesDaRevisao.putExtra("localizacao", p.localizacao.uppercase(Locale.getDefault()))

            startActivity(detalhesDaRevisao)
        }
    }

//    private fun isLoading(answer: Boolean){
//        if(answer){
//            ll_main_dados.visibility = View.GONE
//            progress_bar_dados.visibility = View.VISIBLE
//        }else{
//            Handler(Looper.getMainLooper()).postDelayed({
//                ll_main_dados.visibility = View.VISIBLE
//                progress_bar_dados.visibility = View.GONE
//            }, 1000)
//        }
//    }
    //...
}