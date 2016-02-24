package com.hannesdorfmann.mosby.sample.flow

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import flow.Flow

class MainActivity : AppCompatActivity() {

  override fun attachBaseContext(baseContext: Context) {
    val newBase = Flow.configure(baseContext, this).install()
    super.attachBaseContext(newBase)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }
}
