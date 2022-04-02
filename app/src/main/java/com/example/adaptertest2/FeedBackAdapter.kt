package com.example.adaptertest2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class FeedBackAdapter(private val list: MutableList<Feedback>): RecyclerView.Adapter<FeedBackAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.feedback_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val curr = list[position]
        holder.itemView.apply {
            val image = findViewById<CircleImageView>(R.id.userImage)
            val name = findViewById<TextView>(R.id.userName)
            val message = findViewById<TextView>(R.id.message)
            val one = findViewById<ImageView>(R.id.one)
            val two = findViewById<ImageView>(R.id.two)
            val three = findViewById<ImageView>(R.id.three)
            val four = findViewById<ImageView>(R.id.four)
            val five = findViewById<ImageView>(R.id.five)

            val starList = ArrayList<ImageView>()
            starList.add(one)
            starList.add(two)
            starList.add(three)
            starList.add(four)
            starList.add(five)

            setRating(starList, curr.star!!.toInt())





            Glide.with(context).load("https://yourzaj.xyz/${curr.user?.image}").error(R.drawable.ic_baseline_account_dark_circle_24).into(image)
            name.text = curr.user?.name
            message.text = curr.message
        }
    }

    private fun setRating(list: ArrayList<ImageView>, star: Int){
        for(i in list.indices){
            if(i < star){
                list[i].setImageResource(R.drawable.ic_baseline_star_24)
            }else{
                list[i].setImageResource(R.drawable.ic_baseline_star_border_24)
            }
        }
    }
    override fun getItemCount(): Int {
        return list.size
    }
    fun addItem(item: Feedback){
        list.add(item)
        notifyItemInserted(list.size - 1)
    }
}