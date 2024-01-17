package com.aristo.admin.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aristo.admin.R
import com.aristo.admin.databinding.ActivityHelpCenterBinding

class HelpCenterActivity : AbstractBaseActivity() {

    private val binding by lazy { ActivityHelpCenterBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpListeners()
    }

    private fun setUpListeners() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}