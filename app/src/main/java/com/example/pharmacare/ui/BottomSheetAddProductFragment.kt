package com.example.pharmacare.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pharmacare.databinding.DialogAddProductsBinding
import com.example.pharmacare.response.CommonResponse
import com.example.pharmacare.response.RetrofitInstance
import com.example.pharmacare.response.RetrofitInstance.TYPE
import com.example.pharmacare.utils.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BottomSheetAddProductFragment : BottomSheetDialogFragment() {

    private val bind by lazy { DialogAddProductsBinding.inflate(layoutInflater) }
    private val shared by lazy { SessionManager(requireContext()) }

    var selectedImagePath: Uri? = null
    var id: String = ""
    var name: String = "Admin"
    var rating: String = "5"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        id = "${shared.getUserId()}"
        name = "${shared.getUserName()}"
        rating = "${shared.getSellerRating()}"
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pickImageResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                result.data?.data?.let { uri ->
                    selectedImagePath = uri
                    bind.ivProduct.setImageURI(uri)


                }
            }


        bind.ivProduct.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            pickImageResult.launch(intent)
        }


        bind.etexpiryDate.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                showExpiryDatePicker { selectedDateText ->
                    this.setText(selectedDateText)
                }
            }
        }


        bind.add.setOnClickListener {
            val productName = bind.etName.text.toString().trim()
            val priceStr = bind.etPrice.text.toString().trim()
            val expiryDate = bind.etexpiryDate.text.toString().trim()

            when {
                productName.isEmpty() -> showToast("Please enter item details")
                priceStr.isEmpty() -> showToast("Please enter item price details")
                expiryDate.isEmpty() -> showToast("Please select expiry date")
                selectedImagePath == null -> showToast("Please select the image from the gallery")
                else -> {

                    CoroutineScope(IO).async {
                        context?.contentResolver?.openInputStream(selectedImagePath!!)?.readBytes()
                            ?.let { it: ByteArray ->
                                RetrofitInstance.instance.AddItem(
                                    itemName = productName,
                                    itemPhoto = Base64.encodeToString(
                                        it, Base64.NO_WRAP
                                    ),
                                    price = priceStr,
                                    sellerId = id,
                                    shopName = name,
                                    rating = rating,
                                    isAvailable = "false",
                                    type = TYPE,
                                    expiryDate = expiryDate
                                ).enqueue(object :
                                    Callback<CommonResponse?> {
                                    override fun onResponse(
                                        p0: Call<CommonResponse?>,
                                        p1: Response<CommonResponse?>,
                                    ) {
                                        val response = p1.body()!!
                                        if (response.error) {
                                            showToast("Error occurred")
                                        } else {
                                            showToast("Successfully added")
                                            dismiss()
                                        }
                                    }

                                    override fun onFailure(
                                        p0: Call<CommonResponse?>,
                                        p1: Throwable,
                                    ) {
                                        showToast(p1.message!!)
                                    }
                                })
                            }
                    }.start()
                }
            }
        }
    }

    private fun showExpiryDatePicker(onDateSelected: (String) -> Unit) {
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Expiry Date")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->

            val dateText = datePicker.headerText
            onDateSelected(dateText)
        }

        datePicker.show(childFragmentManager, "EXPIRY_DATE_PICKER")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}

