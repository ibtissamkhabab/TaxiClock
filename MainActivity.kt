package com.example.taxiclock

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var btnMenu: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btn_start)
        btnMenu = findViewById(R.id.btn_menu)

        // Bouton Vas-y → CourseActivity
        btnStart.setOnClickListener {
            val intent = Intent(this@MainActivity, CourseActivity::class.java)
            startActivity(intent)
        }

        // Menu popup
        btnMenu.setOnClickListener {
            val inflater = LayoutInflater.from(this)
            val menuView = inflater.inflate(R.layout.menu_layout, null)
            val popup = PopupWindow(
                menuView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true // focusable
            )

            popup.showAsDropDown(btnMenu, 0, 0)


            menuView.findViewById<LinearLayout>(R.id.menu_settings).setOnClickListener {
                Toast.makeText(this, "Paramètres", Toast.LENGTH_SHORT).show()
                popup.dismiss()
            }

            menuView.findViewById<LinearLayout>(R.id.menu_about).setOnClickListener {
                Toast.makeText(this, "À propos", Toast.LENGTH_SHORT).show()
                popup.dismiss()
            }
        }
    }
}





