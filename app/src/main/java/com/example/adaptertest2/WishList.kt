package com.example.adaptertest2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class WishList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wish_list)


        val clearWish = findViewById<Button>(R.id.clearWish)
        val wishIsEmpty = findViewById<LinearLayout>(R.id.wishIsEmpty)
        val db = UserDatabase(this)
        val token = db.getToken()
        val wishRecycler = findViewById<RecyclerView>(R.id.wishRecycler)
        val wishAdapter = WishListAdapter(mutableListOf(), clearWish, wishIsEmpty)
        wishRecycler.adapter = wishAdapter
        wishRecycler.layoutManager = LinearLayoutManager(this)

        val progressBar = ProgressBar()
        var progress = progressBar.showProgressBar(this, R.layout.loading,"Loading...",R.id.progressText)
        val alerts = RequestAlerts(this)

        CoroutineScope(Dispatchers.IO).launch {
            val wishList = try{RetrofitInstance.retro.getWishList("Bearer $token")}
            catch(e: SocketTimeoutException){
                withContext(Dispatchers.Main){
                    progress.dismiss()
                    alerts.showSocketTimeOutAlert()
                }
                return@launch
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    progress.dismiss()
                    alerts.noInternetAlert()
                }
                return@launch
            }

            withContext(Dispatchers.Main){
                progress.dismiss()
                for(i in wishList.indices){
                    wishAdapter.addItem(wishList[i])
                }
                clearWish.isVisible = wishList.size != 0
            }
        }

        clearWish.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Clear Wishlist")
                .setMessage("Are your sure you want to clear your wish list?")
                .setPositiveButton("YES"){_,_->
                    progress = progressBar.showProgressBar(this, R.layout.loading,"Please Wait...",R.id.progressText)
                    CoroutineScope(Dispatchers.IO).launch {
                        val clearWishResponse = try{ RetrofitInstance.retro.clearWishList("Bearer $token") }
                        catch(e: SocketTimeoutException){
                            withContext(Dispatchers.Main){
                                progress.dismiss()
                                alerts.showSocketTimeOutAlert()
                            }
                            return@launch
                        }catch (e: Exception){
                            withContext(Dispatchers.Main){
                                progress.dismiss()
                                alerts.noInternetAlert()
                            }
                            return@launch
                        }

                        withContext(Dispatchers.Main){
                            progress.dismiss()
                            if(clearWishResponse.code() == 200 && clearWishResponse.headers().contains(Pair("content-type","application/json"))){
                                AlertDialog.Builder(this@WishList)
                                    .setTitle("Success")
                                    .setMessage("Your wishlist is now empty.")
                                    .setPositiveButton("OK", null)
                                    .show()
                                wishAdapter.deleteAll()
                            }else{
                                alerts.somethingWentWrongAlert()
                            }
                        }
                    }
                }
                .setNegativeButton("NO", null)
                .show()
        }
    }
}