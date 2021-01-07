package com.mudryakov.collectivenote.screens.emailLogin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.mudryakov.collectivenote.MyApplication
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.databinding.FragmentEmailLoginBinding
import com.mudryakov.collectivenote.screens.BaseFragmentBack
import com.mudryakov.collectivenote.utility.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil.hideKeyboard
import javax.inject.Inject

class EmailLoginFragment : BaseFragmentBack() {
    var _binding: FragmentEmailLoginBinding? = null
    val mBinding get() = _binding!!
    @Inject
    lateinit var mViewModel: EmailLoginViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailLoginBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initialization()
        loginClick()
    }

    private fun loginClick() {
        mBinding.loginEmailBtn.setOnClickListener {
            val email = mBinding.loginInputEmail.text.toString()
            val pass = mBinding.loginInputPassword.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                showProgressbar()
                checkInternetAtAuth({ onFail() }) {
                    mViewModel.loginEmail(email, pass, { onFail() }) {
                        hideKeyboard(APP_ACTIVITY)
                        fastNavigate(R.id.action_emaiLoginFragment_to_RegistrationFragment)
                    }
                }
            } else showToast(R.string.add_info)
        }

    }


    private fun onFail() {
        mBinding.loginEmailBtn.makeVisible()
        mBinding.emailLoginProgressBar.makeGone()
    }

    private fun showProgressbar() {
        mBinding.loginEmailBtn.makeGone()
        mBinding.emailLoginProgressBar.makeVisible()
    }

    private fun initialization() {
        APP_ACTIVITY.actionBar?.setDisplayHomeAsUpEnabled(true)
        APP_ACTIVITY.actionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        setTitle(R.string.new_account_login)

    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MyApplication.appComponent.inject(this)
    }
}