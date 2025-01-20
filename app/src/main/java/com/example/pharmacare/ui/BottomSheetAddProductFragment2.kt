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
import androidx.compose.ui.test.isFocusable
import androidx.fragment.app.Fragment
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.pharmacare.databinding.DialogExpiredProductsBinding
import com.example.pharmacare.response.ProductResponse
import com.example.pharmacare.response.RetrofitInstance
import com.example.pharmacare.utils.SessionManager

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BottomSheetAddProductFragment2 : BottomSheetDialogFragment() {

    private val bind by lazy { DialogExpiredProductsBinding.inflate(layoutInflater) }
    private val shared by lazy { SessionManager(requireContext()) }
    private val simple = SimpleDateFormat("dd/MMMM/yyyy(hh:mm:ss)", Locale.getDefault())

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

        // Pick Image using ActivityResultContracts
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

        // Set Expiry Date Picker
        bind.etexpiryDate.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                showExpiryDatePicker { selectedDateText ->
                    this.setText(selectedDateText)
                }
            }
        }

        // Add product when button clicked
        bind.add.setOnClickListener {
            val productName = bind.etName.text.toString().trim()
            val priceStr = bind.etPrice.text.toString().trim()
            val expiryDate = bind.etexpiryDate.text.toString().trim()

            // Form validation
            when {
                productName.isEmpty() -> showToast("Please enter item details")
                expiryDate.isEmpty() -> showToast("Please select expiry date")
                selectedImagePath == null -> showToast("Please select the image from the gallery")
                else -> {
                    val userId = "${shared.getUserId()}"
                    val di = "Donated${System.currentTimeMillis()}"


                    CoroutineScope(Dispatchers.IO).launch {
                        context?.contentResolver?.openInputStream(selectedImagePath!!)?.readBytes()
                            ?.let { imageBytes ->
                                val encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                                RetrofitInstance.instance.placeTheOrder(
                                    "$di",
                                    "$userId",
                                    sellerid = "0",
                                    status = "Completed",
                                    itemphoto = "encodedImage",
                                    itemname = productName,
                                    qty = "0",
                                    price = priceStr,
                                    date = simple.format(Date()),
                                    type = RetrofitInstance.TYPE,
                                    dateOfExpiry = expiryDate,
                                    prescription = encodedImage,
                                    isAvailable = "true",
                                    isDonated = "true",
                                    donatedStatus = "Pending",
                                    "Donated"
                                ).enqueue(object : Callback<ProductResponse?> {
                                    override fun onResponse(
                                        call: Call<ProductResponse?>,
                                        response: Response<ProductResponse?>,
                                    ) {
                                        if (response.body() != null) {
                                            showToast("Donation Posted Successfully")
                                            dismiss()
                                        } else {
                                            showToast("Failed to post donation.")
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<ProductResponse?>,
                                        t: Throwable,
                                    ) {
                                        showToast(t.message ?: "Unknown error occurred.")
                                    }
                                })
                            }

                    }
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
