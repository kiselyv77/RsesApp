package com.example.rsesapp.ui.video_otbr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VideosOtbrViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Отборные видео"
   }
    val text: LiveData<String> = _text
}