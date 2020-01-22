package com.example.medicalassesment.models

data class StateLga(
    val state: List<State> = ArrayList()

) {
    data class State(
        val id: Int = 0,
        val lgas: List<Lga> = listOf(),
        val name: String = ""
    ) {
        data class Lga(
            val created_at: String = "",
            val id: Int = 0,
            val name: String = "",
            val state_id: String = "",
            val updated_at: String = "",
            val wards: List<Ward> = listOf()
        ) {

            data class Ward(
                val id: Int = 0,
                val name: String = "",
                val lga_id: String = ""
            )

            fun getWrds(): Array<String> {
                val array: Array<String> = Array(wards.size) {
                    ""
                }
                for (i in array.indices) {
                    array[i] = wards[i].name
                }
                return array;
            }

            fun getId(int: Int): Int {
                return wards[int].id
            }
        }


        fun getLgas(): Array<String> {
            val array: Array<String> = Array(lgas.size) {
                ""
            }
            for (i in array.indices) {
                array[i] = lgas[i].name
            }
            return array;
        }

        fun getId(int: Int): Int {
            return lgas[int].id
        }
    }

    fun toArray(): Array<String?> {
        var array = arrayOfNulls<String>(state.size)
        for (i in array.indices) {
            array[i] = state[i].name
        }
        return array;
    }

    fun getId(int: Int): Int {
        return state[int].id
    }

    fun getName(int: Int): String {
        return state[int].name
    }
}