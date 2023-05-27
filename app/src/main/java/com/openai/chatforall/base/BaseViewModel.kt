package com.openai.chatforall.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    // Use this to handle error messages
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // Show a user-friendly error message
    fun showError(message: String) {
        _errorMessage.value = message
    }
}
