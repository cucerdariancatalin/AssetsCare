package com.gsabr.assetscare
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MyAdapter(private val orderList : ArrayList<Order>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
            return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = orderList[position]
        holder.numos.text = currentItem.numos
        holder.data.text = currentItem.data
        holder.numpatrimonio.text = currentItem.numpatrimonio
        if (currentItem.funcionario != null){
            holder.funcionario.text = currentItem.funcionario.substringBefore(" ").uppercase()
        }
        holder.codcli.text = currentItem.codcli
        if(currentItem.posos != null){
            holder.posos.text = currentItem.posos
        }

        //val a: String = "A"
        val colorGreen = "#4CAF50"
        if(currentItem.status != null && currentItem.status.uppercase() == "A"){

            holder.status.text = "APROVADA"
            holder.status.setTextColor(Color.parseColor(colorGreen))

        }else{
            holder.status.text = "PENDENTE"
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val numos : TextView = itemView.findViewById(R.id.tv_num_os_item)
        val data : TextView = itemView.findViewById(R.id.tv_data_item)
        val numpatrimonio: TextView = itemView.findViewById(R.id.tv_num_patrimonio_item)
        val funcionario: TextView = itemView.findViewById(R.id.tv_nomefunc_item)
        val codcli : TextView = itemView.findViewById(R.id.tv_codfilial_item)
        val posos: TextView = itemView.findViewById(R.id.tv_pos_item)
        val status: TextView = itemView.findViewById(R.id.tv_status_item)

    }
}