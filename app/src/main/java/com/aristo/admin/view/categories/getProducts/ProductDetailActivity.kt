package com.aristo.admin.view.categories.getProducts

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.aristo.admin.databinding.ActivityProductDetailBinding
import com.aristo.admin.data.vos.CategoryVO
import com.aristo.admin.utils.processColorCode
import com.aristo.admin.view.AbstractBaseActivity
import com.bumptech.glide.Glide

class ProductDetailActivity : AbstractBaseActivity() {

    private val binding by lazy { ActivityProductDetailBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpListeners()

        val productDetail: CategoryVO? = intent.getSerializableExtra("product") as? CategoryVO

        binding.tvTitle.text = productDetail?.title
        if (productDetail?.price != 0) {
            binding.tvPrice.visibility = View.VISIBLE
            binding.tvPrice.text = "စျေးနှုန်း - ${productDetail?.price.toString()} "
        }

        if (productDetail != null) {
            if (productDetail.colorCode != ""){
                binding.viewColor.visibility = View.VISIBLE
                binding.ivProduct.visibility = View.GONE

                binding.viewColor.foreground = ColorDrawable(Color.parseColor(processColorCode(productDetail.colorCode)))
            }
            else{
                binding.viewColor.visibility = View.GONE
                binding.ivProduct.visibility = View.VISIBLE
                Glide.with(this).load(productDetail.imageURL).into(binding.ivProduct)
            }
        }

    }

    private fun setUpListeners() {
        binding.ibBack.setOnClickListener {
            super.onBackPressed()
        }
    }

}