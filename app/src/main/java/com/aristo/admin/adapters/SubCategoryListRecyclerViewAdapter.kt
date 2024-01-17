package com.aristo.admin.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristo.admin.data.dataholders.CategoryDataHolder
import com.aristo.admin.data.dataholders.DataListHolder
import com.aristo.admin.utils.components.SharedPreferencesManager
import com.aristo.admin.databinding.CategoryListItemsBinding
import com.aristo.admin.data.vos.CategoryVO
import com.aristo.admin.view.categories.addProducts.CreateSubCategoryActivity
import com.bumptech.glide.Glide

class SubCategoryListRecyclerViewAdapter (private val categoryVOList: ArrayList<CategoryVO>) : RecyclerView.Adapter<SubCategoryListRecyclerViewAdapter.SubCategoryListRecyclerViewHolder>(){

    class SubCategoryListRecyclerViewHolder(private val binding: CategoryListItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(categoryVO: CategoryVO, position: Int) {

            val context: Context = itemView.context

            SharedPreferencesManager.initialize(context)
            binding.tvCatTitle.text = categoryVO.title

            Glide.with(context)
                .load(categoryVO.imageURL)
                .placeholder(com.aristo.admin.R.drawable.ic_placeholder)
                .into(binding.ivCategory)

            // Set click listeners or perform actions here if needed
            itemView.setOnClickListener {

                if (categoryVO.type != ""){
                    DataListHolder.getInstance().setIsType(true)
                    CategoryDataHolder.getInstance().countingType = categoryVO.type
                }
                else{
                    DataListHolder.getInstance().setIsType(false)
                }

                val intent = Intent(context, CreateSubCategoryActivity::class.java)

                DataListHolder.getInstance().setSubIDList(categoryVO.id)
                DataListHolder.getInstance().setSubIndexList(position)
                DataListHolder.getInstance().setChildPath("${categoryVO.id}/")

                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubCategoryListRecyclerViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = CategoryListItemsBinding.inflate(inflater, parent, false)
        return SubCategoryListRecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoryVOList.size
    }

    override fun onBindViewHolder(holder: SubCategoryListRecyclerViewHolder, position: Int) {

        holder.bind(categoryVOList[position], position)

    }

    fun updateData(newData: List<CategoryVO>) {
        categoryVOList.clear()
        categoryVOList.addAll(newData)
        notifyDataSetChanged()
    }

}