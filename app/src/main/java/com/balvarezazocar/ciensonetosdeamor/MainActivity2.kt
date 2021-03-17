package com.balvarezazocar.ciensonetosdeamor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.balvarezazocar.ciensonetosdeamor.ui.theme.CienSonetosDeAmorTheme

class MainActivity2() : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var numeroSoneto="I"
        super.onCreate(savedInstanceState)
        numeroSoneto = super.getIntent().getStringExtra("numeroSoneto").toString()

        setContent {
            CienSonetosDeAmorTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android",numeroSoneto = numeroSoneto)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String,numeroSoneto: String) {
    Text(text = "Hello $name!"+numeroSoneto)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CienSonetosDeAmorTheme {
        Greeting("Android","0")
    }
}