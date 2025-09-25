package com.kmcounty.app.di

import com.jakewharton.timber.Timber
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Dagger Hilt para injeção de dependências
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTimberTree(): Timber.Tree {
        return object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String {
                // Formato personalizado: Classe.Metodo(Linha)
                return String.format(
                    "%s.%s(%s)",
                    super.createStackElementTag(element),
                    element.methodName,
                    element.lineNumber
                )
            }

            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                // Adicionar timestamp e nível de log
                val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                    .format(java.util.Date())

                val level = when (priority) {
                    android.util.Log.VERBOSE -> "V"
                    android.util.Log.DEBUG -> "D"
                    android.util.Log.INFO -> "I"
                    android.util.Log.WARN -> "W"
                    android.util.Log.ERROR -> "E"
                    android.util.Log.ASSERT -> "A"
                    else -> "?"
                }

                val formattedMessage = "[$timestamp $level] ${tag ?: "KMCounty"}: $message"

                super.log(priority, tag, formattedMessage, t)
            }
        }
    }
}
