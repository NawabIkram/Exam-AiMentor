package com.cssaimentor.app.ui.screens.pdf

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cssaimentor.app.ui.components.GlassCard
import com.cssaimentor.app.ui.components.MentorButton
import com.cssaimentor.app.ui.components.ShimmerBlock
import com.cssaimentor.app.ui.theme.MentorBlack
import com.cssaimentor.app.ui.theme.MentorCyan
import com.cssaimentor.app.ui.theme.MentorError
import com.cssaimentor.app.ui.theme.MentorSurface
import com.cssaimentor.app.ui.theme.MentorTextMuted
import com.cssaimentor.app.viewmodel.PdfViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    onBack: () -> Unit,
    viewModel: PdfViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(state.title, style = MaterialTheme.typography.titleMedium)
                        Text("PDF Viewer", style = MaterialTheme.typography.bodyMedium, color = MentorTextMuted)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MentorBlack)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Zoom", color = MentorTextMuted)
                Slider(
                    value = state.zoom,
                    onValueChange = viewModel::updateZoom,
                    valueRange = 0.75f..2.25f,
                    modifier = Modifier.weight(1f)
                )
                Text("${(state.zoom * 100).toInt()}%", color = MentorCyan)
            }

            when {
                state.loading -> PdfLoading()
                state.error != null -> PdfError(state.error.orEmpty(), viewModel::open)
                state.file != null -> PdfDocument(
                    file = state.file!!,
                    zoom = state.zoom,
                    bookmarkPage = state.bookmarkPage,
                    onBookmark = viewModel::bookmark
                )
            }
        }
    }
}

@Composable
private fun PdfDocument(
    file: File,
    zoom: Float,
    bookmarkPage: Int?,
    onBookmark: (Int) -> Unit
) {
    var renderer by remember(file) { mutableStateOf<PdfRenderer?>(null) }
    var descriptor by remember(file) { mutableStateOf<ParcelFileDescriptor?>(null) }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = bookmarkPage ?: 0)

    DisposableEffect(file) {
        val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(pfd)
        descriptor = pfd
        renderer = pdfRenderer
        onDispose {
            pdfRenderer.close()
            pfd.close()
        }
    }

    renderer?.let { pdfRenderer ->
        LaunchedEffect(bookmarkPage) {
            bookmarkPage?.let { listState.scrollToItem(it.coerceIn(0, (pdfRenderer.pageCount - 1).coerceAtLeast(0))) }
        }
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(pdfRenderer.pageCount) { index ->
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Page ${index + 1}", color = MentorTextMuted)
                        IconButton(onClick = { onBookmark(index) }) {
                            Icon(
                                Icons.Rounded.Bookmark,
                                contentDescription = "Bookmark page",
                                tint = if (bookmarkPage == index) MentorCyan else MentorTextMuted
                            )
                        }
                    }
                    PdfPage(renderer = pdfRenderer, pageIndex = index, zoom = zoom)
                }
            }
        }
    }
}

@Composable
private fun PdfPage(
    renderer: PdfRenderer,
    pageIndex: Int,
    zoom: Float
) {
    val bitmap by produceState<Bitmap?>(initialValue = null, renderer, pageIndex, zoom) {
        value = withContext(Dispatchers.IO) {
            synchronized(renderer) {
                val page = renderer.openPage(pageIndex)
                val width = (page.width * zoom).roundToInt().coerceIn(320, 2200)
                val height = (page.height * zoom).roundToInt().coerceIn(420, 3000)
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                bmp.eraseColor(Color.White.toArgb())
                page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                bmp
            }
        }
    }

    GlassCard(accent = MentorCyan) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MentorSurface)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap == null) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ShimmerBlock(width = 220.dp)
                    ShimmerBlock(width = 180.dp)
                }
            } else {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "PDF page ${pageIndex + 1}",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}

@Composable
private fun PdfLoading() {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ShimmerBlock(width = 180.dp)
        repeat(3) {
            GlassCard {
                Box(Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                    ShimmerBlock(width = 220.dp)
                }
            }
        }
    }
}

@Composable
private fun PdfError(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        GlassCard {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(message, color = MentorError)
                MentorButton("Retry", onClick = onRetry, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

