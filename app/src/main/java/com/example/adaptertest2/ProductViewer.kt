package com.example.adaptertest2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.SocketTimeoutException

class ProductViewer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_viewer)




        val feedbacks = findViewById<Button>(R.id.feedbacks)

        val searchText = findViewById<EditText>(R.id.editText)
        val searchButton = findViewById<Button>(R.id.button)

        searchButton.setOnClickListener {
            if(searchText.text.isEmpty()){
                searchText.error = "Please fill out this field"
            }else{
                val search = Search(this)
                search.goToSearchProducts(searchText.text.toString())
            }
        }

        val available = findViewById<TextView>(R.id.quantity)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val nameView = findViewById<TextView>(R.id.nameView)
        val priceView = findViewById<TextView>(R.id.priceView)
        val descView = findViewById<TextView>(R.id.descriptionView)
        val addToCart = findViewById<Button>(R.id.addToCart)
        val db = UserDatabase(this)
        val token = db.getToken()
        val user = db.getAll()
        val buttons = findViewById<LinearLayout>(R.id.linearLayout2)
        buttons.isVisible = false

        val addToWishList = findViewById<Button>(R.id.addToWishList)

        val image = intent.getStringExtra("image")
        val name = intent.getStringExtra("name")
        val price = intent.getStringExtra("price")
        val description = intent.getStringExtra("desc")
        val id = intent.getIntExtra("id",0)
        val qty = intent.getStringExtra("quantity")
        Log.e("id", id.toString())

        Glide.with(this).load("https://yourzaj.xyz/$image").into(imageView)
        nameView.text = name
        priceView.text = price
        descView.text = description
        available.text = "Available: $qty"

        var addToCartValue = "add"
        var wishListValue = "add"
        var progressBar = ProgressBar()
        var progress = progressBar.showProgressBar(this,R.layout.loading,"Loading...",R.id.progressText)
        var alerts = RequestAlerts(this)

        CoroutineScope(Dispatchers.IO).launch {
            val cart = try{ RetrofitInstance.retro.getCartItems("Bearer $token") }
            catch (e: SocketTimeoutException){
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
                buttons.isVisible = true
                for(i in cart.indices){
                    if(id == cart[i].product_id){
                        addToCartValue = "view"
                        addToCart.text = "view cart"
                        break
                    }
                }
            }
        }

        val progre = ProgressBar()
        var prog = progre.showProgressBar(this,R.layout.loading,"Loading...",R.id.progressText)
        val alert = RequestAlerts(this)

        CoroutineScope(Dispatchers.IO).launch {
            val wish = try{ RetrofitInstance.retro.getWishList("Bearer $token") }
            catch (e: SocketTimeoutException){
                withContext(Dispatchers.Main){
                    prog.dismiss()
                    alert.showSocketTimeOutAlert()
                }
                return@launch
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    prog.dismiss()
                    alert.noInternetAlert()
                }
                return@launch
            }
            withContext(Dispatchers.Main){
                prog.dismiss()
                buttons.isVisible = true
                for(i in wish.indices){
                    if(id == wish[i].product_id){
                        wishListValue = "view"
                        addToWishList.text = "view wishlist"
                        break
                    }
                }
            }
        }

        feedbacks.setOnClickListener {
            val feedbackSheet = BottomSheetDialog(this)
            val feedbackView = LayoutInflater.from(this).inflate(R.layout.feedbak_container, null)
            feedbackSheet.setContentView(feedbackView)
            feedbackSheet.show()
            val ratingGrid = feedbackView.findViewById<GridLayout>(R.id.ratingGrid)
            ratingGrid.isVisible = user?.type != "seller"
            val postComment = feedbackView.findViewById<Button>(R.id.postComment)
            val writeComment = feedbackView.findViewById<EditText>(R.id.writeComment)
            val feedbackRecycler = feedbackView.findViewById<RecyclerView>(R.id.feedbackRecycler)
            val feedbackAdapter = FeedBackAdapter(mutableListOf())


            val one = feedbackView.findViewById<ImageView>(R.id.one)
            val two = feedbackView.findViewById<ImageView>(R.id.two)
            val three = feedbackView.findViewById<ImageView>(R.id.three)
            val four = feedbackView.findViewById<ImageView>(R.id.four)
            val five = feedbackView.findViewById<ImageView>(R.id.five)
            var ratingValue = 0
            val buttonList = ArrayList<ImageView>()
            buttonList.add(one)
            buttonList.add(two)
            buttonList.add(three)
            buttonList.add(four)
            buttonList.add(five)

            updateRating(buttonList,0)

            one.setOnClickListener{
                ratingValue = updateRating(buttonList,1)
            }
            two.setOnClickListener{
                ratingValue = updateRating(buttonList,2)
            }
            three.setOnClickListener{
                ratingValue = updateRating(buttonList,3)
            }
            four.setOnClickListener{
                ratingValue = updateRating(buttonList,4)
            }
            five.setOnClickListener{
                ratingValue = updateRating(buttonList,5)
            }




            feedbackRecycler.adapter = feedbackAdapter
            feedbackRecycler.layoutManager = LinearLayoutManager(this)

            progressBar = ProgressBar()
            progress = progressBar.showProgressBar(this,R.layout.loading, "Loading", R.id.progressText)
            alerts = RequestAlerts(this)

            CoroutineScope(Dispatchers.IO).launch {
                val feedbackResponse = try{ RetrofitInstance.retro.getFeedBack(id) }
                catch(e: SocketTimeoutException){
                    withContext(Dispatchers.Main){
                        progress.dismiss()
                        alerts.showSocketTimeOutAlert()
                    }
                    return@launch
                }catch(e: Exception){
                    withContext(Dispatchers.Main){
                        progress.dismiss()
                        alerts.noInternetAlert()
                        Log.e("sellerproductadapter", e.toString())
                    }
                    return@launch
                }

                withContext(Dispatchers.Main){
                    progress.dismiss()
                    for(i in feedbackResponse.feedbacks.indices){
                        feedbackAdapter.addItem(feedbackResponse.feedbacks[i])
                    }
                }
            }

            postComment.setOnClickListener {
                if(writeComment.text.isEmpty()){
                    writeComment.error = "Please write a comment."
                }else{
                    val jsonObject = JSONObject()
                    jsonObject.put("product_id", id)
                    if(user?.type == "seller") {
                        jsonObject.put("star", 5)
                    }else{
                        jsonObject.put("star", ratingValue)
                    }
                    jsonObject.put("message", writeComment.text.toString())

                    val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

                    progressBar = ProgressBar()
                    progress = progressBar.showProgressBar(this,R.layout.loading, "Posting...", R.id.progressText)
                    alerts = RequestAlerts(this)

                    CoroutineScope(Dispatchers.IO).launch {
                        val postCommentResponse = try{ RetrofitInstance.retro.writeFeedBack("Bearer $token",request) }
                        catch(e: SocketTimeoutException){
                            withContext(Dispatchers.Main){
                                progress.dismiss()
                                alerts.showSocketTimeOutAlert()
                            }
                            return@launch
                        }catch(e: Exception){
                            withContext(Dispatchers.Main){
                                progress.dismiss()
                                alerts.noInternetAlert()
                            }
                            return@launch
                        }

                        withContext(Dispatchers.Main){
                            progress.dismiss()
                            if((postCommentResponse.code() == 200 || postCommentResponse.code() == 201) && postCommentResponse.headers().contains(Pair("content-type","application/json"))){
                                android.app.AlertDialog.Builder(this@ProductViewer)
                                    .setTitle("Success")
                                    .setMessage("Your comment has been posted.")
                                    .setPositiveButton("OK", null)
                                    .show()
                                feedbackSheet.dismiss()
                            }else{
                                android.app.AlertDialog.Builder(this@ProductViewer)
                                    .setTitle("Error")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("OK", null)
                                    .show()
                                feedbackSheet.dismiss()
                            }
                        }
                    }
                }
            }
        }

        addToWishList.setOnClickListener {
            if(wishListValue == "add") {
                progress = progressBar.showProgressBar(
                    this,
                    R.layout.loading,
                    "Please Wait...",
                    R.id.progressText
                )
                val jsonObject = JSONObject()
                jsonObject.put("product_id", id)
                val request =
                    jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                CoroutineScope(Dispatchers.IO).launch {
                    val wishListResponse = try {
                        RetrofitInstance.retro.addToWishList("Bearer $token", request)
                    } catch (e: SocketTimeoutException) {
                        withContext(Dispatchers.Main) {
                            progress.dismiss()
                            alerts.showSocketTimeOutAlert()
                        }
                        return@launch
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            progress.dismiss()
                            alerts.noInternetAlert()
                        }
                        return@launch
                    }

                    withContext(Dispatchers.Main) {
                        progress.dismiss()
                        if (wishListResponse.code() == 200 && wishListResponse.headers()
                                .contains(Pair("content-type", "application/json"))
                        ) {
                            AlertDialog.Builder(this@ProductViewer)
                                .setTitle("Success")
                                .setMessage("Product has been added to your wishlist.")
                                .setPositiveButton("OK", null)
                                .show()
                            wishListValue = "view"
                            addToWishList.text = "view wishlist"
                        } else {
                            alerts.somethingWentWrongAlert()
                            Log.e("viewer", wishListResponse.errorBody().toString())
                        }
                    }
                }
            }else{
                val intent = Intent(this, WishList::class.java)
                startActivity(intent)
                finish()
            }
        }
        addToCart.setOnClickListener {
            if(addToCartValue == "add") {

                val addToCartAlert = AlertDialog.Builder(this)
                val addTOCartAlertView =
                    LayoutInflater.from(this).inflate(R.layout.quantity_selector, null)
                addToCartAlert.setView(addTOCartAlertView)
                val alert = addToCartAlert.show()

                val decrease = addTOCartAlertView.findViewById<Button>(R.id.decrease)
                val increase = addTOCartAlertView.findViewById<Button>(R.id.increase)
                val quantityText = addTOCartAlertView.findViewById<TextView>(R.id.quantityText)
                val confirm = addTOCartAlertView.findViewById<Button>(R.id.confirm)
                var quantity = 1
                quantityText.text = quantity.toString()

                decrease.setOnClickListener {
                    if (quantity != 1) {
                        quantity--
                        quantityText.text = quantity.toString()
                    }
                }

                increase.setOnClickListener {
                    if (quantity < qty!!.toInt()) {
                        quantity++
                        quantityText.text = quantity.toString()
                    }
                }

                confirm.setOnClickListener {
                    progress = progressBar.showProgressBar(
                        this,
                        R.layout.loading,
                        "Please Wait...",
                        R.id.progressText
                    )
                    val jsonObject = JSONObject()
                    jsonObject.put("product_id", id)
                    jsonObject.put("quantity", quantity)

                    val request =
                        jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

                    CoroutineScope(Dispatchers.IO).launch {
                        val addToCartResponse = try {
                            RetrofitInstance.retro.addToCart("Bearer $token", request)
                        } catch (e: SocketTimeoutException) {
                            withContext(Dispatchers.Main) {
                                progress.dismiss()
                                alerts.showSocketTimeOutAlert()
                            }
                            return@launch
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                progress.dismiss()
                                alerts.noInternetAlert()
                            }
                            return@launch
                        }

                        withContext(Dispatchers.Main) {
                            progress.dismiss()
                            if (addToCartResponse.code() == 200 && addToCartResponse.headers()
                                    .contains(Pair("content-type", "application/json"))
                            ) {
                                AlertDialog.Builder(this@ProductViewer)
                                    .setTitle("Success")
                                    .setMessage("Product has been successfully added to cart.")
                                    .setPositiveButton("OK", null)
                                    .show()
                                addToCartValue = "view"
                                addToCart.text = "view cart"
                                alert.dismiss()
                            } else {
                                AlertDialog.Builder(this@ProductViewer)
                                    .setTitle("Error")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("OK", null)
                                    .show()
                            }
                        }
                    }
                }
            }else{
                val intent = Intent(this, MyCart::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun updateRating(list: ArrayList<ImageView>, value: Int): Int{
        for(i in list.indices){
            if(i < value){
                list[i].setImageResource(R.drawable.ic_baseline_star_24)
                Log.e("viewer", "$i dark")
            }else{
                list[i].setImageResource(R.drawable.ic_baseline_star_border_24)
                Log.e("viewer", "$i light")
            }
        }
        return value
    }
}