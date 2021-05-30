package com.gsabr.assetscare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val numos : TextView = itemView.findViewById(R.id.tv_num_os_item)
        val data : TextView = itemView.findViewById(R.id.tv_data_fim_os)
    }
}