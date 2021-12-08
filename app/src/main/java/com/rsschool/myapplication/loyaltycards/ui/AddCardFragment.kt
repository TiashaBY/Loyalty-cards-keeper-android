package com.rsschool.myapplication.loyaltycards.ui

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.AddCardFragmentBinding
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.AddCardEvent
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.AddCardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.File


@AndroidEntryPoint
class AddCardFragment : Fragment() {

    private val viewModel: AddCardViewModel by viewModels<AddCardViewModel>()

    private var _binding: AddCardFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    lateinit var spinnerAdapter: ArrayAdapter<BarcodeFormat>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddCardFragmentBinding.inflate(inflater, container, false)
        val callback = object : OnBackPressedCallback(true /** true means that the callback is enabled */) {
            override fun handleOnBackPressed() {
                showLeavingDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun setArguments(args: Bundle?) {
        if (args != null) {
            super.setArguments(Bundle(args).apply {
               // putBundle(BUNDLE_ARGS, args) // Wrap the arguments as BUNDLE_ARGS
            })
        } else {
            super.setArguments(null)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        viewModel.load()

        binding.addCardTop.apply {
/*            cardName.setText(viewModel.name.value)
            cardNumber.setText(viewModel.number.value)*/

            cardNumber.doOnTextChanged { text, _, _, _ ->
                viewModel.onCardNumberChange(text.toString())
            }
            cardName.doOnTextChanged { text, _, _, _ ->
                viewModel.onNameChange(text.toString())//
            }

            val values = BarcodeFormat.values().toMutableList<BarcodeFormat?>()
            values.add(0, null)
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
                        values[pos]?.let { viewModel.onBarcodeTypeChange(it) }
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
            viewModel.onAddCardClick()
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
                    is AddCardEvent.RequestImageEvent -> {
                        findNavController().navigate(R.id.cameraFragment)
                    }
                    is AddCardEvent.RequestBarcodeEvent -> {
                        val action = AddCardFragmentDirections.actionAddCardFragmentToCameraFragment("SCANNER")
                        findNavController().navigate(action)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.number.collect { number ->
                if (number.isNullOrEmpty()) {
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

        lifecycleScope.launchWhenResumed {
            viewModel.name.collect { name ->
                if (name.isNullOrEmpty()) {
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

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.barcodeFormat.collect { f ->
                if (f != null) {
                    val spinnerPosition: Int = spinnerAdapter.getPosition(f)
                    binding.addCardTop.barcodeTypeSpinner.setSelection(spinnerPosition)
                } else {
                    binding.addCardTop.barcodeTypeSpinner.setSelection(0)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.imageBitmap.collect { bitmap ->
                Glide.with(this@AddCardFragment)
                    .load(bitmap)
                    .error(R.drawable.ic_baseline_image_not_supported_24)
                    .into(binding.addCardTop.barcode)
            }
        }

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

    private fun loadBitmap(uri: Uri, view: ImageView) {
        val imgFile = File(uri.path ?: "")
        if (imgFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
            Glide.with(this@AddCardFragment)
                .load(bitmap)
                .error(R.drawable.ic_baseline_image_not_supported_24)
                .centerInside()
                .into(view)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        Log.d("Fragment", "Destroyed")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("Fragment", "Detached")
    }

    private fun showLeavingDialog() {
        AlertDialog.Builder(requireContext()).setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setNegativeButton("No", { dialog, id ->
                dialog.cancel()
            })
            .setPositiveButton("Yes") { dialog, id ->
                viewModel.onLeave()
            }.create().show()
    }
}
