package com.aristo.admin.view.categories.addProducts

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.aristo.admin.databinding.CategoryDeleteConfirmationDialogBinding
import com.aristo.admin.network.FirebaseRealtimeApi

class DeleteMainCategoryDialogFragment : DialogFragment() {

    private lateinit var binding : CategoryDeleteConfirmationDialogBinding
    private val mFirebaseRealtimeApi by lazy { FirebaseRealtimeApi() }

    var id : String? = null
    private var listener: OnFinishDeleteMainDelegate? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        binding = CategoryDeleteConfirmationDialogBinding.inflate(layoutInflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        id = arguments?.getString("main_cat_id")

        setUpListeners()
    }

    private fun setUpListeners() {

        binding.btnCancel.setOnClickListener {
            dialog!!.cancel()
        }

        binding.btnConfirm.setOnClickListener {

            binding.deleteProgress.visibility = View.VISIBLE

            id?.let { it1 ->
                mFirebaseRealtimeApi.deleteMainCategory(it1) { isSuccess, message ->

                    binding.deleteProgress.visibility = View.GONE

                    if (isSuccess){
                        if (message != null) {
                            listener?.onFinishActivity(message)
                        }
                        dialog!!.cancel()
                    }
                    else{
                        listener?.onFinishActivity("Failed to delete.")
                        dialog!!.cancel()
                    }
                }
            }

        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as OnFinishDeleteMainDelegate
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement MyDialogListener")
        }
    }


}