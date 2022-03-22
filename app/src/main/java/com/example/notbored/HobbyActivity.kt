package com.example.notbored

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
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

    private fun isNetworkConnected(): Boolean {
        //1
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //2
        val activeNetwork = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.activeNetwork
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        //3
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        //4
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun getTask() {

        val isNetworkConnected = isNetworkConnected()
        if (isNetworkConnected) {
            when {
                Utils.categorySelected == getString(R.string.random) && Utils.cantParticipants == 0 -> {
                    getRandomTask("activity/")
                }
                Utils.categorySelected == getString(R.string.random) -> {
                    getRandomTask("activity?${Utils.cantParticipants}")
                }
                Utils.categorySelected != getString(R.string.random) && Utils.cantParticipants == 0 -> {
                    getTaskByCategory("activity?type=${Utils.categorySelected}")
                }
                else -> {
                    getTaskByCategory("activity?type=${Utils.categorySelected}&participants=${Utils.cantParticipants}")
                }
            }
        }else{
            Snackbar.make(
                binding.root,
                "No hay conexion a internet... Intentalo mas tarde",
                Snackbar.LENGTH_LONG
            )
                .show()
            Log.e("Networking", "Call failed")
        }
    }

    //http://www.boredapi.com/api/activity/
    private fun getRandomTask(query: String) {

        CoroutineScope(Dispatchers.IO).launch {
            val participants = Utils.cantParticipants
            Log.d("REQUEST", "${participants} ${Utils.categorySelected}")
            val call = getRetroFit().create(ApiService::class.java)
                .getRandomTask(query)
            val hobby: Hobby? = call.body()
            Log.d("HOBBY", hobby.toString())
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
                    .getTaskByCategory(query)
                val hobby: Hobby? = call.body()
                Log.d("HOBBY", hobby.toString())
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