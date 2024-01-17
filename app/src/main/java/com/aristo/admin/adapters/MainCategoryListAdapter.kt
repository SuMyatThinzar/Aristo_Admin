package com.aristo.admin.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristo.admin.R
import com.aristo.admin.databinding.ViewHolderMainCategoryBinding
import com.aristo.admin.data.vos.CategoryVO
import com.aristo.admin.utils.processColorCode
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions

class MainCategoryListAdapter(private val listener: MainCategoriesRecyclerViewListener) : RecyclerView.Adapter<MainCategoryListAdapter.MainCategoryListViewHolder>() {

    private var selectedPosition = 0
    private var mainCategoryListVO = listOf<CategoryVO>()

    class MainCategoryListViewHolder(private var binding: ViewHolderMainCategoryBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(categoryVO: CategoryVO, position: Int) {

            binding.tvCatTitle.text = categoryVO.title
            val context = itemView.context

            if (categoryVO.colorCode != ""){
                binding.viewColor.visibility = View.VISIBLE
                binding.imageView.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.viewColor.foreground = ColorDrawable(Color.parseColor(processColorCode(categoryVO.colorCode)))
            }
            else{
                binding.viewColor.visibility = View.GONE
                binding.imageView.visibility = View.VISIBLE

                binding.progressBar.visibility = View.VISIBLE
                Glide.with(context).load(categoryVO.imageURL).apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            binding.progressBar.visibility = View.GONE
                            binding.imageView.setImageResource(R.drawable.ic_placeholder)
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            binding.progressBar.visibility = View.GONE
                            return false
                        }
                    })
                    .into(binding.imageView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainCategoryListViewHolder {
        val binding = ViewHolderMainCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainCategoryListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mainCategoryListVO.size
    }

    override fun onBindViewHolder(holder: MainCategoryListViewHolder, position: Int) {
        holder.bind(mainCategoryListVO[position], position)

        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.color.white)
        } else {
            holder.itemView.setBackgroundResource(R.color.background)
        }

        holder.itemView.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = holder.adapterPosition

            notifyItemChanged(previousSelectedPosition)

            notifyItemChanged(selectedPosition)

            listener.reloadSubCategoriesRecyclerView(position)
        }
    }

    fun setNewData(categories: List<CategoryVO>) {
        mainCategoryListVO = categories
        notifyDataSetChanged()
    }

    interface MainCategoriesRecyclerViewListener {
        fun reloadSubCategoriesRecyclerView(index : Int)
    }
}
