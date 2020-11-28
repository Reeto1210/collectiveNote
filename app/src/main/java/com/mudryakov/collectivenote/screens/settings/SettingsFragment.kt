package com.mudryakov.collectivenote.screens.settings

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.databinding.FragmentSettingsBinding
import com.mudryakov.collectivenote.screens.BaseFragmentBack
import com.mudryakov.collectivenote.utilits.appPreference
import com.mudryakov.collectivenote.utilits.restartActivity


class SettingsFragment : BaseFragmentBack() {
    private var _Binding: FragmentSettingsBinding? = null
    private val mBinding get() = _Binding!!
    lateinit var mViewModel: SettingsViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _Binding = FragmentSettingsBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initialization()
    }

    private fun initialization() {
        setHasOptionsMenu(true)
        mViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.change_room -> changeRoom()
            R.id.exit_acc -> changeUser()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeUser() {
        mViewModel.signOut()
        appPreference.clear()
        restartActivity()
    }

    fun changeRoom() {
        appPreference.setRoomId("fail")
        appPreference.setSignInRoom(false)
        appPreference.setTotalSumm("0")
        restartActivity()
    }
}