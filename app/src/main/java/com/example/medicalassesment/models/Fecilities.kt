package com.example.medicalassesment.models


data class Fecilities(
    var facilities: List<Facility> = listOf()
) {
    fun getIndex(id: Int): Int {
        facilities.forEach {
            if (id == it.id) {
                return facilities.indexOf(it)
            }
        }
        return -1
    }

    fun getName(id: Int): String {
        facilities.forEach {
            if (id == it.id) {
                return it.name
            }
        }
        return ""
    }

    fun getItem(id: Int): Facility? {
        facilities.forEach {
            if (id == it.id) {
                return it
            }
        }
        return null
    }
}