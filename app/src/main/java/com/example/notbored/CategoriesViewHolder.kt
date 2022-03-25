package com.example.notbored

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.notbored.databinding.ItemCategoriesBinding

class CategoriesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemCategoriesBinding.bind(view)

    fun bind(category: String) {
        binding.tvCategory.text = category
        binding.ivArrow.setOnClickListener {
            //Store the selected category
            Utils.categorySelected = category
            val intentHobbyActivity = Intent(it.context, TaskActivity::class.java)
            it.context.startActivity(intentHobbyActivity)

        }
    }
}