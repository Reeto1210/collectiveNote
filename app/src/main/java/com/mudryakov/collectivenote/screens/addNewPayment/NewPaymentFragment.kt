package com.mudryakov.collectivenote.screens.addNewPayment

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.mudryakov.collectivenote.MyApplication
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.databinding.FragmentNewPaymentBinding
import com.mudryakov.collectivenote.screens.BaseFragmentBack
import com.mudryakov.collectivenote.utility.*
import com.theartofdev.edmodo.cropper.CropImage
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil.hideKeyboard
import javax.inject.Inject


class NewPaymentFragment : BaseFragmentBack() {
    var _Binding: FragmentNewPaymentBinding? = null
    val mBinding get() = _Binding!!
    @Inject
    lateinit var mViewModel: AddNewPaymentViewModel
   @Inject
   lateinit var cropperBuilder:CropImage.ActivityBuilder



    var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _Binding = FragmentNewPaymentBinding.inflate(layoutInflater)
       setTitle(R.string.add_new_payment_title)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initialization()
    }

    private fun initialization() {

        mBinding.addNewPaymentConfirm.setOnClickListener {
            var sum = mBinding.addNewPaymentSumm.text.toString().replace(',','.')
            val description = mBinding.addNewPaymentDescription.text.toString()
            try {
                sum = convertSum(sum)
                if (sum.isNotEmpty() && description.isNotEmpty())
                    checkInternetConnection({restartActivity()}) {
                        hideKeyboard(APP_ACTIVITY)
                        fastNavigate(R.id.action_newPaymentFragment_to_mainFragment)
                        mViewModel.addNewPayment(sum, description, imageUri) {
                            showToast(R.string.toast_payment_added)
                        }
                    } else showToast(R.string.add_info)
            } catch (e: Exception) {
                mBinding.addNewPaymentSumm.setText("")
                showToast(R.string.catch_payment_sum)
            }
        }
        mBinding.addPaymentAttachImage.setOnClickListener {
            cropperBuilder.start(APP_ACTIVITY, this)
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
     MyApplication.appComponent.inject(this)
    }
}