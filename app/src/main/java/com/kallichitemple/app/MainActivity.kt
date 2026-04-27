package com.kallichitemple.app

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "TempleApp"
        private const val HTML_FILE = "file:///android_asset/temple_v8.html"
    }

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    // ── Camera / Gallery file chooser ───────────────────────────
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null
    private var cameraImageUri: Uri? = null

    // Permission request launchers
    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) openFileChooser() else Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
    }
    private val requestStoragePermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* handled after user acts */ }

    // File chooser result
    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val results = when {
            result.resultCode != Activity.RESULT_OK -> null
            result.data?.data != null               -> arrayOf(result.data!!.data!!)
            cameraImageUri != null                  -> arrayOf(cameraImageUri!!)
            else                                    -> null
        }
        fileChooserCallback?.onReceiveValue(results)
        fileChooserCallback = null
        cameraImageUri = null
    }

    // ── onCreate ─────────────────────────────────────────────────
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)
        webView     = findViewById(R.id.webView)

        configureWebView()
        requestStoragePermissions()

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState)
        } else {
            webView.loadUrl(HTML_FILE)
        }
    }

    // ── WebView configuration ─────────────────────────────────────
    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        val settings = webView.settings

        // JavaScript — essential for the app
        settings.javaScriptEnabled = true

        // DOM Storage — used by the app for localStorage (all local data)
        settings.domStorageEnabled = true

        // Database storage
        settings.databaseEnabled = true

        // Allow mixed content (http resources within https — for CDN fonts)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }

        // Zoom controls
        settings.builtInZoomControls = false
        settings.displayZoomControls = false
        settings.setSupportZoom(false)

        // Viewport — important so the responsive design renders correctly
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true

        // File access — needed for camera capture
        settings.allowFileAccess = true
        @Suppress("DEPRECATION")
        settings.allowFileAccessFromFileURLs = true
        @Suppress("DEPRECATION")
        settings.allowUniversalAccessFromFileURLs = true

        // Caching — use cache when offline
        settings.cacheMode = WebSettings.LOAD_DEFAULT

        // Dark mode pass-through (follows system theme)
        if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
            WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, false)
        }

        // Hardware acceleration
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        // ── WebViewClient — navigation + loading bar ─────────────
        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView, url: String, favicon: android.graphics.Bitmap?) {
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                progressBar.visibility = View.GONE
            }

            override fun onReceivedError(
                view: WebView, request: WebResourceRequest, error: WebResourceError
            ) {
                Log.e(TAG, "WebView error: ${error.description}")
                // Only show error for main frame loads, not sub-resources
                if (request.isForMainFrame) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error loading page. Check your connection.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            // Open external links in the system browser, not in the app
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                return if (url.startsWith("file:///")) {
                    false   // local file — let WebView handle it
                } else {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    true
                }
            }
        }

        // ── WebChromeClient — file chooser, alerts, console ──────
        webView.webChromeClient = object : WebChromeClient() {

            // Progress bar
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progressBar.progress = newProgress
                progressBar.visibility = if (newProgress == 100) View.GONE else View.VISIBLE
            }

            // JS alert / confirm / prompt dialogs
            override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                androidx.appcompat.app.AlertDialog.Builder(this@MainActivity)
                    .setMessage(message)
                    .setPositiveButton("OK") { _, _ -> result.confirm() }
                    .setCancelable(false)
                    .show()
                return true
            }

            override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
                androidx.appcompat.app.AlertDialog.Builder(this@MainActivity)
                    .setMessage(message)
                    .setPositiveButton("OK") { _, _ -> result.confirm() }
                    .setNegativeButton("Cancel") { _, _ -> result.cancel() }
                    .show()
                return true
            }

            override fun onJsPrompt(
                view: WebView, url: String, message: String,
                defaultValue: String?, result: JsPromptResult
            ): Boolean {
                val input = android.widget.EditText(this@MainActivity)
                input.setText(defaultValue)
                androidx.appcompat.app.AlertDialog.Builder(this@MainActivity)
                    .setMessage(message)
                    .setView(input)
                    .setPositiveButton("OK") { _, _ -> result.confirm(input.text.toString()) }
                    .setNegativeButton("Cancel") { _, _ -> result.cancel() }
                    .show()
                return true
            }

            // ── File chooser — camera + gallery ──────────────────
            override fun onShowFileChooser(
                view: WebView,
                callback: ValueCallback<Array<Uri>>,
                params: FileChooserParams
            ): Boolean {
                fileChooserCallback?.onReceiveValue(null)
                fileChooserCallback = callback

                val hasCameraPermission = ContextCompat.checkSelfPermission(
                    this@MainActivity, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (!hasCameraPermission) {
                    requestCameraPermission.launch(Manifest.permission.CAMERA)
                    return true
                }
                openFileChooser()
                return true
            }

            // Console log — useful for debugging
            override fun onConsoleMessage(msg: ConsoleMessage): Boolean {
                Log.d(TAG, "JS [${msg.messageLevel()}] ${msg.message()} — ${msg.sourceId()}:${msg.lineNumber()}")
                return true
            }

            // New window requests (navigator.share popups etc.)
            override fun onCreateWindow(
                view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message
            ): Boolean {
                val newWebView = WebView(this@MainActivity)
                newWebView.webViewClient = WebViewClient()
                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                return true
            }
        }

        // ── Download listener — JPG / PDF / Excel ────────────────
        webView.setDownloadListener { url, _, contentDisposition, mimeType, _ ->
            try {
                if (url.startsWith("data:") || url.startsWith("blob:")) {
                    // Trigger JS download via the page itself
                    webView.evaluateJavascript(
                        "var a=document.createElement('a');a.href='$url';a.download='';document.body.appendChild(a);a.click();document.body.removeChild(a);",
                        null
                    )
                } else {
                    val request = android.app.DownloadManager.Request(Uri.parse(url))
                    request.setMimeType(mimeType)
                    request.setDescription("Downloading file…")
                    val filename = URLUtil.guessFileName(url, contentDisposition, mimeType)
                    request.setTitle(filename)
                    request.allowScanningByMediaScanner()
                    request.setNotificationVisibility(
                        android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                    )
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
                    val dm = getSystemService(DOWNLOAD_SERVICE) as android.app.DownloadManager
                    dm.enqueue(request)
                    Toast.makeText(this, "Downloading $filename…", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Download error: ${e.message}")
                Toast.makeText(this, "Download failed. Check storage permission.", Toast.LENGTH_LONG).show()
            }
        }

        // ── JavaScript bridge — native share sheet ────────────────
        webView.addJavascriptInterface(object {
            @JavascriptInterface
            fun shareText(title: String, text: String) {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, title)
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                startActivity(Intent.createChooser(intent, "Share via"))
            }
        }, "AndroidBridge")
    }

    // ── File chooser helper ───────────────────────────────────────
    private fun openFileChooser() {
        // Camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()
        cameraImageUri = FileProvider.getUriForFile(
            this, "${packageName}.fileprovider", photoFile
        )
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)

        // Gallery intent
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        // Chooser combining both
        val chooser = Intent.createChooser(galleryIntent, "Select Image")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
        fileChooserLauncher.launch(chooser)
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("TEMPLE_${timestamp}_", ".jpg", storageDir)
    }

    // ── Storage permissions ───────────────────────────────────────
    private fun requestStoragePermissions() {
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            emptyArray()   // Android 10+ uses scoped storage — no permission needed
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        if (perms.isNotEmpty()) requestStoragePermission.launch(perms)
    }

    // ── Back button ───────────────────────────────────────────────
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            // Ask before exiting
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage("Exit Temple System?")
                .setPositiveButton("Exit") { _, _ -> super.onBackPressed() }
                .setNegativeButton("Stay", null)
                .show()
        }
    }

    // ── State saving — preserve WebView across orientation change ──
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }

    // ── Lifecycle ─────────────────────────────────────────────────
    override fun onResume() {
        super.onResume()
        webView.resumeTimers()
    }

    override fun onPause() {
        super.onPause()
        webView.pauseTimers()
    }

    override fun onDestroy() {
        webView.stopLoading()
        webView.clearHistory()
        webView.destroy()
        super.onDestroy()
    }
}
