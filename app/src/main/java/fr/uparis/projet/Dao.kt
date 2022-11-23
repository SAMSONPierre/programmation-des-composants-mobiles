package fr.uparis.projet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {
    /** Insertions **/
    // ajouter une langue
    @Insert(onConflict=OnConflictStrategy.ABORT)
    fun insertLanguage(vararg lang: Language): List<Long>

    // ajouter la traduction d'un mot (Word)
    @Insert(onConflict=OnConflictStrategy.ABORT)
    fun insertWord(vararg word: Word): List<Long>

    // ajouter un dictionnaire
    @Insert(onConflict=OnConflictStrategy.ABORT)
    fun insertDictionary(vararg dic: Dictionary): List<Long>

    // ajouter un dictionnaire sans la cle
    @Insert(entity=Dictionary::class, onConflict=OnConflictStrategy.ABORT)
    fun insertDictionary(vararg dic: DictionaryLang): List<Long>

    // ajouter l'association mot-dico (WordDic)
    @Insert(onConflict=OnConflictStrategy.ABORT)
    fun insertWordDictionary(vararg wd: WordDicAssociation): List<Long>

    // TODO : quelles tables sont susceptibles d'etre mises a jour ?

    /** Suppressions **/
    // supprimer une traduction
    @Delete(entity=Word::class)
    fun deleteWordsTranslations(ids: List<WordInfo>): Int

    // supprimer un dico
    @Delete(entity=Dictionary::class)
    fun deleteDictionaries(ids: List<DictionaryInfo>): Int

    /** Requetes **/
    // generer toutes les traductions
    @Query("SELECT * FROM Word")
    fun loadAllWordsTranslations(): LiveData<List<Word>>

    // generer tous les dictionnaires
    @Query("SELECT * FROM Dictionary")
    fun loadAllDictionaries(): LiveData<List<Dictionary>>

    // TODO : serait-il interessant d'avoir une requete sur WordDicAssociation ?
}