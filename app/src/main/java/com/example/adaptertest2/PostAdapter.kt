package com.example.adaptertest2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private val list: MutableList<PostsItem>): RecyclerView.Adapter<PostAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item,parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = list[position]
        holder.itemView.apply {
            val body = findViewById<TextView>(R.id.body)
            body.text = current.body
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun add(post: PostsItem){
        list.add(post)
        notifyItemInserted(list.size - 1)
    }
}