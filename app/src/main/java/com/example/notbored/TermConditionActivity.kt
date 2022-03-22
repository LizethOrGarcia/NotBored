package com.example.notbored

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.notbored.databinding.ActivityTermConditionBinding
import com.google.android.material.snackbar.Snackbar

class TermConditionActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTermConditionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermConditionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ibClose.setOnClickListener {
            onBackPressed()
        }

        binding.cbTermsAndCondition.isChecked = Utils.acceptTermsAndCondition

        binding.cbTermsAndCondition.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                Utils.acceptTermsAndCondition = true
                Snackbar.make(binding.root, "You Accept Terms And Conditions...", Snackbar.LENGTH_LONG)
                    .show()
            }else{
                Utils.acceptTermsAndCondition = false
            }

        }

        binding.tvTyC.text = getString(R.string.terms_condition_body)

    }
}