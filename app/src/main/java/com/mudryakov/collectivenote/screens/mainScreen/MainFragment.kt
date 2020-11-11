package com.mudryakov.collectivenote.screens.mainScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.databinding.FragmentMainBinding
import com.mudryakov.collectivenote.models.User
import com.mudryakov.collectivenote.utilits.APP_ACTIVITY
import com.mudryakov.collectivenote.utilits.USER
import com.mudryakov.collectivenote.utilits.appPreference


class MainFragment : Fragment() {
    var _Binding: FragmentMainBinding? = null
    val mBinding get() = _Binding!!
private lateinit var mViewModel:MainFragmentViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _Binding = FragmentMainBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initialization()
    }

    private fun initialization() {
       APP_ACTIVITY.title = APP_ACTIVITY.getString(R.string.app_name)
       mViewModel = ViewModelProvider(this).get(MainFragmentViewModel::class.java)
       USER= User(appPreference.getUserId(),appPreference.getUserName(),appPreference.getRoomId())



        mBinding.INFO.text = "${USER.name}, ${USER.firebaseId}, ${USER.roomId}"


    }
}