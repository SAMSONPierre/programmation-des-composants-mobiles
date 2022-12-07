package fr.uparis.projet

import android.app.Application
import android.provider.UserDictionary
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class MainViewModel(app: Application): AndroidViewModel(app) {
    val dao = (app as DictionaryApplication).database.dao()
    val listDictionaries=dao.loadAllDictionaries()
    var selectedDic: Dictionary? = null
    var selectedDicWords: LiveData<List<Word>>? = null
    var selectedDicWords2: LiveData<List<WordDictionaryPair>>? = null

    fun loadWordsOfSelectedDic(idDic: Long){
        selectedDicWords=dao.loadWordsFromDic(idDic)
        selectedDicWords2=dao.loadWordsFromDic()
    }
}