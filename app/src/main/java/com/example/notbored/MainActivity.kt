package com.example.notbored

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.notbored.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        reset()
        val isNetworkConnected = Utils.checkForInternet(this)
        if (isNetworkConnected) {
            Log.d("INTERNET", "SI")
            binding.btnStart.isEnabled = true
        } else {
            Log.d("INTERNET", "NO")
            binding.btnStart.isEnabled = false
            Snackbar.make(
                binding.root,
                getString(R.string.error_connection),
                Snackbar.LENGTH_INDEFINITE
            )
                .show()
        }

        val prices = resources.getStringArray(R.array.prices)
        binding.spinnerPrice.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, prices)
        binding.spinnerPrice.setSelection(0)

        binding.spinnerPrice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (position == 0) {
                    Utils.priceSelected = getString(R.string.standard)
                } else {
                    Utils.priceSelected = prices[position]
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

        binding.tvTermsCond.setOnClickListener {
            val intentTermConditionActivity = Intent(this, TermConditionActivity::class.java)
            this.startActivity(intentTermConditionActivity)
        }

        binding.etParticipants.addTextChangedListener((object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                when {
                    editable.isBlank() -> {
                        Utils.cantParticipants = 0
                    }
                    Integer.parseInt(editable.toString()) < 0 -> {
                        binding.btnStart.isEnabled = false
                    }
                    else -> {
                        Utils.cantParticipants = (Integer.parseInt(editable.toString()))
                    }
                }
            }
        }))

        binding.btnStart.setOnClickListener {
            if (Utils.acceptTermsAndCondition) {
                val intentCategoriesActivity = Intent(this, CategoriesActivity::class.java)
                this.startActivity(intentCategoriesActivity)
            } else {
                val intentTermConditionActivity = Intent(this, TermConditionActivity::class.java)
                this.startActivity(intentTermConditionActivity)
            }
        }

    }

    private fun reset(){
        binding.etParticipants.setText("")
        binding.spinnerPrice.setSelection(0)
    }


}