package com.hannesdorfmann.mosby3.sample.flow

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hannesdorfmann.mosby3.sample.flow.countries.CountriesScreen
import com.hannesdorfmann.mosby3.sample.flow.flow.AtlasAppDispatcher
import com.hannesdorfmann.mosby3.sample.flow.flow.AtlasAppKeyParceler
import flow.Flow

class MainActivity : AppCompatActivity() {

  override fun attachBaseContext(baseContext: Context) {
    val newBase = Flow.configure(baseContext, this)
        .dispatcher(AtlasAppDispatcher(this))
        .defaultKey(CountriesScreen())
        .keyParceler(AtlasAppKeyParceler())
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
