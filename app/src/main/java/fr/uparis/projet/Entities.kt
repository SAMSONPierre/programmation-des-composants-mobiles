package fr.uparis.projet

import androidx.room.*

@Entity(indices = [Index(value = ["name"], unique = true)])
data class Language(
    @ColumnInfo(name="idLang", index=true)
    @PrimaryKey(autoGenerate=true) var idLang: Long,
    var name: String
)

// pour pouvoir ajouter une langue sans cle primaire
data class Lang(
    var name: String
)

@Entity(
    foreignKeys=[ForeignKey(
        entity=Language::class,
        parentColumns=["name"],
        childColumns=["lang_src"],
        onDelete=ForeignKey.CASCADE
    ), ForeignKey(
        entity=Language::class,
        parentColumns=["name"],
        childColumns=["lang_dst"],
        onDelete=ForeignKey.CASCADE
    )]
)
data class Word(
    @PrimaryKey(autoGenerate=true) var idWord: Long,
    var word: String,
    @ColumnInfo(name="lang_src", index=true) var lang_src: String,
    @ColumnInfo(name="lang_dst", index=true) var lang_dst: String,
    var urlToTranslation: String
)

// pour pouvoir supprimer un "mot" (pas exactement, juste
// une traduction specifique de ce mot) avec sa cle
data class WordInfo(
    var idWord: Long
)

// pour pouvoir inserer un mot sans cle primaire
data class WordInfo2(
    var word: String,
    var lang_src: String,
    var lang_dst: String,
    var urlToTranslation: String
)

@Entity(
    foreignKeys=[ForeignKey(
        entity=Language::class,
        parentColumns=["name"],
        childColumns=["lang_src"],
        onDelete=ForeignKey.CASCADE
    ), ForeignKey(
            entity=Language::class,
            parentColumns=["name"],
            childColumns=["lang_dst"],
            onDelete=ForeignKey.CASCADE
    )]
)
data class Dictionary(
    @ColumnInfo(name="idDic", index=true)
    @PrimaryKey(autoGenerate=true) val idDic: Long,
    @ColumnInfo(name="lang_src", index=true) var lang_src: String,
    @ColumnInfo(name="lang_dst", index=true) var lang_dst: String,
    var urlPrefix: String
)

// TODO : revoir l'utilite
// pour pouvoir inserer un dico sans la cle primaire
data class DictionaryLang(
    var lang_src: String,
    var lang_dst: String,
    var urlPrefix: String
)

// pour pouvoir supprimer un dico juste a partir de sa cle
class DictionaryInfo(
    val idDic: Long
)

// Association plusieurs a plusieurs entre mot et dictionnaire
// car un mot peut apparaitre dans plusierus dicos
// et un dico contient plusieurs mots
@Entity(
    primaryKeys=["idWord", "idDic"],
    indices = [Index(value = ["idWord"] )],
    foreignKeys=[ForeignKey(
        entity=Word::class,
        parentColumns=["idWord"],
        childColumns=["idWord"],
        deferred = true,
        onDelete=ForeignKey.CASCADE
    ), ForeignKey(
        entity=Dictionary::class,
        parentColumns=["idDic"],
        childColumns=["idDic"],
        deferred = true,
        onDelete=ForeignKey.CASCADE
    )]
)
data class WordDicAssociation(
    var idWord: Long,
    @ColumnInfo(name="idDic", index=true) var idDic: Long
)

data class WordDictionaryPair(
    @Embedded
    var dictionary: Dictionary,
    @Relation(
        parentColumn = "idDic",
        entity = Word::class,
        entityColumn = "idWord",
        associateBy = Junction(
            value = WordDicAssociation::class,
            parentColumn = "idDic",
            entityColumn = "idWord"
        )
    )
    var word: List<WordInfo2>
)