package com.mudryakov.collectivenote.screens.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.firebase.CURRENT_UID
import com.mudryakov.collectivenote.database.firebase.USERNAME
import com.mudryakov.collectivenote.databinding.FragmentRegistrationBinding


import com.mudryakov.collectivenote.utilits.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil.hideKeyboard


class RegistrationFragment : Fragment() {
    var _Binding: FragmentRegistrationBinding? = null
    val mBinding get() = _Binding!!
    private lateinit var mViewModel: RegistrationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        animateButtons()
        initialization()
    }

    private fun animateButtons() {
        mBinding.registrationEmailBtn.startRegisterAnimation(false)
        mBinding.registrationDialog.startRegisterAnimation(false)
        val keyBoardListener =
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    if (isOpen) {
                        mBinding.registrationBtnSignInGoogle.startRegisterAnimation(false)
                        mBinding.registrationTip.startRegisterAnimation(false)
                        mBinding.registrationEmailBtn.startRegisterAnimation(true)
                    } else {
                        mBinding.registrationBtnSignInGoogle.startRegisterAnimation(true)
                        mBinding.registrationTip.startRegisterAnimation(true)
                        mBinding.registrationEmailBtn.startRegisterAnimation(false)
                    }
                }
            }
        KeyboardVisibilityEvent.setEventListener(APP_ACTIVITY, viewLifecycleOwner, keyBoardListener)

    }

    private fun initialization() {
        mViewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
         mViewModel.initCommons()
        if (appPreference.getSignIn()) {
            CURRENT_UID = appPreference.getUserId()
                    APP_ACTIVITY.navConroller.navigate(R.id.action_registrationFragment_to_roomChooseFragment)
        } else {

            emailBtnClick()
            gooleSighbtnClick()

        }
    }

    private fun emailBtnClick() {
        mBinding.registrationEmailBtn.setOnClickListener {
            val email = mBinding.registrationInputEmail.text.toString()
            val pass = mBinding.registrationInputPassword.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                mViewModel.connectToFirebase(TYPE_EMAIL, email, pass) {
                    onRegisterSuccess()
                }
            } else showToast(getString(R.string.add_info))
        }

    }

    private fun gooleSighbtnClick() {
        mBinding.registrationBtnSignInGoogle.setOnClickListener {
            mViewModel.connectToFirebase(TYPE_GOOGLE_ACCOUNT) {
                onRegisterSuccess()
            }
        }
    }

    private fun onRegisterSuccess() {
        hideKeyboard(APP_ACTIVITY)
        makeDialog {
            APP_ACTIVITY.navConroller.navigate((R.id.action_registrationFragment_to_roomChooseFragment))
        }
    }

    private fun makeDialog(onConfirm: () -> Unit) {
        mBinding.registrationDialogName.setText(USERNAME)
        mBinding.firstRegistration.startRegisterAnimation(false)
        mBinding.registrationDialog.startRegisterAnimation(true)
        mBinding.alertChangeNameBtn.setOnClickListener {
            val newName = mBinding.registrationDialogName.text.toString()
            mViewModel.changeName(newName) { onConfirm() }
        }
    }

}