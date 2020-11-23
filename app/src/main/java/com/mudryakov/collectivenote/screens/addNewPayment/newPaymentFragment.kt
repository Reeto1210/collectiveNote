package com.mudryakov.collectivenote.screens.addNewPayment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.databinding.FragmentNewPaymentBinding
import com.mudryakov.collectivenote.utilits.APP_ACTIVITY
import com.mudryakov.collectivenote.utilits.showToast
import com.theartofdev.edmodo.cropper.CropImage
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil.hideKeyboard


class newPaymentFragment : Fragment() {
    var _Binding: FragmentNewPaymentBinding? = null
    val mBinding get() = _Binding!!
    lateinit var mViewModel: AddNewPaymentViewModel
    var imageUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _Binding = FragmentNewPaymentBinding.inflate(layoutInflater)
        APP_ACTIVITY.title = getString(R.string.add_new_payment_title)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initialization()
    }

    private fun initialization() {
        mViewModel = ViewModelProvider(this).get(AddNewPaymentViewModel::class.java)
        mBinding.addNewPaymentConfirm.setOnClickListener {
            val sum = mBinding.addNewPaymentSumm.text.toString().replace(',','.')
            val description = mBinding.addNewPaymentDescription.text.toString()

            try {
                sum.toLong()
                if (sum.isNotEmpty() && description.isNotEmpty())
                    mViewModel.addNewPayment(sum, description, imageUri) {
                        showToast(getString(R.string.toast_payment_added))

                    }
                else showToast(getString(R.string.add_info))
                if (sum.isNotEmpty() && description.isNotEmpty()) {
                    hideKeyboard(APP_ACTIVITY)
                    APP_ACTIVITY.navConroller.navigate(R.id.action_newPaymentFragment_to_mainFragment)
                }
            } catch (e: Exception) {
                mBinding.addNewPaymentSumm.setText("")
                showToast(getString(R.string.catch_payment_summ))
            }
        }
        mBinding.addPaymentAttachImage.setOnClickListener {
            CropImage.activity()
                .setAspectRatio(1, 1)
                .setRequestedSize(600, 600)
                .start(APP_ACTIVITY, this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null
            && resultCode == RESULT_OK
        ) {
            imageUri = CropImage.getActivityResult(data).uri
            mBinding.addPaymentAttachImage.setImageURI(imageUri)
        }
    }
}