package com.example.notbored

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CategoriesAdapter(private val categories: List<String>) : RecyclerView.Adapter<CategoriesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CategoriesViewHolder (layoutInflater.inflate(R.layout.item_categories, parent, false))
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val categoriesAtPosition = categories[position]
        holder.bind(categoriesAtPosition)
    }

    override fun getItemCount() = categories.size

}
