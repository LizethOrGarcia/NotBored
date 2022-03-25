package com.example.notbored
/*
This activity is in charge of selecting a task with the selected parameters.
* */
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.notbored.databinding.ActivityTaskBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class TaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val internetConnection = Utils.checkForInternet(this)

        //Check the internet connection and show the task or show error message
        if (internetConnection) {
            getTask(getPath())
        } else {
            showErrorConnection()
        }

        binding.titleToolbar.text = Utils.categorySelected

        binding.btnTryAgain.setOnClickListener {
            //Check the internet connection and show the task or show error message
            if (internetConnection) {
                getTask(getPath())
            } else {
                showErrorConnection()
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnGoHome.setOnClickListener {
            val intentGoHome = Intent(this, MainActivity::class.java)
            startActivity(intentGoHome)
        }
    }


    /*This function returns the add path based on the price*/
    private fun getTaskWithPrice(): String = when (Utils.priceSelected) {
        getString(R.string.free) -> {
            getString(R.string.value_free)
        }
        getString(R.string.low) -> {
            getString(R.string.value_low)
        }
        getString(R.string.medium) -> {
            getString(R.string.value_medium)
        }
        getString(R.string.high) -> {
            getString(R.string.value_high)
        }
        else -> {
            ""
        }
    }

    /*This function show the error connection message*/
    private fun showErrorConnection() {
        Snackbar.make(
            binding.root,
            getString(R.string.error_connection),
            Snackbar.LENGTH_INDEFINITE
        )
            .show()
    }

    /*This function show the error when it does not find task with the selected parameters */
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

    /*This functions builds the path based on the selected parameters */
    private fun getPath(): String {
        var path = "activity?"

        // check if a random task
        val isRandom = Utils.categorySelected == getString(R.string.random)
        if (!isRandom) {
            path+= "type=${Utils.categorySelected.lowercase(Locale.getDefault())}"
        }
        when {
            // Case no participants selected and  no price selected
            isRandom && Utils.cantParticipants == 0 && Utils.priceSelected == getString(R.string.standard) -> {
                path = "activity/"
            }
            // Case no participants selected and no price selected
            Utils.cantParticipants == 0 && Utils.priceSelected != getString(R.string.standard) -> {
                path += "&${getTaskWithPrice()}"
            }
            // Case participants selected and no price selected
            Utils.cantParticipants != 0 && Utils.priceSelected == getString(R.string.standard) -> {
                path += "&participants=${Utils.cantParticipants}"
            }
            // Case participants selected and price selected
            Utils.cantParticipants != 0 && Utils.priceSelected != getString(R.string.standard) -> {
                path += "&participants=${Utils.cantParticipants}&${getTaskWithPrice()}"
            }
        }
        return path
    }

    /*This functions returns the price message */
    private fun getPrice(price: Double): String {
        return when {
            price == Utils.maximumFree -> {
                getString(R.string.free)
            }
            price <= Utils.maximumLow -> {
                getString(R.string.low)
            }
            price <= Utils.maximumMedium -> {
                getString(R.string.medium)
            }
            else -> {
                getString(R.string.high)
            }
        }
    }

    /*This function perfoms the request and configures the view based on the result*/
    private fun getTask(query: String) {
        binding.progressBarTask.visibility = View.VISIBLE
        binding.containerCard.visibility = View.GONE
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val call = getRetroFit().create(ApiService::class.java)
                    .getTask(query)
                val task: Task? = call.body()
                runOnUiThread {
                    if (call.isSuccessful) {
                        task?.error?.let {
                            //When the request returns error show task error
                            showErrorTask()
                        } ?: run {
                            //When the request returns is successful show task
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

    /*This functions create the retrofit instance*/
    private fun getRetroFit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Utils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}