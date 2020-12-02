package com.mudryakov.collectivenote.screens.emailRegistration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.databinding.FragmentEmailRegistrationBinding
import com.mudryakov.collectivenote.screens.BaseFragmentBack
import com.mudryakov.collectivenote.utilits.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil.hideKeyboard

class EmailRegistrationFragment : BaseFragmentBack() {
    var _binding: FragmentEmailRegistrationBinding? = null
    val mBinding get() = _binding!!
    private lateinit var mViewModel: EmailRegistrationViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailRegistrationBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initialization()
        registerClick()
    }

    private fun registerClick() {
        if (checkConnectity()) {
            mBinding.registrationEmailBtn.setOnClickListener {
                val email = mBinding.registrationInputEmail.text.toString()
                val pass = mBinding.registrationInputPassword.text.toString()
                if (email.isNotEmpty() && pass.isNotEmpty()) {
                    showProgressbar()
                    mViewModel.createEmailAccount(email, pass, { onFail() }) {
                        fastNavigate(R.id.action_emailRegistrationFragment_to_loginFragment)
                    }
                } else showToast(getString(R.string.add_info))
            }
        } else {
            buildNoInternetDialog { registerClick() }
        }
    }

    private fun onFail() {
        mBinding.registrationEmailBtn.makeVisible()
        mBinding.emailRegistrationProgressBar.makeGone()
    }

    private fun showProgressbar() {
        hideKeyboard(APP_ACTIVITY)
        mBinding.registrationEmailBtn.makeGone()
        mBinding.emailRegistrationProgressBar.makeVisible()
    }

    private fun initialization() {
        APP_ACTIVITY.actionBar?.setDisplayHomeAsUpEnabled(true)
        APP_ACTIVITY.actionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        APP_ACTIVITY.title = getString(R.string.new_account_register)
        mViewModel = ViewModelProvider(this).get(EmailRegistrationViewModel::class.java)
    }
}