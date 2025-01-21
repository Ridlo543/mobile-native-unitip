package com.unitip.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.unitip.mobile.shared.commons.Application
import com.unitip.mobile.shared.data.managers.SessionManager
import com.unitip.mobile.shared.data.repositories.MqttRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var mqttRepository: MqttRepository

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mqttRepository.initialize()

        // memulai aplikasi compose
        setContent {
            Application(
                isAuthenticated = sessionManager.isAuthenticated()
            )
        }
    }
}
