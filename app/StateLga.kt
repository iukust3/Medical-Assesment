data class StateLga(
    val state: List<State> = listOf()
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
            val updated_at: String = ""
        )
    }
}