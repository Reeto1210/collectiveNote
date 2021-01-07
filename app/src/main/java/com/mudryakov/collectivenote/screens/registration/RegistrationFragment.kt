package com.mudryakov.collectivenote.screens.registration


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mudryakov.collectivenote.MyApplication
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.firebase.USERNAME
import com.mudryakov.collectivenote.databinding.FragmentRegistrationBinding
import com.mudryakov.collectivenote.utility.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil.hideKeyboard
import javax.inject.Inject


class RegistrationFragment : Fragment() {
    var _Binding: FragmentRegistrationBinding? = null
    val mBinding get() = _Binding!!

    @Inject
    lateinit var mViewModel: RegistrationViewModel
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
        hideKeyboard(APP_ACTIVITY)
        checkAuth()
        setTitle(R.string.registration_toolbar_title)
        initHomeUpFalse()
        initClickListeners()
        animateButtons()
    }

    private fun checkAuth() {
       when (mViewModel.checkUserForRegistration()){
           true -> fastNavigate(R.id.action_RegistrationFragment_to_groupChooseFragment)
           false -> showChangeNameLayout()
         }
             }


    private fun initPoliceClick() {
        mBinding.privacyPoliceAddress.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.private_police_url)))
            startActivity(browserIntent)
        }
    }


    private fun showChangeNameLayout() {
        hideKeyboard(APP_ACTIVITY)
        mBinding.firstRegistration.makeGone()
        changeName { fastNavigate(R.id.action_RegistrationFragment_to_groupChooseFragment) }
    }

    private fun animateButtons() {
        if (mViewModel.checkUserForRegistration() == null) {
            mBinding.registrationEmailBtn.startRegisterAnimation(false)
            mBinding.registrationChangeName.startRegisterAnimation(false)
            val keyBoardListener =
                object : KeyboardVisibilityEventListener {
                    override fun onVisibilityChanged(isOpen: Boolean) {
                        if (isOpen) {
                            mBinding.privacyPoliceAddress.startRegisterAnimation(false)
                            mBinding.privacyPoliceText.startRegisterAnimation(false)
                            mBinding.registrationBtnSignInGoogle.startRegisterAnimation(false)
                            mBinding.registrationTip.startRegisterAnimation(false)
                            mBinding.registrationEmailBtn.startRegisterAnimation(true)
                            mBinding.registrationNoAccount.startRegisterAnimation(false)
                        } else {
                            mBinding.registrationBtnSignInGoogle.startRegisterAnimation(true)
                            mBinding.registrationTip.startRegisterAnimation(true)
                            mBinding.registrationEmailBtn.startRegisterAnimation(false)
                            mBinding.registrationNoAccount.startRegisterAnimation(true)
                            mBinding.privacyPoliceAddress.startRegisterAnimation(true)
                            mBinding.privacyPoliceText.startRegisterAnimation(true)
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

    private fun initClickListeners() {
        mBinding.registrationNoAccount.setOnClickListener { fastNavigate(R.id.action_RegistrationFragment_to_emailLoginFragment) }
        emailBtnClick()
        googleSignInBtnClick()
        initPoliceClick()
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MyApplication.appComponent.inject(this)
    }

}