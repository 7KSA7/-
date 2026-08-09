package com.example.security.encryption

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object SecureVaultManager {

    private const val AES_GCM_NOPADDING = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128
    private const val IV_LENGTH = 12
    private const val SALT_LENGTH = 16
    private const val KEY_ITERATIONS = 10000
    private const val KEY_LENGTH = 256

    fun encryptFile(
        context: Context,
        inputFile: File,
        password: CharArray,
        onComplete: (encryptedFile: File, success: Boolean, message: String) -> Unit
    ) {
        if (!inputFile.exists()) {
            onComplete(inputFile, false, "Input file does not exist")
            return
        }

        try {
            val vaultDir = File(context.filesDir, "secure_vault").apply { if (!exists()) mkdirs() }
            val outputFile = File(vaultDir, "${inputFile.name}.vipsecure")

            val salt = ByteArray(SALT_LENGTH)
            SecureRandom().nextBytes(salt)

            val iv = ByteArray(IV_LENGTH)
            SecureRandom().nextBytes(iv)

            val secretKey = deriveKey(password, salt)
            val cipher = Cipher.getInstance(AES_GCM_NOPADDING)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))

            val fos = FileOutputStream(outputFile)
            fos.write(salt)
            fos.write(iv)

            val fis = FileInputStream(inputFile)
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                val outputBuffer = cipher.update(buffer, 0, bytesRead)
                if (outputBuffer != null) {
                    fos.write(outputBuffer)
                }
            }
            val finalBytes = cipher.doFinal()
            if (finalBytes != null) {
                fos.write(finalBytes)
            }

            fis.close()
            fos.flush()
            fos.close()

            onComplete(outputFile, true, "File encrypted successfully to ${outputFile.name}")
        } catch (e: Exception) {
            onComplete(inputFile, false, "Encryption failed: ${e.localizedMessage}")
        }
    }

    fun decryptFile(
        context: Context,
        encryptedFile: File,
        password: CharArray,
        outputDir: File,
        onComplete: (decryptedFile: File?, success: Boolean, message: String) -> Unit
    ) {
        if (!encryptedFile.exists()) {
            onComplete(null, false, "Encrypted file not found")
            return
        }

        try {
            val fis = FileInputStream(encryptedFile)
            val salt = ByteArray(SALT_LENGTH)
            fis.read(salt)

            val iv = ByteArray(IV_LENGTH)
            fis.read(iv)

            val secretKey = deriveKey(password, salt)
            val cipher = Cipher.getInstance(AES_GCM_NOPADDING)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))

            val originalName = encryptedFile.name.removeSuffix(".vipsecure")
            val restoredFile = File(outputDir, originalName)
            val fos = FileOutputStream(restoredFile)

            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                val outputBuffer = cipher.update(buffer, 0, bytesRead)
                if (outputBuffer != null) {
                    fos.write(outputBuffer)
                }
            }
            val finalBytes = cipher.doFinal()
            if (finalBytes != null) {
                fos.write(finalBytes)
            }

            fis.close()
            fos.flush()
            fos.close()

            onComplete(restoredFile, true, "File decrypted successfully")
        } catch (e: Exception) {
            onComplete(null, false, "Decryption failed: Incorrect password or corrupted payload")
        }
    }

    private fun deriveKey(password: CharArray, salt: ByteArray): SecretKeySpec {
        val spec = PBEKeySpec(password, salt, KEY_ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }
}
