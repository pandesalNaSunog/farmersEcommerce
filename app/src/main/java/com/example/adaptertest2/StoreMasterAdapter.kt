package com.example.adaptertest2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.createDeviceProtectedStorageContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable
import java.net.SocketTimeoutException

class StoreMasterAdapter(private val list: MutableList<StoreMasterItem>): RecyclerView.Adapter<StoreMasterAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.store_item,parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = list[position]
        holder.itemView.apply {
            val db = UserDatabase(context)
            val token = db.getToken()
            val name = findViewById<TextView>(R.id.storeName)
            val storeCard = findViewById<CardView>(R.id.storeCard)
            val coopId = current.farmers_cooperative_id
            val coopNameView = findViewById<TextView>(R.id.coopName)
            val coopName = coopId.split("|")[1]
            val follow = findViewById<Button>(R.id.follow)
            val alerts = RequestAlerts(context)

            if(current.can_be_follow){
                follow.text = "Follow"
            }else{
                follow.text = "Unfollow"
            }
            follow.setOnClickListener {
                if(current.can_be_follow) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val followResponse = try {
                            RetrofitInstance.retro.followStore("Bearer $token", current.id)
                        } catch (e: SocketTimeoutException) {
                            withContext(Dispatchers.Main) {
                                alerts.showSocketTimeOutAlert()
                            }
                            return@launch
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                alerts.noInternetAlert()
                            }
                            return@launch
                        }

                        withContext(Dispatchers.Main) {
                            if (followResponse.code() == 200 && followResponse.headers()
                                    .contains(Pair("content-type", "application/json"))
                            ) {
                                follow.text = "Unfollow"
                                current.can_be_follow = false
                            }
                        }
                    }
                }else{
                    CoroutineScope(Dispatchers.IO).launch {
                        val followResponse = try {
                            RetrofitInstance.retro.unfollowStore("Bearer $token", current.id)
                        } catch (e: SocketTimeoutException) {
                            withContext(Dispatchers.Main) {
                                alerts.showSocketTimeOutAlert()
                            }
                            return@launch
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                alerts.noInternetAlert()
                            }
                            return@launch
                        }

                        withContext(Dispatchers.Main) {
                            if (followResponse.code() == 200 && followResponse.headers()
                                    .contains(Pair("content-type", "application/json"))
                            ) {
                                follow.text = "Follow"
                                current.can_be_follow = true
                            }
                        }
                    }
                }
            }

            coopNameView.text = coopName
            name.text = current.store_name

            storeCard.setOnClickListener {
                val categoryBottomSheet = BottomSheetDialog(context)
                val categoryBottomSheetView = LayoutInflater.from(context).inflate(R.layout.category_bottomsheet,null)
                categoryBottomSheet.setContentView(categoryBottomSheetView)
                categoryBottomSheet.show()

                val vegetable = categoryBottomSheetView.findViewById<CardView>(R.id.vegetable)
                val fruit = categoryBottomSheetView.findViewById<CardView>(R.id.fruit)
                val meat = categoryBottomSheetView.findViewById<CardView>(R.id.meat)
                val fish = categoryBottomSheetView.findViewById<CardView>(R.id.fish)
                val dairy = categoryBottomSheetView.findViewById<CardView>(R.id.dairy)
                val poultry = categoryBottomSheetView.findViewById<CardView>(R.id.poultry)
                val seeds = categoryBottomSheetView.findViewById<CardView>(R.id.seeds)
                val plant = categoryBottomSheetView.findViewById<CardView>(R.id.plant)
                val intent = Intent(context,ProductsPerCategory::class.java)
                val bundle = Bundle()

                vegetable.setOnClickListener {
                    bundle.putSerializable("productList", current.Vegetable as Serializable)
                    intent.putExtras(bundle)
                    intent.putExtra("title", "${current.store_name} / Vegetable")
                    intent.putExtra("store_owner_id", current.id)
                    startActivity(context,intent,null)
                }
                fruit.setOnClickListener {
                    bundle.putSerializable("productList", current.Fruit as Serializable)
                    intent.putExtras(bundle)
                    intent.putExtra("title", "${current.store_name} / Fruit")
                    intent.putExtra("store_owner_id", current.id)
                    startActivity(context,intent,null)
                }
                meat.setOnClickListener {
                    bundle.putSerializable("productList", current.Meat as Serializable)
                    intent.putExtras(bundle)
                    intent.putExtra("title", "${current.store_name} / Meat")
                    intent.putExtra("store_owner_id", current.id)
                    startActivity(context,intent,null)
                }
                fish.setOnClickListener {
                    bundle.putSerializable("productList", current.Fish as Serializable)
                    intent.putExtras(bundle)
                    intent.putExtra("title", "${current.store_name} / Fish")
                    intent.putExtra("store_owner_id", current.id)
                    startActivity(context,intent,null)
                }
                dairy.setOnClickListener {
                    bundle.putSerializable("productList", current.Dairy as Serializable)
                    intent.putExtras(bundle)
                    intent.putExtra("title", "${current.store_name} / Dairy")
                    intent.putExtra("store_owner_id", current.id)
                    startActivity(context,intent,null)
                }
                poultry.setOnClickListener {
                    bundle.putSerializable("productList", current.Poultry as Serializable)
                    intent.putExtras(bundle)
                    intent.putExtra("title", "${current.store_name} / Poultry")
                    intent.putExtra("store_owner_id", current.id)
                    startActivity(context,intent,null)
                }
                seeds.setOnClickListener {
                    bundle.putSerializable("productList", current.Seeds as Serializable)
                    intent.putExtras(bundle)
                    intent.putExtra("title", "${current.store_name} / Seeds")
                    intent.putExtra("store_owner_id", current.id)
                    startActivity(context,intent,null)
                }
                plant.setOnClickListener {
                    bundle.putSerializable("productList", current.Plant as Serializable)
                    intent.putExtras(bundle)
                    intent.putExtra("title", "${current.store_name} / Plant")
                    intent.putExtra("store_owner_id", current.id)
                    startActivity(context,intent,null)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addItem(item: StoreMasterItem){
        list.add(item)
        notifyItemInserted(list.size - 1)
    }
}