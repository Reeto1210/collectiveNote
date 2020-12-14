package com.mudryakov.collectivenote.screens.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.databinding.FragmentHelpBinding
import com.mudryakov.collectivenote.screens.BaseFragmentBack
import com.mudryakov.collectivenote.utility.APP_ACTIVITY
import java.math.BigDecimal


class HelpFragment : BaseFragmentBack() {
    private var _Binding: FragmentHelpBinding? = null
    private val mBinding get() = _Binding!!
    lateinit var mViewModel: HelpFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _Binding = FragmentHelpBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initialization()
    }

    private fun initialization() {
        APP_ACTIVITY.title = getString(R.string.help_main_text)
        mViewModel = ViewModelProvider(this).get(HelpFragmentViewModel::class.java)

    mBinding.donateBtn.setOnClickListener {
        //дать денег мне любимому
    }

    }

}