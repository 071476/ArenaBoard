package com.ali.arenaboard.core.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

val supabaseClient = createSupabaseClient(
    supabaseUrl = "https://nfqffizsvffvqocjdvjj.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5mcWZmaXpzdmZmdnFvY2pkdmpqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODc3Nzc2NDYsImV4cCI6MjEwMzM1MzY0Nn0._GKq3MbbdgO2_2TA4KP8b2Kqlpzd2pJ_0astVr5ECDA"
) {
    install(Postgrest)
    install(Realtime)
}
