package com.rsschool.myapplication.loyaltycards.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.AddCardFragmentBinding
import com.rsschool.myapplication.loyaltycards.model.Barcode
import java.lang.Exception

class AddCardFragment : Fragment() {

    private var _binding: AddCardFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddCardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button2.setOnClickListener {
            findNavController().navigate(R.id.cameraPreviewFragment)
        }
        arguments?.let {
            val barcode = arguments?.getParcelable<Barcode>("BARCODE")
            binding.cardNumber.setText(barcode?.code ?: "")
            binding.barcodeType.setText(Barcode.barcodeFormats.get(barcode?.type)?.second)
            binding.barcode.setImageBitmap(generateBarcode(barcode!!))

        }
    }

    fun generateBarcode(barcode: Barcode): Bitmap? {
        var writer = MultiFormatWriter()
        var bitMatrix: BitMatrix
        try {
            bitMatrix = try {
                writer.encode(barcode.code, Barcode.barcodeFormats.get(barcode.type)?.first, 1500, 512, null)
            } catch (e: Exception) {
                // Cast a wider net here and catch any exception, as there are some
                // cases where an encoder may fail if the data is invalid for the
                // barcode type. If this happens, we want to fail gracefully.
                throw WriterException(e)
            }
            val WHITE = -0x1
            val BLACK = -0x1000000
            val bitMatrixWidth = bitMatrix.width
            val bitMatrixHeight = bitMatrix.height
            val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
            for (y in 0 until bitMatrixHeight) {
                val offset = y * bitMatrixWidth
                for (x in 0 until bitMatrixWidth) {
                    val color = if (bitMatrix[x, y]) BLACK else WHITE
                    pixels[offset + x] = color
                }
            }
            var bitmap = Bitmap.createBitmap(
                bitMatrixWidth, bitMatrixHeight,
                Bitmap.Config.ARGB_8888
            )
            bitmap.setPixels(pixels, 0, bitMatrixWidth, 0, 0, bitMatrixWidth, bitMatrixHeight)

            // Determine if the image needs to be scaled.
            // This is necessary because the datamatrix barcode generator
            // ignores the requested size and returns the smallest image necessary
            // to represent the barcode. If we let the ImageView scale the image
            // it will use bi-linear filtering, which results in a blurry barcode.
            // To avoid this, if scaling is needed do so without filtering.
            val heightScale: Int = 512 / bitMatrixHeight
            val widthScale: Int = 1500 / bitMatrixHeight
            val scalingFactor = Math.min(heightScale, widthScale)
            if (scalingFactor > 1) {
                bitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    bitMatrixWidth * scalingFactor,
                    bitMatrixHeight * scalingFactor,
                    false
                )
            }
            return bitmap
        } catch (e: WriterException) {
            //
        } catch (e: java.lang.OutOfMemoryError) {
           //

        }

    return null
}

}