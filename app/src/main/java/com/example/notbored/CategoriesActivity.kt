package com.example.notbored
/*This activity is in charge of the show the category list
* */
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notbored.databinding.ActivityCategoriesBinding

class CategoriesActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCategoriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categoriesAdapter = CategoriesAdapter(Utils.categories)
        binding.rvCategories.adapter = categoriesAdapter

        binding.btnRandom.setOnClickListener {
            //Store the random category
            Utils.categorySelected = getString(R.string.random)
            val intentHobbyActivity = Intent(this, TaskActivity::class.java)
            startActivity(intentHobbyActivity)
        }
    }
}