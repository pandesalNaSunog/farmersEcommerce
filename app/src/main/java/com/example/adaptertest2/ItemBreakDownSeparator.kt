package com.example.adaptertest2

import android.util.Log

class ItemBreakDownSeparator {

    fun separateBySlashN(itemBreakDown: String): ArrayList<String>{
        val listOfItemStrings = itemBreakDown.split("\n")
        val list = ArrayList<String>()
        for(i in listOfItemStrings.indices){
            list.add(listOfItemStrings[i])
        }
        return list
    }

    fun getQuantity(itemBreakDown: String): String{
        return itemBreakDown.split("***")[3]
    }

    fun getPrice(itemBreakDown: String): Double{
        val price = itemBreakDown.split("***")[2]
        return price.toDouble()
    }

    fun productName(itemBreakDown: String): String {
        return itemBreakDown.split("***")[1]
    }
}