package com.mudryakov.collectivenote.screens.settings

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.databinding.FragmentSettingsBinding
import com.mudryakov.collectivenote.screens.BaseFragmentBack
import com.mudryakov.collectivenote.utilits.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil.hideKeyboard


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
        APP_ACTIVITY.title = getString(R.string.settings)
        mBinding.settingsName.text = getString(R.string.user_name, AppPreference.getUserName())
        setHasOptionsMenu(true)
        mViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        initBehaivor()
    }

    private fun initBehaivor() {
        mBinding.settongsChangeNameBtn.setOnClickListener {
            mBinding.settongsChangeNameBtn.makeGone()
            mBinding.settingsEditNameEditText.makeVisible()
            mBinding.settingsBtnContinue.makeVisible()
        }
        mBinding.settingsBtnContinue.setOnClickListener {
            val newName = mBinding.settingsEditNameEditText.text.toString()
            if (newName.isNotEmpty()) {
                mViewModel.changeName(newName) {
                    showToast(getString(R.string.name_changed))
                    hideKeyboard(APP_ACTIVITY)
                    fastNavigate(R.id.action_settingsFragment_to_mainFragment)
                }
            } else showToast(getString(R.string.name_cant_be_empty_toast))
        }
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
        AppPreference.clear()
        restartActivity()
    }

    private fun changeRoom() {
        AppPreference.setCurrency("fail")
        AppPreference.setRoomId("fail")
        AppPreference.setSignInRoom(false)
        AppPreference.setTotalSumm("0")
        restartActivity()
    }
}