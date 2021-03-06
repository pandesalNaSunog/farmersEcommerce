package com.example.adaptertest2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
 * Use the [Sales.newInstance] factory method to
 * create an instance of this fragment.
 */
class Sales : Fragment() {
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
        return inflater.inflate(R.layout.fragment_sales, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = UserDatabase(requireContext())
        val token = db.getToken()
        val salesRecycler = view.findViewById<RecyclerView>(R.id.salesRecycler)
        val salesAdapter = SalesAdapter(mutableListOf())
        salesRecycler.adapter = salesAdapter
        salesRecycler.layoutManager = LinearLayoutManager(requireContext())

        val progressBar = ProgressBar()
        val progress = progressBar.showProgressBar(requireContext(),R.layout.loading,"Loading...", R.id.progressText)
        val alerts = RequestAlerts(requireContext())

        CoroutineScope(Dispatchers.IO).launch {
            val sales = try{ RetrofitInstance.retro.getSales("Bearer $token") }
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
                for(i in sales.indices){
                    salesAdapter.addItem(sales[i])
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
         * @return A new instance of fragment Sales.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Sales().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}