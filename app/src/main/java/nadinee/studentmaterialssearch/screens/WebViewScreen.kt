// screens/WebViewScreen.kt — ФИНАЛЬНАЯ ПРОФЕССИОНАЛЬНАЯ ВЕРСИЯ
package nadinee.studentmaterialssearch.screens

import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    url: String,
    navController: NavController,
    title: String = "Загрузка..."
) {
    var pageTitle by remember { mutableStateOf(title) }
    var isLoading by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(0) }
    var hasError by remember { mutableStateOf(false) }
    var currentUrl by remember { mutableStateOf(url) }

    val webView = remember {
        WebView(navController.context.applicationContext).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK  // КЭШИРОВАНИЕ!
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }


            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    progress = newProgress
                    if (newProgress == 100) isLoading = false
                }
                override fun onReceivedTitle(view: WebView?, newTitle: String?) {
                    newTitle?.let { pageTitle = it }
                }
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    isLoading = true
                    hasError = false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    isLoading = false
                }

                override fun onReceivedError(
                    view: WebView?, request: WebResourceRequest?, error: WebResourceError?
                ) {
                    hasError = true
                    isLoading = false
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?, request: WebResourceRequest?
                ): Boolean {
                    request?.url?.toString()?.let { currentUrl = it }
                    return false
                }
            }

            // Скачивание файлов (PDF и т.д.)
            setDownloadListener { url, _, contentDisposition, mimetype, _ ->
                // Можно потом сделать красивый диалог — пока просто открываем
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                intent.data = android.net.Uri.parse(url)
                navController.context.startActivity(intent)
            }
        }
    }

    LaunchedEffect(currentUrl) {
        webView.loadUrl(currentUrl)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pageTitle, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (webView.canGoBack()) webView.goBack()
                        else navController.popBackStack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { webView.reload() }) {
                        Icon(Icons.Filled.Refresh, "Обновить")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AndroidView(
                factory = { webView },
                modifier = Modifier.fillMaxSize()
            )

            // Прогресс-бар
            if (isLoading) {
                LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Ошибка загрузки
            if (hasError) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Не удалось загрузить страницу", textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        hasError = false
                        webView.reload()
                    }) {
                        Text("Попробовать снова")
                    }
                }
            }
        }
    }
}