package com.example.rsesapp.ui.image

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImagesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Изображения"
    }
    val text: LiveData<String> = _text
}