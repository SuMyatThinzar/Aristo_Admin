package com.aristo.admin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristo.admin.databinding.MainCategoryListItemsBinding
import com.aristo.admin.data.vos.CategoryVO
import com.aristo.admin.utils.components.SharedPreferencesManager
import com.aristo.admin.view.categories.addProducts.BottomSheet
import com.aristo.admin.view.categories.addProducts.CreateSubCategoryActivity
import com.bumptech.glide.Glide

class MainCategoryListRecyclerViewAdapter(
    private val showDeleteBottomSheet: BottomSheet,
    private val categoryVOList: ArrayList<CategoryVO>
) : RecyclerView.Adapter<MainCategoryListRecyclerViewAdapter.MainCategoryListRecyclerViewHolder>() {

    class MainCategoryListRecyclerViewHolder(private val binding: MainCategoryListItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var btnMore = binding.ivMore
        val context: Context = itemView.context

        fun bind(categoryVO: CategoryVO, position: Int) {
            binding.tvCatTitle.text = categoryVO.title

            Glide.with(context)
                .load(categoryVO.imageURL)
                .placeholder(com.aristo.admin.R.drawable.ic_placeholder)
                .into(binding.ivCategory)

            // Set click listeners or perform actions here if needed
            itemView.setOnClickListener {
                // Handle item click
                SharedPreferencesManager.saveCategoryId(categoryVO.id)
                val intent = Intent(context, CreateSubCategoryActivity::class.java)

                SharedPreferencesManager.initialize(context)
                SharedPreferencesManager.saveMainIndex(position)
                //SharedPreferencesManager.saveDatabaseChildPath("${category.id}/")
                //DataListHolder.getInstance().setChildPath("${category.id}/")

                context.startActivity(intent)

            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainCategoryListRecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MainCategoryListItemsBinding.inflate(inflater, parent, false)
        return MainCategoryListRecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoryVOList.size
    }

    override fun onBindViewHolder(holder: MainCategoryListRecyclerViewHolder, position: Int) {

        holder.bind(categoryVOList[position], position)

        holder.btnMore.setOnClickListener {
            showDeleteBottomSheet.onShowBottomSheet(categoryVOList[position].id, categoryVOList[position].title)
        }

    }

    fun updateData(newData: List<CategoryVO>) {
        categoryVOList.clear()
        categoryVOList.addAll(newData)
        notifyDataSetChanged()
    }
}
