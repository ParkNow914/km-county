package com.kmcounty.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jakewharton.timber.Timber
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Classe Application principal do KM County
 * Responsável por inicializar bibliotecas e configurar o ambiente
 */
@HiltAndroidApp
class KMCountyApp : Application() {

    @Inject
    lateinit var timberTree: Timber.Tree

    override fun onCreate() {
        super.onCreate()

        // Inicializar Timber para logging
        Timber.plant(timberTree)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        // Configurar Crashlytics baseado nas preferências do usuário
        // TODO: Carregar preferências e configurar Crashlytics

        Timber.d("KMCountyApp inicializado")
    }
}
