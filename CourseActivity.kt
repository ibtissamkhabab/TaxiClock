
package com.example.taxiclock

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class CourseActivity : AppCompatActivity() {

    private lateinit var btnStartCourse: Button
    private lateinit var btnPauseCourse: Button
    private lateinit var btnProfile: ImageButton
    private lateinit var btnHome: ImageButton
    private lateinit var btnPosition: ImageButton

    private lateinit var tvPrice: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvDistance: TextView

    private var isRunning = false
    private var isPaused = false
    private var seconds = 0
    private var distance = 0.0

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            if (isRunning && !isPaused) {
                seconds++
                distance += 0.01 // exemple : 10 mètres par seconde = 0.01 km
                updateTime()
                updateDistance()
                updatePrice()
            }
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)

        // Demande de permission notifications Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        // Initialisation des composants
        btnStartCourse = findViewById(R.id.btn_start_course)
        btnPauseCourse = findViewById(R.id.btn_pause_course)
        btnProfile = findViewById(R.id.btn_profile)
        btnHome = findViewById(R.id.btn_home)
        btnPosition = findViewById(R.id.btn_position)

        tvPrice = findViewById(R.id.tv_price)
        tvTime = findViewById(R.id.tv_time)
        tvDistance = findViewById(R.id.tv_distance)

        // Prix initial
        tvPrice.text = "2.50"

        // Démarrer / Arrêter la course
        btnStartCourse.setOnClickListener {
            if (!isRunning) {
                isRunning = true
                isPaused = false
                btnStartCourse.text = "ARRÊTER"
                handler.post(runnable)
            } else {
                isRunning = false
                btnStartCourse.text = "DÉMARRER"
                handler.removeCallbacks(runnable)
                showEndCourseNotification()
                resetCourse()
            }
        }

        // Pause / Reprendre
        btnPauseCourse.setOnClickListener {
            if (isRunning) {
                isPaused = !isPaused
                btnPauseCourse.text = if (isPaused) "REPRENDRE" else "PAUSE"
            }
        }

        // Profil → nouvelle activité
        btnProfile.setOnClickListener {
            startActivity(Intent(this, DriverProfileActivity::class.java))
        }

        // Position → MapsActivity
        btnPosition.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        // Home → revenir à MainActivity
        btnHome.setOnClickListener { finish() }
    }

    private fun updateTime() {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        tvTime.text = String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    private fun updateDistance() {
        tvDistance.text = String.format("%.2f Km", distance)
    }

    private fun updatePrice() {
        val minutes = seconds / 60.0
        val price = 2.50 + (minutes * 0.5) // tarif initial + 0.5 MAD par minute
        tvPrice.text = String.format("%.2f", price)
    }

    private fun resetCourse() {
        seconds = 0
        distance = 0.0
        tvTime.text = "00:00:00"
        tvDistance.text = "0.00 Km"
        tvPrice.text = "2.50"
    }

    private fun showEndCourseNotification() {
        // Vérifier permission notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val channelId = "course_channel"
        val channelName = "Notifications Course"
        val notificationId = 1

        // Créer le channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notification fin de course"
                enableLights(true)
                enableVibration(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val contentText = String.format(
            "Distance : %.2f Km, Tarif : %.2f MAD",
            distance,
            2.50 + (seconds / 60.0 * 0.5)
        )

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, CourseActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Course terminée")
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(notificationId, builder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}
