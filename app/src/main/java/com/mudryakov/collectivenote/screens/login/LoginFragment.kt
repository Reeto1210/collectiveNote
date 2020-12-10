package com.mudryakov.collectivenote.screens.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.firebase.AUTH
import com.mudryakov.collectivenote.database.firebase.CURRENT_UID
import com.mudryakov.collectivenote.database.firebase.USERNAME
import com.mudryakov.collectivenote.databinding.FragmentLoginBinding

import com.mudryakov.collectivenote.utility.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil.hideKeyboard


class LoginFragment : Fragment() {
    var _Binding: FragmentLoginBinding? = null
    val mBinding get() = _Binding!!
    private lateinit var mViewModel: LoginViewModel
    var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _Binding = FragmentLoginBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _Binding = null
    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.title = getString(R.string.login_toolbar_title)

        initialization()
        initHomeUpFalse()
        animateButtons()
        checkSignIn()
    hideKeyboard(APP_ACTIVITY)
    }

    private fun checkSignIn() {
        if (AppPreference.getSignIn()) {
            CURRENT_UID = AppPreference.getUserId()
            fastNavigate(R.id.action_loginFragment_to_roomChooseFragment)
        }
        if (AUTH.currentUser != null) {
            showChangeNameLayout()
        }
    }

    private fun showChangeNameLayout() {
        mBinding.firstLogin.makeGone()
        changeName { fastNavigate(R.id.action_loginFragment_to_roomChooseFragment) }
    }

    private fun animateButtons() {
        if (AUTH.currentUser == null) {
            mBinding.loginEmailBtn.startRegisterAnimation(false)
            mBinding.loginChangeName.startRegisterAnimation(false)
            val keyBoardListener =
                object : KeyboardVisibilityEventListener {
                    override fun onVisibilityChanged(isOpen: Boolean) {
                        if (isOpen) {
                            mBinding.loginBtnSignInGoogle.startRegisterAnimation(false)
                            mBinding.loginTip.startRegisterAnimation(false)
                            mBinding.loginEmailBtn.startRegisterAnimation(true)
                            mBinding.loginNoAccount.startRegisterAnimation(false)
                        } else {
                            mBinding.loginBtnSignInGoogle.startRegisterAnimation(true)
                            mBinding.loginTip.startRegisterAnimation(true)
                            mBinding.loginEmailBtn.startRegisterAnimation(false)
                            mBinding.loginNoAccount.startRegisterAnimation(true)
                        }
                    }
                }
            KeyboardVisibilityEvent.setEventListener(
                APP_ACTIVITY,
                viewLifecycleOwner,
                keyBoardListener
            )
        }
    }

    private fun initialization() {
        mViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        mViewModel.initCommons()
        mBinding.loginNoAccount.setOnClickListener { fastNavigate(R.id.action_loginFragment_to_emailRegistrationFragment) }
        emailBtnClick()
        googleSignInBtnClick()
    }

    private fun emailBtnClick() {
        mBinding.loginEmailBtn.setOnClickListener {
            if (!isLoading) {
                val email = mBinding.loginInputEmail.text.toString()
                val pass = mBinding.loginInputPassword.text.toString()
                if (email.isNotEmpty() && pass.isNotEmpty()) {
                    showProgressBar()
                    checkInternetAtAuth({onLoginFail()}) {
                        mViewModel.login(
                            TYPE_EMAIL,
                            email,
                            pass,
                            { onLoginFail() }) {
                            onRegisterSuccess()
                        }
                    }
                } else showToast(R.string.add_info)
            }
        }
    }

    private fun googleSignInBtnClick() {
        mBinding.loginBtnSignInGoogle.setOnClickListener {
            if (!isLoading) {
                showProgressBar()
                checkInternetAtAuth({onLoginFail()})
                {
                    mViewModel.login(
                        TYPE_GOOGLE_ACCOUNT,
                        onFail = { onLoginFail() }) {
                        onRegisterSuccess()
                    }
                }

            }
        }
    }

    private fun onRegisterSuccess() {
        onLoginFail()
        changeName {
           hideKeyboard(APP_ACTIVITY)
            fastNavigate(R.id.action_loginFragment_to_roomChooseFragment)
        }
    }

    private fun changeName(onConfirm: () -> Unit) {
        mBinding.loginChangeNameInputText.setText(USERNAME)
        mBinding.firstLogin.startRegisterAnimation(false)
        mBinding.loginChangeName.startRegisterAnimation(true)
        mBinding.loginChangeNameBtn.setOnClickListener {
            val newName = mBinding.loginChangeNameInputText.text.toString()
            if (newName.isNotEmpty()) {
                if (!isLoading) {
                    showProgressBar()
                    checkInternetAtAuth({onLoginFail()})
                    {
                        mViewModel.changeName(newName) { onConfirm() }
                    }
                }
            } else
                showToast(R.string.name_cant_be_empty_toast)
        }
    }

    private fun onLoginFail() {
        isLoading = false
        mBinding.fragmentLoginProgressBar.makeGone()
    }

    private fun showProgressBar() {
        isLoading = true
        mBinding.fragmentLoginProgressBar.makeVisible()
    }
}