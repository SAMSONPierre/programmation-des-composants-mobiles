package fr.uparis.projet

/** model when saving special session configurations **/
data class Configuration(
    var nbWords: Int,
    val pair: LanguagePair,
    var monday: Boolean,
    var tuesday: Boolean,
    var wednesday: Boolean,
    var thursday: Boolean,
    var friday: Boolean,
    var saturday: Boolean,
    var sunday: Boolean,
    var hour: Int,
    var minute: Int
)
