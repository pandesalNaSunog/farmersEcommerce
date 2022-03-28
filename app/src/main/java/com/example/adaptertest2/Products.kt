package com.example.adaptertest2

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import java.lang.Exception
import java.net.SocketTimeoutException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Products.newInstance] factory method to
 * create an instance of this fragment.
 */
class Products : Fragment() {
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
        return inflater.inflate(R.layout.fragment_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val storeMasterRecycler = view.findViewById<RecyclerView>(R.id.storeMasterRecycler)
        val storeMasterAdapter = StoreMasterAdapter(mutableListOf())
        storeMasterRecycler.adapter = storeMasterAdapter
        storeMasterRecycler.layoutManager = LinearLayoutManager(requireContext())

        val progressbar = ProgressBar()
        val progress = progressbar.showProgressBar(requireContext(), R.layout.loading, "Loading...", R.id.progressText)
        val alerts = RequestAlerts(requireContext())
        val noStores = view.findViewById<LinearLayout>(R.id.noStores)
        CoroutineScope(Dispatchers.IO).launch {
            val storeMaster = try{ RetrofitInstance.retro.getStoreMaster() }
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
                    Log.e("Products", e.toString())
                }
                return@launch
            }
            withContext(Dispatchers.Main){
                progress.dismiss()
                if(storeMaster.isNotEmpty()){
                    noStores.isVisible = false
                    for(i in storeMaster.indices){
                        storeMasterAdapter.addItem(storeMaster[i])
                    }
                }else{
                    noStores.isVisible = true
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
         * @return A new instance of fragment Products.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Products().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}