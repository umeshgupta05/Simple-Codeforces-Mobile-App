package com.example.codeforces.ui.components

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ProblemWebView(
    contestId: Int,
    index: String,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = isSystemInDarkTheme()
) {
    val url = "https://codeforces.com/problemset/problem/$contestId/$index"
    
    val darkModeCSS = if (isDarkMode) {
        """
        body { 
            background-color: #1e1e1e !important; 
            color: #e0e0e0 !important; 
        }
        .problem-statement { 
            background-color: #2d2d2d !important; 
            color: #e0e0e0 !important; 
        }
        .header { 
            background-color: #2d2d2d !important; 
        }
        pre, code { 
            background-color: #1e1e1e !important; 
            color: #dcdcdc !important; 
            border: 1px solid #444 !important;
        }
        .input, .output {
            background-color: #2d2d2d !important;
            border: 1px solid #444 !important;
        }
        """
    } else {
        ""
    }
    
    val cssInjection = """
        <style>
            body { 
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                padding: 12px;
                font-size: 16px;
                line-height: 1.6;
            }
            .header, .second-level-menu, .footer, #sidebar { 
                display: none !important; 
            }
            .problem-statement { 
                max-width: 100% !important; 
                padding: 0 !important;
            }
            img { 
                max-width: 100% !important; 
                height: auto !important; 
            }
            pre { 
                white-space: pre-wrap !important; 
                word-wrap: break-word !important;
                padding: 8px;
                border-radius: 4px;
            }
            .input-file, .output-file {
                margin: 8px 0;
            }
            $darkModeCSS
        </style>
    """
    
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = ProblemWebViewClient()
                
                settings.apply {
                    javaScriptEnabled = false
                    domStorageEnabled = false
                    allowFileAccess = false
                    allowContentAccess = false
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                    loadWithOverviewMode = true
                    useWideViewPort = true
                }
                
                isVerticalScrollBarEnabled = true
                isHorizontalScrollBarEnabled = false
                
                loadUrl(url)
            }
        },
        modifier = modifier,
        update = { webView ->
            webView.evaluateJavascript(
                """
                (function() {
                    var style = document.createElement('style');
                    style.textContent = `$cssInjection`;
                    document.head.appendChild(style);
                })();
                """.trimIndent(),
                null
            )
        }
    )
}

private class ProblemWebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return true
    }
}
