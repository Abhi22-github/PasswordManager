package com.roaa.klef

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricPromptManager(private val activity: FragmentActivity) {

    sealed interface BiometricResult {
        data object Success : BiometricResult
        data object NotAvailable : BiometricResult
        data object NotEnrolled : BiometricResult
        data class Error(val message: String) : BiometricResult
    }

    fun canAuthenticate(): Int = BiometricManager.from(activity)
        .canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)

    fun showPrompt(onResult: (BiometricResult) -> Unit) {
        when (canAuthenticate()) {
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                onResult(BiometricResult.NotAvailable)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                onResult(BiometricResult.NotEnrolled)
                return
            }
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onResult(BiometricResult.Success)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onResult(BiometricResult.Error(errString.toString()))
            }

            // Wrong finger/face — user can retry automatically, do nothing
            override fun onAuthenticationFailed() = Unit
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Klef")
            .setSubtitle("Use biometrics or your device PIN / password")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        BiometricPrompt(activity, executor, callback).authenticate(promptInfo)
    }
}
