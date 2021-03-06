package com.example.adaptertest2

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [More.newInstance] factory method to
 * create an instance of this fragment.
 */
class More : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val showStoreLocations = view.findViewById<CardView>(R.id.storeLocations)

        val wishList = view.findViewById<CardView>(R.id.wishList)
        val myCart = view.findViewById<CardView>(R.id.myCart)
        val logout = view.findViewById<CardView>(R.id.logout)
        val db = UserDatabase(requireContext())
        val user = db.getAll()
        val token = db.getToken()
        Log.e("more", user.toString())
        val myStore = view.findViewById<CardView>(R.id.myStore)
        myStore.isVisible = user?.type == "seller"

        myCart.setOnClickListener {
            val intent = Intent(requireContext(),MyCart::class.java)
            startActivity(intent)
        }

        wishList.setOnClickListener {
            val inent = Intent(requireContext(), WishList::class.java)
            startActivity(inent)
        }

        showStoreLocations.setOnClickListener {
            val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            if(gpsStatus){
                val intent = Intent(requireContext(), StoreLocations::class.java)
                startActivity(intent)
            }else{
                AlertDialog.Builder(requireContext())
                    .setTitle("Error")
                    .setMessage("Please turn your location services on first.")
                    .setPositiveButton("OK"){_,_->
                        val intent1 = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent1)
                    }.show()
            }
        }

        myStore.setOnClickListener {
            Log.e("more",user?.approved_as_store_owner_at.toString())

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
                    if(profile.approved_as_store_owner_at != null) {
                        val intent = Intent(activity, SellerNavigation::class.java)
                        startActivity(intent)
                    }else{
                        AlertDialog.Builder(requireContext())
                            .setTitle("Pending Approval")
                            .setMessage("Your store is currently pending for approval. Please wait for the administrator to approve your store registration.")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
            }


        }

        logout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("YES"){_,_->
                    val progressBar = ProgressBar()
                    val progress = progressBar.showProgressBar(requireContext(), R.layout.loading, "Logging out...", R.id.progressText)
                    val alerts = RequestAlerts(requireContext())

                    CoroutineScope(Dispatchers.IO).launch {
                        val logoutResponse = try{ RetrofitInstance.retro.logout("Bearer $token") }
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
                            if(logoutResponse.code() == 200 && logoutResponse.headers().contains(Pair("content-type","application/json"))){
                                db.deleteAll()
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                startActivity(intent)
                                activity?.finishAffinity()
                            }else{
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Error")
                                    .setMessage("Something went wrong")
                                    .setPositiveButton("OK", null)
                                    .show()
                            }
                        }
                    }
                }
                .setNegativeButton("NO", null)
                .show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment More.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            More().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}