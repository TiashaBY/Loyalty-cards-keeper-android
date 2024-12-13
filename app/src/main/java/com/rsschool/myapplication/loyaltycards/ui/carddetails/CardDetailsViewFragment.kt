package com.rsschool.myapplication.loyaltycards.ui.carddetails

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
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.CardDetailsFragmentBinding
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode
import com.rsschool.myapplication.loyaltycards.ui.util.BarcodeGenerator
import com.rsschool.myapplication.loyaltycards.ui.util.CardSide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private const val cornerRadius = 0.06F

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
        binding.number.text = viewModel.card?.cardNumber
        drawBarcode(number)
    }

    private fun drawBarcode(number: String) {
        val type = viewModel.card?.barcodeType
        type?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = BarcodeGenerator()
                    .generateBarcode(Barcode(number, type))
                val frontBitmap = getRoundedBitmap(Uri.parse(viewModel.card?.frontImage))
                val backBitmap = getRoundedBitmap(Uri.parse(viewModel.card?.backImage))
                withContext(Dispatchers.Main) {
                    drawBitmap(bitmap, binding.barcode)
                    val imageView = binding.cardImage
                    imageView.setImageDrawable(if (viewModel.cardSide == CardSide.FRONT) frontBitmap else backBitmap)

                    imageView.setOnClickListener {
                        runAnimation(imageView, backBitmap, frontBitmap)
                    }
                }
            }
        }
    }

    private fun runAnimation(
        imageView: ImageView,
        backBitmap: RoundedBitmapDrawable?,
        frontBitmap: RoundedBitmapDrawable?
    ) {
        val animatorFirst = ObjectAnimator.ofFloat(imageView, "scaleX", 1F, 0F)
        val animatorSecond = ObjectAnimator.ofFloat(imageView, "scaleX", 0F, 1F)
        animatorFirst.interpolator = DecelerateInterpolator()
        animatorSecond.interpolator = AccelerateDecelerateInterpolator()
        animatorFirst.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                val cardSide = viewModel.cardSide
                if (cardSide == CardSide.FRONT) {
                    drawBitmap(backBitmap, imageView)
                    animatorSecond.start()
                    viewModel.onCardSideChange()
                } else {
                    drawBitmap(frontBitmap, imageView)
                    animatorSecond.start()
                    viewModel.onCardSideChange()
                }
            }
        })
        animatorFirst.start()
    }

    private fun <T> drawBitmap(drawable: T, imageView: ImageView) {
        Glide.with(this)
            .load(drawable)
            .error(R.drawable.ic_baseline_image_not_supported_24)
            .fitCenter()
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
            .into(imageView)
    }

    private fun getRoundedBitmap(uri: Uri): RoundedBitmapDrawable? {
        val imgFile = File(uri.path ?: "")
        if (imgFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            val roundPx = bitmap.width * cornerRadius
            val roundedBitmap = RoundedBitmapDrawableFactory.create(resources, bitmap)
            roundedBitmap.cornerRadius = roundPx
            return roundedBitmap
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
