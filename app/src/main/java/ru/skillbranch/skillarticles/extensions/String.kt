package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int> {
    if(this.isNullOrEmpty() || substr.isEmpty()) return listOf()
    val res = mutableListOf<Int>()
    val result = Regex(if(ignoreCase) substr.toLowerCase() else substr).findAll(if(ignoreCase) this.toLowerCase() else this, 0)
    for (i in result) {
        res.add(i.range.first)
    }
    return res
}