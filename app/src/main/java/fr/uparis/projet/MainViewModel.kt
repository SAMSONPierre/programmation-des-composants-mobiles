package fr.uparis.projet

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlin.concurrent.thread

/**
 * VIEW MODEL SHARED BY ALL FRAGMENTS AND MAIN ACTIVITY
 */
class MainViewModel(app: Application): AndroidViewModel(app) {
    val dao = (app as DictionaryApplication).database.dao()
    // pour creer les alarmes
    var requestCodeAlarm = 2 // 1 == pour la session globale uniquement

    val listDictionaries=dao.loadAllDictionaries()
    var listDictionariesSpinner=dao.loadDictionariesFromPrefix("")

    /** used for listing words of selected dic **/
    var selectedDic: Dictionary? = null
    var selectedDicWords: LiveData<List<DictionaryWithWords>>? = null
    var listWords: List<Word>? = null

    /** used to review single word or reset selected words **/
    var selectedWords = mutableListOf<Word>()

    /** used for daily learning **/
    var globalWordsOfTheDay = mutableListOf<Word>()
    var isEnabledGlobal = false
    var nbWordsGlobal = 10

    /** for deleting on swipe **/
    var swipedDictionary: Dictionary? = null
    var swipedWord: Word? = null

    /** for special learning sessions **/
    var listPairLanguages=dao.loadLanguagesPairs()
    val listConfiguration= mutableListOf<Configuration>()

    fun loadWordsOfSelectedDic(idDic: Long){
        selectedDicWords=dao.loadWordsFromDic(idDic)
        listWords=selectedDicWords?.value?.get(0)?.words
    }

    /** pour le spinner **/
    fun loadPartialName(prefix: String): LiveData<List<String>>{
        listDictionariesSpinner=dao.loadDictionariesFromPrefix(prefix)
        return listDictionariesSpinner
    }

    /** delete word **/
    fun deleteWord() {
        thread {
            dao.deleteWord(swipedWord!!) // ne supprime pas le dictionnaire s'il est vide
            swipedWord = null
        }
    }

    /** delete dictionary **/
    fun deleteDictionary(){
        thread{
            val toDelete = dao.loadWordsToDelete(swipedDictionary!!.idDic)
            for(word: Word in toDelete) dao.deleteWord(word)
            dao.deleteDictionaryId(swipedDictionary!!.idDic)
            dao.deleteDictionary(swipedDictionary!!)
            swipedDictionary=null
        }
    }

    /** liste des traductions de mots **/
    fun getWordsTranslation(words: MutableList<Word>): ArrayList<String> {
        val wordsUrl = mutableListOf<String>()
        for(i in 0 until words.size) wordsUrl.add(words[i].urlToTranslation)
        return ArrayList(wordsUrl)
    }

    /** select random words from all available words **/
    fun getGlobalDailyWords(nbWords: Int): MutableList<Word> {
        /* tests purpose */
        val words = dao.loadAllWordsTranslations()
        for(word in words) dao.updateLookedUp(0, word.idWord)

        globalWordsOfTheDay = dao.loadWordsGlobal()
            .asSequence().shuffled().take(nbWords).toMutableList()
        return globalWordsOfTheDay
    }

    /** retourne les langues sources des mots global **/
    fun getGlobalDailyWordsLangSrc(): ArrayList<String> {
        val wordsLangSrc = mutableListOf<String>()
        for(i in 0 until globalWordsOfTheDay.size) wordsLangSrc.add(globalWordsOfTheDay[i].lang_src)
        return ArrayList(wordsLangSrc)
    }

    /** retourne les langues dst des mots global **/
    fun getGlobalDailyWordsLangDst(): ArrayList<String> {
        val wordsLangDst = mutableListOf<String>()
        for(i in 0 until globalWordsOfTheDay.size) wordsLangDst.add(globalWordsOfTheDay[i].lang_dst)
        return ArrayList(wordsLangDst)
    }
    /** end of daily words' info **/

    /** get words for special sessions -- similaire a globalWords **/
    fun getWordsForPair(lang_src: String, lang_dst: String, nbWords: Int): MutableList<Word>{
        val wordsForPair = dao.loadWordsOfPair(lang_src, lang_dst)
            .asSequence().shuffled().take(nbWords).toMutableList()
        return wordsForPair
    }
    /** end of special sessions info **/

    /** back to new word state **/
    fun resetWords(){
        for(word in selectedWords){
            updateLookedUp(0, word.idWord)
        }
    }

    fun updateLookedUp(newVal: Int, idWord: Long){
        dao.updateLookedUp(newVal, idWord)
    }

    /** for special session configuration **/
    fun addConfiguration(
        nbWords: Int,
        pair: LanguagePair,
        monday: Boolean, tuesday: Boolean, wednesday: Boolean,
        thursday: Boolean, friday: Boolean, saturday: Boolean,
        sunday: Boolean, hour: Int, minute: Int
    ){
        var existingConfig: Configuration?=null
        for(config in listConfiguration)
            if(pair == config.pair) existingConfig = config
        if(existingConfig!=null){ // on ne fait que mettre a jour la configuration si elle existe deja
            existingConfig.nbWords = nbWords
            existingConfig.monday = monday
            existingConfig.tuesday = tuesday
            existingConfig.wednesday = wednesday
            existingConfig.thursday = thursday
            existingConfig.friday = friday
            existingConfig.saturday = saturday
            existingConfig.sunday = sunday
            existingConfig.hour = hour
            existingConfig.minute = minute

        }
        else listConfiguration.add(
            Configuration(
                nbWords, pair, monday, tuesday, wednesday, thursday,
                friday, saturday, sunday, hour, minute
            )
        ) // sinon on la cree
    }

    fun getConfiguration(pair: LanguagePair): Configuration?{
        for(config in listConfiguration) {
            if(config.pair == pair) return config
        }
        return null
    }
}