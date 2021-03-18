package com.balvarezazocar.ciensonetosdeamor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.balvarezazocar.ciensonetosdeamor.component.convertirANumerosRomanos
import com.balvarezazocar.ciensonetosdeamor.persistence.AdminSQLiteOpenHelper
import com.balvarezazocar.ciensonetosdeamor.ui.theme.CienSonetosDeAmorTheme
import com.google.accompanist.glide.GlideImage
import com.google.accompanist.imageloading.ImageLoadState
import com.google.accompanist.imageloading.MaterialLoadingImage

class MainActivity2() : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        var numeroSoneto = super.getIntent().getIntExtra("numeroSoneto",1)

        setContent {
            CienSonetosDeAmorTheme {
                // A surface container using the 'background' color from the theme
                MyApp(content = {
                    ItemSoneto {
                        Poem(name = "Soneto ", numeroSoneto = numeroSoneto)
                    }
                });
            }
        }
    }
}
@Composable
private fun MyApp(content:@Composable ()-> Unit) {
    CienSonetosDeAmorTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            //                    PhotographerCardPreview()
            content()
        }
    }
}

@Composable
fun ItemSoneto(Content: @Composable () -> Unit){
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Cien Sonetos de Amor")
            },
                actions = {
                    IconButton(onClick = {

                    }) {
                        //Content
                        Icon(Icons.Filled.Favorite, contentDescription = null)
                    }
                },
                backgroundColor = MaterialTheme.colors.onPrimary
            )
        }
    ) { innerPadding ->  BodyContent(
        Modifier
            .padding(innerPadding)
            .padding(2.dp),Content)
    }
}
@Composable
fun BodyContent(modifier: Modifier = Modifier,Content: @Composable () -> Unit){
    Content()
}

@Composable
private fun Poem(name: String,numeroSoneto: Int) {
    val context = LocalContext.current
    val admin = AdminSQLiteOpenHelper(context, "Carga", null, 1)
    val bd = admin.writableDatabase
    val fila = bd.rawQuery("select descripcion from sonetos where codigo=$numeroSoneto", null)
    //size activity screen
    val largeHeight=1
    val scrollState = rememberLazyListState()
    if (fila.moveToFirst()) {
        val pointerImage = painterResource(id = R.drawable.b)
        LazyColumn(content = { /*TODO*/
            items(largeHeight){

                Column(
                    modifier = Modifier
                        .padding(16.dp)
//                    .verticalScroll(ScrollState(1))
                ) {
                    val imageModifier = Modifier
                        //.height(120.dp)
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(shape = RoundedCornerShape(40.dp))


                    GlideImage(
                        data = "https://cdn.videotapenews.com/wp-content/uploads/2020/08/sm3c82b2tgg31.png",
                    ) { imageState ->
                        when (imageState) {
                            is ImageLoadState.Success -> {
                                MaterialLoadingImage(
                                    result = imageState,
                                    contentDescription = null,
                                    fadeInEnabled = true,
                                    fadeInDurationMs = 600,
                                )
                            }
                            is ImageLoadState.Error -> {/* TODO */
                            }
                            ImageLoadState.Loading -> {/* TODO */
                            }
                            ImageLoadState.Empty -> { /* TODO */
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Soneto ${convertirANumerosRomanos(numeroSoneto)}\n",
                        style = MaterialTheme.typography.h5,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colors.primary
                    )
                    var brk = "";
                    Text(
                        text = fila.getString(0),
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.primaryVariant,
                        textAlign = TextAlign.Justify,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Cursive
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Cien sonetos de amor (1959)",
                        style = MaterialTheme.typography.body2,
                        color = Color.LightGray
                    )
                }
            }
        })
    } else
        Text(text = "No existe el soneto $numeroSoneto en la base de datos")
    bd.close()
}