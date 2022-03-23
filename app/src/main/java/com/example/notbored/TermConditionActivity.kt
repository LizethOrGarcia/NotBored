package com.example.notbored

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notbored.databinding.ActivityTermConditionBinding
import com.google.android.material.snackbar.Snackbar

class TermConditionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTermConditionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermConditionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ibClose.setOnClickListener {
            onBackPressed()
        }

        binding.cbTermsAndCondition.isChecked = Utils.acceptTermsAndCondition

        binding.cbTermsAndCondition.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Utils.acceptTermsAndCondition = true
                Snackbar.make(
                    binding.root,
                    getString(R.string.you_accept_terms_and_conditions),
                    Snackbar.LENGTH_LONG
                )
                    .show()
            } else {
                Utils.acceptTermsAndCondition = false
            }

        }
    }
}