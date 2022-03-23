package com.example.adaptertest2

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.json.JSONTokener
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.SocketTimeoutException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SellerProducts.newInstance] factory method to
 * create an instance of this fragment.
 */
class SellerProducts : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var productAdapter: SellerProductAdapter
    var name = ""
    var category = ""
    var sprice = 0.0
    var sdescription = ""
    var squantity = 0
    var image = ""
    private lateinit var alert: AlertDialog.Builder
    private lateinit var showAlert: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seller_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val db = UserDatabase(requireContext())
        val token = db.getToken()
        val productRecycler = view.findViewById<RecyclerView>(R.id.productRecycler)
        productAdapter = SellerProductAdapter(mutableListOf())
        productRecycler.adapter = productAdapter
        productRecycler.layoutManager = LinearLayoutManager(requireContext())

        val progressBar = ProgressBar()
        val progress = progressBar.showProgressBar(requireContext(),R.layout.loading,"Loading...",R.id.progressText)
        val alerts = RequestAlerts(requireContext())

        CoroutineScope(Dispatchers.IO).launch {
            val myProducts = try{ RetrofitInstance.retro.getSellerProducts("Bearer $token") }
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
                for(i in myProducts.indices){
                    productAdapter.addItem(myProducts[i])
                }
            }
        }

        val addProduct = view.findViewById<Button>(R.id.addProduct)

        addProduct.setOnClickListener {
            alert = AlertDialog.Builder(requireContext())
            val alertView = LayoutInflater.from(requireContext()).inflate(R.layout.add_product_form, null)
            alert.setView(alertView)


            val productName = alertView.findViewById<EditText>(R.id.name)
            val productCategory = alertView.findViewById<TextView>(R.id.category)
            val price = alertView.findViewById<EditText>(R.id.price)
            val desc = alertView.findViewById<EditText>(R.id.description)
            val quantity = alertView.findViewById<EditText>(R.id.quantity)
            val chooseImage = alertView.findViewById<Button>(R.id.chooseImage)
            val categorySelector = alertView.findViewById<Button>(R.id.categorySelector)

            categorySelector.setOnClickListener {
                val categoryBottomSheet = BottomSheetDialog(requireContext())
                val categoryBottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.category_bottomsheet,null)
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

                vegetable.setOnClickListener {
                    productCategory.text = "Vegetable"
                    categoryBottomSheet.dismiss()
                }
                fruit.setOnClickListener {
                    productCategory.text = "Fruit"
                    categoryBottomSheet.dismiss()
                }
                meat.setOnClickListener {
                    productCategory.text = "Meat"
                    categoryBottomSheet.dismiss()
                }
                fish.setOnClickListener {
                    productCategory.text = "Fish"
                    categoryBottomSheet.dismiss()
                }
                dairy.setOnClickListener {
                    productCategory.text = "Dairy"
                    categoryBottomSheet.dismiss()
                }
                poultry.setOnClickListener {
                    productCategory.text = "Poultry"
                    categoryBottomSheet.dismiss()
                }
                seeds.setOnClickListener {
                    productCategory.text = "Seeds"
                    categoryBottomSheet.dismiss()
                }
                plant.setOnClickListener {
                    productCategory.text = "Plant"
                    categoryBottomSheet.dismiss()
                }
            }


            chooseImage.setOnClickListener {
                if(productName.text.isEmpty()){
                    productName.error = "Please fill out this field."
                }else if(productCategory.text.isEmpty()){
                    productCategory.error = "Please fill out this field."
                }else if(price.text.isEmpty()){
                    price.error = "Please fill out this field."
                }else if(desc.text.isEmpty()){
                    desc.error = "Please fill out this field."
                }else if(quantity.text.isEmpty()){
                    quantity.error = "Please fill out this field."
                }else{
                    name = productName.text.toString()
                    category = productCategory.text.toString()
                    sprice = price.text.toString().toDouble()
                    sdescription = desc.text.toString()
                    squantity = quantity.text.toString().toInt()
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, 100)
                }
            }
            showAlert = alert.show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val progressbar = ProgressBar()

        val progress = progressbar.showProgressBar(requireContext(),R.layout.loading,"Adding Product...", R.id.progressText)
        val db = UserDatabase(requireContext())
        val token = db.getToken()

        val alerts = RequestAlerts(requireContext())

        if(requestCode == 100 && resultCode == AppCompatActivity.RESULT_OK && data != null){
            val uri = data.data

            try{
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver,uri)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                val bytes: ByteArray = stream.toByteArray()

                image = Base64.encodeToString(bytes, Base64.DEFAULT)


                val jsonObject = JSONObject()
                jsonObject.put("name", name)
                jsonObject.put("image", image)
                jsonObject.put("category", category)
                jsonObject.put("price", sprice)
                jsonObject.put("description", sdescription)
                jsonObject.put("quantity", squantity)

                val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

                CoroutineScope(Dispatchers.IO).launch {
                    val addProductResponse = try{ RetrofitInstance.retro.addProduct("Bearer $token",request) }
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
                        if(addProductResponse.code() == 200 && addProductResponse.headers().contains(Pair("content-type","application/json"))){
                            val gson = GsonBuilder().setPrettyPrinting().create()
                            val json = gson.toJson(JsonParser.parseString(addProductResponse.body()?.string()))
                            Log.e("seller", json)

                            val jsonResponse = JSONTokener(json).nextValue() as JSONObject
                            val productJson = jsonResponse.getJSONObject("product")
                            val thisId = productJson.getInt("id")
                            val thisImage = productJson.getString("image")
                            showAlert.dismiss()
                            val product = ProductItemX(category,null,sdescription,thisId,thisImage,name,sprice.toString(),squantity.toString(),null,null,null)
                            productAdapter.addItem(product)
                            AlertDialog.Builder(requireContext())
                                .setTitle("Success!")
                                .setMessage("Product has been added.")
                                .setPositiveButton("OK", null)
                                .show()
                        }else{
                            showAlert.dismiss()
                            AlertDialog.Builder(requireContext())
                                .setTitle("Error")
                                .setMessage("Something went wrong. Please try again")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }
            }catch(e: IOException){
                e.printStackTrace()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SellerProducts.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SellerProducts().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}