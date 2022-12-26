package fr.uparis.projet

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.concurrent.thread

class MainViewModel(app: Application): AndroidViewModel(app) {
    val dao = (app as DictionaryApplication).database.dao()
    val listDictionaries=dao.loadAllDictionaries()
    var listDictionariesSpinner=dao.loadDictionariesFromPrefix("")

    var selectedDic: Dictionary? = null
    var selectedDicWords: LiveData<List<DictionaryWithWords>>? = null
    var listWords: List<Word>? = null

    fun loadWordsOfSelectedDic(idDic: Long){
        selectedDicWords=dao.loadWordsFromDic(idDic)
        listWords=selectedDicWords?.value?.get(0)?.words
    }

    fun loadPartialName(prefix: String): LiveData<List<String>>{
        listDictionariesSpinner=dao.loadDictionariesFromPrefix(prefix)
        Log.d("spinner 0", listDictionariesSpinner.value.toString())
        return listDictionariesSpinner
    }
}