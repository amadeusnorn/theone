package com.example.theone

import android.content.DialogInterface.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.theone.databinding.ActivityMainBinding
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.refeshButton.setOnClickListener{fetchCurrencyData().start()}
    }

    private fun fetchCurrencyData(): Thread {
        return Thread {
            val url = URL("https://open.er-api.com/v6/latest/usd")
            val connection = url.openConnection() as HttpsURLConnection

            if (connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val request = Gson().fromJson(inputStreamReader, Request::class.java)
                updateUI(request)
                inputStreamReader.close()
                inputSystem.close()
                System.out.println(request.time_last_update_utc)
            } else {
                binding.baseCurrency.text = "Failed Connection"
            }
        }
    }

    private fun updateUI(request: Request) {
        runOnUiThread {
            kotlin.run {
                binding.lastUpdated.text = request.time_last_update_utc
                binding.nzd.text = String.format("NZD: %.2f", request.rates.NZD)
                binding.aud.text = String.format("AUD: %.2f", request.rates.AUD)
                binding.gbp.text = String.format("GBP: %.2f", request.rates.GBP)
            }
        }
    }
}