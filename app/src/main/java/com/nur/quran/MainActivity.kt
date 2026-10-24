package com.nur.quran

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext

private val Emerald = Color(0xFF0B2823)
private val CardGreen = Color(0xFF1C4136)
private val SoftGreen = Color(0xFF2B5547)
private val Gold = Color(0xFFE8CB92)
private val Cream = Color(0xFFFFF8E9)
private val Pale = Color(0xFFE1EDE5)
private val Muted = Color(0xFFBBD0C1)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
            )
        )
        setContent { NurApp() }
    }
}

@Composable
private fun NurApp() {
    val context = LocalContext.current
    val store = remember { ProgressStore(context) }
    val result by produceState<Result<QuranRepository>?>(initialValue = null) {
        value = runCatching { withContext(Dispatchers.IO) { QuranRepository.load(context.assets) } }
    }
    var tab by rememberSaveable { mutableStateOf("home") }
    var readingSurah by rememberSaveable { mutableIntStateOf(0) }
    var fontSize by remember { mutableIntStateOf(store.fontSize()) }
    var dark by remember { mutableStateOf(store.isDark()) }
    val colors = darkColorScheme(
        primary = Gold, onPrimary = Emerald,
        background = Emerald, onBackground = Color.White,
        surface = CardGreen, onSurface = Color.White
    )
    val lightColors = lightColorScheme(
        primary = Color(0xFF16533E), onPrimary = Color.White,
        background = Cream, onBackground = Emerald,
        surface = Color.White, onSurface = Emerald
    )
    val bg = if (dark) Emerald else Cream
    val fg = if (dark) Color.White else Emerald
    val card = if (dark) CardGreen else Color.White
    MaterialTheme(colorScheme = if (dark) colors else lightColors) {
        Box(Modifier.fillMaxSize().background(bg).windowInsetsPadding(WindowInsets.safeDrawing)) {
        when {
            result == null -> Box(Modifier.fillMaxSize().background(bg), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Gold)
            }
            result!!.isFailure -> Column(
                Modifier.fillMaxSize().background(bg).padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Kur’an verisi yüklenemedi", color = fg, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Text(result!!.exceptionOrNull()?.localizedMessage ?: "Veri dosyası hatası", color = fg)
            }
            else -> {
                val repository = result!!.getOrThrow()
                BackHandler(enabled = readingSurah != 0) { readingSurah = 0 }
                if (readingSurah in 1..114) {
                    key(readingSurah) {
                        PremiumReaderScreen(
                            surah = repository.surah(readingSurah),
                            globalAyahOffset = repository.surahs.take(readingSurah - 1).sumOf { it.ayahCount },
                            store = store,
                            fontSize = fontSize,
                            onFontSize = { fontSize = it; store.setFontSize(it) },
                            onBack = { readingSurah = 0 }
                        )
                    }
                } else {
                    Scaffold(
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        containerColor = bg,
                        bottomBar = {
                            NavigationBar(containerColor = card, windowInsets = WindowInsets(0, 0, 0, 0)) {
                                listOf(
                                    Triple("home", "Kur’an", Icons.Default.MenuBook),
                                    Triple("quran", "Sureler", Icons.Default.List),
                                    Triple("search", "Ara", Icons.Default.Search),
                                    Triple("worship", "Namaz", Icons.Default.Explore),
                                    Triple("settings", "Ayarlar", Icons.Default.Settings)
                                ).forEach { (id, label, icon) ->
                                    NavigationBarItem(
                                        selected = tab == id,
                                        onClick = { tab = id },
                                        icon = { Icon(icon, contentDescription = label) },
                                        label = { Text(label, fontSize = 11.sp) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Emerald,
                                            selectedTextColor = if (dark) Gold else Emerald,
                                            indicatorColor = Gold,
                                            unselectedIconColor = if (dark) Muted else SoftGreen,
                                            unselectedTextColor = if (dark) Muted else SoftGreen
                                        )
                                    )
                                }
                            }
                        }
                    ) { padding ->
                        Box(Modifier.fillMaxSize().padding(padding)) {
                            when (tab) {
                                "home" -> HomeScreen(
                                    store = store, repo = repository, dark = dark,
                                    onContinue = { readingSurah = store.position().surah },
                                    onOpenQuran = { tab = "quran" },
                                    onOpenLearn = { tab = "learn" },
                                    onOpenWorship = { tab = "worship" },
                                    onChangeTheme = { dark = !dark; store.setDark(dark) }
                                )
                                "quran" -> ReferenceSurahListScreen(repository, onOpen = { readingSurah = it })
                                "search" -> ReferenceSurahListScreen(repository, onOpen = { readingSurah = it }, focusSearch = true)
                                "learn" -> LearningScreen(dark)
                                "worship" -> WorshipScreen(dark)
                                else -> ReferenceAppSettings(dark = dark, onTheme = { dark = !dark; store.setDark(dark) })
                            }
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun HomeScreen(
    store: ProgressStore,
    repo: QuranRepository,
    dark: Boolean,
    onContinue: () -> Unit,
    onOpenQuran: () -> Unit,
    onOpenLearn: () -> Unit,
    onOpenWorship: () -> Unit,
    onChangeTheme: () -> Unit
) {
    val fg = if (dark) Color.White else Emerald
    val card = if (dark) CardGreen else Color.White
    val pos = store.position()
    val name = repo.surah(pos.surah).turkishName
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.MenuBook, null, tint = Gold, modifier = Modifier.size(35.dp))
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("NUR", color = fg, fontSize = 27.sp, fontWeight = FontWeight.Bold)
                Text("Kur’an ve İslam Rehberi", color = if (dark) Muted else SoftGreen, fontSize = 12.sp)
            }
            IconButton(onClick = onChangeTheme) {
                Icon(if (dark) Icons.Default.LightMode else Icons.Default.DarkMode, "Temayı değiştir", tint = if (dark) Gold else SoftGreen)
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onContinue),
            color = if (dark) SoftGreen else Color(0xFFE7F0E6),
            shape = RoundedCornerShape(23.dp)
        ) {
            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("KALDIĞIN YERDEN DEVAM ET", color = if (dark) Gold else SoftGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(name + " Suresi", color = fg, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(pos.ayah.toString() + ". ayet • Dokunarak devam et", color = fg, fontSize = 14.sp)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Icon(Icons.Default.PlayCircle, "Devam et", tint = if (dark) Gold else SoftGreen, modifier = Modifier.size(40.dp))
                }
            }
        }
        Text("Keşfet ve öğren", color = fg, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HomeTile(Modifier.weight(1f), "Kur’an-ı Kerim", "114 sure • Çevrimdışı", Icons.Default.MenuBook, card, fg, onOpenQuran)
            HomeTile(Modifier.weight(1f), "Elifba Öğren", "Harfleri tanı", Icons.Default.School, card, fg, onOpenLearn)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HomeTile(Modifier.weight(1f), "İbadet Rehberi", "Geliştirme planı", Icons.Default.Explore, card, fg, onOpenWorship)
            HomeTile(Modifier.weight(1f), "Yer İmleri", "Ayetlerini sakla", Icons.Default.Bookmarks, card, fg, onOpenQuran)
        }
        Surface(color = card, shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("KUR’AN-I KERİM", color = if (dark) Gold else SoftGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", color = fg, fontSize = 29.sp,
                    lineHeight = 54.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(textDirection = TextDirection.Rtl))
                Text("Rahmân ve Rahîm olan Allah'ın adıyla.", color = fg, fontSize = 13.sp,
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Text("Fâtiha 1:1 • Kur’an metni: Tanzil Project", color = if (dark) Muted else SoftGreen,
                    fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun HomeTile(
    modifier: Modifier, title: String, subtitle: String, icon: ImageVector,
    bg: Color, fg: Color, onClick: () -> Unit
) {
    Surface(modifier = modifier.clickable(onClick = onClick), color = bg, shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(17.dp).heightIn(min = 112.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Icon(icon, null, tint = Gold, modifier = Modifier.size(29.dp))
            Text(title, color = fg, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = if (fg == Color.White) Muted else SoftGreen, fontSize = 11.sp)
        }
    }
}

@Composable
private fun SurahListScreen(repo: QuranRepository, dark: Boolean, onOpen: (Int) -> Unit) {
    var search by rememberSaveable { mutableStateOf("") }
    val fg = if (dark) Color.White else Emerald
    val filtered = remember(search, repo) {
        repo.surahs.filter {
            search.isBlank() ||
            it.number.toString() == search.trim() ||
            it.turkishName.contains(search, ignoreCase = true) ||
            it.latinName.contains(search, ignoreCase = true) ||
            it.arabicName.contains(search)
        }
    }
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(20.dp))
        Text("Kur’an-ı Kerim", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = fg)
        Text("114 sure • 6.236 ayet • Çevrimdışı", color = if (dark) Muted else SoftGreen, fontSize = 13.sp)
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(
            value = search, onValueChange = { search = it },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Sure adı veya numarası ara") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            items(filtered, key = { it.number }) { surah ->
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { onOpen(surah.number) },
                    color = if (dark) CardGreen else Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(43.dp).background(SoftGreen, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(surah.number.toString(), color = Gold, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(surah.turkishName, color = fg, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                            Text(surah.ayahCount.toString() + " ayet", color = if (dark) Muted else SoftGreen, fontSize = 12.sp)
                        }
                        Text(surah.arabicName, color = if (dark) Gold else Emerald, fontSize = 23.sp,
                            style = TextStyle(textDirection = TextDirection.Rtl))
                    }
                }
            }
        }
    }
}

@Composable
private fun ReaderScreen(
    surah: Surah,
    globalAyahOffset: Int,
    store: ProgressStore,
    fontSize: Int,
    onFontSize: (Int) -> Unit,
    onBack: () -> Unit,
    dark: Boolean
) {
    val fg = if (dark) Color.White else Emerald
    val saved = store.position()
    val initialIndex = if (saved.surah == surah.number) (saved.ayah - 1).coerceIn(0, surah.ayahCount - 1) else 0
    val state = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    var showAyah by remember { mutableIntStateOf(0) }
    var audioAyah by remember(surah.number) { mutableIntStateOf(0) }
    var requestedAyah by remember(surah.number) { mutableIntStateOf(1) }
    var playNonce by remember(surah.number) { mutableIntStateOf(0) }
    var bookmarkRevision by remember { mutableIntStateOf(0) }
    val unusedRevision = bookmarkRevision
    LaunchedEffect(surah.number, state) {
        snapshotFlow { state.firstVisibleItemIndex }.distinctUntilChanged().collect { index ->
            store.savePosition(surah.number, (index + 1).coerceAtMost(surah.ayahCount))
        }
    }
    LaunchedEffect(audioAyah) {
        if (audioAyah in 1..surah.ayahCount &&
            state.firstVisibleItemIndex != audioAyah - 1) {
            state.animateScrollToItem(audioAyah - 1)
        }
    }
    Column(Modifier.fillMaxSize().background(if (dark) Emerald else Cream)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri", tint = fg) }
            Column(Modifier.weight(1f)) {
                Text(surah.turkishName + " Suresi", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = fg)
                Text(surah.ayahCount.toString() + " ayet • Tanzil", color = if (dark) Muted else SoftGreen, fontSize = 12.sp)
            }
            IconButton(onClick = { onFontSize((fontSize - 2).coerceAtLeast(24)) }) {
                Icon(Icons.Default.Remove, "Yazıyı küçült", tint = fg)
            }
            Text(fontSize.toString(), color = fg, fontSize = 13.sp)
            IconButton(onClick = { onFontSize((fontSize + 2).coerceAtMost(52)) }) {
                Icon(Icons.Default.Add, "Yazıyı büyüt", tint = fg)
            }
        }
        AyahAudioPanel(
            surah = surah,
            globalAyahOffset = globalAyahOffset,
            store = store,
            dark = dark,
            requestedAyah = requestedAyah,
            playNonce = playNonce,
            onAyahChanged = { audioAyah = it; store.savePosition(surah.number, it) }
        )
        var showLocalAudio by remember(surah.number) { mutableStateOf(false) }
        TextButton(onClick = { showLocalAudio = !showLocalAudio }) {
            Text(if (showLocalAudio) "Yerel MP3 seçeneğini gizle" else "Cihazımdan MP3 oynat (çevrimdışı)")
        }
        if (showLocalAudio) LocalAudioPanel(surah.number, store, dark)
        HorizontalDivider(color = if (dark) SoftGreen else Color(0xFFE1DCCB))
        LazyColumn(
            state = state,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(surah.verses, key = { index, _ -> index }) { index, verse ->
                val verseNo = index + 1
                val bookmarked = store.hasBookmark(surah.number, verseNo)
                val check = unusedRevision // forces recomposition when bookmark changes
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable {
                        store.savePosition(surah.number, verseNo)
                        showAyah = verseNo
                    },
                    shape = RoundedCornerShape(17.dp),
                    color = if (audioAyah == verseNo) {
                        if (dark) SoftGreen else Color(0xFFFFE5AF)
                    } else if (dark) CardGreen else Color.White
                ) {
                    Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                surah.number.toString() + ":" + verseNo,
                                fontSize = 12.sp, color = if (dark) Gold else SoftGreen,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                requestedAyah = verseNo
                                playNonce += 1
                            }, modifier = Modifier.size(40.dp)) {
                                Icon(Icons.Default.PlayCircle, "Bu ayeti sesli oku",
                                    tint = if (dark) Gold else SoftGreen)
                            }
                            IconButton(onClick = {
                                store.toggleBookmark(surah.number, verseNo)
                                bookmarkRevision += 1
                            }, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    if (bookmarked && check >= 0) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    "Yer imi", tint = if (dark) Gold else SoftGreen
                                )
                            }
                        }
                        Text(
                            verse,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = fontSize.sp,
                            lineHeight = (fontSize * 1.95f).sp,
                            textAlign = TextAlign.Right,
                            color = fg,
                            style = TextStyle(textDirection = TextDirection.Rtl)
                        )
                        Text(
                            if (audioAyah == verseNo) "▶ Sesli okunan ayet"
                            else "Ayetin sesini dinlemek için ▶ düğmesine dokun",
                            color = if (dark) Gold else SoftGreen, fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
    if (showAyah in 1..surah.ayahCount) {
        var selectedWord by remember(showAyah) { mutableStateOf<String?>(null) }
        val text = surah.verses[showAyah - 1]
        AlertDialog(
            onDismissRequest = { showAyah = 0 },
            title = { Text(surah.turkishName + " " + showAyah, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text, fontSize = 23.sp, lineHeight = 42.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Right,
                        style = TextStyle(textDirection = TextDirection.Rtl))
                    Text("Kelimeyi incelemek için seç:", fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        text.split(" ").filter { it.isNotBlank() }.forEach { word ->
                            FilterChip(
                                selected = selectedWord == word,
                                onClick = { selectedWord = word },
                                label = { Text(word, fontSize = 20.sp, style = TextStyle(textDirection = TextDirection.Rtl)) }
                            )
                        }
                    }
                    if (selectedWord != null) {
                        Text("Seçilen kelime: " + selectedWord, fontSize = 17.sp)
                        Text("Doğrulanmış harf ve kelime ses kayıtları sonraki sürüme eklenecek.",
                            fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAyah = 0 }) { Text("Kapat") }
            }
        )
    }
}

@Composable
internal fun LocalAudioPanel(surahNumber: Int, store: ProgressStore, dark: Boolean) {
    val context = LocalContext.current
    var audioUri by remember(surahNumber) { mutableStateOf(store.audioUri(surahNumber)) }
    var player by remember(surahNumber) { mutableStateOf<MediaPlayer?>(null) }
    var playing by remember(surahNumber) { mutableStateOf(false) }
    var error by remember(surahNumber) { mutableStateOf<String?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                store.setAudioUri(surahNumber, uri.toString())
                player?.release()
                player = null
                playing = false
                audioUri = uri.toString()
                error = null
            }.onFailure { error = "Ses dosyasına erişim izni alınamadı." }
        }
    }
    DisposableEffect(surahNumber) {
        onDispose { player?.release() }
    }
    Surface(
        color = if (dark) SoftGreen else Color(0xFFE3EEDF),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 5.dp),
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Headphones, null, tint = if (dark) Gold else SoftGreen)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text("Çevrimdışı sure sesi", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        color = if (dark) Color.White else Emerald)
                    Text(if (audioUri == null) "Kendi MP3 dosyanı seç" else "Yerel ses dosyası seçildi",
                        color = if (dark) Pale else SoftGreen, fontSize = 11.sp)
                }
                TextButton(onClick = { launcher.launch(arrayOf("audio/*")) }) {
                    Text(if (audioUri == null) "MP3 seç" else "Değiştir", color = if (dark) Gold else SoftGreen)
                }
                IconButton(onClick = {
                    if (audioUri == null) {
                        launcher.launch(arrayOf("audio/*"))
                    } else {
                        runCatching {
                            if (player == null) {
                                player = MediaPlayer.create(context, Uri.parse(audioUri))
                                    ?: throw IllegalStateException("Ses açılamadı")
                                player?.setOnCompletionListener { playing = false }
                            }
                            if (player?.isPlaying == true) {
                                player?.pause()
                                playing = false
                            } else {
                                player?.start()
                                playing = true
                            }
                            error = null
                        }.onFailure { error = "Ses dosyası açılamadı. Başka bir dosya seç." }
                    }
                }) {
                    Icon(if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                        if (playing) "Duraklat" else "Oynat", tint = if (dark) Gold else SoftGreen)
                }
            }
            if (error != null) Text(error ?: "", color = Color(0xFFFFBEA5), fontSize = 11.sp)
        }
    }
}

@Composable
private fun LearningScreen(dark: Boolean) {
    val fg = if (dark) Color.White else Emerald
    val letters = listOf(
        "ا" to "Elif", "ب" to "Be", "ت" to "Te", "ث" to "Se",
        "ج" to "Cim", "ح" to "Hâ", "خ" to "Hı", "د" to "Dal",
        "ذ" to "Zel", "ر" to "Ra", "ز" to "Ze", "س" to "Sin",
        "ش" to "Şın", "ص" to "Sad", "ض" to "Dad", "ط" to "Tı",
        "ظ" to "Zı", "ع" to "Ayn", "غ" to "Gayn", "ف" to "Fe",
        "ق" to "Kaf", "ك" to "Kef", "ل" to "Lam", "م" to "Mim",
        "ن" to "Nun", "ه" to "He", "و" to "Vav", "ي" to "Ye"
    )
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)) {
        Text("Elifba Akademisi", fontWeight = FontWeight.Bold, fontSize = 25.sp, color = fg)
        Text("Kur’an okumayı öğrenmeye harflerden başla.", fontSize = 13.sp,
            color = if (dark) Muted else SoftGreen)
        Surface(color = if (dark) CardGreen else Color.White, shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Arapça harfleri tanı", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = fg)
                letters.chunked(4).forEach { group ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        group.forEach { (letter, label) ->
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = if (dark) SoftGreen else Color(0xFFF0E8D3),
                                shape = RoundedCornerShape(13.dp)
                            ) {
                                Column(Modifier.padding(vertical = 11.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(letter, fontSize = 29.sp, color = if (dark) Gold else Emerald)
                                    Text(label, fontSize = 11.sp, color = fg)
                                }
                            }
                        }
                    }
                }
            }
        }
        Surface(color = if (dark) CardGreen else Color.White, shape = RoundedCornerShape(17.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text("Sıradaki eğitim modülü", fontWeight = FontWeight.Bold, color = fg)
                Text("Harekeler, kelime birleştirme, tecvid ve uzman tarafından doğrulanmış harf sesleri aşamalı eklenecek.",
                    color = if (dark) Muted else SoftGreen, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun WorshipScreen(dark: Boolean) {
    val fg = if (dark) Color.White else Emerald
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("İbadet ve Dini Bilgiler", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = fg)
        Text("Bu modüller kaynak doğrulama ve lisans denetiminden sonra eklenecek.",
            color = if (dark) Muted else SoftGreen, fontSize = 13.sp)
        listOf(
            Triple("Namaz Rehberi", "Abdest ve namaz adımları", Icons.Default.Explore),
            Triple("Namaz Vakitleri", "Şehir bazlı resmî veriler", Icons.Default.Schedule),
            Triple("Meal ve Tefsir", "Kaynaklı Türkçe açıklamalar", Icons.Default.Translate),
            Triple("Dualar ve Hadisler", "Kaynaklı dini kütüphane", Icons.Default.MenuBook),
            Triple("Kıble Pusulası", "Sensör destekli kıble", Icons.Default.Navigation)
        ).forEach { (title, text, icon) ->
            Surface(color = if (dark) CardGreen else Color.White, shape = RoundedCornerShape(16.dp)) {
                Row(Modifier.fillMaxWidth().padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, null, tint = if (dark) Gold else SoftGreen, modifier = Modifier.size(30.dp))
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        Text(title, color = fg, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text, color = if (dark) Muted else SoftGreen, fontSize = 12.sp)
                    }
                    Text("Planlandı", color = if (dark) Gold else SoftGreen, fontSize = 11.sp)
                }
            }
        }
        Surface(color = if (dark) SoftGreen else Color(0xFFE3EEDF),
            shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Kur’an metni kaynağı", fontWeight = FontWeight.Bold, color = fg)
                Text("Tanzil Quran Text © 2007–2021 Tanzil Project. CC BY 3.0. Metin değiştirilmeden kullanılır.",
                    fontSize = 12.sp, color = fg)
                TextButton(onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://tanzil.net/")))
                }) { Text("tanzil.net • Güncellemeleri görüntüle", color = if (dark) Gold else SoftGreen) }
            }
        }
    }
}
