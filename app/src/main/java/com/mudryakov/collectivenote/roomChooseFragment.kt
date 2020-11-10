package com.mudryakov.collectivenote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mudryakov.collectivenote.databinding.FragmentRoomChooseBinding
import com.mudryakov.collectivenote.utilits.APP_ACTIVITY


class roomChooseFragment : Fragment() {
    var _Binding: FragmentRoomChooseBinding? = null
    val mBinding get() = _Binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        APP_ACTIVITY.title = "Выбор комнаты"
        _Binding = FragmentRoomChooseBinding.inflate(layoutInflater)

        return mBinding.root
    }

    override fun onStart() {
        super.onStart()

    }
}