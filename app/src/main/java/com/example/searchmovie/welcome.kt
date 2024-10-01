package com.example.searchmovie

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject

class welcome : AppCompatActivity() {

    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var enter: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        login = findViewById(R.id.login)
        password = findViewById(R.id.password)
        enter = findViewById(R.id.enter)

        // Обработка нажатия кнопки "Войти"
        enter.setOnClickListener {
            val loginText = login.text.toString()
            val passwordText = password.text.toString()

            if (loginText.isNotEmpty() && passwordText.isNotEmpty()) {
                // Сохраняем данные пользователя
                saveUserData(loginText, passwordText)

                // Переходим на экран поиска фильма
                val intent = Intent(this@welcome, searchMovie::class.java)
                startActivity(intent)
            } else {
                // Выводим сообщение об ошибке, если поля пусты
                Toast.makeText(this, "Пожалуйста, введите логин и пароль", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Функция для сохранения данных пользователя в SharedPreferences
    private fun saveUserData(login: String, password: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val userData = JSONObject()
        userData.put("login", login)
        userData.put("password", password)
        editor.putString("user_data", userData.toString())
        editor.apply()
    }
}