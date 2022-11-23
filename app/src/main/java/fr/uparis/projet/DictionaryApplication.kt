package fr.uparis.projet

import android.app.Application

class DictionaryApplication: Application() {
    val database by lazy{ DictionariesDatabase.getDB(this) }
}