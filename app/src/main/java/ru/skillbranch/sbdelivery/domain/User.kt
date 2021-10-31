package ru.skillbranch.sbdelivery.domain

import java.io.Serializable

data class User(
    val name: String,
    val email: String
    ) : Serializable