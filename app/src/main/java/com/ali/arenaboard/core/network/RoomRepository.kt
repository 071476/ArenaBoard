package com.ali.arenaboard.core.network

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
data class Room(
    val id: String? = null,
    val code: String,
    val game_type: String,
    val player_x: String,
    val player_o: String? = null,
    val board: String = "[\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"]",
    val current_turn: String = "X",
    val winner: String? = null,
    val status: String = "waiting",
    val created_at: String? = null
)

object RoomRepository {

    private fun generateCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..5).map { chars.random() }.joinToString("")
    }

    suspend fun createRoom(gameType: String, playerId: String): String = withContext(Dispatchers.IO) {
        val code = generateCode()
        val room = Room(
            code = code,
            game_type = gameType,
            player_x = playerId
        )
        supabaseClient.from("rooms").insert(room)
        code
    }

    suspend fun joinRoom(code: String, playerId: String): Room? = withContext(Dispatchers.IO) {
        val rooms = supabaseClient.from("rooms")
            .select {
                filter {
                    eq("code", code)
                    eq("status", "waiting")
                }
            }
            .decodeList<Room>()

        val room = rooms.firstOrNull() ?: return@withContext null

        supabaseClient.from("rooms")
            .update({
                set("player_o", playerId)
                set("status", "playing")
            }) {
                filter {
                    eq("code", code)
                }
            }

        room.copy(player_o = playerId, status = "playing")
    }

    suspend fun getRoom(code: String): Room? = withContext(Dispatchers.IO) {
        val rooms = supabaseClient.from("rooms")
            .select {
                filter {
                    eq("code", code)
                }
            }
            .decodeList<Room>()
        rooms.firstOrNull()
    }

    suspend fun updateBoard(code: String, board: String, turn: String, winner: String? = null) = withContext(Dispatchers.IO) {
        val status = if (winner != null) "finished" else "playing"
        supabaseClient.from("rooms")
            .update({
                set("board", board)
                set("current_turn", turn)
                if (winner != null) set("winner", winner)
                set("status", status)
            }) {
                filter {
                    eq("code", code)
                }
            }
    }

    suspend fun deleteRoom(code: String) = withContext(Dispatchers.IO) {
        supabaseClient.from("rooms")
            .delete {
                filter {
                    eq("code", code)
                }
            }
    }
}
