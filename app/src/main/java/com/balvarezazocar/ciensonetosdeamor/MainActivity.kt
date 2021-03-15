package com.balvarezazocar.ciensonetosdeamor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.balvarezazocar.ciensonetosdeamor.ui.theme.CienSonetosDeAmorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CienSonetosDeAmorTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = colors.background) {
//                    PhotographerCardPreview()
                    LayoutsCienSonetosDeAmorTheme()
                }
            }
        }
    }
}
@Composable
fun LayoutsCienSonetosDeAmorTheme(){
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Cien Sonetos de Amor")
            },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Info, contentDescription = null)
                    }
                },
                backgroundColor = MaterialTheme.colors.onPrimary
            )
        }
    ) {
            innerPadding ->  BodyContent(
        Modifier
            .padding(innerPadding)
            .padding(8.dp))
    }
}

@Composable
fun BodyContent(modifier: Modifier = Modifier){
        ListPoem()
}
@Composable
fun ListPoem (){
    val scrollState = rememberScrollState()
    Column(
        Modifier.verticalScroll(scrollState)
    ) {
        repeat(100){
            ItemPoem(modifier = Modifier, convertirANumerosRomanos(it+1).toString())
        }
    }
}
@Preview
@Composable
fun LayoutsCienSonetosDeAmorThemePreview(){
    CienSonetosDeAmorTheme() {
        LayoutsCienSonetosDeAmorTheme()
    }
}
@Composable
fun ItemPoem(modifier: Modifier = Modifier,numeroSoneto: String){
    Row(
        modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colors.surface)
            .clickable {

            }
            .padding(16.dp)
            .fillMaxWidth()

    ){
        Surface(
            modifier.size(50.dp),
            shape = CircleShape,
            color = colors.onSurface.copy(alpha = 0.2f)
        ) {
            //My photo
            Image(painter = painterResource(
                id = R.drawable.images),
                contentDescription = "Soneto " + numeroSoneto
            )
        }

        Column(
            modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically)
        ) {

            Text(text = "Soneto "+ numeroSoneto,fontWeight = FontWeight.Bold)
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = "", style = MaterialTheme.typography.body2)
            }
        }

    }
}
fun convertirANumerosRomanos(numero: Int): String? {
    var i: Int
    val miles: Int
    val centenas: Int
    val decenas: Int
    val unidades: Int
    var romano = ""
    //obtenemos cada cifra del número
    miles = numero / 1000
    centenas = numero / 100 % 10
    decenas = numero / 10 % 10
    unidades = numero % 10

    //millar
    i = 1
    while (i <= miles) {
        romano = romano + "M"
        i++
    }

    //centenas
    if (centenas == 9) {
        romano = romano + "CM"
    } else if (centenas >= 5) {
        romano = romano + "D"
        i = 6
        while (i <= centenas) {
            romano = romano + "C"
            i++
        }
    } else if (centenas == 4) {
        romano = romano + "CD"
    } else {
        i = 1
        while (i <= centenas) {
            romano = romano + "C"
            i++
        }
    }

    //decenas
    if (decenas == 9) {
        romano = romano + "XC"
    } else if (decenas >= 5) {
        romano = romano + "L"
        i = 6
        while (i <= decenas) {
            romano = romano + "X"
            i++
        }
    } else if (decenas == 4) {
        romano = romano + "XL"
    } else {
        i = 1
        while (i <= decenas) {
            romano = romano + "X"
            i++
        }
    }

    //unidades
    if (unidades == 9) {
        romano = romano + "IX"
    } else if (unidades >= 5) {
        romano = romano + "V"
        i = 6
        while (i <= unidades) {
            romano = romano + "I"
            i++
        }
    } else if (unidades == 4) {
        romano = romano + "IV"
    } else {
        i = 1
        while (i <= unidades) {
            romano = romano + "I"
            i++
        }
    }
    return romano
}