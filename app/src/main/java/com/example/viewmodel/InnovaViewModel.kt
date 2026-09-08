package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.ContentItem
import com.example.data.api.GeminiRepository
import com.example.data.api.InlineDataItem
import com.example.data.api.PartItem
import com.example.data.db.ConversationEntity
import com.example.data.db.InnovaDatabase
import com.example.data.db.MessageEntity
import com.example.data.db.SavedCreationEntity
import com.example.data.preferences.UserProfile
import com.example.data.preferences.UserPreferencesManager
import com.example.service.VoiceManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
    HOME, CHAT, STUDIOS, HISTORY, PROFILE
}

enum class StudioTab {
    WRITER, IMAGES, CODE, STUDY, TRANSLATE, SUMMARIZE, BRAINSTORM
}

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class FlashCard(
    val term: String,
    val definition: String
)

class InnovaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = InnovaDatabase.getDatabase(application)
    private val dao = db.innovaDao()
    private val repository = GeminiRepository()
    private val prefsManager = UserPreferencesManager(application)
    val voiceManager = VoiceManager(application)

    // Navigation state
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _currentStudio = MutableStateFlow(StudioTab.WRITER)
    val currentStudio: StateFlow<StudioTab> = _currentStudio.asStateFlow()

    // User Profile & Onboarding
    private val _userProfile = MutableStateFlow(prefsManager.getUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _isOnboardingCompleted = MutableStateFlow(prefsManager.hasCompletedOnboarding())
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    private val _primaryInterests = MutableStateFlow(prefsManager.getPrimaryInterests())
    val primaryInterests: StateFlow<Set<String>> = _primaryInterests.asStateFlow()

    // Welcome Greeting & Text-to-Speech
    private val _welcomeMessage = MutableStateFlow<String?>(null)
    val welcomeMessage: StateFlow<String?> = _welcomeMessage.asStateFlow()

    private val _isWelcomeMessageVisible = MutableStateFlow(false)
    val isWelcomeMessageVisible: StateFlow<Boolean> = _isWelcomeMessageVisible.asStateFlow()

    private val _isGeneratingWelcome = MutableStateFlow(false)
    val isGeneratingWelcome: StateFlow<Boolean> = _isGeneratingWelcome.asStateFlow()

    init {
        if (prefsManager.hasCompletedOnboarding() && !prefsManager.hasPlayedWelcomeSpeech()) {
            playWelcomeSpeech(forceReplay = false)
        }
    }

    // Conversations Flow from Room
    val conversations: StateFlow<List<ConversationEntity>> = dao.getAllConversations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedCreations: StateFlow<List<SavedCreationEntity>> = dao.getAllSavedCreations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Chat State
    private val _activeConversationId = MutableStateFlow<Long?>(null)
    val activeConversationId: StateFlow<Long?> = _activeConversationId.asStateFlow()

    private val _activeConversation = MutableStateFlow<ConversationEntity?>(null)
    val activeConversation: StateFlow<ConversationEntity?> = _activeConversation.asStateFlow()

    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messages: StateFlow<List<MessageEntity>> = _messages.asStateFlow()

    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _streamingText = MutableStateFlow("")
    val streamingText: StateFlow<String> = _streamingText.asStateFlow()

    private var streamingJob: Job? = null

    // Selected model for chat
    private val _selectedModel = MutableStateFlow("gemini-3.5-flash")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    // Studio states
    // 1. Image Studio
    private val _imagePrompt = MutableStateFlow("")
    val imagePrompt: StateFlow<String> = _imagePrompt.asStateFlow()
    private val _imageStyle = MutableStateFlow("Futuristic Neon")
    val imageStyle: StateFlow<String> = _imageStyle.asStateFlow()
    private val _imageAspectRatio = MutableStateFlow("1:1")
    val imageAspectRatio: StateFlow<String> = _imageAspectRatio.asStateFlow()
    private val _selectedImageModel = MutableStateFlow("gemini-2.5-flash-image")
    val selectedImageModel: StateFlow<String> = _selectedImageModel.asStateFlow()
    private val _isGeneratingImage = MutableStateFlow(false)
    val isGeneratingImage: StateFlow<Boolean> = _isGeneratingImage.asStateFlow()
    private val _isEnhancingPrompt = MutableStateFlow(false)
    val isEnhancingPrompt: StateFlow<Boolean> = _isEnhancingPrompt.asStateFlow()
    private val _imageGenerationStatus = MutableStateFlow("Ready")
    val imageGenerationStatus: StateFlow<String> = _imageGenerationStatus.asStateFlow()
    private val _generatedImageBase64 = MutableStateFlow<String?>(null)
    val generatedImageBase64: StateFlow<String?> = _generatedImageBase64.asStateFlow()
    private val _generatedImageDesc = MutableStateFlow<String?>(null)
    val generatedImageDesc: StateFlow<String?> = _generatedImageDesc.asStateFlow()
    private val _fullscreenImageBase64 = MutableStateFlow<String?>(null)
    val fullscreenImageBase64: StateFlow<String?> = _fullscreenImageBase64.asStateFlow()

    // 2. Writer Studio
    private val _writerTopic = MutableStateFlow("")
    val writerTopic: StateFlow<String> = _writerTopic.asStateFlow()
    private val _writerType = MutableStateFlow("Blog Post")
    val writerType: StateFlow<String> = _writerType.asStateFlow()
    private val _writerTone = MutableStateFlow("Professional")
    val writerTone: StateFlow<String> = _writerTone.asStateFlow()
    private val _writerResult = MutableStateFlow("")
    val writerResult: StateFlow<String> = _writerResult.asStateFlow()
    private val _isGeneratingWriter = MutableStateFlow(false)
    val isGeneratingWriter: StateFlow<Boolean> = _isGeneratingWriter.asStateFlow()

    // 3. Code Studio
    private val _codeLanguage = MutableStateFlow("Kotlin")
    val codeLanguage: StateFlow<String> = _codeLanguage.asStateFlow()
    private val _codeTask = MutableStateFlow("Generate Code")
    val codeTask: StateFlow<String> = _codeTask.asStateFlow()
    private val _codeInput = MutableStateFlow("")
    val codeInput: StateFlow<String> = _codeInput.asStateFlow()
    private val _codeResult = MutableStateFlow("")
    val codeResult: StateFlow<String> = _codeResult.asStateFlow()
    private val _isGeneratingCode = MutableStateFlow(false)
    val isGeneratingCode: StateFlow<Boolean> = _isGeneratingCode.asStateFlow()

    // 4. Study Studio
    private val _studyTopic = MutableStateFlow("")
    val studyTopic: StateFlow<String> = _studyTopic.asStateFlow()
    private val _studyMode = MutableStateFlow("Concept Explainer") // "Concept Explainer", "Flashcards", "Quiz"
    val studyMode: StateFlow<String> = _studyMode.asStateFlow()
    private val _studyResult = MutableStateFlow("")
    val studyResult: StateFlow<String> = _studyResult.asStateFlow()
    private val _flashcards = MutableStateFlow<List<FlashCard>>(emptyList())
    val flashcards: StateFlow<List<FlashCard>> = _flashcards.asStateFlow()
    private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions.asStateFlow()
    private val _selectedQuizAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val selectedQuizAnswers: StateFlow<Map<Int, Int>> = _selectedQuizAnswers.asStateFlow()
    private val _isGeneratingStudy = MutableStateFlow(false)
    val isGeneratingStudy: StateFlow<Boolean> = _isGeneratingStudy.asStateFlow()

    // 5. Translate Studio
    private val _translateSourceText = MutableStateFlow("")
    val translateSourceText: StateFlow<String> = _translateSourceText.asStateFlow()
    private val _translateSourceLang = MutableStateFlow("English")
    val translateSourceLang: StateFlow<String> = _translateSourceLang.asStateFlow()
    private val _translateTargetLang = MutableStateFlow("Spanish")
    val translateTargetLang: StateFlow<String> = _translateTargetLang.asStateFlow()
    private val _translateResult = MutableStateFlow("")
    val translateResult: StateFlow<String> = _translateResult.asStateFlow()
    private val _isTranslating = MutableStateFlow(false)
    val isTranslating: StateFlow<Boolean> = _isTranslating.asStateFlow()

    // 6. Summarize Studio
    private val _summarizeInput = MutableStateFlow("")
    val summarizeInput: StateFlow<String> = _summarizeInput.asStateFlow()
    private val _summarizeResult = MutableStateFlow("")
    val summarizeResult: StateFlow<String> = _summarizeResult.asStateFlow()
    private val _isSummarizing = MutableStateFlow(false)
    val isSummarizing: StateFlow<Boolean> = _isSummarizing.asStateFlow()

    // 7. Brainstorm Studio
    private val _brainstormTopic = MutableStateFlow("")
    val brainstormTopic: StateFlow<String> = _brainstormTopic.asStateFlow()
    private val _brainstormCategory = MutableStateFlow("Startup Ideas")
    val brainstormCategory: StateFlow<String> = _brainstormCategory.asStateFlow()
    private val _brainstormResult = MutableStateFlow("")
    val brainstormResult: StateFlow<String> = _brainstormResult.asStateFlow()
    private val _isBrainstorming = MutableStateFlow(false)
    val isBrainstorming: StateFlow<Boolean> = _isBrainstorming.asStateFlow()

    // History filter & search
    private val _historySearch = MutableStateFlow("")
    val historySearch: StateFlow<String> = _historySearch.asStateFlow()
    private val _historyCategory = MutableStateFlow("All")
    val historyCategory: StateFlow<String> = _historyCategory.asStateFlow()

    init {
        // Observe active conversation messages
        viewModelScope.launch {
            _activeConversationId.collectLatest { convId ->
                if (convId != null) {
                    _activeConversation.value = dao.getConversationById(convId)
                    dao.getMessagesForConversation(convId).collectLatest { msgList ->
                        _messages.value = msgList
                    }
                } else {
                    _activeConversation.value = null
                    _messages.value = emptyList()
                }
            }
        }
    }

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setStudioTab(studio: StudioTab) {
        _currentStudio.value = studio
        _currentTab.value = AppTab.STUDIOS
    }

    fun selectModel(model: String) {
        _selectedModel.value = model
    }

    fun startNewConversation(category: String = "Chat", initialPrompt: String? = null) {
        viewModelScope.launch {
            val title = if (!initialPrompt.isNullOrBlank()) {
                initialPrompt.take(36).trim() + if (initialPrompt.length > 36) "..." else ""
            } else {
                "New $category"
            }
            val conv = ConversationEntity(
                title = title,
                category = category,
                modelUsed = _selectedModel.value,
                previewSnippet = initialPrompt ?: "Started a new conversation"
            )
            val newId = dao.insertConversation(conv)
            _activeConversationId.value = newId
            _currentTab.value = AppTab.CHAT

            if (!initialPrompt.isNullOrBlank()) {
                sendMessage(initialPrompt)
            }
        }
    }

    fun openConversation(id: Long) {
        _activeConversationId.value = id
        _currentTab.value = AppTab.CHAT
    }

    fun togglePinConversation(conversation: ConversationEntity) {
        viewModelScope.launch {
            dao.setPinned(conversation.id, !conversation.pinned)
        }
    }

    fun deleteConversation(id: Long) {
        viewModelScope.launch {
            dao.deleteConversationById(id)
            if (_activeConversationId.value == id) {
                _activeConversationId.value = null
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            dao.clearAllConversations()
            _activeConversationId.value = null
        }
    }

    fun updateHistorySearch(query: String) {
        _historySearch.value = query
    }

    fun updateHistoryCategory(category: String) {
        _historyCategory.value = category
    }

    // Chat Message Execution
    fun sendMessage(
        text: String,
        bitmap: Bitmap? = null
    ) {
        if (text.isBlank() && bitmap == null) return

        val convId = _activeConversationId.value
        if (convId == null) {
            startNewConversation(category = "Chat", initialPrompt = text)
            return
        }

        viewModelScope.launch {
            val userMsg = MessageEntity(
                conversationId = convId,
                role = "user",
                content = text,
                timestamp = System.currentTimeMillis()
            )
            dao.insertMessage(userMsg)
            dao.updateLastActivity(convId, System.currentTimeMillis(), text.take(60))

            // Build history for API
            val currentMsgs = dao.getMessagesListForConversation(convId)
            val history = currentMsgs.map { m ->
                val parts = mutableListOf<PartItem>()
                parts.add(PartItem(text = m.content))
                ContentItem(
                    role = if (m.role == "user") "user" else "model",
                    parts = parts
                )
            }

            // Prepare system instruction based on persona
            val systemInstruction = _userProfile.value.getSystemPrompt()

            // Stream response
            _isStreaming.value = true
            _streamingText.value = ""

            val accumulatedResponse = StringBuilder()

            streamingJob?.cancel()
            streamingJob = launch {
                repository.streamChat(
                    modelName = _selectedModel.value,
                    systemInstruction = systemInstruction,
                    history = history,
                    temperature = _userProfile.value.temperature
                ).collect { chunk ->
                    accumulatedResponse.append(chunk)
                    _streamingText.value = accumulatedResponse.toString()
                }

                // Finalize response
                val finalContent = accumulatedResponse.toString().ifEmpty {
                    "I am ready to assist you with any questions, code, writing, or analysis."
                }

                val aiMsg = MessageEntity(
                    conversationId = convId,
                    role = "model",
                    content = finalContent,
                    timestamp = System.currentTimeMillis()
                )
                dao.insertMessage(aiMsg)
                dao.updateLastActivity(convId, System.currentTimeMillis(), finalContent.take(60))

                _isStreaming.value = false
                _streamingText.value = ""

                if (_userProfile.value.autoReadAloud) {
                    voiceManager.speak(finalContent, _userProfile.value.ttsSpeed)
                }
            }
        }
    }

    fun stopStreaming() {
        streamingJob?.cancel()
        _isStreaming.value = false
        val current = _streamingText.value
        val convId = _activeConversationId.value
        if (current.isNotBlank() && convId != null) {
            viewModelScope.launch {
                val aiMsg = MessageEntity(
                    conversationId = convId,
                    role = "model",
                    content = current,
                    timestamp = System.currentTimeMillis()
                )
                dao.insertMessage(aiMsg)
                _streamingText.value = ""
            }
        }
    }

    fun regenerateLastMessage() {
        val convId = _activeConversationId.value ?: return
        val currentMsgs = _messages.value
        if (currentMsgs.isEmpty()) return

        val lastUserMsg = currentMsgs.findLast { it.role == "user" }
        if (lastUserMsg != null) {
            sendMessage(lastUserMsg.content)
        }
    }

    fun bookmarkMessage(message: MessageEntity) {
        viewModelScope.launch {
            dao.setMessageSaved(message.id, !message.isSaved)
            if (!message.isSaved) {
                dao.insertSavedCreation(
                    SavedCreationEntity(
                        title = message.content.take(30) + "...",
                        type = "chat",
                        content = message.content
                    )
                )
            }
        }
    }

    fun deleteSavedCreation(creation: SavedCreationEntity) {
        viewModelScope.launch {
            dao.deleteSavedCreation(creation)
        }
    }

    // Studio 1: Image Studio Operations
    fun setImagePrompt(p: String) { _imagePrompt.value = p }
    fun setImageStyle(s: String) { _imageStyle.value = s }
    fun setImageAspectRatio(r: String) { _imageAspectRatio.value = r }
    fun setSelectedImageModel(m: String) { _selectedImageModel.value = m }
    fun setFullscreenImage(base64: String?) { _fullscreenImageBase64.value = base64 }
    fun clearImagePrompt() { _imagePrompt.value = "" }

    fun applyPresetPrompt(preset: String, style: String? = null) {
        _imagePrompt.value = preset
        if (style != null) {
            _imageStyle.value = style
        }
    }

    fun loadCreationIntoViewer(creation: SavedCreationEntity) {
        _imagePrompt.value = creation.title
        if (creation.metadata.isNotBlank()) {
            _imageStyle.value = creation.metadata
        }
        _generatedImageBase64.value = creation.imageBase64
        _generatedImageDesc.value = creation.content
    }

    fun generateImage() {
        val prompt = _imagePrompt.value.trim()
        if (prompt.isBlank()) return
        val style = _imageStyle.value
        val ratio = _imageAspectRatio.value
        val model = _selectedImageModel.value

        viewModelScope.launch {
            _isGeneratingImage.value = true
            _imageGenerationStatus.value = "Initializing synthesis engine..."
            _generatedImageBase64.value = null
            _generatedImageDesc.value = null

            _imageGenerationStatus.value = "Generating with $model..."
            val result = repository.generateImage(
                prompt = prompt,
                style = style,
                aspectRatio = ratio,
                modelName = model
            )

            result.onSuccess { (base64, desc) ->
                _generatedImageBase64.value = base64
                _generatedImageDesc.value = desc
                _imageGenerationStatus.value = "Artwork successfully rendered"
                if (base64 != null) {
                    dao.insertSavedCreation(
                        SavedCreationEntity(
                            title = prompt.take(45),
                            type = "image",
                            content = desc ?: prompt,
                            imageBase64 = base64,
                            metadata = style
                        )
                    )
                }
            }.onFailure { error ->
                _imageGenerationStatus.value = "Notice: ${error.localizedMessage}"
                _generatedImageDesc.value = "Image generation notice: ${error.localizedMessage}"
            }
            _isGeneratingImage.value = false
        }
    }

    fun enhanceImagePrompt() {
        val prompt = _imagePrompt.value.trim()
        if (prompt.isBlank()) return

        viewModelScope.launch {
            _isEnhancingPrompt.value = true
            val enhanceReq = "Enhance this image prompt for maximum artistic fidelity and visual detail. Provide ONLY the refined prompt text in 1-2 descriptive sentences without preamble or quotes: \"$prompt\""
            val res = repository.generateContent(enhanceReq, "You are an expert AI visual prompt engineer.")
            res.onSuccess { enhanced ->
                _imagePrompt.value = enhanced.trim().removeSurrounding("\"").removeSurrounding("'")
            }
            _isEnhancingPrompt.value = false
        }
    }

    // Studio 2: Writer Studio Operations
    fun setWriterTopic(t: String) { _writerTopic.value = t }
    fun setWriterType(t: String) { _writerType.value = t }
    fun setWriterTone(t: String) { _writerTone.value = t }

    fun generateWriterContent() {
        val topic = _writerTopic.value
        if (topic.isBlank()) return

        viewModelScope.launch {
            _isGeneratingWriter.value = true
            _writerResult.value = ""

            val prompt = "Write a high quality ${_writerType.value} about: \"$topic\".\n" +
                    "Tone: ${_writerTone.value}.\n" +
                    "Formatting: Structured with engaging titles, markdown formatting, bullet points where applicable, and a strong conclusion."

            val result = repository.generateContent(
                prompt = prompt,
                systemInstruction = "You are Innova AI's Master Copywriter and Author. Produce vivid, polished, publish-ready content."
            )

            result.onSuccess { text ->
                _writerResult.value = text
                dao.insertSavedCreation(
                    SavedCreationEntity(
                        title = "${_writerType.value}: ${topic.take(24)}",
                        type = "write",
                        content = text,
                        metadata = _writerTone.value
                    )
                )
            }.onFailure { e ->
                _writerResult.value = "Error generating content: ${e.message}"
            }
            _isGeneratingWriter.value = false
        }
    }

    // Studio 3: Code Studio Operations
    fun setCodeLanguage(l: String) { _codeLanguage.value = l }
    fun setCodeTask(t: String) { _codeTask.value = t }
    fun setCodeInput(c: String) { _codeInput.value = c }

    fun generateCode() {
        val input = _codeInput.value
        if (input.isBlank()) return

        viewModelScope.launch {
            _isGeneratingCode.value = true
            _codeResult.value = ""

            val prompt = when (_codeTask.value) {
                "Generate Code" -> "Write clean, robust, modern ${_codeLanguage.value} code for:\n$input\nProvide idiomatic code in a code block with concise commentary."
                "Debug & Fix" -> "Find and fix bugs, errors, and performance issues in this ${_codeLanguage.value} code:\n```${_codeLanguage.value}\n$input\n```\nProvide the fixed code and a bulleted list of fixes."
                "Explain Code" -> "Explain how this ${_codeLanguage.value} code works step-by-step with complexity analysis:\n```${_codeLanguage.value}\n$input\n```"
                "Refactor" -> "Refactor and optimize this ${_codeLanguage.value} code for cleaner architecture, readability, and modern standards:\n```${_codeLanguage.value}\n$input\n```"
                "Unit Tests" -> "Write comprehensive unit tests for this ${_codeLanguage.value} code using standard test frameworks:\n```${_codeLanguage.value}\n$input\n```"
                else -> "Process this ${_codeLanguage.value} code task: $input"
            }

            val result = repository.generateContent(
                prompt = prompt,
                systemInstruction = "You are Innova AI Senior Software Architect. Return clean, production-grade code with markdown syntax highlighting."
            )

            result.onSuccess { text ->
                _codeResult.value = text
                dao.insertSavedCreation(
                    SavedCreationEntity(
                        title = "${_codeLanguage.value} ${_codeTask.value}",
                        type = "code",
                        content = text,
                        metadata = _codeLanguage.value
                    )
                )
            }.onFailure { e ->
                _codeResult.value = "Error generating code: ${e.message}"
            }
            _isGeneratingCode.value = false
        }
    }

    // Studio 4: Study Studio Operations
    fun setStudyTopic(t: String) { _studyTopic.value = t }
    fun setStudyMode(m: String) { _studyMode.value = m }

    fun selectQuizAnswer(questionIndex: Int, optionIndex: Int) {
        val current = _selectedQuizAnswers.value.toMutableMap()
        current[questionIndex] = optionIndex
        _selectedQuizAnswers.value = current
    }

    fun generateStudyContent() {
        val topic = _studyTopic.value
        if (topic.isBlank()) return

        viewModelScope.launch {
            _isGeneratingStudy.value = true
            _studyResult.value = ""
            _flashcards.value = emptyList()
            _quizQuestions.value = emptyList()
            _selectedQuizAnswers.value = emptyMap()

            when (_studyMode.value) {
                "Concept Explainer" -> {
                    val prompt = "Explain the concept of \"$topic\" using the Feynman technique. Include: 1) Simple ELI5 analogy, 2) Core principles, 3) Real-world examples, 4) Common misconceptions."
                    val result = repository.generateContent(prompt, "You are a master educator and learning coach.")
                    result.onSuccess { _studyResult.value = it }
                }
                "Flashcards" -> {
                    val prompt = "Create 4 key flashcards for studying: \"$topic\". Output strictly in this format for each card:\nFRONT: [Term/Question]\nBACK: [Clear Definition/Answer]\n---"
                    val result = repository.generateContent(prompt, "You create high-yield active recall flashcards.")
                    result.onSuccess { text ->
                        val cards = parseFlashcards(text)
                        _flashcards.value = cards
                        if (cards.isEmpty()) _studyResult.value = text
                    }
                }
                "Quiz" -> {
                    val prompt = "Create a 3-question multiple choice quiz on \"$topic\". Format each question strictly as:\nQ: [Question text]\nA) [Option 1]\nB) [Option 2]\nC) [Option 3]\nD) [Option 4]\nCORRECT: [A/B/C/D]\nEXPLANATION: [Brief reasoning]\n---"
                    val result = repository.generateContent(prompt, "You create interactive assessment quizzes.")
                    result.onSuccess { text ->
                        val questions = parseQuiz(text)
                        _quizQuestions.value = questions
                        if (questions.isEmpty()) _studyResult.value = text
                    }
                }
            }
            _isGeneratingStudy.value = false
        }
    }

    private fun parseFlashcards(text: String): List<FlashCard> {
        val list = mutableListOf<FlashCard>()
        val blocks = text.split("---")
        for (b in blocks) {
            val frontMatch = Regex("FRONT:\\s*(.+)", RegexOption.IGNORE_CASE).find(b)
            val backMatch = Regex("BACK:\\s*(.+)", RegexOption.IGNORE_CASE).find(b)
            if (frontMatch != null && backMatch != null) {
                list.add(
                    FlashCard(
                        term = frontMatch.groupValues[1].trim(),
                        definition = backMatch.groupValues[1].trim()
                    )
                )
            }
        }
        return list
    }

    private fun parseQuiz(text: String): List<QuizQuestion> {
        val list = mutableListOf<QuizQuestion>()
        val blocks = text.split("---")
        for (b in blocks) {
            val qMatch = Regex("Q:\\s*(.+)", RegexOption.IGNORE_CASE).find(b)
            val aMatch = Regex("A\\)\\s*(.+)", RegexOption.IGNORE_CASE).find(b)
            val bMatch = Regex("B\\)\\s*(.+)", RegexOption.IGNORE_CASE).find(b)
            val cMatch = Regex("C\\)\\s*(.+)", RegexOption.IGNORE_CASE).find(b)
            val dMatch = Regex("D\\)\\s*(.+)", RegexOption.IGNORE_CASE).find(b)
            val corrMatch = Regex("CORRECT:\\s*([A-D])", RegexOption.IGNORE_CASE).find(b)
            val expMatch = Regex("EXPLANATION:\\s*(.+)", RegexOption.IGNORE_CASE).find(b)

            if (qMatch != null && aMatch != null && bMatch != null) {
                val opts = listOfNotNull(
                    aMatch.groupValues[1].trim(),
                    bMatch.groupValues[1].trim(),
                    cMatch?.groupValues?.get(1)?.trim(),
                    dMatch?.groupValues?.get(1)?.trim()
                )
                val corrLetter = corrMatch?.groupValues?.get(1)?.uppercase() ?: "A"
                val corrIdx = when (corrLetter) {
                    "A" -> 0
                    "B" -> 1
                    "C" -> 2
                    "D" -> 3
                    else -> 0
                }
                list.add(
                    QuizQuestion(
                        question = qMatch.groupValues[1].trim(),
                        options = opts,
                        correctIndex = corrIdx,
                        explanation = expMatch?.groupValues?.get(1)?.trim() ?: ""
                    )
                )
            }
        }
        return list
    }

    // Studio 5: Translate Studio Operations
    fun setTranslateSourceText(t: String) { _translateSourceText.value = t }
    fun setTranslateSourceLang(l: String) { _translateSourceLang.value = l }
    fun setTranslateTargetLang(l: String) { _translateTargetLang.value = l }

    fun swapTranslateLanguages() {
        val temp = _translateSourceLang.value
        _translateSourceLang.value = _translateTargetLang.value
        _translateTargetLang.value = temp
        if (_translateResult.value.isNotBlank()) {
            _translateSourceText.value = _translateResult.value
            _translateResult.value = ""
        }
    }

    fun translateText() {
        val text = _translateSourceText.value
        if (text.isBlank()) return

        viewModelScope.launch {
            _isTranslating.value = true
            _translateResult.value = ""

            val prompt = "Translate the following text from ${_translateSourceLang.value} to ${_translateTargetLang.value}.\n" +
                    "Maintain the natural nuance, idiom, and emotional tone.\n" +
                    "Text to translate:\n\"$text\"\n\n" +
                    "Provide the translation followed by an optional pronunciation/phonetic guide."

            val result = repository.generateContent(prompt, "You are a master polyglot translator.")
            result.onSuccess { res ->
                _translateResult.value = res
                dao.insertSavedCreation(
                    SavedCreationEntity(
                        title = "${_translateSourceLang.value} -> ${_translateTargetLang.value}",
                        type = "translation",
                        content = res,
                        metadata = text.take(30)
                    )
                )
            }.onFailure { e ->
                _translateResult.value = "Translation error: ${e.message}"
            }
            _isTranslating.value = false
        }
    }

    // Studio 6: Summarizer Studio Operations
    fun setSummarizeInput(s: String) { _summarizeInput.value = s }

    fun summarizeContent(format: String = "Executive Summary") {
        val text = _summarizeInput.value
        if (text.isBlank()) return

        viewModelScope.launch {
            _isSummarizing.value = true
            _summarizeResult.value = ""

            val prompt = when (format) {
                "Executive Summary" -> "Analyze and provide an Executive Summary of this document/text:\n$text\nInclude: 1) Executive Summary (2-3 sentences), 2) Key Findings (bullet points), 3) Recommended Action Items."
                "TL;DR Bullets" -> "Extract the top 5 essential takeaways from this text in crisp, punchy bullet points:\n$text"
                "Action Items" -> "Extract all tasks, action items, deadlines, and responsibilities from this text:\n$text"
                else -> "Summarize the key points of this text clearly:\n$text"
            }

            val result = repository.generateContent(prompt, "You are an expert document and data analyst.")
            result.onSuccess { res ->
                _summarizeResult.value = res
                dao.insertSavedCreation(
                    SavedCreationEntity(
                        title = "Summary: ${text.take(24)}...",
                        type = "summary",
                        content = res,
                        metadata = format
                    )
                )
            }.onFailure { e ->
                _summarizeResult.value = "Error summarizing text: ${e.message}"
            }
            _isSummarizing.value = false
        }
    }

    // Studio 7: Brainstorm Studio Operations
    fun setBrainstormTopic(t: String) { _brainstormTopic.value = t }
    fun setBrainstormCategory(c: String) { _brainstormCategory.value = c }

    fun generateBrainstormIdeas() {
        val topic = _brainstormTopic.value
        if (topic.isBlank()) return

        viewModelScope.launch {
            _isBrainstorming.value = true
            _brainstormResult.value = ""

            val prompt = "Brainstorm 5 innovative, out-of-the-box ideas for \"$topic\" in the category of ${_brainstormCategory.value}.\n" +
                    "For each idea, provide:\n" +
                    "- **Concept Name**\n" +
                    "- **Core Value Proposition**\n" +
                    "- **Why it Disrupts / Stands Out**\n" +
                    "- **Immediate Next Step**"

            val result = repository.generateContent(prompt, "You are an visionary venture strategist and creative director.")
            result.onSuccess { res ->
                _brainstormResult.value = res
            }.onFailure { e ->
                _brainstormResult.value = "Error generating ideas: ${e.message}"
            }
            _isBrainstorming.value = false
        }
    }

    // Profile & Settings
    fun updateUserProfile(profile: UserProfile) {
        _userProfile.value = profile
        prefsManager.saveUserProfile(profile)
    }

    fun completeOnboarding(
        name: String,
        title: String,
        persona: String,
        interests: Set<String>,
        initialPrompt: String? = null,
        targetTab: AppTab? = null,
        targetStudio: StudioTab? = null
    ) {
        val updatedProfile = _userProfile.value.copy(
            name = if (name.isNotBlank()) name else _userProfile.value.name,
            title = if (title.isNotBlank()) title else _userProfile.value.title,
            persona = persona
        )
        _userProfile.value = updatedProfile
        prefsManager.saveUserProfile(updatedProfile)
        prefsManager.savePrimaryInterests(interests)
        _primaryInterests.value = interests
        prefsManager.setOnboardingCompleted(true)
        _isOnboardingCompleted.value = true

        // Trigger warm AI-generated spoken welcome greeting
        playWelcomeSpeech(forceReplay = true)

        // Navigate or launch initial interaction if selected
        if (targetStudio != null) {
            _currentTab.value = AppTab.STUDIOS
            _currentStudio.value = targetStudio
        } else if (targetTab != null) {
            _currentTab.value = targetTab
        }

        if (!initialPrompt.isNullOrBlank()) {
            startNewConversation(category = "Chat", initialPrompt = initialPrompt)
        }
    }

    fun playWelcomeSpeech(forceReplay: Boolean = false) {
        if (!forceReplay && prefsManager.hasPlayedWelcomeSpeech()) {
            return
        }
        viewModelScope.launch {
            _isGeneratingWelcome.value = true
            _isWelcomeMessageVisible.value = true
            val profile = _userProfile.value
            val interestsList = _primaryInterests.value
            val interestSummary = if (interestsList.isNotEmpty()) interestsList.joinToString(", ") else "AI Chat, Content & Coding"

            val fallbackGreeting = "Welcome to Innova AI, ${profile.name}! I am your ${profile.persona} intelligence companion. I'm excited to help you brainstorm, build, write, and explore your ideas. Let's create something extraordinary together."
            _welcomeMessage.value = fallbackGreeting

            try {
                val prompt = "Create a warm, sophisticated, and encouraging spoken welcome greeting (exactly 2 sentences, under 32 words) for ${profile.name} who works as ${profile.title}. The user's AI persona is ${profile.persona}, and their primary interests are $interestSummary. Address them directly with genuine warmth in second person. Do not use asterisks, emojis, bullet points, or markdown formatting so that it can be synthesized seamlessly via text-to-speech."
                val result = repository.generateContent(prompt, "You are a warm, articulate AI assistant delivering a spoken audio welcome.")
                result.onSuccess { text ->
                    val cleanText = text
                        .replace(Regex("[#*_`~]"), "")
                        .replace("\n", " ")
                        .trim()
                    if (cleanText.isNotBlank()) {
                        _welcomeMessage.value = cleanText
                    }
                }
            } catch (_: Exception) {
                // Fallback text is already set
            } finally {
                _isGeneratingWelcome.value = false
            }

            val finalSpeechText = _welcomeMessage.value ?: fallbackGreeting
            voiceManager.speak(finalSpeechText, speed = profile.ttsSpeed)
            prefsManager.setWelcomeSpeechPlayed(true)
        }
    }

    fun dismissWelcomeMessage() {
        _isWelcomeMessageVisible.value = false
        voiceManager.stopSpeaking()
    }

    fun replayWelcomeMessage() {
        val current = _welcomeMessage.value
        val profile = _userProfile.value
        if (!current.isNullOrBlank()) {
            _isWelcomeMessageVisible.value = true
            voiceManager.speak(current, speed = profile.ttsSpeed)
        } else {
            playWelcomeSpeech(forceReplay = true)
        }
    }

    fun replayOnboarding() {
        prefsManager.setOnboardingCompleted(false)
        prefsManager.setWelcomeSpeechPlayed(false)
        _isOnboardingCompleted.value = false
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.shutdown()
    }
}
