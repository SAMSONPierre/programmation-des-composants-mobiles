package fr.uparis.projet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {
    /** Insertions **/
    // ajouter une langue sans la cle
    @Insert(entity=Language::class, onConflict=OnConflictStrategy.IGNORE)
    fun insertLanguage(vararg lang: Lang): List<Long>

    // ajouter une langue
    @Insert(onConflict=OnConflictStrategy.IGNORE)
    fun insertLanguage(vararg lang: Language): List<Long>

    // ajouter la traduction d'un mot (Word)
    @Insert(onConflict=OnConflictStrategy.IGNORE)
    fun insertWord(vararg word: Word): List<Long>

    // ajouter la traduction d'un mot sans cle prmimaire
    @Insert(entity=Word::class, onConflict=OnConflictStrategy.IGNORE)
    fun insertWord(vararg word: WordInfo2): List<Long>

    // ajouter un dictionnaire
    @Insert(onConflict=OnConflictStrategy.IGNORE)
    fun insertDictionary(vararg dic: Dictionary): List<Long>

    // ajouter un dictionnaire sans la cle
    @Insert(entity=Dictionary::class, onConflict=OnConflictStrategy.IGNORE)
    fun insertDictionary(vararg dic: DictionaryLang): List<Long>

    // ajouter l'association mot-dico (WordDic)
    @Insert(onConflict=OnConflictStrategy.IGNORE)
    fun insertWordDictionary(vararg wd: WordDicAssociation): List<Long>

    /** Suppressions **/
    // supprimer un mot
    @Delete(entity=Word::class)
    fun deleteWord(word: Word): Int

    @Query("SELECT * FROM Dictionary, WordDicAssociation " +
            "WHERE idWord = :idWord " +
            "AND Dictionary.idDic = WordDicAssociation.idDic")
    fun findDic(idWord: Long): List<Dictionary>

    // supprimer un dictionnaire
    @Delete(entity=Dictionary::class)
    fun deleteDictionary(dic: Dictionary): Int

    // supprimer dans l'association quand on supp un dictionnaire (on supprime ses mots)
    @Query("DELETE FROM WordDicAssociation WHERE idDic = :idDic")
    fun deleteDictionaryId(idDic: Long): Int

    // si un mot n'appartient qu'a un seul dictionnaire, on le supprime si on supprime le dico
    @Query("SELECT * FROM Word, WordDicAssociation " +
            "WHERE idDic = :idDic " +
            "AND Word.idWord = WordDicAssociation.idWord " +
            "AND (SELECT COUNT(*) FROM WordDicAssociation as wd2 " +
                "WHERE wd2.idWord = Word.idWord) = 1")
    fun loadWordsToDelete(idDic: Long): List<Word>

    // supprimer une traduction
    @Delete(entity=Word::class)
    fun deleteWordsTranslations(ids: List<WordInfo>): Int

    // supprimer un dico
    @Delete(entity=Dictionary::class)
    fun deleteDictionaries(ids: List<DictionaryInfo>): Int

    /** Requetes **/

    // generer toutes les traductions
    @Query("SELECT * FROM Word")
    fun loadAllWordsTranslations(): List<Word>

    // generer tous les dictionnaires
    @Query("SELECT * FROM Dictionary")
    fun loadAllDictionaries(): LiveData<List<Dictionary>>

    // generer tous les dictionnaires qui contiennent les mots qui ont un certain prefixe
    @Query("SELECT DISTINCT urlToTranslation FROM Word " +
            "WHERE word LIKE :prefix || '%' ")
    fun loadDictionariesFromPrefix(prefix: String): LiveData<List<String>>

    //selectionner les mots d'un dictionnaire
    @Transaction
    @Query("SELECT * FROM Dictionary WHERE idDic=:idDictionary")
    fun loadWordsFromDic(idDictionary: Long): LiveData<List<DictionaryWithWords>>

    @Transaction
    @Query("SELECT * FROM Dictionary")
    fun loadDictionariesWithWords(): List<DictionaryWithWords>

    @Query("SELECT idDic FROM Dictionary WHERE Dictionary.urlPrefix = :url")
    fun getDicID(url : String): Long

    @Query("SELECT * FROM Word WHERE word = :word")
    fun getWord(word : String): Word

    // selectionner tous les mots avec lang_src et lang_dst
    @Query("SELECT word FROM Word " +
            "WHERE lang_src = :langSrc AND lang_dst = :langDst")
    fun getWordFromLang(langSrc: String, langDst: String): List<String>

    // for global session
    @Query("SELECT * FROM Word WHERE lookedUp < 4")
    fun loadWordsGlobal(): List<Word>

    // for special sessions : selectionner tous les paires de langues disponibles
    @Query("SELECT DISTINCT lang_src, lang_dst FROM Word")
    fun loadLanguagesPairs(): LiveData<List<LanguagePair>>

    @Query("SELECT * FROM Word " +
            "WHERE lang_src = :langSrc AND lang_dst = :langDst" +
            " AND lookedUp < 4")
    fun loadWordsOfPair(langSrc: String, langDst: String): List<Word>

    /** updating and getting learning parameters **/
    @Query("UPDATE Word SET lookedUp = :newVal WHERE idWord = :idWord")
    fun updateLookedUp(newVal: Int, idWord: Long)

    @Query("SELECT lookedUp FROM Word WHERE idWord = :idWord")
    fun getLookedUp(idWord: Long): Int
}
