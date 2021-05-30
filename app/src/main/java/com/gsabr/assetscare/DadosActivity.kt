package com.gsabr.assetscare
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_dados.*
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


//    lateinit var descricaoEquipamento: String
      lateinit var localizacaoEquipamento: String
//    lateinit var dataInstalacaoS: String
//    lateinit var dataUltManutencaoS: String
//    lateinit var codigoEquipamento: String
//    lateinit var responsavelEquipamento: String
//    lateinit var nomeResponsavel: String
      lateinit var codEquip: String
//    lateinit var fabricante: String
      lateinit var codFilial: String
//    lateinit var custo: String
      lateinit var p: Patrimonio


    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dados)
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) // Deixar layout somente na modo retrato


        var numPatrimonio = intent.getStringExtra("patrimonio")
        var r = Run()


        jsonobj.put("", numPatrimonio)
        val que: RequestQueue = Volley.newRequestQueue(this)
        val url = "http://192.168.110.239:8080/api_manutencao/api/equipamento/${numPatrimonio}"
        val req = JsonObjectRequest(Request.Method.GET, url, jsonobj,
            { response ->

                p = Gson().fromJson(response.toString(), Patrimonio::class.java)

                Toast.makeText(this, p.nome_responsavel, Toast.LENGTH_SHORT).show()
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

                Toast.makeText(this, p.dtcadastro, Toast.LENGTH_LONG).show()
                //Toast.makeText(this, resp, Toast.LENGTH_SHORT).show()





            },{
                sv_dados.visibility = View.INVISIBLE
                iv_dados.visibility = View.GONE
                r.changeView(arrayOf(iv_erro_rede, tv_alerta_rede), View.VISIBLE)
                Toast.makeText(this, "Erro ao conectar com o servidor!", Toast.LENGTH_SHORT).show()
            })
        que.add(req)

        //iv_dados.visibility = View.VISIBLE
        //sv_dados.visibility = View.VISIBLE

        //
        //Toast.makeText(this@DadosActivity, p.toString(), Toast.LENGTH_LONG).show()


        //(numPatrimonio ?: dadoAusente).also { tv_numero_patrimonio.text = it }
          //tv_descricao_equipamento.text = "TESTE"

        //Toast.makeText(this, p.nome_responsavel, Toast.LENGTH_SHORT).show()
          //tv_data_instalacao.text = if(p.dtcadastro == "null") dadoAusente else p.dtcadastro
//        tv_data_revisao.text = if(p.dataultmanutencao == "null") dadoAusente else p.dataultmanutencao
//        tv_responsavel.text = p.responsavel.toString() + " - " + p.nome_responsavel.uppercase(Locale.getDefault())
//        tv_localizacao.text = if(p.localizacao == "null") dadoAusente else p.localizacao.uppercase(Locale.getDefault())
//        tv_fabricante.text = if(p.fabricante == "null") dadoAusente else p.fabricante.uppercase(Locale.getDefault())
//        tv_custo.text = ("""R$ ${p.custo}""")


//                nomeResponsavel = response.getString("nome_responsavel")
//                descricaoEquipamento = response.getString("descricao")
//                codigoEquipamento = response.getString("codequip")
//                dataInstalacaoS = response.getString("dtcad")
//                dataUltManutencaoS = response.getString("dtrevisao")
//                responsavelEquipamento = response.getString("responsavel")
//                localizacaoEquipamento = response.getString("localizacao")
//                fabricante = response.getString("fabricante")
//                custo = response.getString("custo")
//                codEquip = response.getString("codequip")
//                codFilial = response.getString("codfilial")
//
//
//

//
//            }, {
//                sv_dados.visibility = View.INVISIBLE
//                iv_dados.visibility = View.GONE
//                iv_erro_rede.visibility = View.VISIBLE
//                tv_alerta_rede.visibility = View.VISIBLE
//                Toast.makeText(this, "Erro ao conectar com o servidor!", Toast.LENGTH_SHORT).show()
//            })
//        que.add(req)


//
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


}