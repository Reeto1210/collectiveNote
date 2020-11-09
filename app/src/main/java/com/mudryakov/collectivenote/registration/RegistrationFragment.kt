package com.mudryakov.collectivenote.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.firebase.FireBaseRepository
import com.mudryakov.collectivenote.databinding.FragmentRegistrationBinding
import com.mudryakov.collectivenote.models.User
import com.mudryakov.collectivenote.utilits.*


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
        initialization()
    }

    private fun initialization() {
        mViewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
        mViewModel.initCommons()
        if (appPreference.getSignIn()) {
            APP_ACTIVITY.navConroller.navigate(R.id.action_registrationFragment_to_roomChooseFragment)
        } else {
            mBinding.registrationBtnSignInGoogle.setOnClickListener {
                mViewModel.connectToFirebase(TYPE_GOOGLE_ACCOUNT) {
                    APP_ACTIVITY.navConroller.navigate(R.id.action_registrationFragment_to_roomChooseFragment)
                }
            }
            mBinding.registrationFab.setOnClickListener {
                val email = mBinding.registrationInputEmail.text.toString()
                val pass = mBinding.registrationInputPassword.toString()
                mViewModel.connectToFirebase(TYPE_EMAIL, email, pass) {
                    APP_ACTIVITY.navConroller.navigate((R.id.action_registrationFragment_to_roomChooseFragment))
                }
            }

        }


    }

}