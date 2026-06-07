package com.cssaimentor.app.data.model

import com.cssaimentor.app.domain.model.Book
import com.cssaimentor.app.domain.model.Paper
import com.cssaimentor.app.domain.model.QuizQuestion
import com.cssaimentor.app.domain.model.QuizTopic
import com.cssaimentor.app.utils.Constants

object SampleContent {
    val papers = listOf(
        Paper("paper-pak-2024", "Pakistan Affairs", 2024, "Pakistan Affairs CSS 2024", Constants.DEMO_PDF_URL),
        Paper("paper-current-2024", "Current Affairs", 2024, "Current Affairs CSS 2024", Constants.DEMO_PDF_URL),
        Paper("paper-essay-2023", "Essay", 2023, "English Essay CSS 2023", Constants.DEMO_PDF_URL),
        Paper("paper-isl-2022", "Islamiat", 2022, "Islamiat CSS 2022", Constants.DEMO_PDF_URL),
        Paper("paper-precis-2021", "Precis", 2021, "Precis and Composition CSS 2021", Constants.DEMO_PDF_URL)
    )

    val books = listOf(
        Book("book-current", "Contemporary Current Affairs", "CSS AI Mentor", "Current Affairs", Constants.DEMO_PDF_URL),
        Book("book-pak", "Pakistan Affairs Smart Notes", "CSS AI Mentor", "Pakistan Affairs", Constants.DEMO_PDF_URL),
        Book("book-essay", "Essay Frameworks and Outlines", "CSS AI Mentor", "Essays", Constants.DEMO_PDF_URL),
        Book("book-isl", "Islamiat High Yield Notes", "CSS AI Mentor", "Islamiat", Constants.DEMO_PDF_URL)
    )

    val topics = listOf(
        QuizTopic("pak-affairs", "Pakistan Affairs", "Constitution, history, geography", 5, 0xFF7CE7FF),
        QuizTopic("current-affairs", "Current Affairs", "Pakistan and global updates", 5, 0xFFA855F7),
        QuizTopic("islamiat", "Islamiat", "Core concepts and events", 5, 0xFF22C55E)
    )

    val questions = listOf(
        QuizQuestion(
            "q1",
            "pak-affairs",
            "The Objectives Resolution was passed in which year?",
            listOf("1947", "1949", "1956", "1973"),
            1,
            "The Objectives Resolution was passed by Pakistan's Constituent Assembly in 1949."
        ),
        QuizQuestion(
            "q2",
            "pak-affairs",
            "Which document is regarded as Pakistan's first constitution?",
            listOf("Government of India Act 1935", "Constitution of 1956", "Constitution of 1962", "Constitution of 1973"),
            1,
            "The Constitution of 1956 was Pakistan's first constitution."
        ),
        QuizQuestion(
            "q3",
            "pak-affairs",
            "The Indus Waters Treaty was signed in:",
            listOf("1958", "1960", "1965", "1971"),
            1,
            "The Indus Waters Treaty was signed in 1960."
        ),
        QuizQuestion(
            "q4",
            "current-affairs",
            "Which institution publishes Pakistan's Economic Survey?",
            listOf("State Bank of Pakistan", "Finance Division", "Election Commission", "Planning Commission"),
            1,
            "The Economic Survey is published by the Finance Division before the federal budget."
        ),
        QuizQuestion(
            "q5",
            "current-affairs",
            "SAARC was established in:",
            listOf("1979", "1985", "1991", "1999"),
            1,
            "SAARC was established in 1985 in Dhaka."
        ),
        QuizQuestion(
            "q6",
            "islamiat",
            "The treaty of Hudaibiyah took place in which Hijri year?",
            listOf("5 AH", "6 AH", "7 AH", "8 AH"),
            1,
            "The Treaty of Hudaibiyah was concluded in 6 AH."
        )
    )
}

