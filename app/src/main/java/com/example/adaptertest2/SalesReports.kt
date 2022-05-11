package com.example.adaptertest2

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.anychart.AnyChart
import com.anychart.AnyChart.pie
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.core.Chart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SalesReports.newInstance] factory method to
 * create an instance of this fragment.
 */
class SalesReports : Fragment() {
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
    var entries = ArrayList<BarEntry>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = UserDatabase(requireContext())
        val token = db.getToken()
        val from = view.findViewById<TextView>(R.id.from)
        val to = view.findViewById<TextView>(R.id.to)
        val fromDateSelector = view.findViewById<Button>(R.id.fromDateSelector)
        val toDateSelector = view.findViewById<Button>(R.id.toDateSelector)


        val generate = view.findViewById<Button>(R.id.generateChart)

        fromDateSelector.setOnClickListener {
            val calendar = Calendar.getInstance()
            val myear = calendar.get(Calendar.YEAR)
            val mmonth = calendar.get(Calendar.MONTH)
            val dday = calendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val date = "$year-${month+1}-$dayOfMonth"
                from.text = date
            }, myear,mmonth,dday)
                .show()
        }
        toDateSelector.setOnClickListener {
            val calendar = Calendar.getInstance()
            val myear = calendar.get(Calendar.YEAR)
            val mmonth = calendar.get(Calendar.MONTH)
            val dday = calendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                to.text = "$year-${month+1}-$dayOfMonth"
            }, myear,mmonth,dday)
                .show()
        }

        generate.setOnClickListener {
            if(from.text.isEmpty()){
                from.error = "Please choose a date"
            }else if(to.text.isEmpty()){
                to.error = "Please choose a date"
            }else{
                val progress = ProgressBar()
                val progressBar = progress.showProgressBar(requireContext(), R.layout.loading, "Generating...", R.id.progressText)
                val alerts = RequestAlerts(requireContext())

                val jsonObject = JSONObject()
                jsonObject.put("from", from.text.toString())
                jsonObject.put("to", to.text.toString())
                val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())



                CoroutineScope(Dispatchers.IO).launch {
                    val salesReport = try{ RetrofitInstance.retro.getReport("Bearer $token", request) }
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
                        val chart = view.findViewById<BarChart>(R.id.barChart)
                        val productList = view.findViewById<TextView>(R.id.productList)
                        var productListText = ""
                        for(i in salesReport.indices){
                            entries.add(BarEntry(i.toFloat(), (salesReport[i].product_price.toFloat() * salesReport[i].order_quantity.toInt())))
                            if(i == 0){
                                productListText = "${productListText}Products from left to right:\n\n${salesReport[i].product_name}"
                            }else{
                                productListText = "${productListText}\n${salesReport[i].product_name}"
                            }
                        }

                        productList.text = productListText
                        val dataSet = BarDataSet(entries,"SalesReport")
                        val barData = BarData(dataSet)
                        chart.data = barData
                        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toMutableList()
                        dataSet.valueTextColor = Color.BLACK
                        dataSet.valueTextSize = 16f
                        chart.description.isEnabled = true
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sales_reports, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SalesReports.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SalesReports().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}