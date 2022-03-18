package com.example.notbored

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.notbored.databinding.ActivityHobbyBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class HobbyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHobbyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getTask()

        binding.btnTryAgain.setOnClickListener {
            getTask()
        }
    }

    private fun getTask() {
        if (Utils.categorySelected == getString(R.string.random)) {
            getRandomTask()
        } else {
            getTaskByCategory(Utils.categorySelected)
        }
    }

    //http://www.boredapi.com/api/activity/
    private fun getRandomTask() {

        CoroutineScope(Dispatchers.IO).launch {
            val participants = Utils.cantParticipants
            Log.d("REQUEST", "${participants} ${Utils.categorySelected}")
            val call = getRetroFit().create(ApiService::class.java)
                .getRandomTask("activity?participants=$participants")
            val hobby: Hobby? = call.body()
            Log.d("HOBBY",hobby.toString())
            runOnUiThread {
                hobby?.error?.let {
                    Snackbar.make(
                        binding.root,
                        "ERROR EN CANTIDAD DE PARTICIPANTES",
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                    Log.e("Networking", "Call failed")

                } ?: run {
                    binding.tvTask.text = hobby?.activity
                    binding.tvCantParticipants.text = hobby?.participants.toString()
                    binding.tvCategory.text = hobby?.type
                    binding.tvPrice.text = hobby?.price?.let { getPrice(it) }
                }
            }
        }
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

    private fun getTaskByCategory(query: String) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val participants = Utils.cantParticipants
                Log.d("REQUEST", "${participants} ${Utils.categorySelected}")
                val call = getRetroFit().create(ApiService::class.java)
                    .getTaskByCategory("activity?type=$query&participants=$participants")
                val hobby: Hobby? = call.body()
                Log.d("HOBBY",hobby.toString())
                runOnUiThread {
                    if (call.isSuccessful) {
                        hobby?.error?.let {
                            Snackbar.make(
                                binding.root,
                                "ERROR EN CANTIDAD DE PARTICIPANTES",
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                            Log.e("Networking", "Call failed")

                        } ?: run {
                            binding.tvTask.text = hobby?.activity
                            binding.tvCantParticipants.text = hobby?.participants.toString()
                            binding.containerCategory.visibility = View.GONE
                            binding.tvPrice.text = hobby?.price?.let { getPrice(it) }
                        }
                    } else {
                        Snackbar.make(binding.root, "ERROR DE  CONEXION", Snackbar.LENGTH_LONG)
                            .show()
                        Log.e("Networking", "Call failed")
                    }

                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
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