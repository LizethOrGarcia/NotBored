package com.example.notbored

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.notbored.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            }else{
                val intentTermConditionActivity = Intent(this, TermConditionActivity::class.java)
                this.startActivity(intentTermConditionActivity)
            }
        }

    }
}