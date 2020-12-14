package com.mudryakov.collectivenote.screens.registration


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
import com.mudryakov.collectivenote.databinding.FragmentRegistrationBinding

import com.mudryakov.collectivenote.utility.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil.hideKeyboard


class RegistrationFragment : Fragment() {
    var _Binding: FragmentRegistrationBinding? = null
    val mBinding get() = _Binding!!
    private lateinit var mViewModel: RegistrationViewModel
    var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _Binding = FragmentRegistrationBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _Binding = null
    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.title = getString(R.string.registration_toolbar_title)
        initialization()
        initHomeUpFalse()
        animateButtons()
        checkSignIn()
        hideKeyboard(APP_ACTIVITY)
    }

    private fun checkSignIn() {
        if (AppPreference.getSignIn()) {
            CURRENT_UID = AppPreference.getUserId()
            fastNavigate(R.id.action_RegistrationFragment_to_groupChooseFragment)
        }
        if (AUTH.currentUser != null) {
            showChangeNameLayout()
        }
    }

    private fun showChangeNameLayout() {
        mBinding.firstRegistration.makeGone()
        changeName { fastNavigate(R.id.action_RegistrationFragment_to_groupChooseFragment) }
    }

    private fun animateButtons() {
        if (AUTH.currentUser == null) {
            mBinding.registrationEmailBtn.startRegisterAnimation(false)
            mBinding.registrationChangeName.startRegisterAnimation(false)
            val keyBoardListener =
                object : KeyboardVisibilityEventListener {
                    override fun onVisibilityChanged(isOpen: Boolean) {
                        if (isOpen) {
                            mBinding.registrationBtnSignInGoogle.startRegisterAnimation(false)
                            mBinding.registrationTip.startRegisterAnimation(false)
                            mBinding.registrationEmailBtn.startRegisterAnimation(true)
                            mBinding.registrationNoAccount.startRegisterAnimation(false)
                        } else {
                            mBinding.registrationBtnSignInGoogle.startRegisterAnimation(true)
                            mBinding.registrationTip.startRegisterAnimation(true)
                            mBinding.registrationEmailBtn.startRegisterAnimation(false)
                            mBinding.registrationNoAccount.startRegisterAnimation(true)
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
        mViewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
        mViewModel.initCommons()
        mBinding.registrationNoAccount.setOnClickListener { fastNavigate(R.id.action_RegistrationFragment_to_emailLoginFragment) }
        emailBtnClick()
        googleSignInBtnClick()
    }

    private fun emailBtnClick() {
        mBinding.registrationEmailBtn.setOnClickListener {
            if (!isLoading) {
                val email = mBinding.registrationInputEmail.text.toString()
                val pass = mBinding.registrationInputPassword.text.toString()
                if (email.isNotEmpty() && pass.isNotEmpty()) {
                    showProgressBar()
                    checkInternetAtAuth({ onLoginFail() }) {
                        mViewModel.registration(
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
        mBinding.registrationBtnSignInGoogle.setOnClickListener {
            if (!isLoading) {
                showProgressBar()
                checkInternetAtAuth({ onLoginFail() })
                {
                    mViewModel.registration(
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
            fastNavigate(R.id.action_RegistrationFragment_to_groupChooseFragment)
        }
    }

    private fun changeName(onConfirm: () -> Unit) {
        mBinding.registrationChangeNameInputText.setText(USERNAME)
        mBinding.firstRegistration.startRegisterAnimation(false)
        mBinding.registrationChangeName.startRegisterAnimation(true)
        mBinding.registrationChangeNameBtn.setOnClickListener {
            val newName = mBinding.registrationChangeNameInputText.text.toString()
            if (newName.isNotEmpty()) {
                if (!isLoading) {
                    showProgressBar()
                    checkInternetAtAuth({ onLoginFail() })
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
        mBinding.fragmentRegistrationProgressBar.makeGone()
    }

    private fun showProgressBar() {
        isLoading = true
        mBinding.fragmentRegistrationProgressBar.makeVisible()
    }
}