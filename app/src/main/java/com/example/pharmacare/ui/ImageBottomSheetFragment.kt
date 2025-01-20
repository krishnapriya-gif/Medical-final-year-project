package com.example.pharmacare.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.pharmacare.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ImageBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var imageView: ImageView
    private lateinit var cancelButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.bottom_sheet_layout, container, false)
        imageView = view.findViewById(R.id.imageView)
        cancelButton = view.findViewById(R.id.btnCancel)


        val imageUrl = arguments?.getString("image_url") ?: ""

        Glide.with(this)
            .load(imageUrl)
            .into(imageView)
        cancelButton.setOnClickListener {
            dismiss()
        }
        return view
    }

    companion object {

        fun newInstance(imageUrl: String): ImageBottomSheetFragment {
            val fragment = ImageBottomSheetFragment()
            val bundle = Bundle()
            bundle.putString("image_url", imageUrl)
            fragment.arguments = bundle
            return fragment
        }
    }
}
