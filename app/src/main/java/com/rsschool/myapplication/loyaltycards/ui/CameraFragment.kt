package com.rsschool.myapplication.loyaltycards.ui

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.CameraPreviewFragmentBinding
import com.rsschool.myapplication.loyaltycards.domain.utils.ResultContainer
import com.rsschool.myapplication.loyaltycards.ui.UiConst.PHOTO_RESULT
import com.rsschool.myapplication.loyaltycards.ui.UiConst.SCANNER_RESULT
import com.rsschool.myapplication.loyaltycards.ui.util.BarcodeAnalyzer
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.CameraEvents
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.CameraViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.camera_preview_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.util.concurrent.Executors

@ExperimentalCoroutinesApi
@SuppressLint("UnsafeOptInUsageError")
@AndroidEntryPoint
class CameraFragment : Fragment() {

    private var _binding: CameraPreviewFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    private var cameraExecutor = Executors.newSingleThreadExecutor()
    private lateinit var imageCapture: ImageCapture

    private val cameraViewModel: CameraViewModel by viewModels()

    private val scannerLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            bindUseCases(
                UseCaseGroup.Builder()
                    .addUseCase(previewUseCase())
                    .addUseCase(imageAnalysisUseCase()).build()
            )
        } else {
            Toast.makeText(
                context,
                getString(R.string.camera_permissions),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private val photoLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { p ->
        if ((p[READ_EXTERNAL_STORAGE] == true && p[WRITE_EXTERNAL_STORAGE] == true && p[CAMERA] == true)
        ) {
            bindUseCases(
                UseCaseGroup.Builder()
                    .addUseCase(previewUseCase())
                    .addUseCase(captureImageUseCase()).build()
            )
        } else {
            Toast.makeText(
                context,
                "Necessary permissions are not granted: " +
                        p.filter { it.value == false }.map { it.key }.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CameraPreviewFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cameraCaptureButton.visibility = GONE
        lifecycleScope.launchWhenCreated {
            binding.viewPort.visibility = GONE
            cameraViewModel.cameraEvent.collect { event ->
                when (event) {
                    CameraEvents.OpenScanner -> {
                        binding.cameraText.text = getString(R.string.scan_barcode)
                        if (cameraPermissionGranted()) {
                            bindUseCases(
                                UseCaseGroup.Builder()
                                    .addUseCase(previewUseCase())
                                    .addUseCase(imageAnalysisUseCase()).build()
                            )
                        } else {
                            scannerLauncher.launch(CAMERA)
                        }
                    }
                    CameraEvents.CaptureFrontImage -> {
                        binding.viewPort.visibility = GONE
                        val cardSide = CardSide.FRONT
                        binding.cameraText.text = getString(R.string.capture_front)
                        binding.cameraCaptureButton.visibility = VISIBLE
                        binding.cameraCaptureButton.setOnClickListener {
                            captureImage(cardSide)
                        }
                        startCameraForCapturing()
                    }
                    CameraEvents.CaptureBackImage -> {
                        val cardSide = CardSide.BACK
                        binding.cameraCaptureButton.visibility = VISIBLE
                        binding.cameraText.text = getString(R.string.capture_back)
                        binding.cameraCaptureButton.setOnClickListener {
                            captureImage(cardSide)
                        }
                        startCameraForCapturing()
                    }
                    is CameraEvents.BarcodeScanned -> {
                        setFragmentResult(
                            SCANNER_RESULT, bundleOf(SCANNER_RESULT to event.barcode)
                        )
                        findNavController().navigateUp()
                    }
                    is CameraEvents.CameraError -> {
                        Toast.makeText(requireContext(), event.msg, Toast.LENGTH_LONG)
                        findNavController().navigateUp()
                    }
                    CameraEvents.CameraFinishedCapturing -> {
                        setFragmentResult(
                            PHOTO_RESULT, bundleOf(
                                PHOTO_RESULT to listOf(
                                    cameraViewModel.frontImageUri.toString(),
                                    cameraViewModel.backImageUri.toString()
                                )
                            )
                        )
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startCameraForCapturing() {
        if (storagePermissionsGranted() && cameraPermissionGranted()) {
            photoLauncher.launch((STORAGE_PERMISSIONS + CAMERA).toTypedArray())
            bindUseCases(
                UseCaseGroup.Builder()
                    .addUseCase(previewUseCase())
                    .addUseCase(captureImageUseCase()).build()
            )
        } else {
            photoLauncher.launch((STORAGE_PERMISSIONS + CAMERA).toTypedArray())
        }
    }


    private fun previewUseCase(): Preview {
        binding.viewPort.visibility = VISIBLE
        return Preview.Builder()
            .build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }
    }

    private fun imageAnalysisUseCase(): ImageAnalysis {
        return ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { result ->
                    cameraViewModel.onBarcodeScanned(result)
                })
            }
    }

    private fun captureImageUseCase(): ImageCapture {
        imageCapture =
            ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
        return imageCapture
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindUseCases(useCases: UseCaseGroup) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Select lensFacing depending on the available cameras
            val lensFacing = when {
                cameraProvider.hasCamera(DEFAULT_BACK_CAMERA) -> DEFAULT_BACK_CAMERA
                cameraProvider.hasCamera(DEFAULT_FRONT_CAMERA) -> DEFAULT_FRONT_CAMERA
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }
            try {
                cameraProvider.unbindAll()
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, lensFacing, useCases
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun captureImage(side: CardSide) {
        val imageCapture = imageCapture
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    val msg = "Photo capture succeeded"
                    Log.d("CameraFragment", msg)
                    cameraViewModel.onCardCaptured(side, image)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.d("CameraFragment", exception.message.toString())
                    cameraViewModel.onErrorEvent(exception.message.toString())
                }
            })
    }

    private fun cameraPermissionGranted() = ContextCompat.checkSelfPermission(
        requireContext(), CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun storagePermissionsGranted(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            !Environment.isExternalStorageManager()
        } else {
            STORAGE_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(
                    requireContext(), it
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        _binding = null
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private val STORAGE_PERMISSIONS = listOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)

    }
}

typealias BarcodeListener = (resultEvent: ResultContainer<*>) -> Unit
