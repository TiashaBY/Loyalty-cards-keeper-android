package com.rsschool.myapplication.loyaltycards.ui

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.AddCardFragmentBinding
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode
import com.rsschool.myapplication.loyaltycards.domain.utils.CameraMode
import com.rsschool.myapplication.loyaltycards.ui.UiConst.PHOTO_RESULT
import com.rsschool.myapplication.loyaltycards.ui.UiConst.SCANNER_RESULT
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.AddCardEvent
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.AddCardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import java.io.File

@AndroidEntryPoint
class AddCardFragment : Fragment() {

    private val viewModel: AddCardViewModel by viewModels()

    private var _binding: AddCardFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    lateinit var spinnerAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddCardFragmentBinding.inflate(inflater, container, false)
        val callback = object : OnBackPressedCallback(true ) {
            override fun handleOnBackPressed() {
                showLeavingDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        viewModel.load()

        binding.addCardTop.apply {
            cardNumber.doOnTextChanged { text, _, _, _ ->
                viewModel.onCardNumberChange(text.toString())
            }
            cardName.doOnTextChanged { text, _, _, _ ->
                viewModel.onNameChange(text.toString())//
            }

            val values = BarcodeFormat.values().map { it.name }.toMutableList()
            values.add(0, "Barcode type")
            spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                values
            )
            barcodeTypeSpinner.adapter = spinnerAdapter
            barcodeTypeSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>, view: View?, pos: Int, id: Long
                    ) {
                        if (pos == 0) {
                            viewModel.onBarcodeTypeChange(null)
                        } else {
                            viewModel.onBarcodeTypeChange(BarcodeFormat.valueOf(values[pos]))
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
        }

        binding.addCardTop.scanBarcode.setOnClickListener {
            viewModel.onScanBarcodeClick()
        }

        binding.saveButton.setOnClickListener {
            viewModel.onSaveClick()
        }

        binding.addCardBottom.addCardButton.setOnClickListener {
            viewModel.onAddCardImageClick()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.addCardEventsFlow.collect { event ->
                when (event) {
                    is AddCardEvent.ShowInvalidInputMessage -> {
                        Toast.makeText(requireContext(), event.msg, Toast.LENGTH_LONG).show()
                    }
                    is AddCardEvent.NavigateBackWithResult -> {
                        findNavController().popBackStack()
                        findNavController().navigate(R.id.cardsDashboardFragment)
                    }
                    AddCardEvent.RequestImageEvent -> {
                        val action =
                            AddCardFragmentDirections.actionAddCardFragmentToCameraFragment(
                                CameraMode.PHOTO)
                        setFragmentResultListener(PHOTO_RESULT) { _, bundle ->
                            val uriList = bundle.get(PHOTO_RESULT) as List<String>
                            viewModel.onPhotoCaptured(uriList)
                        }
                        findNavController().navigate(action)
                    }
                    AddCardEvent.RequestBarcodeEvent -> {
                        val action =
                            AddCardFragmentDirections.actionAddCardFragmentToCameraFragment(
                                CameraMode.SCANNER)
                        setFragmentResultListener(SCANNER_RESULT) { _, bundle ->
                            val barcode = bundle.getParcelable<Barcode>(SCANNER_RESULT)
                            viewModel.onBarcodeScanned(barcode)
                        }
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.number.collect { number ->
                if (number.isEmpty()) {
                    disableSaveButton()
                } else {
                    enableSaveButton()
                }
                with(binding.addCardTop.cardNumber) {
                    if (text.toString() != number) {
                        setText(number)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.name.collect { name ->
                if (name.isEmpty()) {
                    disableSaveButton()
                } else {
                    enableSaveButton()
                }
                with(binding.addCardTop.cardName) {
                    if (text.toString() != name) {
                        setText(name)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.barcodeFormat.collect { f ->
                if (f != null) {
                    val spinnerPosition: Int = spinnerAdapter.getPosition(f.name)
                    binding.addCardTop.barcodeTypeSpinner.setSelection(spinnerPosition)
                } else {
                    binding.addCardTop.barcodeTypeSpinner.setSelection(0)
                }
            }
        }

/*        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.imageBitmap.collect { bitmap ->
                Glide.with(this@AddCardFragment)
                    .load(bitmap)
                    .error(R.drawable.ic_baseline_image_not_supported_24)
                    .into(binding.addCardTop.barcode)
            }
        }*/

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.frontImageUri.collect { uri ->
                loadBitmap(uri, binding.addCardBottom.cardFrontImage)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.backImageUri.collect { uri ->
                loadBitmap(uri, binding.addCardBottom.cardBackImage)
            }
        }
    }

    private fun enableSaveButton() {
        binding.saveButton.isEnabled = true
    }

    private fun disableSaveButton() {
        binding.saveButton.isEnabled = false
    }

    private suspend fun loadBitmap(uri: Uri?, view: ImageView) {
        val imgFile = File(uri?.path ?: "")
        if (imgFile.exists()) {
            withContext(Dispatchers.IO) {
                val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                view.post {
                    Glide.with(this@AddCardFragment)
                        .load(bitmap)
                        .error(R.drawable.ic_baseline_image_not_supported_24)
                        .centerInside()
                        .into(view)
                }
            }
        }
    }

    private fun showLeavingDialog() =
        AlertDialog.Builder(requireContext()).setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("Yes") { _, _ ->
                viewModel.onLeave()
            }.create().show()
}
