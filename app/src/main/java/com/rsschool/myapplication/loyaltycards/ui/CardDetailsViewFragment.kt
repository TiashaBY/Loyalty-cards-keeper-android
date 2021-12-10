package com.rsschool.myapplication.loyaltycards.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.CardDetailsFragmentBinding
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode
import com.rsschool.myapplication.loyaltycards.ui.util.BarcodeGenerator
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.CardDetailsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class CardDetailsViewFragment : Fragment() {

    private var _binding: CardDetailsFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val viewModel: CardDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CardDetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val number = viewModel.card?.cardNumber ?: ""
        val type = viewModel.card?.barcodeType
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = BarcodeGenerator()
                .generateBarcode(Barcode(number, type))
            withContext(Dispatchers.Main) {
                Glide.with(this@CardDetailsViewFragment)
                    .load(bitmap)
                    .error(R.drawable.ic_baseline_image_not_supported_24)
                    .fitCenter()
                    .into(binding.barcode)
                binding.number.text = viewModel.card?.cardNumber
            }
        }

        // val myImg = BitmapFactory.decodeResource(resources, R.drawable.image)

        val imgFile1 = File(Uri.parse(viewModel.card?.frontImage).path)
        val imgFile2 = File(Uri.parse(viewModel.card?.backImage).path)
        if (imgFile1.exists()) {
            val bitmap1 = BitmapFactory.decodeFile(imgFile1.absolutePath)
            val roundedBitmap = RoundedBitmapDrawableFactory.create(resources, bitmap1)
            val roundPx = bitmap1.width * 0.06f
            roundedBitmap.cornerRadius = roundPx


            val bitmap2 = BitmapFactory.decodeFile(imgFile2.absolutePath)
            val roundedBitmap2 = RoundedBitmapDrawableFactory.create(resources, bitmap1)
            roundedBitmap2.cornerRadius = roundPx

            val imageView = binding.cardImage
            imageView.setImageDrawable(roundedBitmap)
            var side = CardSide.FRONT

            imageView.setOnClickListener {
                val oa1 = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 0f)
                val oa2 = ObjectAnimator.ofFloat(imageView, "scaleX", 0f, 1f)
                oa1.interpolator = DecelerateInterpolator()
                oa2.interpolator = AccelerateDecelerateInterpolator()
                oa1.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        if (side == CardSide.FRONT) {
                            imageView.setImageBitmap(bitmap2)
                            oa2.start()
                            side = CardSide.BACK
                        } else {
                            imageView.setImageDrawable(roundedBitmap2)
                            oa2.start()
                            side = CardSide.FRONT
                        }
                    }
                })
                oa1.start()
            }
        }

    }

}