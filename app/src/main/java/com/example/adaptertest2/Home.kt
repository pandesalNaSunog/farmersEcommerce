package com.example.adaptertest2

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.getSystemService
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import retrofit2.HttpException
import java.net.SocketTimeoutException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var isRequestingNotification = true
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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val noProducts = view.findViewById<LinearLayout>(R.id.noProducts)
        val db = UserDatabase(requireContext())
        val token = db.getToken()
        val user = db.getAll()
        val productRecycler = view.findViewById<RecyclerView>(R.id.productRecycler)
        val productAdapter = ProductAdapter(mutableListOf())
        productRecycler.adapter = productAdapter
        productRecycler.layoutManager = GridLayoutManager(requireContext(),2)

        val progressBar = ProgressBar()
        val progress = progressBar.showProgressBar(requireContext(),R.layout.loading,"Loading...",R.id.progressText)
        val alerts = RequestAlerts(requireContext())


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(requireContext(), NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
        CoroutineScope(Dispatchers.IO).launch {
            val products = try{ RetrofitInstance.retro.getProducts() }
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
                for(i in products.indices){
                    if(products[i].store_owner != null) {
                        productAdapter.addItem(products[i])
                    }
                }
                noProducts.isVisible = products.size == 0
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            isRequestingNotification = true
            do{
                val notifications = try{ RetrofitInstance.retro.getNotifications("Bearer $token") }
                catch(e: SocketTimeoutException){
                    Log.e("Home", e.toString())
                    return@launch
                }catch(e: Exception){
                    Log.e("Home", e.toString())
                    return@launch
                }

                withContext(Dispatchers.Main){
                    for(i in notifications.indices) {
                        val notifBuilder = NotificationCompat.Builder(requireContext(),"My Notification")
                        notifBuilder.setContentTitle("New Product")
                        notifBuilder.setContentText(notifications[i].data.message)
                        notifBuilder.setSmallIcon(R.drawable.iani)
                        notifBuilder.setAutoCancel(true)

                        val managerCompat = NotificationManagerCompat.from(requireContext())
                        managerCompat.notify(i + 1, notifBuilder.build())

                        Toast.makeText(requireContext(), "New Product Added", Toast.LENGTH_LONG).show()
                    }

//                    val gson = GsonBuilder().setPrettyPrinting().create()
//                    val json = gson.toJson(JsonParser.parseString(notifications.body()?.string()))
//                    Log.e("Home", json)
                }
                delay(1000)
            }while(isRequestingNotification)

        }
        CoroutineScope(Dispatchers.IO).launch {
            val products = try{ RetrofitInstance.retro.getCartItems("Bearer $token") }
            catch(e: SocketTimeoutException){
                withContext(Dispatchers.Main){
                    progress.dismiss()
                    alerts.showSocketTimeOutAlert()
                }
                return@launch
            }catch(e: HttpException){
                withContext(Dispatchers.Main){
                    progress.dismiss()
                    AlertDialog.Builder(requireContext())
                        .setTitle("Error")
                        .setMessage("You're not authorized to use the application. Your account might have been deleted. Please sign up for a new account")
                        .setCancelable(false)
                        .setPositiveButton("OK"){_,_->
                            db.deleteAll()
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            activity?.finishAffinity()
                        }
                        .show()
                }
                return@launch
            }catch(e: Exception){
                withContext(Dispatchers.Main){
                    progress.dismiss()
                    alerts.noInternetAlert()
                    Log.e("Home", e.toString())
                }
                return@launch
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRequestingNotification = false
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}