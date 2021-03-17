package com.balvarezazocar.ciensonetosdeamor

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.balvarezazocar.ciensonetosdeamor.component.convertirANumerosRomanos
import com.balvarezazocar.ciensonetosdeamor.component.firstBaseLineToTop
import com.balvarezazocar.ciensonetosdeamor.ui.theme.CienSonetosDeAmorTheme
import com.google.accompanist.glide.GlideImage
import com.google.accompanist.imageloading.ImageLoadState
import com.google.accompanist.imageloading.MaterialLoadingImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat.startActivity

import android.content.Intent




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val listSize = 100
            // We save the scrolling position with this state
            val scrollState = rememberLazyListState()
            // We save the coroutine scope where our animated scroll will be executed
            val coroutineScope = rememberCoroutineScope()

            MyApp(content = {
                LayoutsCienSonetosDeAmorTheme(listSize,scrollState,coroutineScope)
            })
        }
    }
}
@Composable
private fun MyApp(content:@Composable ()-> Unit) {
    CienSonetosDeAmorTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = colors.background) {
            //                    PhotographerCardPreview()
            content()
        }
    }
}

@Composable
fun LayoutsCienSonetosDeAmorTheme(
    listSize: Int,
    scrollState: LazyListState,
    coroutineScope: CoroutineScope
){
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Cien Sonetos de Amor")
            },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            // 0 is the first item index
                            scrollState.animateScrollToItem(0)
                        }
                    }) {
                        Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null)
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            // listSize - 1 is the last index of the list
                            scrollState.animateScrollToItem(listSize -1)
                        }
                    }) {
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null)
                    }
                },
                backgroundColor = MaterialTheme.colors.onPrimary
            )
        }
    ) { innerPadding ->  BodyContent(
        Modifier
            .padding(innerPadding)
            .padding(2.dp),listSize,scrollState)
    }
}

@Composable
fun BodyContent(modifier: Modifier = Modifier, listSize: Int, scrollState: LazyListState){
    ListPoem(listSize,scrollState)
}
@Composable
fun ListPoem(listSize: Int, scrollState: LazyListState) {
    Column () {
        LazyColumn(
            state = scrollState,
            content = {
            items(listSize) {
                ItemPoem(modifier = Modifier, convertirANumerosRomanos(it + 1).toString())
            }
        })
    }
}

@Composable
fun ItemPoem(modifier: Modifier = Modifier,numeroSoneto: String){
    val context = LocalContext.current
    Row(
        modifier
            .padding(1.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colors.surface)
            .clickable(
                onClick = {
//                    val text = "Item Seleccionado "+numeroSoneto
//                    val duration = Toast.LENGTH_SHORT
                    val applicationContext = context
//                    val toast = Toast.makeText(applicationContext, text, duration)
//                    toast.show()
                    var inte  = Intent(applicationContext,MainActivity2()::class.java)
                    inte.putExtra("numeroSoneto",numeroSoneto)
                    applicationContext.startActivity(inte)


                }
            )
            .padding(10.dp)
            .fillMaxWidth()
    ){
        Surface(
            modifier.size(60.dp),
            shape = CircleShape,
            color = colors.onSurface.copy(alpha = 0.2f)
        ) {
            //My photo

//            GlideImage(
//                data = "https://picsum.photos/300/300",
//                contentDescription = "My content description",
//                requestBuilder = {
//                    val options = RequestOptions()
//                    options.centerCrop()
//                    apply(options)
//                },
//                modifier = Modifier.size(50.dp),
//                error = {
//                    Image(painter = painterResource(
//                        id = R.drawable.images),
//                        contentDescription = "Soneto " + numeroSoneto
//                    )
//                },
//                fadeIn = true
//            )

            GlideImage(
                data = "https://picsum.photos/300/300",
            ) {
                    imageState ->
                when (imageState) {
                    is ImageLoadState.Success -> {
                        MaterialLoadingImage(
                            result = imageState,
                            contentDescription = "My content description",
                            fadeInEnabled = true,
                            fadeInDurationMs = 600,
                        )
                    }
                    is ImageLoadState.Error -> {/* TODO */}
                    ImageLoadState.Loading -> {/* TODO */}
                    ImageLoadState.Empty -> { /* TODO */}
                }
            }
        }


        Column(
            modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically)
        ) {

            Text(text = "Soneto "+ numeroSoneto,fontWeight = FontWeight.Bold
            )
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                TextClicked()
            }
        }

    }
}

@Composable
fun TextClicked() {
    val count = remember{mutableStateOf(0)}
    Text(text = "Presionado ",
        modifier = Modifier
            .firstBaseLineToTop(12.dp),

        style = MaterialTheme.typography.body2
    )
}






