package com.example.taskforwork

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.taskforwork.databinding.ActivityGameBinding
import java.util.Random

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private val horses = mutableListOf<View>() // Список лошадей
    private var selectedHorseName: String? = null // Выбранная лошадь

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    // Инициализация
    private fun init() {
        var amount = binding.gamePoints // Кол-во денег
        val bet = binding.gameBet.text // Введенное значение ставки
        val btnStart = binding.startButton // Кнопка старта
        val horseSpinner = findViewById<Spinner>(R.id.horseSpinner)

        // Создаем адаптер для Spinner
        val horseNames = resources.getStringArray(R.array.game_horse_names)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, horseNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Применяем адаптер к Spinner
        horseSpinner.adapter = adapter

        // Устанавливаем слушатель выбора элемента
        horseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedHorseName = horseNames[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        amount.text = SharedPreferencesHelper.getAmount(this).toString()
        horses.addAll(listOf(binding.horse1, binding.horse2, binding.horse3, binding.horse4, binding.horse5))

        btnStart.setOnClickListener {
            if (bet.isNullOrEmpty()) {
                val toast = Toast.makeText(applicationContext, "Enter a Bet before starting", Toast.LENGTH_LONG)
                toast.show()
            } else {
                startRace()
            }
        }
    }

    // Получение денег
    private fun getFreeMoney() {
        if (SharedPreferencesHelper.getAmount(this) <= 500) {
            val newAmount = SharedPreferencesHelper.getAmount(this) + 1000
            SharedPreferencesHelper.setAmount(this, newAmount)
            binding.gamePoints.text = SharedPreferencesHelper.getAmount(this).toString()
            val toast = Toast.makeText(applicationContext, "1000 points were added. Now you have $newAmount points", Toast.LENGTH_LONG)
            toast.show()
        }
    }

    private fun startRace() {
        val chooseHorse = selectedHorseName.toString().toInt() - 1
        val winningHorse = (0..4).random()

        // Флаг для отслеживания, было ли уже выведено сообщение
        var messageDisplayed = false

        for (i in horses.indices) {
            val horse = horses[i]

            // Случайно определяем длительность анимации для каждой лошади
            val duration = (4000..6000).random().toLong()

            // Анимация для перемещения лошади
            val animator = ObjectAnimator.ofFloat(horse, "translationX", 0f, 900f)
            animator.duration = duration
            animator.interpolator = LinearInterpolator()

            if (i == winningHorse) {
                animator.duration = (2500..3000).random().toLong() // Длительность для победившей лошади
            }

            // Обработка завершения анимации для определения победителя
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {}
                override fun onAnimationEnd(p0: Animator) {
                    if (!messageDisplayed) {
                        // Введенная ставка
                        val bet = binding.gameBet.text.toString().toInt()
                        if (chooseHorse == winningHorse) {
                            val toast = Toast.makeText(applicationContext, "The first came the horse ${i + 1}. You won!", Toast.LENGTH_LONG)
                            toast.show()

                            val newAmount = bet + SharedPreferencesHelper.getAmount(applicationContext)
                            SharedPreferencesHelper.setAmount(applicationContext, newAmount)
                            binding.gamePoints.text = newAmount.toString()
                        } else {
                            val toast = Toast.makeText(applicationContext, "The first came the horse ${i + 1}. You loose...", Toast.LENGTH_LONG)
                            toast.show()

                            val newAmount = SharedPreferencesHelper.getAmount(applicationContext) - bet
                            SharedPreferencesHelper.setAmount(applicationContext, newAmount)
                            getFreeMoney()
                        }

                        // Устанавливаем флаг, чтобы сообщение было выведено только один раз
                        messageDisplayed = true
                        binding.gameBet.setText("")
                    }
                }
                override fun onAnimationCancel(p0: Animator) {}
                override fun onAnimationRepeat(p0: Animator) {}
            })

            // Запустите анимацию для текущей лошади
            animator.start()
        }
    }

    //    override fun onResume() {
//        super.onResume()
//
//        // Загружаем настройки с Firebase Remote Config
//        firebaseRemoteConfig.fetchAndActivate()
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    val url = firebaseRemoteConfig.getString("url")
//                    if (url.isNotEmpty()) {
//                        // Открываем WebView и сохраняем ссылку локально
//                        openWebView(url)
//                    } else {
//                        // Проверяем, является ли устройство эмулятором или устройством Google
//                        if (isEmulatorOrGoogleDevice()) {
//                            // Открываем заглушку
//                            openPlaceholder()
//                        } else {
//                            // Отображаем экран ожидания подключения к сети (на английском)
//                            showNetworkConnectionRequiredScreen()
//                        }
//                    }
//                } else {
//                    // Ошибка при получении данных с Firebase Remote Config
//                    // Отображаем экран с сообщением о необходимости подключения к сети (на английском)
//                    showNetworkConnectionRequiredScreen()
//                }
//            }
//    }
//
}