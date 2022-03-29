package com.example.adaptertest2

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
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
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.json.JSONTokener
import java.io.ByteArrayOutputStream
import java.net.SocketTimeoutException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyAccount.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyAccount : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var image: String
    private lateinit var imageText: TextView

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
        return inflater.inflate(R.layout.fragment_my_account, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1 && data != null){
            val uri = data.data

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                val bytes: ByteArray = stream.toByteArray()

                image = Base64.encodeToString(bytes, Base64.DEFAULT)
                imageText.text = uri?.path
            }catch(e: Exception){
                e.printStackTrace()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = UserDatabase(requireContext())
        val token = db.getToken()
        var user = db.getAll()

        val editProfile = view.findViewById<Button>(R.id.editProfile)
        val name = view.findViewById<TextView>(R.id.name)
        val email = view.findViewById<TextView>(R.id.email)
        val address = view.findViewById<TextView>(R.id.address)
        val profileImage = view.findViewById<CircleImageView>(R.id.profileImage)

        var progress = ProgressBar()
        var progressBar = progress.showProgressBar(requireContext(),R.layout.loading,"Loading...", R.id.progressText)
        val alerts = RequestAlerts(requireContext())

        CoroutineScope(Dispatchers.IO).launch {
            val profile = try{ RetrofitInstance.retro.getMyProfile("Bearer $token") }
            catch(e: SocketTimeoutException){
                withContext(Dispatchers.Main){
                    progressBar.dismiss()
                    alerts.showSocketTimeOutAlert()
                }
                return@launch
            }catch(e: Exception){
                withContext(Dispatchers.Main){
                    progressBar.dismiss()
                    alerts.noInternetAlert()
                }
                return@launch
            }

            withContext(Dispatchers.Main){
                progressBar.dismiss()
                progressBar.dismiss()
                name.text = profile.name
                email.text = profile.email
                address.text = profile.address
                Glide.with(requireContext()).load("https://yourzaj.xyz/${profile.image}").error(R.drawable.ic_baseline_account_circle_24).into(profileImage)
            }
        }



        editProfile.setOnClickListener {
            val bottomSheet = BottomSheetDialog(requireContext())
            val bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.update_profile, null)
            bottomSheet.setContentView(bottomSheetView)
            bottomSheet.show()
            val storeNameCard = bottomSheetView.findViewById<CardView>(R.id.storeNameCard)
            val storeName = bottomSheetView.findViewById<EditText>(R.id.storeName)
            val updateEmail = bottomSheetView.findViewById<EditText>(R.id.updateEmail)
            val password = bottomSheetView.findViewById<EditText>(R.id.password)
            val confirmPassword = bottomSheetView.findViewById<EditText>(R.id.confirmPassword)
            val coopId = bottomSheetView.findViewById<EditText>(R.id.coopId)
            val contact = bottomSheetView.findViewById<EditText>(R.id.contact)
            val update = bottomSheetView.findViewById<Button>(R.id.update)
            val updateName = bottomSheetView.findViewById<EditText>(R.id.updateName)
            val farmersCooperative = bottomSheetView.findViewById<TextView>(R.id.coop)
            val coopSelector = bottomSheetView.findViewById<Button>(R.id.coopSelector)
            val addressEditText = bottomSheetView.findViewById<EditText>(R.id.address)
            val addressCard = bottomSheetView.findViewById<CardView>(R.id.addressCard)
            val coopCard = bottomSheetView.findViewById<CardView>(R.id.coopCard)
            val chooseImage = bottomSheetView.findViewById<Button>(R.id.chooseImage)
            val coopIdCard = bottomSheetView.findViewById<CardView>(R.id.coopIdCard)
            imageText = bottomSheetView.findViewById(R.id.imageText)

            coopSelector.setOnClickListener {
                val coopBottomSheet = BottomSheetDialog(requireContext())
                val coopBottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.cooperative_bottom_sheet, null)
                coopBottomSheet.setContentView(coopBottomSheetView)
                coopBottomSheet.show()


            }

            chooseImage.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent,1,null)
            }
            var coopName = ""

            if(user!!.type == "seller"){
                storeNameCard.isVisible = true
                coopIdCard.isVisible = true
                coopCard.isVisible = true
                addressCard.isVisible = true
            }else{
                storeNameCard.isVisible = false
                coopIdCard.isVisible = false
                coopCard.isVisible = false
                addressCard.isVisible = true
            }

            Log.e("MyAccount", user!!.type.toString())

            if(storeName.text.isEmpty()){
                    storeName.setText(user!!.store_name)
            }

            if(address.text.isEmpty()){
                address.setText(user!!.address)
            }

            if(updateEmail.text.isEmpty()){
                updateEmail.setText(user!!.email)
            }
            if(coopId.text.isEmpty()){
                if(user!!.farmers_cooperative_id != "") {
                    coopId.setText(user!!.farmers_cooperative_id?.split("|")?.get(0))
                }
            }

            if(contact.text.isEmpty()){
                contact.setText(user!!.phone)
            }

            if(updateName.text.isEmpty()){
                updateName.setText(user!!.name)
            }
            if(farmersCooperative.text.isEmpty()){
                if(user!!.farmers_cooperative_id != "") {
                    farmersCooperative.text = user!!.farmers_cooperative_id?.split("|")?.get(1)
                }
            }

            update.setOnClickListener {
                if(password.text.isEmpty()){
                    password.error = "Please fill out this field"
                }else if(password.text.toString() != confirmPassword.text.toString()){
                    password.error = "Password mismatch"
                }else if(imageText.text.isEmpty()){
                    imageText.error = "Please choose an image"
                }else{
                    val jsonObject = JSONObject()
                    jsonObject.put("name", updateName.text.toString())
                    jsonObject.put("address", addressEditText.text.toString())
                    jsonObject.put("password", password.text.toString())
                    jsonObject.put("farmers_cooperative_id", "${coopId.text}|${farmersCooperative.text}")
                    jsonObject.put("phone", contact.text.toString())
                    jsonObject.put("store_name", storeName.text.toString())
                    jsonObject.put("coordinates", user!!.coordinates)
                    jsonObject.put("image", image)

                    val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

                    progress = ProgressBar()
                    progressBar = progress.showProgressBar(requireContext(),R.layout.loading,"Updating Profile...", R.id.progressText)

                    CoroutineScope(Dispatchers.IO).launch {
                        val updateProfileResponse = try{ RetrofitInstance.retro.updateProfile("Bearer $token",request) }
                        catch(e: SocketTimeoutException){
                            withContext(Dispatchers.Main){
                                progressBar.dismiss()
                                alerts.showSocketTimeOutAlert()
                            }
                            return@launch
                        }catch(e: Exception){
                            withContext(Dispatchers.Main){
                                progressBar.dismiss()
                                alerts.noInternetAlert()
                            }
                            return@launch
                        }

                        withContext(Dispatchers.Main){
                            progressBar.dismiss()

                            if(updateProfileResponse.code() == 200 && updateProfileResponse.headers().contains(Pair("content-type","application/json"))){

                                val gson = GsonBuilder().setPrettyPrinting().create()
                                val json = gson.toJson(JsonParser.parseString(updateProfileResponse.body()?.string()))

                                val profileResponseObject = JSONTokener(json).nextValue() as JSONObject

                                val image = profileResponseObject.getString("image")

                                Log.e("MyAccount", json)
                                val userData = UserXX(address.text.toString(),user?.approved_as_store_owner_at,user?.coordinates,null,user?.email,null,"${coopId.text}|${farmersCooperative.text}",
                                user?.id,image,user?.name,user?.phone,null,user?.store_name,user?.type,null)
                                db.updateProfile(userData)
                                user = db.getAll()
                                name.text = user!!.name
                                email.text = user!!.email
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Success")
                                    .setMessage("Your profile has been updated.")
                                    .setPositiveButton("OK", null)
                                    .show()
                                bottomSheet.dismiss()
                                Glide.with(requireContext()).load("https://yourzaj.xyz/$image").error(R.drawable.ic_baseline_account_circle_24).into(profileImage)
                                address.text = addressEditText.text.toString()

                            }else{
                                alerts.somethingWentWrongAlert()
                            }
                        }
                    }
                }
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
         * @return A new instance of fragment MyAccount.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyAccount().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}