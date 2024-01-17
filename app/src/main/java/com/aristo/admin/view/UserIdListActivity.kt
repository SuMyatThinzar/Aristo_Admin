package com.aristo.admin.view

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.aristo.admin.R
import com.aristo.admin.databinding.ActivityUserIdListBinding
import com.aristo.admin.adapters.UserIdListRecyclerViewAdapter

class UserIdListActivity : AbstractBaseActivity() {

    private lateinit var binding : ActivityUserIdListBinding
    private val userIdListAdapter by lazy { UserIdListRecyclerViewAdapter( ArrayList()) }
    private val userIdListLayoutManager by lazy { LinearLayoutManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserIdListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setUpModel()
        setupRecyclerView()
        setUpListeners()
        fetchNetworkData()
    }

    private fun fetchNetworkData() {
        binding.progressLoading.visibility = View.VISIBLE

        mFirebaseRealtimeModel.getCustomerIdList { isSuccess, data ->
            binding.progressLoading.visibility = View.GONE

            if (isSuccess) {
                if (data != null) {
                    userIdListAdapter.updateData(data)
                }
            } else {
                Toast.makeText(this, "Can't retrieve datas.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setUpListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.rvUserIdList.layoutManager = userIdListLayoutManager
        binding.rvUserIdList.adapter = userIdListAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView

        // Customize the searchView as needed (optional)
        searchView.queryHint = "Search..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                userIdListAdapter.filter.filter(newText)
                return true
            }
        })

        return true
    }



}