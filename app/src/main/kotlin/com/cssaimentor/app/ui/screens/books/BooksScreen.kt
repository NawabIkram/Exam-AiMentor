package com.cssaimentor.app.ui.screens.books

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cssaimentor.app.domain.model.Book
import com.cssaimentor.app.ui.components.GlassCard
import com.cssaimentor.app.ui.components.MentorOutlinedButton
import com.cssaimentor.app.ui.components.SearchField
import com.cssaimentor.app.ui.components.SectionHeader
import com.cssaimentor.app.ui.components.ShimmerBlock
import com.cssaimentor.app.ui.theme.MentorAmber
import com.cssaimentor.app.ui.theme.MentorCyan
import com.cssaimentor.app.ui.theme.MentorError
import com.cssaimentor.app.ui.theme.MentorTextMuted
import com.cssaimentor.app.viewmodel.BooksViewModel

@Composable
fun BooksScreen(
    onOpenPdf: (String, String, String) -> Unit,
    viewModel: BooksViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(Modifier.height(16.dp)) }
        item { SectionHeader("Books Library", "Curated notes and high-yield CSS reading") }
        item { SearchField(state.query, viewModel::updateSearch, "Search books, notes, categories") }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.categories) { category ->
                    FilterChip(
                        selected = state.selectedCategory == category,
                        onClick = { viewModel.selectCategory(category) },
                        label = { Text(category) }
                    )
                }
            }
        }
        if (state.loading) {
            items(4) {
                GlassCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ShimmerBlock(width = 220.dp)
                        ShimmerBlock(width = 150.dp)
                    }
                }
            }
        } else if (state.error != null) {
            item { Text(state.error.orEmpty(), color = MentorError) }
        } else {
            items(state.books, key = { it.id }) { book ->
                BookCard(
                    book = book,
                    onFavorite = { viewModel.toggleFavorite(book.id) },
                    onOpen = { onOpenPdf(book.id, book.title, book.pdfUrl) }
                )
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

@Composable
private fun BookCard(
    book: Book,
    onFavorite: () -> Unit,
    onOpen: () -> Unit
) {
    GlassCard(accent = MentorAmber) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Book, contentDescription = null, tint = MentorAmber)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(book.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${book.category} • ${book.author}", color = MentorTextMuted)
            }
            IconButton(onClick = onFavorite) {
                Icon(
                    imageVector = if (book.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (book.isFavorite) MentorCyan else MentorTextMuted
                )
            }
            MentorOutlinedButton(text = "Open", onClick = onOpen)
        }
    }
}

