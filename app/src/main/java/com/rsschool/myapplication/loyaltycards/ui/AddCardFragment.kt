package com.rsschool.myapplication.loyaltycards.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.AddCardFragmentBinding
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.AddCardEvent
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.AddCardViewModel
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.CameraActionsRequest
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.CardImageType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.File


@AndroidEntryPoint
class AddCardFragment : Fragment() {

    private val viewModel: AddCardViewModel by viewModels()

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

        with(binding.addCardTop) {
            scanBarcode.setOnClickListener {
                viewModel.onScanBarcodeClick()
            }
            selectBarcodeType.setOnClickListener {
                val number = viewModel.number.value
                if (number.isNullOrEmpty()) {
                    Toast.makeText(context, "Enter card number first", Toast.LENGTH_LONG).show()
                } else {
                    val barcode = Barcode(number, viewModel.barcodeFormat.value)
                    val action: AddCardFragmentDirections.ActionAddCardFragmentToSelectBarcodeFragment =
                        AddCardFragmentDirections.actionAddCardFragmentToSelectBarcodeFragment(
                            barcode
                        )
                    findNavController().navigate(action)
                }
            }
            saveButton.setOnClickListener {
                viewModel.onSaveClick()
            }

            cardNumber.doOnTextChanged { text, start, count, after ->
                viewModel.onCardNumberChange(text.toString())
            }
            cardName.doOnTextChanged { text, _, _, _ ->
                viewModel.onNameChange(text.toString())//
            }
        }

        binding.addCardFront.setOnClickListener {
            viewModel.addCardFront()
        }
        binding.addCardBack.setOnClickListener {
            viewModel.addCardBack()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.event.collect { event ->
                when (event) {
                    is AddCardEvent.ShowInvalidInputMessage -> {
                        Toast.makeText(requireContext(), event.msg, Toast.LENGTH_LONG).show()
                    }
                    is AddCardEvent.NavigateBackWithResult -> {
                        findNavController().popBackStack()
                        findNavController().navigate(R.id.cardsDashboardFragment)
                    }
                    is AddCardEvent.NavigateToCameraScanBarcode -> {
                        val action = AddCardFragmentDirections
                            .actionAddCardFragmentToCameraFragment(CameraActionsRequest.ScanBarcodeAction)
                        findNavController().navigate(action)
                    }
                    is AddCardEvent.NavigateToCameraTakeFrontImage -> {
                        val action = AddCardFragmentDirections
                            .actionAddCardFragmentToCameraFragment(
                                CameraActionsRequest.CaptureImageAction(
                                    CardImageType.FRONT
                                )
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.number.collect { barcode ->
                with(binding.addCardTop.cardNumber) {
                    if (text.toString() != barcode) {
                        setText(barcode)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.barcodeFormat.collect { format ->
                binding.addCardTop.barcodeType.setText(format?.name)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.imageBitmap.collect { bitmap ->
                Glide.with(this@AddCardFragment)
                    .load(bitmap)
                    .error(R.drawable.ic_baseline_image_not_supported_24)
                    .into(binding.addCardTop.barcode)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.frontImageUri.collect { uri ->
                val imgFile = File(uri?.path)
                if (imgFile.exists()) {
                    val bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
                    Glide.with(this@AddCardFragment)
                        .load(bitmap)
                        .error(R.drawable.ic_baseline_image_not_supported_24)
                        .centerInside()
                        .into(binding.cardFrontImage)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.backImageUri.collect { uri ->
                Glide.with(this@AddCardFragment)
                    .load(uri)
                    .error(R.drawable.ic_baseline_image_not_supported_24)
                    .into(binding.cardBackImage)
            }
        }
    }
}
