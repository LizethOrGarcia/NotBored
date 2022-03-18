package com.example.notbored

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notbored.databinding.ActivityTermConditionBinding

class TermConditionActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTermConditionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermConditionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ibClose.setOnClickListener {
            onBackPressed()
        }

        binding.tvTyC.text = getString(R.string.terms_condition_body)

    }
}