package com.aristo.admin.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristo.admin.network.FirebaseRealtimeApi
import com.aristo.admin.R
import com.aristo.admin.databinding.ViewHolderChildCategoryBinding
import com.aristo.admin.view.categories.getProducts.ChildCategoriesActivity
import com.aristo.admin.data.vos.CategoryVO
import com.aristo.admin.utils.processColorCode
import com.aristo.admin.view.categories.getProducts.ProductDetailActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions

class ChildCategoryListAdapter(private val listener: ChildCategoryListener, private val type: String) : RecyclerView.Adapter<ChildCategoryListAdapter.SubCategoryListViewHolder>() {

    private var subCategoryListVO: List<CategoryVO> = listOf()

    class SubCategoryListViewHolder(
        private var binding: ViewHolderChildCategoryBinding,
        private var listener: ChildCategoryListener
    ) : RecyclerView.ViewHolder(binding.root){

        private val mFirebaseRealtimeApi : FirebaseRealtimeApi = FirebaseRealtimeApi()

        fun bind(categoryVO: CategoryVO, position: Int, type: String) {
            binding.tvCatTitle.text = categoryVO.title

            if (categoryVO.colorCode != "" && categoryVO.colorCode.count() >6){
                binding.viewColor.visibility = View.VISIBLE
                binding.imageView.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.viewColor.foreground = ColorDrawable(Color.parseColor(processColorCode(categoryVO.colorCode)))
            }
            else{
                binding.viewColor.visibility = View.GONE
                binding.imageView.visibility = View.VISIBLE

                binding.progressBar.visibility = View.VISIBLE
                Glide.with(itemView.context).load(categoryVO.imageURL).apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder))
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


            mFirebaseRealtimeApi.getNewProducts { success, data ->
                if (success) {
                    if (data != null) {
                        for (it in data) {
                            if (categoryVO.id == it.id) {
                                binding.ivNew.visibility = View.VISIBLE
                                break
                            } else {
                                binding.ivNew.visibility = View.GONE
                            }

                        }
                    }
                }
            }

            binding.ivMore.setOnClickListener {
                listener.onTapMore(categoryVO, type)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryListViewHolder {
        val itemView = ViewHolderChildCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubCategoryListViewHolder(itemView, listener)
    }

    override fun getItemCount(): Int {
        return subCategoryListVO.size
    }

    override fun onBindViewHolder(holder: SubCategoryListViewHolder, position: Int) {

        holder.bind(subCategoryListVO[position], position, type)

        holder.itemView.setOnClickListener {
            val context = it.context

            if (subCategoryListVO[position].subCategories.isNotEmpty()){
                val intent = Intent(context, ChildCategoriesActivity::class.java)
                intent.putExtra("childCategoriesList", subCategoryListVO[position])
                context.startActivity(intent)
            } else {
                val intent = Intent(context, ProductDetailActivity::class.java)
                intent.putExtra("product", subCategoryListVO[position])
                context.startActivity(intent)
            }
        }
    }

    fun setNewData(categories: List<CategoryVO>) {
        subCategoryListVO = categories
        notifyDataSetChanged()
    }

    interface ChildCategoryListener {
        fun onTapMore(categoryVO: CategoryVO, type: String)
    }

}