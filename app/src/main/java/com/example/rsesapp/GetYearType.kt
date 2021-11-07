package com.example.rsesapp

import android.widget.RadioGroup

class GetYearType {
    fun getYear(radio_group: RadioGroup): String {
        var year: String? = null
        if(radio_group.checkedRadioButtonId == R.id.year2016) year = "2016"
        else if(radio_group.checkedRadioButtonId == R.id.year2017) year = "2017"
        else if(radio_group.checkedRadioButtonId == R.id.year2018) year = "2018"
        else if(radio_group.checkedRadioButtonId == R.id.year2019) year = "2019"
        else if(radio_group.checkedRadioButtonId == R.id.year2020) year = "2020"
        else if(radio_group.checkedRadioButtonId == R.id.year2021) year = "2021"
        else year = ""

        return year
    }

    fun getType(radio_group: RadioGroup): String {
        var type:String? = null
        if (radio_group.checkedRadioButtonId == R.id.OTBR) type = "OTBR"
        else if(radio_group.checkedRadioButtonId == R.id.NOT_OTBR) type = "NORMAL"
        else type = ""
        return type
    }
}