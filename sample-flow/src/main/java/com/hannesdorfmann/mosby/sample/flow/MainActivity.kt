package com.hannesdorfmann.mosby.sample.flow

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hannesdorfmann.mosby.sample.flow.countries.CountriesScreen
import com.hannesdorfmann.mosby.sample.flow.flow.AppDispatcher
import com.hannesdorfmann.mosby.sample.flow.flow.AppKeyParceler
import flow.Flow

class MainActivity : AppCompatActivity() {

  override fun attachBaseContext(baseContext: Context) {
    val newBase = Flow.configure(baseContext, this)
        .dispatcher(AppDispatcher(this))
        .defaultKey(CountriesScreen())
        .keyParceler(AppKeyParceler())
        .install()
    super.attachBaseContext(newBase)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  override fun onBackPressed() {
    if (!Flow.get(this).goBack()) {
      super.onBackPressed();
    }
  }
}
