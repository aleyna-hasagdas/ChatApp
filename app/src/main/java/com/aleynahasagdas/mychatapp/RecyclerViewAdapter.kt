package com.aleynahasagdas.mychatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(private val chatMessages: List<String>) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycle_rows, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val chatMessage = chatMessages[position]
        holder.chatMessage.text = chatMessage
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatMessage: TextView = itemView.findViewById(R.id.reycler_rows_text)
        }
}

