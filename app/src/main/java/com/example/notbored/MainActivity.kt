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
                binding.btnStart.isEnabled = editable.toString().isNotBlank()
            }
        }))

        binding.btnStart.setOnClickListener {
            val intentCategoriesActivity = Intent(this, CategoriesActivity::class.java)
            Utils.cantParticipants = Integer.parseInt(binding.etParticipants.text.toString())
            this.startActivity(intentCategoriesActivity)
        }

    }
}