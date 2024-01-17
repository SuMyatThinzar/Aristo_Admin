package com.aristo.admin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.aristo.admin.databinding.UserIdListItemBinding
import com.aristo.admin.data.vos.UserVO
import com.aristo.admin.view.AddPointActivity
import java.util.Locale

class UserIdListRecyclerViewAdapter(
    private var originalList: ArrayList<UserVO>
) : RecyclerView.Adapter<UserIdListRecyclerViewAdapter.UserIdListRecyclerViewHolder>(),
    Filterable {

    private var filteredList: ArrayList<UserVO> = originalList

    class UserIdListRecyclerViewHolder(private val binding: UserIdListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(userVO: UserVO) {
            binding.tvId.text = "Id : ${userVO.userId}"

            itemView.setOnClickListener {
                val intent = Intent(it.context, AddPointActivity::class.java)
                intent.putExtra("user", userVO)
                it.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserIdListRecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UserIdListItemBinding.inflate(inflater, parent, false)
        return UserIdListRecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: UserIdListRecyclerViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val queryString = constraint?.toString()?.toLowerCase(Locale.getDefault())

                filteredList = if (queryString.isNullOrBlank()) {
                    originalList
                } else {
                    val tempList = ArrayList<UserVO>()
                    for (item in originalList) {
                        if (item.userId.toString().toLowerCase(Locale.getDefault()).contains(queryString)) {
                            tempList.add(item)
                        }
                    }
                    tempList
                }

                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as ArrayList<UserVO>
                notifyDataSetChanged()
            }
        }
    }


    fun updateData(newData: ArrayList<UserVO>) {
        originalList.clear()
        originalList.addAll(newData)
        notifyDataSetChanged()
    }
}