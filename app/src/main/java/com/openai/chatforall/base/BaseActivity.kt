package com.openai.chatforall.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity() {
    // Our binding instance which can be used in derived classes
    protected lateinit var binding: B

    protected abstract fun onCreate()

    // Binding needs to be initialized before calling super.onCreate()
    // Because onCreate method in Activity is final we cannot override it.
    // Therefore we need to create our own method
    protected abstract fun initBinding(): B

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = initBinding()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        onCreate()
    }
}
