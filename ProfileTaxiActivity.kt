package com.example.taxiclock

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class ProfileTaxiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_taxi)

        // ImageView dans ton layout
        val ivQrCode = findViewById<ImageView>(R.id.iv_qr_code)

        // 🔹 Le contenu du QR code (fixe)
        val contenuQR = "Nom : Ali\nPrénom : Ahmed\nÂge : 35 ans\nType de permis : B"

        // 🔹 Génération du QR code
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(contenuQR, BarcodeFormat.QR_CODE, 400, 400)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }

            // 🔹 Affiche le QR code dans l'image
            ivQrCode.setImageBitmap(bitmap)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
