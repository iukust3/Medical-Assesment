package com.example.medicalassesment.models

data class Catogaries(
    val categories: List<Category> = listOf()
) {
    data class Category(
        val id: Int = 0,
        val name: String = ""
    )
}