package com.example.voxmind

// Data class representing a quiz, including its metadata and questions
data class VoxModel(
    val id: String,                    // Unique identifier for the quiz
    val title: String,                 // Title of the quiz
    val subtitle: String,              // Subtitle or description of the quiz
    val time: String,                  // Time limit for the quiz in minutes
    val questionList: List<QuestionModel> // List of questions in the quiz
) {
    // Default constructor required for Firebase deserialization
    constructor() : this("", "", "", "", emptyList())
}

// Data class representing a single question in the quiz
data class QuestionModel(
    val question: String,              // The text of the question
    val options: List<String>,         // List of possible answers
    val correct: String                // The correct answer
) {
    // Default constructor required for Firebase deserialization
    constructor() : this("", emptyList(), "")
}
