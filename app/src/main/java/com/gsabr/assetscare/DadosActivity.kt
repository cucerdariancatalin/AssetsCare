package com.gsabr.assetscare
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_dados.*
import kotlinx.android.synthetic.main.activity_dados.sv_dados
import kotlinx.android.synthetic.main.activity_dados.tv_numero_patrimonio
import kotlinx.android.synthetic.main.activity_revisao.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class DadosActivity : AppCompatActivity() {

    var dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val jsonobj = JSONObject()
    var dadoAusente = "DADO AUSENTE OU NÃO INFORMADO!"
    lateinit var descricaoEquipamento: String
    lateinit var localizacaoEquipamento: String
    lateinit var dataInstalacaoS: String
    lateinit var dataUltManutencaoS: String
    lateinit var codigoEquipamento: String
    lateinit var responsavelEquipamento: String
    lateinit var fabricante: String
    lateinit var custo: String
    lateinit var nomeResponsavel: String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dados)
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) // Deixar layout somente na modo retrato

        var patrimonio = intent.getStringExtra("patrimonio")
        jsonobj.put("", patrimonio)
        val que: RequestQueue = Volley.newRequestQueue(this)
        val url = "http://192.168.110.239:8080/api_manutencao/api/equipamento/${patrimonio}"
        val req = JsonObjectRequest(Request.Method.GET, url, jsonobj,
            {
                    response ->
                nomeResponsavel = response.getString("nome_responsavel")
                descricaoEquipamento = response.getString("descricao")
                codigoEquipamento = response.getString("codequip")
                //dataInstalacaoS = response.getString("dtcadastro")
                dataInstalacaoS = response.getString("dtcad")
                dataUltManutencaoS = response.getString("dtrevisao")
                responsavelEquipamento = response.getString("responsavel")
                localizacaoEquipamento = response.getString("localizacao")
                fabricante = response.getString("fabricante")
                custo = response.getString("custo")


                tv_numero_patrimonio.text = if(patrimonio == null) dadoAusente else patrimonio
                tv_descricao_equipamento.text = if(descricaoEquipamento == "null") dadoAusente else descricaoEquipamento.toUpperCase()
                tv_data_instalacao.text = if(dataInstalacaoS == "null") dadoAusente else dataInstalacaoS
                tv_data_revisao.text = if(dataUltManutencaoS == "null") dadoAusente else dataUltManutencaoS
                tv_responsavel.text = if(responsavelEquipamento == "null") dadoAusente else responsavelEquipamento.toUpperCase()
                tv_localizacao.text = if(localizacaoEquipamento == "null") dadoAusente else localizacaoEquipamento.toUpperCase()
                tv_fabricante.text = if(fabricante == "null") dadoAusente else fabricante.toUpperCase()
                tv_custo.text = if(custo == "null") dadoAusente else "R$ " + custo


                iv_dados.visibility = View.VISIBLE
                sv_dados.visibility = View.VISIBLE

            }, {
                sv_dados.visibility = View.INVISIBLE
                iv_dados.visibility = View.GONE
                iv_erro_rede.visibility = View.VISIBLE
                tv_alerta_rede.visibility = View.VISIBLE
                Toast.makeText(this, "Erro ao conectar com o servidor!", Toast.LENGTH_SHORT).show()
            })
        que.add(req)

        btn_registrar_revisao.setOnClickListener {

            val detalhesDaRevisao = Intent(this@DadosActivity, RevisaoActivity::class.java)
            detalhesDaRevisao.putExtra("patrimonio", patrimonio)
            detalhesDaRevisao.putExtra("localizacao", localizacaoEquipamento.toUpperCase())
            startActivity(detalhesDaRevisao)
        }
    }
}