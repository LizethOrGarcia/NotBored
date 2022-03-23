package com.example.notbored

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.notbored.databinding.ActivityHobbyBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class TaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHobbyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val internetConnection = Utils.checkForInternet(this)

        if (internetConnection) {
            getTask(getPath())
        } else {
            showErrorConnection()
        }


        binding.titleToolbar.text = Utils.categorySelected

        binding.btnTryAgain.setOnClickListener {
            if (internetConnection) {
                getTask(getPath())
            } else {
                showErrorConnection()
            }
        }
        binding.btnGoHome.setOnClickListener {
            val intentGoHome = Intent(this, MainActivity::class.java)
            startActivity(intentGoHome)
        }
    }

    private fun getTaskWithPrice(): String = when (Utils.priceSelected) {
        getString(R.string.free) -> {
            "price=0.0"
        }
        getString(R.string.low) -> {
            "minprice=0.1&maxprice=0.3"
        }
        getString(R.string.medium) -> {
            "minprice=0.4&maxprice=0.6"
        }
        getString(R.string.high) -> {
            "minprice=0.7&maxprice=1.0"
        }
        else -> {
            ""
        }
    }

    private fun showErrorConnection() {
        Snackbar.make(
            binding.root,
            getString(R.string.error_connection),
            Snackbar.LENGTH_INDEFINITE
        )
            .show()
    }

    private fun showErrorTask() {
        Snackbar.make(
            binding.root,
            getString(R.string.error_participants),
            Snackbar.LENGTH_INDEFINITE
        ).setAction(getString(R.string.back)) {
            onBackPressed()
        }
            .show()
    }


    private fun getPath(): String {
        var path: String = "activity?"

        val isRandom = Utils.categorySelected == getString(R.string.random)
        if (!isRandom) {
            path+= "type=${Utils.categorySelected.lowercase(Locale.getDefault())}"
        }

        Log.d("PATHINITIAL", path)
        val priceText = getString(R.string.standard)
        Log.d("PRICE", "${Utils.priceSelected} == ${priceText}")
        when {
            // no participantes no precio
            isRandom && Utils.cantParticipants == 0 && Utils.priceSelected == getString(R.string.standard) -> {
                path = "activity/"
            }
            // no participante con precio
            Utils.cantParticipants == 0 && Utils.priceSelected != getString(R.string.standard) -> {
                path += "&${getTaskWithPrice()}"
            }
            // participante no precio
            Utils.cantParticipants != 0 && Utils.priceSelected == getString(R.string.standard) -> {
                path += "&participants=${Utils.cantParticipants}"
            }
            // participante  precio
            Utils.cantParticipants != 0 && Utils.priceSelected != getString(R.string.standard) -> {
                path += "&participants=${Utils.cantParticipants}&${getTaskWithPrice()}"
            }
        }
        Log.d("PATHTYPE", path)
        return path
    }

    private fun getPrice(price: Double): String {
        return when {
            price == 0.0 -> {
                getString(R.string.free)
            }
            price <= 0.3 -> {
                getString(R.string.low)
            }
            price <= 0.6 -> {
                getString(R.string.medium)
            }
            else -> {
                getString(R.string.high)
            }
        }
    }

    private fun getTask(query: String) {
        Log.d("URL", query)
        binding.progressBarTask.visibility = View.VISIBLE
        binding.containerCard.visibility = View.GONE
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val participants = Utils.cantParticipants
                Log.d("REQUEST", "${participants} ${Utils.categorySelected}")
                val call = getRetroFit().create(ApiService::class.java)
                    .getTask(query)
                val task: Task? = call.body()
                Log.d("HOBBY", task.toString())
                runOnUiThread {
                    if (call.isSuccessful) {
                        task?.error?.let {
                            showErrorTask()
                        } ?: run {
                            binding.progressBarTask.visibility = View.GONE
                            binding.containerCard.visibility = View.VISIBLE
                            binding.tvTask.text = task?.activity
                            binding.tvCantParticipants.text = task?.participants.toString()
                            binding.tvPrice.text = task?.price?.let { getPrice(it) }
                            if(Utils.categorySelected==getString(R.string.random)){
                                binding.tvCategory.text = task?.type?.capitalize(Locale.ROOT)
                            }else{
                                binding.containerCategory.visibility = View.GONE
                            }
                        }
                    } else {
                        showErrorConnection()
                        Log.e("Networking", "Call failed")
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            showErrorConnection()
            Log.e("Networking", "Call failed ${e.message}")

        }
    }


    private fun getRetroFit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Utils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}