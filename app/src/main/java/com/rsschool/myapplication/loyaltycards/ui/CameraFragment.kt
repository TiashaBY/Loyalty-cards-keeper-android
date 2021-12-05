package com.rsschool.myapplication.loyaltycards.ui

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.CameraPreviewFragmentBinding
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode
import com.rsschool.myapplication.loyaltycards.domain.utils.BarcodeAnalyzer
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.AddCardViewModel
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.CameraMode
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.CameraResultEvent
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.MyResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.camera_preview_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.io.File
import java.util.*
import java.util.concurrent.Executors

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CameraFragment : Fragment() {

    private var _binding: CameraPreviewFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var cameraExecutor = Executors.newSingleThreadExecutor()

    private lateinit var imageCapture: ImageCapture

    private val cameraViewModel: AddCardViewModel by navGraphViewModels(R.id.add_card_graph)
    { defaultViewModelProviderFactory }

    private val scannerLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(
                context,
                "Camera permissions not granted",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private val photoLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { p ->
        if ((p[READ_EXTERNAL_STORAGE] == true &&
                    p[WRITE_EXTERNAL_STORAGE] == true &&
                    p[CAMERA] == true
                    )
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

        lifecycleScope.launchWhenStarted {
            cameraViewModel.cameraMode.collect {
                when (val mode = it) {
                    CameraMode.SCANNER -> {
                        binding.cameraText.text = "Scanner your card barcode"
                        binding.cameraCaptureButton.visibility = GONE
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
                    CameraMode.CAPTURE_IMAGE_FRONT,
                    CameraMode.CAPTURE_IMAGE_BACK -> {
                        binding.cameraCaptureButton.visibility = VISIBLE
                        if (it == CameraMode.CAPTURE_IMAGE_FRONT) {
                            binding.cameraText.text = "Capture image front"
                        } else {
                            binding.cameraText.text = "Capture image back"
                        }
                        binding.cameraCaptureButton.setOnClickListener {
                            captureImage(mode)
                        }
                        if (storagePermissionsGranted() && cameraPermissionGranted()) {
                            bindUseCases(
                                UseCaseGroup.Builder()
                                    .addUseCase(previewUseCase())
                                    .addUseCase(captureImageUseCase()).build()
                            )
                        } else {
                            photoLauncher.launch((STORAGE_PERMISSIONS + CAMERA).toTypedArray())
                        }
                    }
                    CameraMode.NOT_ACTIVE -> {
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    private fun previewUseCase(): Preview {
        return Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }
    }

    private fun imageAnalysisUseCase() = ImageAnalysis.Builder()
        .build()
        .also {
            it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { result ->
                when (result) {
                    is com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.MyResult.Success -> {
                        cameraViewModel.onBarcodeScanned(result.data as Barcode)
                    }
                    com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.MyResult.Empty -> {
                    }
                    is com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.MyResult.Failure -> {
                    }
                }
            })
        }

    private fun captureImageUseCase(): ImageCapture {
        imageCapture = ImageCapture.Builder().build()
        return imageCapture
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindUseCases(useCases: UseCaseGroup) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, useCases
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun captureImage(mode: CameraMode) {
        val imageCapture = imageCapture

        val photoFile = File(requireContext().filesDir, UUID.randomUUID().toString() + ".jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraFragment", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Log.d("CameraFragment", msg)
                    cameraViewModel.onCardCaptured(mode, savedUri)
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

typealias BarcodeListener = (resultEvent: MyResult<*>) -> Unit
