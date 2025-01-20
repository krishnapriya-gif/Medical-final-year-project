package com.example.pharmacare.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pharmacare.databinding.ActivitySellerDashboardBinding
import com.example.pharmacare.utils.SessionManager

class ChemistDashboard : AppCompatActivity() {
    private val bind by lazy { ActivitySellerDashboardBinding.inflate(layoutInflater) }
    private val shared by lazy { SessionManager(applicationContext) }

   /* private lateinit var pickImageResult: ActivityResultLauncher<Intent>
    private var selectedImagePath: Uri? = null*/
    var id = ""
    var rating = ""
    var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)


//        pickImageResult =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                result.data?.data?.let { uri ->
//                    selectedImagePath = uri
//                    currentDialog?.ivProduct?.setImageURI(uri)
//
//
//                }
//            }

        id = shared.getUserId()!!
        val role = shared.getUserRole()!!
        rating = shared.getSellerRating()!!
        name = shared.getUserName()!!
        bind.usetTxt.text = "Welcome\nto\nthe\n${role} Dashboard"

        bind.profile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        bind.addItems.setOnClickListener {
            val bottomSheet = BottomSheetAddProductFragment()
            bottomSheet.show(supportFragmentManager, "AddProductBottomSheet")
        }
        bind.inventory.setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java).apply {
                putExtra("role", role)
            })
        }
        bind.pending.setOnClickListener {
            startActivity(Intent(this, OrderDetailsActivity::class.java).apply {
                putExtra("order", "Pending")
            })
        }
        bind.accepted.setOnClickListener {
            startActivity(Intent(this, OrderDetailsActivity::class.java).apply {
                putExtra("order", "Accepted")
            })
        }
        bind.completed.setOnClickListener {
            startActivity(Intent(this, OrderDetailsActivity::class.java).apply {
                putExtra("order", "Completed")
            })
        }

    }

//    private var currentDialog: DialogAddProductsBinding? = null

//    private fun addItems() {
//        val view = DialogAddProductsBinding.inflate(layoutInflater)
//        val dialog = AlertDialog.Builder(this).setView(view.root).create()
//
//        currentDialog = view
//
//        view.ivProduct.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//                type = "image/*"
//            }
//            pickImageResult.launch(intent)
//        }
//
//
//        view.add.setOnClickListener {
//            val name1 = view.etName.text.toString()
//            val priceStr = view.etPrice.text.toString().trim()
//
//            when {
//                name1.isEmpty() -> showToast("Please enter item details")
//                priceStr.isEmpty() -> showToast("Please enter item price details")
//                selectedImagePath == null -> showToast("Please select the image from the gallery")
//                else -> {
//
//                    CoroutineScope(IO).async {
//                        contentResolver.openInputStream(selectedImagePath!!)?.readBytes()
//                            ?.let { it: ByteArray ->
//                                RetrofitInstance.instance.AddItem(
//                                    name1, Base64.encodeToString(
//                                        it, Base64.NO_WRAP
//                                    ), priceStr, id, name, rating, "false", TYPE,""
//                                ).enqueue(object :
//                                    Callback<CommonResponse?> {
//                                    override fun onResponse(
//                                        p0: Call<CommonResponse?>,
//                                        p1: Response<CommonResponse?>,
//                                    ) {
//                                        val response = p1.body()!!
//                                        if (response.error) {
//                                            showToast("Error occurred")
//                                        } else {
//                                            showToast("Successfully added")
//                                        }
//                                    }
//
//                                    override fun onFailure(
//                                        p0: Call<CommonResponse?>,
//                                        p1: Throwable,
//                                    ) {
//                                        showToast(p1.message!!)
//                                    }
//                                })
//                            }
//                    }.start()
//                }
//            }
//            dialog.dismiss()
//        }
//        dialog.show()
//
//
//    }



}