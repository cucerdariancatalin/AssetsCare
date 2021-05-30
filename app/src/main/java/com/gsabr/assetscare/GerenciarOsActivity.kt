package com.gsabr.assetscare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_revisao.*
import java.util.*
import kotlin.collections.ArrayList

class GerenciarOsActivity : AppCompatActivity() {

    private lateinit var osRecyclerview: RecyclerView
    private lateinit var osArrayList: ArrayList<Order>
    lateinit var num_os : Array<String>
    lateinit var datafim : Array<String>
    lateinit var p: Order


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gerenciar_os)


        num_os = arrayOf("1587", "1588")
        datafim = arrayOf("16/03/2021 02:03", "16/03/2021 02:03")

        osRecyclerview = findViewById(R.id.recyclerView)
        osRecyclerview.layoutManager = LinearLayoutManager(this)
        osRecyclerview.setHasFixedSize(true)

        osArrayList = arrayListOf<Order>()
        //getUserData()
        objectRequestOrders("http://192.168.110.239:8080/api_manutencao/api/listar_os")
    }






    //Obtendo lista de OS
    private fun objectRequestOrders(url: String) {

        val queue = Volley.newRequestQueue(this)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->

//                var arrayListPecas = arrayListOf<String>()
                //arrayListPecas.add("00 ; NENHUMA")
                for (i in 0 until response.length()) {

                    val orderJsonObject = response.getJSONObject(i)
                    //val numos = orderJsonObject.getString("numos")
                    //val data = orderJsonObject.getString("data")
                    p = Gson().fromJson(orderJsonObject.toString(), Order::class.java)

                    osArrayList.add(p)

                    osRecyclerview.adapter = MyAdapter(osArrayList)
                }

                Toast.makeText(
                    applicationContext,
                    "OS's carregadas!",
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
//
//    private fun getUserData() {
//
//       for(i in num_os.indices){
//
//            val oss = Order("Num. OS: " + num_os[i], "Data: " + datafim[i])
//            osArrayList.add(oss)
//
//            osRecyclerview.adapter = MyAdapter(osArrayList)
//        }
//    }
}