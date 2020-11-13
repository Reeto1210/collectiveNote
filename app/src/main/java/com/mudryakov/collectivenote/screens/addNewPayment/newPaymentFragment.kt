package com.mudryakov.collectivenote.screens.addNewPayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.databinding.FragmentNewPaymentBinding
import com.mudryakov.collectivenote.utilits.APP_ACTIVITY


class newPaymentFragment : Fragment() {
    var _Binding: FragmentNewPaymentBinding? = null
    val mBinding get() = _Binding!!
    lateinit var mViewModel: AddNewPaymentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _Binding = FragmentNewPaymentBinding.inflate(layoutInflater)
       APP_ACTIVITY.title = getString(R.string.add_new_payment_title)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initialization()

    }

    private fun initialization() {
        mViewModel = ViewModelProvider(this).get(AddNewPaymentViewModel::class.java)
    }
}