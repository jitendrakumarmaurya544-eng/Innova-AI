package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences

data class UserProfile(
    val name: String = "Alex Rivera",
    val title: String = "Product Innovator",
    val persona: String = "Tech Luminary", // "Tech Luminary", "Friendly Tutor", "Executive Pro", "Creative Muse", "Code Guru"
    val customInstructions: String = "Be insightful, concise, and structured. Use markdown formatting with clear headings and bullet points.",
    val preferredModel: String = "gemini-3.5-flash",
    val temperature: Float = 0.7f,
    val autoReadAloud: Boolean = false,
    val ttsSpeed: Float = 1.0f
) {
    fun getSystemPrompt(): String {
        val baseInstruction = when (persona) {
            "Tech Luminary" -> "You are Innova AI, an ultra-intelligent, futuristic AI companion. You explain concepts with cutting-edge clarity, engineering depth, and bold innovation."
            "Friendly Tutor" -> "You are Innova AI, a patient, enthusiastic tutor. You break down complex ideas step-by-step using analogies, Socratic questioning, and encouraging feedback."
            "Executive Pro" -> "You are Innova AI, an executive strategic advisor. You deliver high-impact executive summaries, strategic frameworks, bulleted action items, and crisp communication."
            "Creative Muse" -> "You are Innova AI, an imaginative creative collaborator. You generate vibrant storytelling, poetic prose, unique angles, and vivid imagery."
            "Code Guru" -> "You are Innova AI, a senior staff software architect. You write clean, idiomatic, production-ready code with concise explanations and performance notes."
            else -> "You are Innova AI, a helpful, intelligent, and versatile assistant."
        }
        return if (customInstructions.isNotBlank()) {
            "$baseInstruction\n\nUser Directives: $customInstructions"
        } else {
            baseInstruction
        }
    }
}

class UserPreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("innova_user_prefs", Context.MODE_PRIVATE)

    fun getUserProfile(): UserProfile {
        return UserProfile(
            name = prefs.getString("user_name", "Alex Rivera") ?: "Alex Rivera",
            title = prefs.getString("user_title", "Product Innovator") ?: "Product Innovator",
            persona = prefs.getString("assistant_persona", "Tech Luminary") ?: "Tech Luminary",
            customInstructions = prefs.getString(
                "custom_instructions",
                "Be insightful, concise, and structured. Use markdown formatting with clear headings and code blocks."
            ) ?: "",
            preferredModel = prefs.getString("preferred_model", "gemini-3.5-flash") ?: "gemini-3.5-flash",
            temperature = prefs.getFloat("temperature", 0.7f),
            autoReadAloud = prefs.getBoolean("auto_read_aloud", false),
            ttsSpeed = prefs.getFloat("tts_speed", 1.0f)
        )
    }

    fun hasCompletedOnboarding(): Boolean {
        return prefs.getBoolean("has_completed_onboarding", false)
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean("has_completed_onboarding", completed).apply()
    }

    fun hasPlayedWelcomeSpeech(): Boolean {
        return prefs.getBoolean("has_played_welcome_speech", false)
    }

    fun setWelcomeSpeechPlayed(played: Boolean) {
        prefs.edit().putBoolean("has_played_welcome_speech", played).apply()
    }

    fun getPrimaryInterests(): Set<String> {
        return prefs.getStringSet("primary_interests", setOf("AI Chat", "Code & Dev", "Creative Writing")) ?: emptySet()
    }

    fun savePrimaryInterests(interests: Set<String>) {
        prefs.edit().putStringSet("primary_interests", interests).apply()
    }

    fun saveUserProfile(profile: UserProfile) {
        prefs.edit()
            .putString("user_name", profile.name)
            .putString("user_title", profile.title)
            .putString("assistant_persona", profile.persona)
            .putString("custom_instructions", profile.customInstructions)
            .putString("preferred_model", profile.preferredModel)
            .putFloat("temperature", profile.temperature)
            .putBoolean("auto_read_aloud", profile.autoReadAloud)
            .putFloat("tts_speed", profile.ttsSpeed)
            .apply()
    }
}
