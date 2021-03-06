package com.balvarezazocar.ciensonetosdeamor

import android.content.ContentValues
import android.os.Bundle
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

import android.content.Intent
import com.balvarezazocar.ciensonetosdeamor.persistence.AdminSQLiteOpenHelper


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cargaDatos();

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
    ) { BodyContent( //Modifier.padding(innerPadding).padding(2.dp),
        listSize,scrollState)
    }
}

@Composable
fun BodyContent(listSize: Int, scrollState: LazyListState){
    ListPoem(listSize,scrollState)
}
@Composable
fun ListPoem(listSize: Int, scrollState: LazyListState) {
    Column  {
        LazyColumn(
            state = scrollState,
            content = {
            items(listSize) {
                ItemPoem(modifier = Modifier, it + 1)
            }
        })
    }
}

@Composable
fun ItemPoem(modifier: Modifier = Modifier,numeroSoneto: Int){
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
//                    val toast = Toast.makeText(applicationContext, text, duration)
//                    toast.show()
                    val inte  = Intent(context,MainActivity2()::class.java)
                    inte.putExtra("numeroSoneto",numeroSoneto)
                    context.startActivity(inte)


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

            Text(text = "Soneto "+ convertirANumerosRomanos(numeroSoneto).toString(),fontWeight = FontWeight.Bold
            )
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                TextClicked()
            }
        }

    }
}

@Composable
fun TextClicked() {
    Text(text = "Presionado ",
        modifier = Modifier
            .firstBaseLineToTop(12.dp),
        style = MaterialTheme.typography.body2
    )
}
   private fun cargaDatos(){
        val admin = AdminSQLiteOpenHelper(this,"Carga",null,1)
        val bd = admin.writableDatabase
//        val fila = bd.rawQuery("select * from sonetos", null)
//        if (!fila.moveToFirst()) {
        val registro = ContentValues()
        registro.put("codigo", 1)
        registro.put(
            "descripcion", "Matilde, nombre de planta o piedra o vino,\n" +
                    "de lo que nace de la tierra y dura,\n" +
                    "palabra en cuyo crecimiento amanece,\n" +
                    "en cuyo est??o estalla la luz de los limones.\n" +
                    "En ese nombre corren nav??os de madera\n" +
                    "rodeados por enjambres de fuego azul marino,\n" +
                    "y esas letras son el agua de un r??o\n" +
                    "que desemboca en mi coraz??n calcinado.\n" +
                    "Oh nombre descubierto bajo una enredadera\n" +
                    "como la puerta de un t??nel desconocido\n" +
                    "que comunica con la fragancia del mundo!\n" +
                    "Oh inv??deme con tu boca abrasadora,\n" +
                    "ind??game, si quieres, con tus ojos nocturnos,\n" +
                    "pero en tu nombre d??jame navegar y dormir."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 2)
        registro.put(
            "descripcion", "Amor, cu??ntos caminos hasta llegar a un beso,\n" +
                    "qu?? soledad errante hasta tu compa????a!\n" +
                    "Siguen los trenes solos rodando con la lluvia.\n" +
                    "En Taltal no amanece a??n la primavera.\n" +
                    "Pero t?? y yo, amor m??o, estamos juntos,\n" +
                    "juntos desde la ropa a las ra??ces,\n" +
                    "juntos de oto??o, de agua, de caderas,\n" +
                    "hasta ser s??lo t??, s??lo yo juntos.\n" +
                    "Pensar que cost?? tantas piedras que lleva el r??o,\n" +
                    "la desembocadura del agua de Boroa,\n" +
                    "pensar que separados por trenes y naciones\n" +
                    "t?? y yo ten??amos que simplemente amarnos,\n" +
                    "con todos confundidos, con hombres y mujeres,\n" +
                    "con la tierra que implanta y educa los claveles. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 3)
        registro.put(
            "descripcion", "Aspero amor, violeta coronada de espinas,\n" +
                    "matorral entre tantas pasiones erizado,\n" +
                    "lanza de los dolores, corola de la c??lera,\n" +
                    "por qu?? caminos y c??mo te dirigiste a mi alma?\n" +
                    "Por qu?? precipitaste tu fuego doloroso,\n" +
                    "de pronto, entre las hojas fr??as de mi camino?\n" +
                    "Qui??n te ense???? los pasos que hasta m?? te llevaron?\n" +
                    "Qu?? flor, qu?? piedra, qu?? humo mostraron mi morada?\n" +
                    "Lo cierto es que tembl?? la noche pavorosa,\n" +
                    "el alba llen?? todas las copas con su vino\n" +
                    "y el sol estableci?? su presencia celeste,\n" +
                    "mientras que el cruel amor me cercaba sin tregua\n" +
                    "hasta que lacer??ndome con espadas y espinas\n" +
                    "abri?? en mi coraz??n un camino quemante."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 4)
        registro.put(
            "descripcion", "Recordar??s aquella quebrada caprichosa\n" +
                    "a donde los aromas palpitantes treparon,\n" +
                    "de cuando en cuando un p??jaro vestido\n" +
                    "con agua y lentitud: traje de invierno.\n" +
                    "Recordar??s los dones de la tierra:\n" +
                    "irascible fragancia, barro de oro,\n" +
                    "hierbas del matorral, locas ra??ces,\n" +
                    "sort??legas espinas como espadas.\n" +
                    "Recordar??s el ramo que trajiste,\n" +
                    "ramo de sombra y agua con silencio,\n" +
                    "ramo como una piedra con espuma.\n" +
                    "Y aquella vez fue como nunca y siempre:\n" +
                    "vamos all?? donde no espera nada\n" +
                    "y hallamos todo lo que est?? esperando."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 5)
        registro.put(
            "descripcion", "No te toque la noche ni el aire ni la aurora,\n" +
                    "s??lo la tierra, la virtud de los racimos,\n" +
                    "las manzanas que crecen oyendo el agua pura,\n" +
                    "el barro y las resinas de tu pa??s fragante.\n" +
                    "Desde Quinchamal?? donde hicieron tus ojos\n" +
                    "hasta tus pies creados para m?? en la Frontera\n" +
                    "eres la greda oscura que conozco:\n" +
                    "en tus caderas toco de nuevo todo el trigo.\n" +
                    "Tal vez t?? no sab??as, araucana,\n" +
                    "que cuando antes de amarte me olvid?? de tus besos\n" +
                    "mi coraz??n qued?? recordando tu boca\n" +
                    "y fui como un herido por las calles\n" +
                    "hasta que comprend?? que hab??a encontrado,\n" +
                    "amor, mi territorio de besos y volcanes."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 6)
        registro.put(
            "descripcion", "En los bosques, perdido, cort?? una rama oscura\n" +
                    "y a los labios, sediento, levant?? su susurro:\n" +
                    "era tal vez la voz de la lluvia llorando,\n" +
                    "una campana rota o un coraz??n cortado.\n" +
                    "Algo que desde tan lejos me parec??a\n" +
                    "oculto gravemente, cubierto por la tierra,\n" +
                    "un grito ensordecido por inmensos oto??os,\n" +
                    "por la entreabierta y h??meda tiniebla de las hojas.\n" +
                    "Pero all??, despertando de los sue??os del bosque,\n" +
                    "la rama de avellano cant?? bajo mi boca\n" +
                    "y su errabundo olor trep?? por mi criterio\n" +
                    "como si me buscaran de pronto las ra??ces\n" +
                    "que abandon??, la tierra perdida con mi infancia,\n" +
                    "y me detuve herido por el aroma errante."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 7)
        registro.put(
            "descripcion", "Vendr??s conmigo dije -sin que nadie supiera\n" +
                    "d??nde y c??mo lat??a mi estado doloroso,\n" +
                    "y para m?? no hab??a clavel ni barcarola,\n" +
                    "nada sino una herida por el amor abierta.\n" +
                    "Repet??: ven conmigo, como si me muriera,\n" +
                    "y nadie vio en mi boca la luna que sangraba,\n" +
                    "nadie vio aquella sangre que sub??a al silencio.\n" +
                    "Oh amor ahora olvidemos la estrella con espinas!\n" +
                    "Por eso cuando o?? que tu voz repet??a\n" +
                    "Vendr??s conmigo -fue como si desataras\n" +
                    "dolor, amor, la furia del vino encarcelado\n" +
                    "que desde su bodega sumergida subiera\n" +
                    "y otra vez en mi boca sent?? un sabor de llama,\n" +
                    "de sangre y de claveles, de piedra y quemadura."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 8)
        registro.put(
            "descripcion", "Si no fuera porque tus ojos tienen color de luna,\n" +
                    "de d??a con arcilla, con trabajo, con fuego,\n" +
                    "y aprisionada tienes la agilidad del aire,\n" +
                    "si no fuera porque eres una semana de ??mbar,\n" +
                    "si no fuera porque eres el momento amarillo\n" +
                    "en que el oto??o sube por las enredaderas\n" +
                    "y eres a??n el pan que la luna fragante\n" +
                    "elabora paseando su harina por el cielo,\n" +
                    "oh, bienamada, yo no te amar??a!\n" +
                    "En tu abrazo yo abrazo lo que existe,\n" +
                    "la arena, el tiempo, el ??rbol de la lluvia,\n" +
                    "y todo vive para que yo viva:\n" +
                    "sin ir tan lejos puedo verlo todo:\n" +
                    "veo en tu vida todo lo viviente. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 9)
        registro.put(
            "descripcion", "Al golpe de la ola contra la piedra ind??cil\n" +
                    "la claridad estalla y establece su rosa\n" +
                    "y el c??rculo del mar se reduce a un racimo,\n" +
                    "a una sola gota de sal azul que cae.\n" +
                    "Oh radiante magnolia desatada en la espuma,\n" +
                    "magn??tica viajera cuya muerte florece\n" +
                    "y eternamente vuelve a ser y a no ser nada:\n" +
                    "sal rota, deslumbrante movimiento marino.\n" +
                    "Juntos t?? y yo, amor m??o, sellamos el silencio,\n" +
                    "mientras destruye el mar sus constantes estatuas\n" +
                    "y derrumba sus torres de arrebato y blancura,\n" +
                    "porque en la trama de estos tejidos invisibles\n" +
                    "del agua desbocada, de la incesante arena,\n" +
                    "sostenemos la ??nica y acosada ternura."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 10)
        registro.put(
            "descripcion", "Suave es la bella como si m??sica y madera,\n" +
                    "??gata, telas, trigo, duraznos transparentes,\n" +
                    "hubieran erigido la fugitiva estatua.\n" +
                    "Hacia la ola dirige su contraria frescura.\n" +
                    "El mar moja bru??idos pies copiados\n" +
                    "a la forma reci??n trabajada en la arena\n" +
                    "y es ahora su fuego femenino de rosa\n" +
                    "una sola burbuja que el sol y el mar combaten.\n" +
                    "Ay, que nada te toque sino la sal del fr??o!\n" +
                    "Que ni el amor destruya la primavera intacta.\n" +
                    "Hermosa, reverbero de la indeleble espuma,\n" +
                    "deja que tus caderas impongan en el agua\n" +
                    "una medida nueva de cisne o de nen??far\n" +
                    "y navegue tu estatua por el cristal eterno. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 11)
        registro.put(
            "descripcion", "Suave es la bella como si m??sica y madera,\n" +
                    "??gata, telas, trigo, duraznos transparentes,\n" +
                    "hubieran erigido la fugitiva estatua.\n" +
                    "Hacia la ola dirige su contraria frescura.\n" +
                    "El mar moja bru??idos pies copiados\n" +
                    "a la forma reci??n trabajada en la arena\n" +
                    "y es ahora su fuego femenino de rosa\n" +
                    "una sola burbuja que el sol y el mar combaten.\n" +
                    "Ay, que nada te toque sino la sal del fr??o!\n" +
                    "Que ni el amor destruya la primavera intacta.\n" +
                    "Hermosa, reverbero de la indeleble espuma,\n" +
                    "deja que tus caderas impongan en el agua\n" +
                    "una medida nueva de cisne o de nen??far\n" +
                    "y navegue tu estatua por el cristal eterno."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 12)
        registro.put(
            "descripcion", "Plena mujer, manzana carnal, luna caliente,\n" +
                    "espeso aroma de algas, lodo y luz machacados,\n" +
                    "qu?? oscura claridad se abre entre tus columnas?\n" +
                    "Qu?? antigua noche el hombre toca con sus sentidos?\n" +
                    "Ay, amar es un viaje con agua y con estrellas,\n" +
                    "con aire ahogado y bruscas tempestades de harina:\n" +
                    "amar es un combate de rel??mpagos\n" +
                    "y dos cuerpos por una sola miel derrotados.\n" +
                    "Beso a beso recorro tu peque??o infinito,\n" +
                    "tus m??rgenes, tus r??os, tus pueblos diminutos,\n" +
                    "y el fuego genital transformado en delicia\n" +
                    "corre por los delgados caminos de la sangre\n" +
                    "hasta precipitarse como un clavel nocturno,\n" +
                    "hasta ser y no ser sino un rayo en la sombra."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 13)
        registro.put(
            "descripcion", "La luz que de tus pies sube a tu cabellera,\n" +
                    "la turgencia que envuelve tu forma delicada,\n" +
                    "no es de n??car marino, nunca de plata fr??a:\n" +
                    "eres de pan, de pan amado por el fuego.\n" +
                    "La harina levant?? su granero contigo\n" +
                    "y creci?? incrementada por la edad venturosa,\n" +
                    "cuando los cereales duplicaron tu pecho\n" +
                    "mi amor era el carb??n trabajando en la tierra.\n" +
                    "Oh, pan tu frente, pan tus piernas, pan tu boca,\n" +
                    "pan que devoro y nace con luz cada ma??ana,\n" +
                    "bienamada, bandera de las panader??as,\n" +
                    "una lecci??n de sangre te dio el fuego,\n" +
                    "de la harina aprendiste a ser sagrada,\n" +
                    "y del pan el idioma y el aroma."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 14)
        registro.put(
            "descripcion", "Me falta tiempo para celebrar tus cabellos.\n" +
                    "Uno por uno debo contarlos y alabarlos:\n" +
                    "otros amantes quieren vivir con ciertos ojos,\n" +
                    "yo s??lo quiero ser tu peluquero.\n" +
                    "En Italia te bautizaron Medusa\n" +
                    "por la encrespada y alta luz de tu cabellera.\n" +
                    "Yo te llamo chascona m??a y enmara??ada:\n" +
                    "mi coraz??n conoce las puertas de tu pelo.\n" +
                    "Cuando t?? te extrav??es en tus propios cabellos,\n" +
                    "no me olvides, acu??rdate que te amo,\n" +
                    "no me dejes perdido ir sin tu cabellera\n" +
                    "por el mundo sombr??o de todos los caminos\n" +
                    "que s??lo tiene sombra, transitorios dolores,\n" +
                    "hasta que el sol sube a la torre de tu pelo. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 15)
        registro.put(
            "descripcion", "Desde hace mucho tiempo la tierra te conoce:\n" +
                    "eres compacta como el pan o la madera,\n" +
                    "eres cuerpo, racimo de segura substancia,\n" +
                    "tienes peso de acacia, de legumbre dorada.\n" +
                    "S?? que existes no s??lo porque tus ojos vuelan\n" +
                    "y dan luz a las cosas como ventana abierta,\n" +
                    "sino porque de barro te hicieron y cocieron\n" +
                    "en Chill??n, en un horno de adobe estupefacto.\n" +
                    "Los seres se derraman como aire o agua o fr??o\n" +
                    "y vagos son, se borran al contacto del tiempo,\n" +
                    "como si antes de muertos fueran desmenuzados.\n" +
                    "T?? caer??s conmigo como piedra en la tumba\n" +
                    "y as?? por nuestro amor que no fue consumido\n" +
                    "continuar?? viviendo con nosotros la tierra."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 16)
        registro.put(
            "descripcion", "Amo el trozo de tierra que t?? eres,\n" +
                    "porque de las praderas planetarias\n" +
                    "otra estrella no tengo. T?? repites\n" +
                    "la multiplicaci??n del universo.\n" +
                    "Tus anchos ojos son la luz que tengo\n" +
                    "de las constelaciones derrotadas,\n" +
                    "tu piel palpita como los caminos\n" +
                    "que recorre en la lluvia el meteoro.\n" +
                    "De tanta luna fueron para m?? tus caderas,\n" +
                    "de todo el sol tu boca profunda y su delicia,\n" +
                    "de tanta luz ardiente como miel en la sombra\n" +
                    "tu coraz??n quemado por largos rayos rojos,\n" +
                    "y as?? recorro el fuego de tu forma bes??ndote,\n" +
                    "peque??a y planetaria, paloma y geograf??a. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 17)
        registro.put(
            "descripcion", "No te amo como si fueras rosa de sal, topacio \n" +
                    "o flecha de claveles que propagan el fuego: \n" +
                    "te amo como se aman ciertas cosas oscuras, \n" +
                    "secretamente, entre la sombra y el alma. \n" +
                    "Te amo como la planta que no florece y lleva \n" +
                    "dentro de s??, escondida, la luz de aquellas flores, \n" +
                    "y gracias a tu amor vive oscuro en mi cuerpo \n" +
                    "el apretado aroma que ascendi?? de la tierra. \n" +
                    "Te amo sin saber c??mo, ni cu??ndo, ni de d??nde, \n" +
                    "te amo directamente sin problemas ni orgullo: \n" +
                    "as?? te amo porque no s?? amar de otra manera, \n" +
                    "sino as?? de este modo en que no soy ni eres, \n" +
                    "tan cerca que tu mano sobre mi pecho es m??a, \n" +
                    "tan cerca que se cierran tus ojos con mi sue??o."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 18)
        registro.put(
            "descripcion", "Por las monta??as vas como viene la brisa\n" +
                    "o la corriente brusca que baja de la nieve\n" +
                    "o bien tu cabellera palpitante confirma\n" +
                    "los altos ornamentos del sol en la espesura.\n" +
                    "Toda la luz del C??ucaso cae sobre tu cuerpo\n" +
                    "como en una peque??a vasija interminable\n" +
                    "en que el agua se cambia de vestido y de canto\n" +
                    "a cada movimiento transparente del r??o.\n" +
                    "Por los montes el viejo camino de guerreros\n" +
                    "y abajo enfurecida brilla como una espada\n" +
                    "el agua entre murallas de manos minerales,\n" +
                    "hasta que t?? recibes de los bosques de pronto\n" +
                    "el ramo o el rel??mpago de unas flores azules\n" +
                    "y la ins??lita flecha de un aroma salvaje."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 19)
        registro.put(
            "descripcion", "Mientras la magna espuma de Isla Negra,\n" +
                    "la sal azul, el sol en las olas te mojan,\n" +
                    "yo miro los trabajos de la avispa\n" +
                    "empe??ada en la miel de su universo.\n" +
                    "Va y viene equilibrando su recto y rubio vuelo\n" +
                    "como si deslizara de un alambre invisible\n" +
                    "la elegancia del baile, la sed de su cintura,\n" +
                    "y los asesinatos del aguij??n maligno.\n" +
                    "De petr??leo y naranja es su arco iris,\n" +
                    "busca como un avi??n entre la hierba,\n" +
                    "con un rumor de espiga vuela, desaparece,\n" +
                    "mientras que t?? sales del mar, desnuda,\n" +
                    "y regresas al mundo llena de sal y sol,\n" +
                    "reverberante estatua y espada de la arena."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 20)
        registro.put(
            "descripcion", "Mi fea, eres una casta??a despeinada,\n" +
                    "mi bella, eres hermosa como el viento,\n" +
                    "mi fea, de tu boca se pueden hacer dos,\n" +
                    "mi bella, son tus besos frescos como sand??as.\n" +
                    "Mi fea, d??nde est??n escondidos tus senos?\n" +
                    "Son m??nimos como dos copas de trigo.\n" +
                    "Me gustar??a verte dos lunas en el pecho:\n" +
                    "las gigantescas torres de tu soberan??a.\n" +
                    "Mi fea, el mar no tiene tus u??as en su tienda,\n" +
                    "mi bella, flor a flor, estrella por estrella,\n" +
                    "ola por ola, amor, he contado tu cuerpo:\n" +
                    "mi fea, te amo por tu cintura de oro,\n" +
                    "mi bella, te amo por una arruga en tu frente,\n" +
                    "amor, te amo por clara y por oscura. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 21)
        registro.put(
            "descripcion", "Oh que todo el amor propague en m?? su boca,\n" +
                    "que no sufra un momento m??s sin primavera,\n" +
                    "yo no vend?? sino mis manos al dolor,\n" +
                    "ahora, bienamada, d??jame con tus besos.\n" +
                    "Cubre la luz del mes abierto con tu aroma,\n" +
                    "cierra las puertas con tu cabellera,\n" +
                    "y en cuanto a m?? no olvides que si despierto y lloro\n" +
                    "es porque en sue??os s??lo soy un ni??o perdido\n" +
                    "que busca entre las hojas de la noche tus manos,\n" +
                    "el contacto del trigo que t?? me comunicas,\n" +
                    "un rapto centelleante de sombra y energ??a.\n" +
                    "Oh, bienamada, y nada m??s que sombra\n" +
                    "por donde me acompa??es en tus sue??os\n" +
                    "y me digas la hora de la luz."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 22)
        registro.put(
            "descripcion", "Cu??ntas veces, amor, te am?? sin verte y tal vez sin recuerdo,\n" +
                    "sin reconocer tu mirada, sin mirarte, centaura,\n" +
                    "en regiones contrarias, en un mediod??a quemante:\n" +
                    "eras s??lo el aroma de los cereales que amo.\n" +
                    "Tal vez te vi, te supuse al pasar levantando una copa\n" +
                    "en Angol, a la luz de la luna de Junio,\n" +
                    "o eras t?? la cintura de aquella guitarra\n" +
                    "que toqu?? en las tinieblas y son?? como el mar desmedido.\n" +
                    "Te am?? sin que yo lo supiera, y busqu?? tu memoria.\n" +
                    "En las casas vac??as entr?? con linterna a robar tu retrato.\n" +
                    "Pero yo ya sab??a c??mo era. De pronto\n" +
                    "mientras ibas conmigo te toqu?? y se detuvo mi vida:\n" +
                    "frente a mis ojos estabas, rein??ndome, y reinas.\n" +
                    "Como hoguera en los bosques el fuego es tu reino."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 23)
        registro.put(
            "descripcion", "Fue luz el fuego y pan la luna rencorosa,\n" +
                    "el jazm??n duplic?? su estrellado secreto,\n" +
                    "y del terrible amor las suaves manos puras\n" +
                    "dieron paz a mis ojos y sol a mis sentidos.\n" +
                    "Oh amor, c??mo de pronto, de las desgarraduras\n" +
                    "hiciste el edificio de la dulce firmeza,\n" +
                    "derrotaste las u??as malignas y celosas\n" +
                    "y hoy frente al mundo somos como una sola vida.\n" +
                    "As?? fue, as?? es y as?? ser?? hasta cuando,\n" +
                    "salvaje y dulce amor, bienamada Matilde,\n" +
                    "el tiempo nos se??ale la flor final del d??a.\n" +
                    "Sin ti, sin m??, sin luz ya no seremos:\n" +
                    "entonces m??s all?? del la tierra y la sombra\n" +
                    "el resplandor de nuestro amor seguir?? vivo."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 24)
        registro.put(
            "descripcion", "Amor, amor, las nubes a la torre del cielo\n" +
                    "subieron como triunfantes lavanderas,\n" +
                    "y todo ardi?? en azul, todo fue estrella:\n" +
                    "el mar, la nave, el d??a se desterraron juntos.\n" +
                    "Ven a ver los cerezos del agua constelada\n" +
                    "y la clave redonda del r??pido universo,\n" +
                    "ven a tocar el fuego del azul instant??neo,\n" +
                    "ven antes de que sus p??talos se consuman.\n" +
                    "No hay aqu?? sino luz, cantidades, racimos,\n" +
                    "espacio abierto por las virtudes del viento\n" +
                    "hasta entregar los ??ltimos secretos de la espuma.\n" +
                    "Y entre tantos azules celestes, sumergidos,\n" +
                    "se pierden nuestros ojos adivinando apenas\n" +
                    "los poderes del aire, las llaves submarinas."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 25)
        registro.put(
            "descripcion", "Antes de amarte, amor, nada era m??o:\n" +
                    "vacil?? por las calles y las cosas:\n" +
                    "nada contaba ni ten??a nombre:\n" +
                    "el mundo era del aire que esperaba.\n" +
                    "Yo conoc?? salones cenicientos,\n" +
                    "t??neles habitados por la luna,\n" +
                    "hangares crueles que se desped??an,\n" +
                    "preguntas que insist??an en la arena.\n" +
                    "Todo estaba vac??o, muerto y mudo,\n" +
                    "ca??do, abandonado y deca??do,\n" +
                    "todo era inalienablemente ajeno,\n" +
                    "todo era de los otros y de nadie,\n" +
                    "hasta que tu belleza y tu pobreza\n" +
                    "llenaron el oto??o de regalos."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 26)
        registro.put(
            "descripcion", "Ni el color de las dunas terribles en Iquique,\n" +
                    "ni el estuario del R??o Dulce de Guatemala,\n" +
                    "cambiaron tu perfil conquistado en el trigo,\n" +
                    "tu estilo de uva grande, tu boca de guitarra.\n" +
                    "Oh coraz??n, oh m??a desde todo el silencio,\n" +
                    "desde las cumbres donde rein?? la enredadera\n" +
                    "hasta las desoladas planicies del platino,\n" +
                    "en toda patria pura te repiti?? la tierra.\n" +
                    "Pero ni hura??a mano de montes minerales,\n" +
                    "ni nieve tibetana, ni piedra de Polonia,\n" +
                    "nada alter?? tu forma de cereal viajero,\n" +
                    "como si greda o trigo, guitarras o racimos\n" +
                    "de Chill??n defendieran en ti su territorio\n" +
                    "imponiendo el mandato de la luna silvestre. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 27)
        registro.put(
            "descripcion", "Desnuda eres tan simple como una de tus manos,\n" +
                    "lisa, terrestre, m??nima, redonda, transparente,\n" +
                    "tienes l??neas de luna, caminos de manzana,\n" +
                    "desnuda eres delgada como el trigo desnudo.\n" +
                    "Desnuda eres azul como la noche en Cuba,\n" +
                    "tienes enredaderas y estrellas en el pelo,\n" +
                    "desnuda eres enorme y amarilla\n" +
                    "como el verano en una iglesia de oro.\n" +
                    "Desnuda eres peque??a como una de tus u??as,\n" +
                    "curva, sutil, rosada hasta que nace el d??a\n" +
                    "y te metes en el subterr??neo del mundo\n" +
                    "como en un largo t??nel de trajes y trabajos:\n" +
                    "tu claridad se apaga, se viste, se deshoja\n" +
                    "y otra vez vuelve a ser una mano desnuda."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 28)
        registro.put(
            "descripcion", "Amor, de grano a grano, de planeta a planeta,\n" +
                    "la red del viento con sus pa??ses sombr??os,\n" +
                    "la guerra con sus zapatos de sangre,\n" +
                    "o bien el d??a y la noche de la espiga.\n" +
                    "Por donde fuimos, islas o puentes o banderas,\n" +
                    "violines del fugaz oto??o acribillado,\n" +
                    "repiti?? la alegr??a los labios de la copa,\n" +
                    "el dolor nos detuvo con su lecci??n de llanto.\n" +
                    "En todas las rep??blicas desarrollaba el viento\n" +
                    "su pabell??n impune, su glacial cabellera\n" +
                    "y luego regresaba la flor a sus trabajos.\n" +
                    "Pero en nosotros nunca se calcin?? el oto??o.\n" +
                    "Y en nuestra patria inm??vil germinaba y crec??a\n" +
                    "el amor con los derechos del roc??o. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 29)
        registro.put(
            "descripcion", "Vienes de la pobreza de las casas del Sur,\n" +
                    "de las regiones duras con fr??o y terremoto\n" +
                    "que cuando hasta sus dioses rodaron a la muerte\n" +
                    "nos dieron la lecci??n de la vida en la greda.\n" +
                    "Eres un caballito de greda negra, un beso\n" +
                    "de barro oscuro, amor, amapola de greda,\n" +
                    "paloma del crep??sculo que vol?? en los caminos,\n" +
                    "alcanc??a con l??grimas de nuestra pobre infancia.\n" +
                    "Muchacha, has conservado tu coraz??n de pobre,\n" +
                    "tus pies de pobre acostumbrados a las piedras,\n" +
                    "tu boca que no siempre tuvo pan o delicia.\n" +
                    "Eres del pobre Sur, de donde viene mi alma:\n" +
                    "en su cielo tu madre sigue lavando ropa\n" +
                    "con mi madre. Por eso te escog??, compa??era."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 30)
        registro.put(
            "descripcion", "Tienes del archipi??lago las hebras del alerce,\n" +
                    "la carne trabajada por los siglos del tiempo,\n" +
                    "venas que conocieron el mar de las maderas,\n" +
                    "sangre verde ca??da del cielo a la memoria.\n" +
                    "Nadie recoger?? mi coraz??n perdido\n" +
                    "entre tantas ra??ces, en la amarga frescura\n" +
                    "del sol multiplicado por la furia del agua,\n" +
                    "all?? vive la sombra que no viaja conmigo.\n" +
                    "Por eso t?? saliste del Sur como una isla\n" +
                    "poblada y coronada por plumas y maderas\n" +
                    "y yo sent?? el aroma de los bosques errantes,\n" +
                    "hall?? la miel oscura que conoc?? en la selva,\n" +
                    "y toqu?? en tus caderas los p??talos sombr??os\n" +
                    "que nacieron conmigo y construyeron mi alma. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 31)
        registro.put(
            "descripcion", "Con laureles del Sur y or??gano de Lota\n" +
                    "te corono, peque??a monarca de mis huesos,\n" +
                    "y no puede faltarte esa corona\n" +
                    "que elabora la tierra con b??lsamo y follaje.\n" +
                    "Eres, como el que te ama, de las provincias verdes:\n" +
                    "de all?? trajimos barro que nos corre en la sangre,\n" +
                    "en la ciudad andamos, como tantos, perdidos,\n" +
                    "temerosos de que cierren el mercado.\n" +
                    "Bienamada, tu sombra tiene olor a ciruela,\n" +
                    "tus ojos escondieron en el Sur sus ra??ces,\n" +
                    "tu coraz??n es una paloma de alcanc??a,\n" +
                    "tu cuerpo es liso como las piedras en el agua,\n" +
                    "tus besos son racimos con roc??o,\n" +
                    "y yo a tu lado vivo con la tierra."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 32)
        registro.put(
            "descripcion", "La casa en la ma??ana con la verdad revuelta\n" +
                    "de s??banas y plumas, el origen del d??a\n" +
                    "sin direcci??n, errante como una pobre barca,\n" +
                    "entre los horizontes del orden y del sue??o.\n" +
                    "Las cosas quieren arrastrar vestigios,\n" +
                    "adherencias sin rumbo, herencias fr??as,\n" +
                    "los papeles esconden vocales arrugadas\n" +
                    "y en la botella el vino quiere seguir su ayer.\n" +
                    "Ordenadora, pasas vibrando como abeja\n" +
                    "tocando las regiones perdidas por la sombra,\n" +
                    "conquistando la luz con tu blanca energ??a.\n" +
                    "Y se construye entonces la claridad de nuevo:\n" +
                    "obedecen las cosas al viento de la vida\n" +
                    "y el orden establece su pan y su paloma. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 33)
        registro.put(
            "descripcion", "Amor, ahora nos vamos a la casa\n" +
                    "donde la enredadera sube por las escalas:\n" +
                    "antes que llegues t?? lleg?? a tu dormitorio\n" +
                    "el verano desnudo con pies de madreselva.\n" +
                    "Nuestros besos errantes recorrieron el mundo:\n" +
                    "Armenia, espesa gota de miel desenterrada,\n" +
                    "Ceyl??n, paloma verde, y el Yang Ts?? separando\n" +
                    "con antigua paciencia los d??as de las noches.\n" +
                    "Y ahora, bienamada, por el mar crepitante\n" +
                    "volvemos como dos aves ciegas al muro,\n" +
                    "al nido de la lejana primavera,\n" +
                    "porque el amor no puede volar sin detenerse:\n" +
                    "al muro o a las piedras del mar van nuestras vidas,\n" +
                    "a nuestro territorio regresaron los besos."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 34)
        registro.put(
            "descripcion", "Eres hija del mar y prima del or??gano,\n" +
                    "nadadora, tu cuerpo es de agua pura,\n" +
                    "cocinera, tu sangre es tierra viva\n" +
                    "y tus costumbres son floridas y terrestres.\n" +
                    "Al agua van tus ojos y levantan las olas,\n" +
                    "a la tierra tus manos y saltan las semillas,\n" +
                    "en agua y tierra tienes propiedades profundas\n" +
                    "que en ti se juntan como las leyes de la greda.\n" +
                    "N??yade, corta tu cuerpo la turquesa\n" +
                    "y luego resurrecto florece en la cocina\n" +
                    "de tal modo que asumes cuanto existe\n" +
                    "y al fin duermes rodeada por mis brazos que apartan\n" +
                    "de la sormbra sombr??a, para que t?? descanses,\n" +
                    "legumbres, algas, hierbas: la espuma de tus sue??os. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 35)
        registro.put(
            "descripcion", "Tu mano fue volando de mis ojos al d??a.\n" +
                    "Entr?? la luz como un rosal abierto.\n" +
                    "Arena y cielo palpitaban como una\n" +
                    "culminante colmena cortada en las turquesas.\n" +
                    "Tu mano toc?? s??labas que tintineaban, copas,\n" +
                    "alcuzas con aceites amarillos,\n" +
                    "corolas, manantiales y, sobre todo, amor,\n" +
                    "amor: tu mano pura preserv?? las cucharas.\n" +
                    "La tarde fue. La noche desliz?? sigilosa\n" +
                    "sobre el sue??o del hombre su c??psula celeste.\n" +
                    "Un triste olor salvaje solt?? la madreselva.\n" +
                    "Y tu mano volvi?? de su vuelo volando\n" +
                    "a cerrar su plumaje que yo cre?? perdido\n" +
                    "sobre mis ojos devorados por la sombra."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 36)
        registro.put(
            "descripcion", "Coraz??n m??o, reina del apio y de la artesa:\n" +
                    "peque??a leoparda del hilo y la cebolla:\n" +
                    "me gusta ver brillar tu imperio diminuto,\n" +
                    "las armas de la cera, del vino, del aceite,\n" +
                    "del ajo, de la tierra por tus manos abierta\n" +
                    "de la sustancia azul encendida en tus manos,\n" +
                    "de la transmigraci??n del sue??o a la ensalada,\n" +
                    "del reptil enrollado en la manguera.\n" +
                    "T?? con tu podadora levantando el perfume,\n" +
                    "t??, con la direcci??n del jab??n en la espuma,\n" +
                    "t??, subiendo mis locas escalas y escaleras,\n" +
                    "t??, manejando el s??ntoma de mi caligraf??a\n" +
                    "y encontrando en la arena del cuaderno\n" +
                    "las letras extraviadas que buscaban tu boca."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 37)
        registro.put(
            "descripcion", "Oh amor, oh rayo loco y amenaza purp??rea,\n" +
                    "me visitas y subes por tu fresca escalera\n" +
                    "el castillo que el tiempo coron?? de neblinas,\n" +
                    "las p??lidas paredes del coraz??n cerrado.\n" +
                    "Nadie sabr?? que s??lo fue la delicadeza\n" +
                    "construyendo cristales duros como ciudades\n" +
                    "y que la sangre abr??a t??neles desdichados\n" +
                    "sin que su monarqu??a derribara el invierno.\n" +
                    "Por eso, amor, tu boca, tu piel, tu luz, tus penas,\n" +
                    "fueron el patrimonio de la vida, los dones\n" +
                    "sagrados de la lluvia, de la naturaleza\n" +
                    "que recibe y levanta la gravidez del grano,\n" +
                    "la tempestad secreta del vino en las bodegas,\n" +
                    "la llamarada del cereal en el suelo."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 38)
        registro.put(
            "descripcion", "Tu casa suena como un tren a mediod??a,\n" +
                    "zumban las avispas, cantan las cacerolas,\n" +
                    "la cascada enumera los hechos del roc??o,\n" +
                    "tu risa desarrolla su trino de palmera.\n" +
                    "La luz azul del muro conversa con la piedra,\n" +
                    "llega como un pastor silbando un telegrama\n" +
                    "y entre las dos higueras de voz verde\n" +
                    "Homero sube con zapatos sigilosos.\n" +
                    "S??lo aqu?? la ciudad no tiene voz ni llanto,\n" +
                    "ni sin fin, ni sonatas, ni labios, ni bocina\n" +
                    "sino un discurso de cascada y de leones,\n" +
                    "y t?? que subes, cantas, corres, caminas, bajas,\n" +
                    "plantas, coses, cocinas, clavas, escribes, vuelves,\n" +
                    "o te has ido y se sabe que comenz?? el invierno. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 39)
        registro.put(
            "descripcion", "Pero olvid?? que tus manos satisfac??an\n" +
                    "las ra??ces, regando rosas enmara??adas,\n" +
                    "hasta que florecieron tus huellas digitales\n" +
                    "en la plenaria paz de la naturaleza.\n" +
                    "El azad??n y el agua como animales tuyos\n" +
                    "te acompa??an, mordiendo y lamiendo la tierra,\n" +
                    "y es as?? c??mo, trabajando, desprendes\n" +
                    "fecundidad, fogosa frescura de claveles.\n" +
                    "Amor y honor de abejas pido para tus manos\n" +
                    "que en la tierra confunden su estirpe transparente,\n" +
                    "y hasta en mi coraz??n abren su agricultura,\n" +
                    "de tal modo que soy como piedra quemada\n" +
                    "que de pronto, contigo, canta, porque recibe\n" +
                    "el agua de los bosques por tu voz conducida."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 40)
        registro.put(
            "descripcion", "Era verde el silencio, mojada era la luz,\n" +
                    "temblaba el mes de Junio como una mariposa\n" +
                    "y en el austral dominio, desde el mar y las piedras,\n" +
                    "Matilde, atravesaste el mediod??a.\n" +
                    "Ibas cargada de flores ferruginosas,\n" +
                    "algas que el viento sur atormenta y olvida,\n" +
                    "a??n blancas, agrietadas por la sal devorante,\n" +
                    "tus manos levantaban las espigas de arena.\n" +
                    "Amo tus dones puros, tu piel de piedra intacta,\n" +
                    "tus u??as ofrecidas en el sol de tus dedos,\n" +
                    "tu boca derramada por toda la alegr??a,\n" +
                    "pero, para mi casa vecina del abismo,\n" +
                    "dame el atormentado sistema del silencio,\n" +
                    "el pabell??n del mar olvidado en la arena. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 41)
        registro.put(
            "descripcion", "Desdichas del mes de Enero cuando el indiferente\n" +
                    "mediod??a establece su ecuaci??n en el cielo,\n" +
                    "un oro duro como el vino de una copa colmada\n" +
                    "llena la tierra hasta sus l??mites azules.\n" +
                    "Desdichas de este tiempo parecidas a uvas\n" +
                    "peque??as que agruparon verde amargo,\n" +
                    "confusas, escondidas l??grimas de los d??as\n" +
                    "hasta que la intemperie public?? sus racimos.\n" +
                    "S??, g??rmenes, dolores, todo lo que palpita\n" +
                    "aterrado, a la luz crepitante de Enero,\n" +
                    "madurar??, arder?? como ardieron los frutos.\n" +
                    "Divididos ser??n los pesares: el alma\n" +
                    "dar?? un golpe de viento, y la morada\n" +
                    "quedar?? limpia con el pan fresco en la mesa."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 42)
        registro.put(
            "descripcion", "Radiantes d??as balanceados por el agua marina,\n" +
                    "concentrados como el interior de una piedra amarilla\n" +
                    "cuyo esplendor de miel no derrib?? el desorden:\n" +
                    "preserv?? su pureza de rect??ngulo.\n" +
                    "Crepita, s??, la hora como fuego o abejas\n" +
                    "y es verde la tarea de sumergirse en hojas,\n" +
                    "hasta que hacia la altura es el follaje\n" +
                    "un mundo centelleante que se apaga y susurra.\n" +
                    "Sed del fuego, abrasadora multitud del est??o\n" +
                    "que construye un Ed??n con unas cuantas hojas,\n" +
                    "porque la tierra de rostro oscuro no quiere sufrimientos\n" +
                    "sino frescura o fuego, agua o pan para todos,\n" +
                    "y nada deber??a dividir a los hombres\n" +
                    "sino el sol o la noche, la luna o las espigas."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 43)
        registro.put(
            "descripcion", "Un signo tuyo busco en todas las otras,\n" +
                    "en el brusco, ondulante r??o de las mujeres,\n" +
                    "trenzas, ojos apenas sumergidos,\n" +
                    "pies claros que resbalan navegando en la espuma.\n" +
                    "De pronto me parece que diviso tus u??as\n" +
                    "oblongas, fugitivas, sobrinas de un cerezo,\n" +
                    "y otra vez es tu pelo que pasa y me parece\n" +
                    "ver arder en el agua tu retrato de hoguera.\n" +
                    "Mir??, pero ninguna llevaba tu latido,\n" +
                    "tu luz, la greda oscura que trajiste del bosque,\n" +
                    "ninguna tuvo tus diminutas orejas.\n" +
                    "T?? eres total y breve, de todas eres una,\n" +
                    "y as?? contigo voy recorriendo y amando\n" +
                    "un ancho Mississippi de estuario femenino."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 44)
        registro.put(
            "descripcion", "Sabr??s que no te amo y que te amo\n" +
                    "puesto que de dos modos es la vida,\n" +
                    "la palabra es un ala del silencio,\n" +
                    "el fuego tiene una mitad de fr??o.\n" +
                    "Yo te amo para comenzar a amarte,\n" +
                    "para recomenzar el infinito\n" +
                    "y para no dejar de amarte nunca:\n" +
                    "por eso no te amo todav??a.\n" +
                    "Te amo y no te amo como si tuviera\n" +
                    "en mis manos las llaves de la dicha\n" +
                    "y un incierto destino desdichado.\n" +
                    "Mi amor tiene dos vidas para armarte.\n" +
                    "Por eso te amo cuando no te amo\n" +
                    "y por eso te amo cuando te amo. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 45)
        registro.put(
            "descripcion", "No est??s lejos de m?? un solo d??a, porque c??mo,\n" +
                    "porque, no s?? decirlo, es largo el d??a,\n" +
                    "y te estar?? esperando como en las estaciones\n" +
                    "cuando en alguna parte se durmieron los trenes.\n" +
                    "No te vayas por una hora porque entonces\n" +
                    "en esa hora se juntan las gotas del desvelo\n" +
                    "y tal vez todo el humo que anda buscando casa\n" +
                    "venga a matar a??n mi coraz??n perdido.\n" +
                    "Ay que no se quebrante tu silueta en la arena,\n" +
                    "ay que no vuelen tus p??rpados en la ausencia:\n" +
                    "no te vayas por un minuto, bienamada,\n" +
                    "porque en ese minuto te habr??s ido tan lejos\n" +
                    "que yo cruzar?? toda la tierra preguntando\n" +
                    "si volver??s o si me dejar??s muriendo."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 46)
        registro.put(
            "descripcion", "De las estrellas que admir??, mojadas\n" +
                    "por r??os y roc??os diferentes,\n" +
                    "yo no escog?? sino la que yo amaba\n" +
                    "y desde entonces duermo con la noche.\n" +
                    "De la ola, una ola y otra ola,\n" +
                    "verde mar, verde fr??o, rama verde,\n" +
                    "yo no escog?? sino una sola ola:\n" +
                    "la ola indivisible de tu cuerpo.\n" +
                    "Todas las gotas, todas las ra??ces,\n" +
                    "todos los hilos de la luz vinieron,\n" +
                    "me vinieron a ver tarde o temprano.\n" +
                    "Yo quise para m?? tu cabellera.\n" +
                    "Y de todos los dones de mi patria\n" +
                    "s??lo escog?? tu coraz??n salvaje. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 47)
        registro.put(
            "descripcion", "Detr??s de m?? en la rama quiero verte.\n" +
                    "Poco a poco te convertiste en fruto.\n" +
                    "No te cost?? subir de las ra??ces\n" +
                    "cantando con tu s??laba de savia.\n" +
                    "Y aqu?? estar??s primero en flor fragante,\n" +
                    "en la estatua de un beso convertida,\n" +
                    "hasta que sol y tierra, sangre y cielo,\n" +
                    "te otorguen la delicia y la dulzura.\n" +
                    "En la rama ver?? tu cabellera,\n" +
                    "tu signo madurando en el follaje,\n" +
                    "acercando las hojas a mi sed,\n" +
                    "y llenar?? mi boca tu substancia,\n" +
                    "el beso que subi?? desde la tierra\n" +
                    "con tu sangre de fruta enamorada."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 48)
        registro.put(
            "descripcion", "Dos amantes dichosos hacen un solo pan,\n" +
                    "una sola gota de luna en la hierba,\n" +
                    "dejan andando dos sombras que se re??nen,\n" +
                    "dejan un solo sol vac??o en una cama.\n" +
                    "De todas las verdades escogieron el d??a:\n" +
                    "no se ataron con hilos sino con un aroma,\n" +
                    "y no despedazaron la paz ni las palabras.\n" +
                    "La dicha es una torre transparente.\n" +
                    "El aire, el vino van con los dos amantes,\n" +
                    "la noche les regala sus p??talos dichosos,\n" +
                    "tienen derecho a todos los claveles.\n" +
                    "Dos amantes dichosos no tienen fin ni muerte,\n" +
                    "nacen y mueren muchas veces mientras viven,\n" +
                    "tienen la eternidad de la naturaleza."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 49)
        registro.put(
            "descripcion", "Es hoy: todo el ayer se fue cayendo\n" +
                    "entre dedos de luz y ojos de sue??o,\n" +
                    "ma??ana llegar?? con pasos verdes:\n" +
                    "nadie detiene el r??o de la aurora.\n" +
                    "Nadie detiene el r??o de tus manos,\n" +
                    "los ojos de tu sue??o, bienamada,\n" +
                    "eres temblor del tiempo que transcurre\n" +
                    "entre luz vertical y sol sombr??o,\n" +
                    "y el cielo cierra sobre ti sus alas\n" +
                    "llev??ndote y tray??ndote a mis brazos\n" +
                    "con puntual, misteriosa cortes??a:\n" +
                    "Por eso canto al d??a y a la luna,\n" +
                    "al mar, al tiempo, a todos los planetas,\n" +
                    "a tu voz diurna y a tu piel nocturna."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 50)
        registro.put(
            "descripcion", "Cotapos dice que tu risa cae\n" +
                    "como un halc??n desde una brusca torre\n" +
                    "y, es verdad, atraviesas el follaje del mundo\n" +
                    "con un solo rel??mpago de tu estirpe celeste\n" +
                    "que cae, y corta, y saltan las lenguas del roc??o,\n" +
                    "las aguas del diamante, la luz con sus abejas\n" +
                    "y all?? donde viv??a con su barba el silencio\n" +
                    "estallan las granadas del sol y las estrellas,\n" +
                    "se viene abajo el cielo con la noche sombr??a,\n" +
                    "arden a plena luna campanas y claveles,\n" +
                    "y corren los caballos de los talabarteros:\n" +
                    "porque t?? siendo tan peque??ita como eres\n" +
                    "dejas caer la risa desde tu meteoro\n" +
                    "electrizando el nombre de la naturaleza."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 51)
        registro.put(
            "descripcion", "Tu risa pertenece a un ??rbol entreabierto\n" +
                    "por un rayo, por un rel??mpago plateado\n" +
                    "que desde el cielo cae quebr??ndose en la copa,\n" +
                    "partiendo en dos el ??rbol con una sola espada.\n" +
                    "S??lo en las tierras altas del follaje con nieve\n" +
                    "nace una risa como la tuya, bienamante,\n" +
                    "es la risa del aire desatado en la altura,\n" +
                    "costumbres de araucaria, bienamada.\n" +
                    "Cordillerana m??a, chillaneja evidente,\n" +
                    "corta con los cuchillos de tu risa la sombra,\n" +
                    "la noche, la ma??ana, la miel del mediod??a,\n" +
                    "y que salten al cielo las aves del follaje\n" +
                    "cuando como una luz derrochadora\n" +
                    "rompe tu risa el ??rbol de la vida."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 52)
        registro.put(
            "descripcion", "Cantas y a sol y a cielo con tu canto\n" +
                    "tu voz desgrana el cereal del d??a,\n" +
                    "hablan los pinos con su lengua verde:\n" +
                    "trinan todas las aves del invierno.\n" +
                    "El mar llena sus s??tanos de pasos,\n" +
                    "de campanas, cadenas y gemidos,\n" +
                    "tintinean metales y utensilios,\n" +
                    "suenan las ruedas de la caravana.\n" +
                    "Pero s??lo tu voz escucho y sube\n" +
                    "tu voz con vuelo y precisi??n de flecha,\n" +
                    "baja tu voz con gravedad de lluvia,\n" +
                    "tu voz esparce alt??simas espadas,\n" +
                    "vuelve tu voz cargada de violetas\n" +
                    "y luego me acompa??a por el cielo. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 53)
        registro.put(
            "descripcion", "Aqu?? est?? el pan, el vino, la mesa, la morada:\n" +
                    "el menester del hombre, la mujer y la vida:\n" +
                    "a este sitio corr??a la paz vertiginosa,\n" +
                    "por esta luz ardi?? la com??n quemadura.\n" +
                    "Honor a tus dos manos que vuelan preparando\n" +
                    "los blancos resultados del canto y la cocina,\n" +
                    "salve! la integridad de tus pies corredores,\n" +
                    "viva! la bailarina que baila con la escoba.\n" +
                    "Aquellos bruscos r??os con aguas y amenazas,\n" +
                    "aquel atormentado pabell??n de la espuma,\n" +
                    "aquellos incendiaron panales y arrecifes\n" +
                    "son hoy este reposo de tu sangre en la m??a,\n" +
                    "este cauce estrellado y azul como la noche,\n" +
                    "esta simplicidad sin fin de la ternura."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 54)
        registro.put(
            "descripcion", "Espl??ndida raz??n, demonio claro\n" +
                    "del racimo absoluto, del recto mediod??a,\n" +
                    "aqu?? estamos al fin, sin soledad y solos,\n" +
                    "lejos del desvar??o de la ciudad salvaje.\n" +
                    "Cuando la l??nea pura rodea su paloma\n" +
                    "y el fuego condecora la paz con su alimento\n" +
                    "t?? y yo erigimos este celeste resultado!\n" +
                    "Raz??n y amor desnudos viven en esta casa.\n" +
                    "Sue??os furiosos, r??os de amarga certidumbre\n" +
                    "decisiones m??s duras que el sue??o de un martillo\n" +
                    "cayeron en la doble copa de los amantes.\n" +
                    "Hasta que en la balanza se elevaron, gemelos,\n" +
                    "la raz??n y el amor como dos alas.\n" +
                    "As?? se construy?? la transparencia. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 55)
        registro.put(
            "descripcion", "Espinas, vidrios rotos, enfermedades, llanto\n" +
                    "asedian d??a y noche la miel de los felices\n" +
                    "y no sirve la torre, ni el viaje, ni los muros:\n" +
                    "la desdicha atraviesa la paz de los dormidos,\n" +
                    "el dolor sube y baja y acerca sus cucharas\n" +
                    "y no hay hombre sin este movimiento,\n" +
                    "no hay natalicio, no hay techo ni cercado:\n" +
                    "hay que tomar en cuenta este atributo.\n" +
                    "Y en el amor no valen tampoco ojos cerrados,\n" +
                    "profundos lechos lejos del pestilente herido,\n" +
                    "o del que paso a paso conquista su bandera.\n" +
                    "Porque la vida pega como c??lera o r??o\n" +
                    "y abre un t??nel sangriento por donde nos vigilan\n" +
                    "los ojos de una inmensa familia de dolores."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 56)
        registro.put(
            "descripcion", "Acost??mbrate a ver detr??s de m?? la sombra\n" +
                    "y que tus manos salgan del rencor, transparentes,\n" +
                    "como si en la ma??ana del mar fueran creadas:\n" +
                    "la sal te dio, amor m??o, proporci??n cristalina.\n" +
                    "La envidia sufre, muere, se agota con mi canto.\n" +
                    "Uno a uno agonizan sus tristes capitanes.\n" +
                    "Yo digo amor, y el mundo se puebla de palomas.\n" +
                    "Cada s??laba m??a trae la primavera.\n" +
                    "Entonces t??, florida, coraz??n, bienamada,\n" +
                    "sobre mis ojos como los follajes del cielo\n" +
                    "eres, y yo te miro recostada en la tierra.\n" +
                    "Veo el sol trasmigrar racimos a tu rostro,\n" +
                    "mirando hacia la altura reconozco tus pasos.\n" +
                    "Matilde, bienamada, diadema, bienvenida!"
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 57)
        registro.put(
            "descripcion", "Mienten los que dijeron que yo perd?? la luna,\n" +
                    "los que profetizaron mi porvenir de arena,\n" +
                    "aseveraron tantas cosas con lenguas fr??as:\n" +
                    "quisieron prohibir la flor del universo.\n" +
                    "??Ya no cantar?? m??s el ??mbar insurgente\n" +
                    "de la sirena, no tiene sino pueblo.??\n" +
                    "Y masticaban sus incesantes papeles\n" +
                    "patrocinando para mi guitarra el olvido.\n" +
                    "Yo les lanc?? a los ojos las lanzas deslumbrantes\n" +
                    "de nuestro amor clavando tu coraz??n y el m??o,\n" +
                    "yo reclam?? el jazm??n que dejaban tus huellas,\n" +
                    "yo me perd?? de noche sin luz bajo tus p??rpados\n" +
                    "y cuando me envolvi?? la claridad\n" +
                    "nac?? de nuevo, due??o de mi propia tiniebla."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 58)
        registro.put(
            "descripcion", "Entre los espadones de fierro literario\n" +
                    "paso yo como un marinero remoto\n" +
                    "que no conoce las esquinas y que canta\n" +
                    "porque s??, porque c??mo si no fuera por eso.\n" +
                    "De los atormentados archipi??lagos traje\n" +
                    "mi acorde??n con borrascas, rachas de lluvia loca,\n" +
                    "y una costumbre lenta de cosas naturales:\n" +
                    "ellas determinaron mi coraz??n silvestre.\n" +
                    "As?? cuando los dientes de la literatura\n" +
                    "trataron de morder mis honrados talones,\n" +
                    "yo pas??, sin saber, cantando con el viento\n" +
                    "hacia los almacenes lluviosos de mi infancia,\n" +
                    "hacia los bosques fr??os del Sur indefinible,\n" +
                    "hacia donde mi vida se llen?? con tu aroma. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 59)
        registro.put(
            "descripcion", "(G.M.)\n" +
                    "Pobres poetas a quienes la vida y la muerte\n" +
                    "persiguieron con la misma tenacidad sombr??a\n" +
                    "y luego son cubiertos por impasible pompa\n" +
                    "entregados al rito y al diente funerario.\n" +
                    "Ellos -oscuros como piedrecitas- ahora\n" +
                    "detr??s de los caballos arrogantes, tendidos\n" +
                    "van, gobernados al fin por los intrusos,\n" +
                    "entre los edecanes, a dormir sin silencio.\n" +
                    "Antes y ya seguros de que est?? muerto el muerto\n" +
                    "hacen de las exequias un fest??n miserable\n" +
                    "con pavos, puercos y otros oradores.\n" +
                    "Acecharon su muerte y entonces la ofendieron:\n" +
                    "s??lo porque su boca est?? cerrada\n" +
                    "y ya no puede contestar su canto."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 60)
        registro.put(
            "descripcion", "A ti te hiere aquel que quiso hacerme da??o,\n" +
                    "y el golpe del veneno contra m?? dirigido\n" +
                    "como por una red pasa entre mis trabajos\n" +
                    "y en ti deja una mancha de ??xido y desvelo.\n" +
                    "No quiero ver, amor, en la luna florida\n" +
                    "de tu frente cruzar el odio que me acecha.\n" +
                    "No quiero que en tu sue??o deje el rencor ajeno\n" +
                    "olvidada su in??til corona de cuchillos.\n" +
                    "Donde voy van detr??s de m?? pasos amargos,\n" +
                    "donde r??o una mueca de horror copia mi cara,\n" +
                    "donde canto la envidia maldice, r??e y roe.\n" +
                    "Y es ??sa, amor, la sombra que la vida me ha dado:\n" +
                    "es un traje vac??o que me sigue cojeando\n" +
                    "como un espantap??jaros de sonrisa sangrienta."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 61)
        registro.put(
            "descripcion", "Trajo el amor su cola de dolores,\n" +
                    "su largo rayo est??tico de espinas\n" +
                    "y cerramos los ojos porque nada,\n" +
                    "porque ninguna herida nos separe.\n" +
                    "No es culpa de tus ojos este llanto:\n" +
                    "tus manos no clavaron esta espada:\n" +
                    "no buscaron tus pies este camino:\n" +
                    "lleg?? a tu coraz??n la miel sombr??a.\n" +
                    "Cuando el amor como una inmensa ola\n" +
                    "nos estrell?? contra la piedra dura,\n" +
                    "nos amas?? con una sola harina,\n" +
                    "cay?? el dolor sobre otro dulce rostro\n" +
                    "y as?? en la luz de la estaci??n abierta\n" +
                    "se consagr?? la primavera herida."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 62)
        registro.put(
            "descripcion", "Ay de m??, ay de nosotros, bienamada,\n" +
                    "s??lo quisimos s??lo amor, amarnos,\n" +
                    "y entre tantos dolores se dispuso\n" +
                    "s??lo nosotros dos ser malheridos.\n" +
                    "Quisimos el t?? y yo para nosotros,\n" +
                    "el t?? del beso, el yo del pan secreto,\n" +
                    "y as?? era todo, eternamente simple,\n" +
                    "hasta que el odio entr?? por la ventana.\n" +
                    "Odian los que no amaron nuestro amor,\n" +
                    "ni ning??n otro amor, desventurados\n" +
                    "como las sillas de un sal??n perdido,\n" +
                    "hasta que se enredaron en ceniza\n" +
                    "y el rostro amenazante que tuvieron\n" +
                    "se apag?? en el crep??sculo apagado."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 63)
        registro.put(
            "descripcion", "No s??lo por las tierras desiertas donde la piedra salina\n" +
                    "es como la ??nica rosa, la flor por el mar enterrada,\n" +
                    "anduve, sino por la orilla de r??os que cortan la nieve.\n" +
                    "Las amargas alturas de las cordilleras conocen mis pasos.\n" +
                    "Enmara??ada, silbante regi??n de mi patria salvaje,\n" +
                    "lianas cuyo beso mortal se encadena en la selva,\n" +
                    "lamento mojado del ave que surge lanzando sus escalofr??os,\n" +
                    "oh regi??n de perdidos dolores y llanto inclemente!\n" +
                    "No s??lo son m??os la piel venenosa del cobre\n" +
                    "o el salitre extendido como estatua yacente y nevada,\n" +
                    "sino la vi??a, el cerezo premiado por la primavera,\n" +
                    "son m??os, y yo pertenezco como ??tomo negro\n" +
                    "a las ??ridas tierras y a la luz del oto??o en las uvas,\n" +
                    "a esta patria met??lica elevada por torres de nieve."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 64)
        registro.put(
            "descripcion", "De tanto amor mi vida se ti???? de violeta\n" +
                    "y fui de rumbo en rumbo como las aves ciegas\n" +
                    "hasta llegar a tu ventana, amiga m??a:\n" +
                    "t?? sentiste un rumor de coraz??n quebrado\n" +
                    "y all?? de la tinieblas me levant?? a tu pecho,\n" +
                    "sin ser y sin saber fui a la torre del trigo,\n" +
                    "surg?? para vivir entre tus manos,\n" +
                    "me levant?? del mar a tu alegr??a.\n" +
                    "Nadie puede contar lo que te debo, es l??cido\n" +
                    "lo que te debo, amor, y es como una ra??z\n" +
                    "natal de Araucan??a, lo que te debo, amada.\n" +
                    "Es sin duda estrellado todo lo que te debo,\n" +
                    "lo que te debo es como el pozo de una zona silvestre\n" +
                    "en donde guard?? el tiempo rel??mpagos errantes. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 65)
        registro.put(
            "descripcion", "Matilde, d??nde est??s? Not??, hacia abajo,\n" +
                    "entre corbata y coraz??n, arriba,\n" +
                    "cierta melancol??a intercostal:\n" +
                    "era que t?? de pronto eras ausente.\n" +
                    "Me hizo falta la luz de tu energ??a\n" +
                    "y mir?? devorando la esperanza,\n" +
                    "mir?? el vac??o que es sin ti una casa,\n" +
                    "no quedan sino tr??gicas ventanas.\n" +
                    "De puro taciturno el techo escucha\n" +
                    "caer antiguas lluvias deshojadas,\n" +
                    "plumas, lo que la noche aprision??:\n" +
                    "y as?? te espero como casa sola\n" +
                    "y volver??s a verme y habitarme.\n" +
                    "De otro modo me duelen las ventanas."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 66)
        registro.put(
            "descripcion", "No te quiero sino porque te quiero\n" +
                    "y de quererte a no quererte llego\n" +
                    "y de esperarte cuando no te espero\n" +
                    "pasa mi coraz??n del fr??o al fuego.\n" +
                    "Te quiero s??lo porque a ti te quiero,\n" +
                    "te odio sin fin, y odi??ndote te ruego,\n" +
                    "y la medida de mi amor viajero\n" +
                    "es no verte y amarte como un ciego.\n" +
                    "Tal vez consumir?? la luz de Enero,\n" +
                    "su rayo cruel, mi coraz??n entero,\n" +
                    "rob??ndome la llave del sosiego.\n" +
                    "En esta historia s??lo yo me muero\n" +
                    "y morir?? de amor porque te quiero,\n" +
                    "porque te quiero, amor, a sangre y fuego."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 67)
        registro.put(
            "descripcion", "La gran lluvia del sur cae sobre Isla Negra\n" +
                    "como una sola gota transparente y pesada,\n" +
                    "el mar abre sus hojas fr??as y la recibe,\n" +
                    "la tierra aprende el h??medo destino de una copa.\n" +
                    "Alma m??a, dame en tus besos el agua\n" +
                    "salobre de estos mares, la miel del territorio,\n" +
                    "la fragancia mojada por mil labios del cielo,\n" +
                    "la paciencia sagrada del mar en el invierno.\n" +
                    "Algo nos llama, todas las puertas se abren solas,\n" +
                    "relata el agua un largo rumor a las ventanas,\n" +
                    "crece el cielo hacia abajo tocando las ra??ces,\n" +
                    "y as?? teje y desteje su red celeste el d??a\n" +
                    "con tiempo, sal, susurros, crecimientos, caminos,\n" +
                    "una mujer, un hombre, y el invierno en la tierra."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 68)
        registro.put(
            "descripcion", "(Mascar??n de Proa)\n" +
                    "La ni??a de madera no lleg?? caminando:\n" +
                    "all?? de pronto estuvo sentada en los ladrillos,\n" +
                    "viejas flores del mar cubr??an su cabeza,\n" +
                    "su mirada ten??a tristeza de ra??ces.\n" +
                    "All?? qued?? mirando nuestras vidas abiertas,\n" +
                    "el ir y ser y andar y volver por la tierra,\n" +
                    "el d??a desti??endo sus p??talos graduales.\n" +
                    "Vigilaba sin vernos la ni??a de madera.\n" +
                    "La ni??a coronada por las antiguas olas,\n" +
                    "all?? miraba con sus ojos derrotados:\n" +
                    "sab??a que vivimos en una red remota\n" +
                    "de tiempo y agua y olas y sonidos y lluvia,\n" +
                    "sin saber si existimos o si somos su sue??o.\n" +
                    "??sta es la historia de la muchacha de madera. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 69)
        registro.put(
            "descripcion", "Tal vez no ser es ser sin que t?? seas,\n" +
                    "sin que vayas cortando el mediod??a\n" +
                    "como una flor azul, sin que camines\n" +
                    "m??s tarde por la niebla y los ladrillos,\n" +
                    "sin esa luz que llevas en la mano\n" +
                    "que tal vez otros no ver??n dorada,\n" +
                    "que tal vez nadie supo que crec??a\n" +
                    "como el origen rojo de la rosa,\n" +
                    "sin que seas, en fin, sin que vinieras\n" +
                    "brusca, incitante, a conocer mi vida,\n" +
                    "r??faga de rosal, trigo del viento,\n" +
                    "y desde entonces soy porque t?? eres,\n" +
                    "y desde entonces eres, soy y somos,\n" +
                    "y por amor ser??, ser??s, seremos."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 70)
        registro.put(
            "descripcion", "Tal vez herido voy sin ir sangriento\n" +
                    "por uno de los rayos de tu vida\n" +
                    "y a media selva me detiene el agua:\n" +
                    "la lluvia que se cae con su cielo.\n" +
                    "Entonces toco el coraz??n llovido:\n" +
                    "all?? s?? que tus ojos penetraron\n" +
                    "por la regi??n extensa de mi duelo\n" +
                    "y un susurro de sombra surge solo:\n" +
                    "Qui??n es? Qui??n es? Pero no tuvo nombre\n" +
                    "la hoja o el agua oscura que palpita\n" +
                    "a media selva, sorda, en el camino,\n" +
                    "y as??, amor m??o, supe que fui herido\n" +
                    "y nadie hablaba all?? sino la sombra,\n" +
                    "la noche errante, el beso de la lluvia. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 71)
        registro.put(
            "descripcion", "De pena en pena cruza sus islas el amor\n" +
                    "y establece ra??ces que luego riega el llanto,\n" +
                    "y nadie puede, nadie puede evadir los pasos\n" +
                    "del coraz??n que corre callado y carnicero.\n" +
                    "As?? t?? y yo buscamos un hueco, otro planeta\n" +
                    "en donde no tocara la sal tu cabellera,\n" +
                    "en donde no crecieran dolores por mi culpa,\n" +
                    "en donde viva el pan sin agon??a.\n" +
                    "Un planeta enredado por distancia y follajes,\n" +
                    "un p??ramo, una piedra cruel y deshabitada,\n" +
                    "con nuestras propias manos hacer un nido duro,\n" +
                    "quer??amos, sin da??o ni herida ni palabra,\n" +
                    "y no fue as?? el amor, sino una ciudad loca\n" +
                    "donde la gente palidece en los balcones."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 72)
        registro.put(
            "descripcion", "Amor m??o, el invierno regresa a sus cuarteles,\n" +
                    "establece la tierra sus dones amarillos\n" +
                    "y pasamos la mano sobre un pa??s remoto,\n" +
                    "sobre la cabellera de la geograf??a.\n" +
                    "Irnos! Hoy! Adelante, ruedas, naves, campanas,\n" +
                    "aviones acerados por el diurno infinito\n" +
                    "hacia el olor nupcial del archipi??lago,\n" +
                    "por longitudinales harinas de usufructo!\n" +
                    "Vamos, lev??ntate, y endiad??mate y sube\n" +
                    "y baja y corre y trina con el aire y conmigo\n" +
                    "v??monos a los trenes de Arabia o Tocopilla,\n" +
                    "sin m??s que trasmigrar hacia el polen lejano,\n" +
                    "a pueblos lancinantes de harapos y gardenias\n" +
                    "gobernados por pobres monarcas sin zapatos. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 73)
        registro.put(
            "descripcion", "Recordar??s tal vez aquel hombre afilado\n" +
                    "que de la oscuridad sali?? como un cuchillo\n" +
                    "y antes de que supi??ramos, sab??a:\n" +
                    "vio el humo y decidi?? que ven??a del fuego.\n" +
                    "La p??lida mujer de cabellera negra\n" +
                    "surgi?? como un pescado del abismo\n" +
                    "y entre los dos alzaron en contra del amor\n" +
                    "una m??quina armada de dientes numerosos.\n" +
                    "Hombre y mujer talaron monta??as y jardines,\n" +
                    "bajaron a los r??os, treparon por los muros,\n" +
                    "subieron por los montes su atroz artiller??a.\n" +
                    "El amor supo entonces que se llamaba amor.\n" +
                    "Y cuando levant?? mis ojos a tu nombre\n" +
                    "tu coraz??n de pronto dispuso mi camino."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 74)
        registro.put(
            "descripcion", "El camino mojado por el agua de Agosto\n" +
                    "brilla como si fuera cortado en plena luna,\n" +
                    "en plena claridad de la manzana,\n" +
                    "en mitad de la fruta del oto??o.\n" +
                    "Neblina, espacio o cielo, la vaga red del d??a\n" +
                    "crece con fr??os sue??os, sonidos y pescados,\n" +
                    "el vapor de las islas combate la comarca,\n" +
                    "palpita el mar sobre la luz de Chile.\n" +
                    "Todo se reconcentra como el metal, se esconden\n" +
                    "las hojas, el invierno enmascara su estirpe\n" +
                    "y s??lo ciegos somos, sin cesar, solamente.\n" +
                    "Solamente sujetos al cauce sigiloso\n" +
                    "del movimiento, adi??s, del viaje, del camino:\n" +
                    "adi??s, caen las l??grimas de la naturaleza. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 75)
        registro.put(
            "descripcion", "??sta es la casa, el mar y la bandera.\n" +
                    "Err??bamos por otros largos muros.\n" +
                    "No hall??bamos la puerta ni el sonido\n" +
                    "desde la ausencia, como desde muertos.\n" +
                    "Y al fin la casa abre su silencio,\n" +
                    "entramos a pisar el abandono,\n" +
                    "las ratas muertas, el adi??s vac??o,\n" +
                    "el agua que llor?? en las ca??er??as.\n" +
                    "Llor??, llor?? la casa noche y d??a,\n" +
                    "gimi?? con las ara??as, entreabierta,\n" +
                    "se desgran?? desde sus ojos negros,\n" +
                    "y ahora de pronto la volvemos viva,\n" +
                    "la poblamos y no nos reconoce:\n" +
                    "tiene que florecer, y no se acuerda."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 76)
        registro.put(
            "descripcion", "Diego Rivera con la paciencia del oso\n" +
                    "buscaba la esmeralda del bosque en la pintura\n" +
                    "o el bermell??n, la flor s??bita de la sangre\n" +
                    "recog??a la luz del mundo en tu retrato.\n" +
                    "Pintaba el imperioso traje de tu nariz,\n" +
                    "la centella de tus pupilas desbocadas,\n" +
                    "tus u??as que alimentan la envidia de la luna,\n" +
                    "y en tu piel estival, tu boca de sand??a.\n" +
                    "Te puso dos cabezas de volc??n encendidas\n" +
                    "por fuego, por amor, por estirpe araucana,\n" +
                    "y sobre los dos rostros dorados de la greda\n" +
                    "te cubri?? con el casco de un incendio brav??o\n" +
                    "y all?? secretamente quedaron enredados\n" +
                    "mis ojos en su torre total: tu cabellera. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 77)
        registro.put(
            "descripcion", "Hoy es hoy con el peso de todo el tiempo ido,\n" +
                    "con las alas de todo lo que ser?? ma??ana,\n" +
                    "hoy es el Sur del mar, la vieja edad del agua\n" +
                    "y la composici??n de un nuevo d??a.\n" +
                    "A tu boca elevada a la luz o a la luna\n" +
                    "se agregaron los p??talos de un d??a consumido,\n" +
                    "y ayer viene trotando por su calle sombr??a\n" +
                    "para que recordemos su rostro que se ha muerto.\n" +
                    "Hoy, ayer y ma??ana se comen caminando,\n" +
                    "consumimos un d??a como una vaca ardiente,\n" +
                    "nuestro ganado espera con sus d??as contados,\n" +
                    "pero en tu coraz??n el tiempo ech?? su harina,\n" +
                    "mi amor construy?? un horno con barro de Temuco:\n" +
                    "t?? eres el pan de cada d??a para mi alma."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 78)
        registro.put(
            "descripcion", "No tengo nunca m??s, no tengo siempre. En la arena\n" +
                    "la victoria dej?? sus pies perdidos.\n" +
                    "Soy un pobre hombre dispuesto a amar a sus semejantes.\n" +
                    "No s?? qui??n eres. Te amo. No doy, no vendo espinas.\n" +
                    "Alguien sabr?? tal vez que no tej?? coronas\n" +
                    "sangrientas, que combat?? la burla,\n" +
                    "y que en verdad llen?? la pleamar de mi alma.\n" +
                    "Yo pagu?? la vileza con palomas.\n" +
                    "Yo no tengo jam??s porque distinto\n" +
                    "fui, soy, ser??. Y en nombre\n" +
                    "de mi cambiante amor proclamo la pureza.\n" +
                    "La muerte es s??lo piedra del olvido.\n" +
                    "Te amo, beso en tu boca la alegr??a.\n" +
                    "Traigamos le??a. Haremos fuego en la monta??a."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 79)
        registro.put(
            "descripcion", "De noche, amada, amarra tu coraz??n al m??o\n" +
                    "y que ellos en el sue??o derroten las tinieblas\n" +
                    "como un doble tambor combatiendo en el bosque\n" +
                    "contra el espeso muro de las hojas mojadas.\n" +
                    "Nocturna traves??a, brasa negra del sue??o\n" +
                    "interceptando el hilo de las uvas terrestres\n" +
                    "con la puntualidad de un tren descabellado\n" +
                    "que sombra y piedras fr??as sin cesar arrastrara.\n" +
                    "Por eso, amor, am??rrame el movimiento puro,\n" +
                    "a la tenacidad que en tu pecho golpea\n" +
                    "con las alas de un cisne sumergido,\n" +
                    "para que a las preguntas estrelladas del cielo\n" +
                    "responda nuestro sue??o con una sola llave,\n" +
                    "con una sola puerta cerrada por la sombra."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 80)
        registro.put(
            "descripcion", "De viajes y dolores yo regres??, amor m??o,\n" +
                    "a tu voz, a tu mano volando en la guitarra,\n" +
                    "al fuego que interrumpe con besos el oto??o,\n" +
                    "a la circulaci??n de la noche en el cielo.\n" +
                    "Para todos los hombres pido pan y reinado,\n" +
                    "pido tierra para el labrador sin ventura,\n" +
                    "que nadie espere tregua de mi sangre o mi canto.\n" +
                    "Pero a tu amor no puedo renunciar sin morirme.\n" +
                    "Por eso toca el vals de la serena luna,\n" +
                    "la barcarola en el agua de la guitarra\n" +
                    "hasta que se doblegue mi cabeza so??ando:\n" +
                    "que todos los desvelos de mi vida tejieron\n" +
                    "esta enramada en donde tu mano vive y vuela\n" +
                    "custodiando la noche del viajero dormido."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 81)
        registro.put(
            "descripcion", "Ya eres m??a. Reposa con tu sue??o en mi sue??o.\n" +
                    "Amor, dolor, trabajos, deben dormir ahora.\n" +
                    "Gira la noche sobre sus invisibles ruedas\n" +
                    "y junto a m?? eres pura como el ??mbar dormido.\n" +
                    "Ninguna m??s, amor, dormir?? con mis sue??os.\n" +
                    "Ir??s, iremos juntos por las aguas del tiempo.\n" +
                    "Ninguna viajar?? por la sombra conmigo,\n" +
                    "s??lo t??, siempreviva, siempre sol, siempre luna.\n" +
                    "Ya tus manos abrieron los pu??os delicados\n" +
                    "y dejaron caer suaves signos sin rumbo,\n" +
                    "tus ojos se cerraron como dos alas grises,\n" +
                    "mientras yo sigo el agua que llevas y me lleva:\n" +
                    "la noche, el mundo, el viento devanan su destino,\n" +
                    "y ya no soy sin ti sino s??lo tu sue??o."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 82)
        registro.put(
            "descripcion", "Amor m??o, al cerrar esta puerta nocturna\n" +
                    "te pido, amor, un viaje por oscuro recinto:\n" +
                    "cierra tus sue??os, entra con tu cielo en mis ojos,\n" +
                    "exti??ndete en mi sangre como en un ancho r??o.\n" +
                    "Adi??s, adi??s, cruel claridad que fue cayendo\n" +
                    "en el saco de cada d??a del pasado,\n" +
                    "adi??s a cada rayo de reloj o naranja,\n" +
                    "salud oh sombra, intermitente compa??era!\n" +
                    "En esta nave o agua o muerte o nueva vida,\n" +
                    "una vez m??s unidos, dormidos, resurrectos,\n" +
                    "somos el matrimonio de la noche en la sangre.\n" +
                    "No s?? qui??n vive o muere, qui??n reposa o despierta,\n" +
                    "pero es tu coraz??n el que reparte\n" +
                    "en mi pecho los dones de la aurora. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 83)
        registro.put(
            "descripcion", "Es bueno, amor, sentirte cerca de m?? en la noche,\n" +
                    "invisible en tu sue??o, seriamente nocturna,\n" +
                    "mientras yo desenredo mis preocupaciones\n" +
                    "como si fueran redes confundidas.\n" +
                    "Ausente, por los sue??os tu coraz??n navega,\n" +
                    "pero tu cuerpo as?? abandonado respira\n" +
                    "busc??ndome sin verme, completando mi sue??o\n" +
                    "como una planta que se duplica en la sombra.\n" +
                    "Erguida, ser??s otra que vivir?? ma??ana,\n" +
                    "pero de las fronteras perdidas en la noche,\n" +
                    "de este ser y no ser en que nos encontramos\n" +
                    "algo queda acerc??ndonos en la luz de la vida\n" +
                    "como si el sello de la sombra se??alara\n" +
                    "con fuego sus secretas criaturas."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 84)
        registro.put(
            "descripcion", "Una vez m??s, amor, la red del d??a extingue\n" +
                    "trabajos, ruedas, fuegos, estertores, adioses,\n" +
                    "y a la noche entregamos el trigo vacilante\n" +
                    "que el mediod??a obtuvo de la luz y la tierra.\n" +
                    "S??lo la luna en medio de su p??gina pura\n" +
                    "sostiene las columnas del estuario del cielo,\n" +
                    "la habitaci??n adopta la lentitud del oro\n" +
                    "y van y van tus manos preparando la noche.\n" +
                    "Oh amor, oh noche, oh c??pula cerrada por un r??o\n" +
                    "de impenetrables aguas en la sombra del cielo\n" +
                    "que destaca y sumerge sus uvas tempestuosas,\n" +
                    "hasta que s??lo somos un solo espacio oscuro,\n" +
                    "una copa en que cae la ceniza celeste,\n" +
                    "una gota en el pulso de un lento y largo r??o."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 85)
        registro.put(
            "descripcion", "Del mar hacia las calles corre la vaga niebla\n" +
                    "como el vapor de un buey enterrado en el fr??o,\n" +
                    "y largas lenguas de agua se acumulan cubriendo\n" +
                    "el mes que a nuestras vidas prometi?? ser celeste.\n" +
                    "Adelantado oto??o, panal silbante de hojas,\n" +
                    "cuando sobre los pueblos palpita tu estandarte\n" +
                    "cantan mujeres locas despidiendo a los r??os,\n" +
                    "los caballos relinchan hacia la Patagonia.\n" +
                    "Hay una enredadera vespertina en tu rostro\n" +
                    "que crece silenciosa por el amor llevada\n" +
                    "hasta las herraduras crepitantes del cielo.\n" +
                    "Me inclino sobre el fuego de tu cuerpo nocturno\n" +
                    "y no s??lo tus senos amo sino el oto??o\n" +
                    "que esparce por la niebla su sangre ultramarina."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 86)
        registro.put(
            "descripcion", "Oh Cruz del Sur, oh tr??bol de f??sforo fragante,\n" +
                    "con cuatro besos hoy penetr?? tu hermosura\n" +
                    "y atraves?? la sombra y mi sombrero:\n" +
                    "la luna iba redonda por el fr??o.\n" +
                    "Entonces con mi amor, con mi amada, oh diamantes\n" +
                    "de escarcha azul, serenidad del cielo,\n" +
                    "espejo, apareciste y se llen?? la noche\n" +
                    "con tus cuatro bodegas temblorosas de vino.\n" +
                    "Oh palpitante plata de pez pulido y puro,\n" +
                    "cruz verde, perejil de la sombra radiante,\n" +
                    "luci??rnaga a la unidad del cielo condenada,\n" +
                    "descansa en m??, cerremos tus ojos y los m??os.\n" +
                    "Por un minuto duerme con la noche del hombre.\n" +
                    "Enciende en m?? tus cuatro n??meros constelados. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 87)
        registro.put(
            "descripcion", "Las tres aves del mar, tres rayos, tres tijeras\n" +
                    "cruzaron por el cielo fr??o hacia Antofagasta,\n" +
                    "por eso qued?? el aire tembloroso,\n" +
                    "todo tembl?? como bandera herida.\n" +
                    "Soledad, dame el signo de tu incesante origen,\n" +
                    "el apenas camino de los p??jaros crueles,\n" +
                    "y la palpitaci??n que sin duda precede\n" +
                    "a la miel, a la m??sica, al mar, al nacimiento.\n" +
                    "(Soledad sostenida por un constante rostro\n" +
                    "como una grave flor sin cesar extendida\n" +
                    "hasta abarcar la pura muchedumbre del cielo.)\n" +
                    "Volaban alas fr??as del mar, del Archipi??lago,\n" +
                    "hacia la arena del Noroeste de Chile.\n" +
                    "Y la noche cerr?? su celeste cerrojo."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 88)
        registro.put(
            "descripcion", "El mes de Marzo vuelve con su luz escondida\n" +
                    "y se deslizan peces inmensos por el cielo,\n" +
                    "vago vapor terrestre progresa sigiloso,\n" +
                    "una por una caen al silencio las cosas.\n" +
                    "Por suerte en esta crisis de atm??sfera errabunda\n" +
                    "reuniste las vidas del mar con las del fuego,\n" +
                    "el movimiento gris de la nave de invierno,\n" +
                    "la forma que el amor imprimi?? a la guitarra.\n" +
                    "Oh amor, rosa mojada por sirenas y espumas,\n" +
                    "fuego que baila y sube la invisible escalera\n" +
                    "y despierta en el t??nel del insomnio a la sangre\n" +
                    "para que se consuman las olas en el cielo,\n" +
                    "olvide el mar sus bienes y leones\n" +
                    "y caiga el mundo adentro de las redes oscuras. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 89)
        registro.put(
            "descripcion", "Cuando yo muera quiero tus manos en mis ojos:\n" +
                    "quiero la luz y el trigo de tus manos amadas\n" +
                    "pasar una vez m??s sobre m?? su frescura:\n" +
                    "sentir la suavidad que cambi?? mi destino.\n" +
                    "Quiero que vivas mientras yo, dormido, te espero,\n" +
                    "quiero que tus o??dos sigan oyendo el viento,\n" +
                    "que huelas el aroma del mar que amamos juntos\n" +
                    "y que sigas pisando la arena que pisamos.\n" +
                    "Quiero que lo que amo siga vivo\n" +
                    "y a ti te am?? y cant?? sobre todas las cosas,\n" +
                    "por eso sigue t?? floreciendo, florida,\n" +
                    "para que alcances todo lo que mi amor te ordena,\n" +
                    "para que se pasee mi sombra por tu pelo,\n" +
                    "para que as?? conozcan la raz??n de mi canto."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 90)
        registro.put(
            "descripcion", "Pens?? morir, sent?? de cerca el fr??o,\n" +
                    "y de cuanto viv?? s??lo a ti te dejaba:\n" +
                    "tu boca eran mi d??a y mi noche terrestres\n" +
                    "y tu piel la rep??blica fundada por mis besos.\n" +
                    "En ese instante se terminaron los libros,\n" +
                    "la amistad, los tesoros sin tregua acumulados,\n" +
                    "la casa transparente que t?? y yo construimos:\n" +
                    "todo dej?? de ser, menos tus ojos.\n" +
                    "Porque el amor, mientras la vida nos acosa,\n" +
                    "es simplemente una ola alta sobre las olas,\n" +
                    "pero ay cuando la muerte viene a tocar a la puerta\n" +
                    "hay s??lo tu mirada para tanto vac??o,\n" +
                    "s??lo tu claridad para no seguir siendo,\n" +
                    "s??lo tu amor para cerrar la sombra. \n"
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 91)
        registro.put(
            "descripcion", "La edad nos cubre como la llovizna,\n" +
                    "interminable y ??rido es el tiempo,\n" +
                    "una pluma de sal toca tu rostro,\n" +
                    "una gotera carcomi?? mi traje:\n" +
                    "el tiempo no distingue entre mis manos\n" +
                    "o un vuelo de naranjas en las tuyas:\n" +
                    "pica con nieve y azad??n la vida:\n" +
                    "la vida tuya que es la vida m??a.\n" +
                    "La vida m??a que te di se llena\n" +
                    "de a??os, como el volumen de un racimo.\n" +
                    "Regresar??n las uvas a la tierra.\n" +
                    "Y a??n all?? abajo el tiempo sigue siendo,\n" +
                    "esperando, lloviendo sobre el polvo,\n" +
                    "??vido de borrar hasta la ausencia."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 92)
        registro.put(
            "descripcion", "Amor m??o, si muero y t?? no mueres,\n" +
                    "no demos al dolor m??s territorio:\n" +
                    "amor m??o, si mueres y no muero,\n" +
                    "no hay extensi??n como la que vivimos.\n" +
                    "Polvo en el trigo, arena en las arenas\n" +
                    "el tiempo, el agua errante, el viento vago\n" +
                    "nos llev?? como grano navegante.\n" +
                    "Pudimos no encontrarnos en el tiempo.\n" +
                    "Esta pradera en que nos encontramos,\n" +
                    "oh peque??o infinito! devolvemos.\n" +
                    "Pero este amor, amor, no ha terminado,\n" +
                    "y as?? como no tuvo nacimiento\n" +
                    "no tiene muerte, es como un largo r??o,\n" +
                    "s??lo cambia de tierras y de labios. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 93)
        registro.put(
            "descripcion", "Si alguna vez tu pecho se detiene,\n" +
                    "si algo deja de andar ardiendo por tus venas,\n" +
                    "si tu voz en tu boca se va sin ser palabra,\n" +
                    "si tus manos se olvidan de volar y se duermen,\n" +
                    "Matilde, amor, deja tus labios entreabiertos\n" +
                    "porque ese ??ltimo beso debe durar conmigo,\n" +
                    "debe quedar inm??vil para siempre en tu boca\n" +
                    "para que as?? tambi??n me acompa??e en mi muerte.\n" +
                    "Me morir?? besando tu loca boca fr??a,\n" +
                    "abrazando el racimo perdido de tu cuerpo,\n" +
                    "y buscando la luz de tus ojos cerrados.\n" +
                    "Y as?? cuando la tierra reciba nuestro abrazo\n" +
                    "iremos confundidos en una sola muerte\n" +
                    "a vivir para siempre la eternidad de un beso."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 94)
        registro.put(
            "descripcion", "Si muero sobrev??veme con tanta fuerza pura\n" +
                    "que despiertes la furia del p??lido y del fr??o,\n" +
                    "de sur a sur levanta tus ojos indelebles,\n" +
                    "de sol a sol que suene tu boca de guitarra.\n" +
                    "No quiero que vacilen tu risa ni tus pasos,\n" +
                    "no quiero que se muera mi herencia de alegr??a,\n" +
                    "no llames a mi pecho, estoy ausente.\n" +
                    "Vive en mi ausencia como en una casa.\n" +
                    "Es una casa tan grande la ausencia\n" +
                    "que pasar??s en ella a trav??s de los muros\n" +
                    "y colgar??s los cuadros en el aire.\n" +
                    "Es una casa tan transparente la ausencia\n" +
                    "que yo sin vida te ver?? vivir\n" +
                    "y si sufres, mi amor, me morir?? otra vez. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 95)
        registro.put(
            "descripcion", "Qui??nes se amaron como nosotros? Busquemos\n" +
                    "las antiguas cenizas del coraz??n quemado\n" +
                    "y all?? que caigan uno por uno nuestros besos\n" +
                    "hasta que resucite la flor deshabitada.\n" +
                    "Amemos el amor que consumi?? su fruto\n" +
                    "y descendi?? a la tierra con rostro y poder??o:\n" +
                    "t?? y yo somos la luz que contin??a,\n" +
                    "su inquebrantable espiga delicada.\n" +
                    "Al amor sepultado por tanto tiempo fr??o,\n" +
                    "por nieve y primavera, por olvido y oto??o,\n" +
                    "acerquemos la luz de una nueva manzana,\n" +
                    "de la frescura abierta por una nueva herida,\n" +
                    "como el amor antiguo que camina en silencio\n" +
                    "por una eternidad de bocas enterradas."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 96)
        registro.put(
            "descripcion", "Pienso, esta ??poca en que t?? me amaste\n" +
                    "se ir?? por otra azul sustituida,\n" +
                    "ser?? otra piel sobre los mismos huesos,\n" +
                    "otros ojos ver??n la primavera.\n" +
                    "Nadie de los que ataron esta hora,\n" +
                    "de los que conversaron con el humo,\n" +
                    "gobiernos, traficantes, transe??ntes,\n" +
                    "continuar??n movi??ndose en sus hilos.\n" +
                    "Se ir??n los crueles dioses con anteojos,\n" +
                    "los peludos carn??voros con libro,\n" +
                    "los pulgones y los pipipasseyros.\n" +
                    "Y cuando est?? reci??n lavado el mundo\n" +
                    "nacer??n otros ojos en el agua\n" +
                    "y crecer?? sin l??grimas el trigo. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 97)
        registro.put(
            "descripcion", "Hay que volar en este tiempo, a d??nde?\n" +
                    "Sin alas, sin avi??n, volar sin duda:\n" +
                    "ya los pasos pasaron sin remedio,\n" +
                    "no elevaron los pies del pasajero.\n" +
                    "Hay que volar a cada instante como\n" +
                    "las ??guilas, las moscas y los d??as,\n" +
                    "hay que vencer los ojos de Saturno\n" +
                    "y establecer all?? nuevas campanas.\n" +
                    "Ya no bastan zapatos ni caminos,\n" +
                    "ya no sirve la tierra a los errantes,\n" +
                    "ya cruzaron la noche las ra??ces,\n" +
                    "y t?? aparecer??s en otra estrella\n" +
                    "determinadamente transitoria\n" +
                    "convertida por fin en amapola."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 98)
        registro.put(
            "descripcion", "Y esta palabra, este papel escrito\n" +
                    "por las mil manos de una sola mano,\n" +
                    "no queda en ti, no sirve para sue??os,\n" +
                    "cae a la tierra: all?? se contin??a.\n" +
                    "No importa que la luz o la alabanza\n" +
                    "se derramen y salgan de la copa\n" +
                    "si fueron un tenaz temblor del vino,\n" +
                    "si se ti???? tu boca de amaranto.\n" +
                    "No quiere m??s la s??laba tard??a,\n" +
                    "lo que trae y retrae el arrecife\n" +
                    "de mis recuerdos, la irritada espuma,\n" +
                    "no quiere m??s sino escribir tu nombre.\n" +
                    "Y aunque lo calle mi sombr??o amor\n" +
                    "m??s tarde lo dir?? la primavera. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 99)
        registro.put(
            "descripcion", "Otros d??as vendr??n, ser?? entendido\n" +
                    "el silencio de plantas y planetas\n" +
                    "y cu??ntas cosas puras pasar??n!\n" +
                    "Tendr??n olor a luna los violines!\n" +
                    "El pan ser?? tal vez como t?? eres:\n" +
                    "tendr?? tu voz, tu condici??n de trigo,\n" +
                    "y hablar??n otras cosas con tu voz:\n" +
                    "los caballos perdidos del Oto??o.\n" +
                    "Aunque no sea como est?? dispuesto\n" +
                    "el amor llenar?? grandes barricas\n" +
                    "como la antigua miel de los pastores,\n" +
                    "y t?? en el polvo de mi coraz??n\n" +
                    "(en donde habr??n inmensos almacenes)\n" +
                    "ir??s y volver??s entre sand??as."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 100)
        registro.put(
            "descripcion", "En medio de la tierra apartar??\n" +
                    "las esmeraldas para divisarte\n" +
                    "y t?? estar??s copiando las espigas\n" +
                    "con una pluma de agua mensajera.\n" +
                    "Qu?? mundo! Qu?? profundo perejil!\n" +
                    "Qu?? nave navegando en la dulzura!\n" +
                    "Y t?? tal vez y yo tal vez topacio!\n" +
                    "Ya no habr?? divisi??n en las campanas.\n" +
                    "Ya no habr?? sino todo el aire libre,\n" +
                    "las manzanas llevadas por el viento,\n" +
                    "el suculento libro en la enramada,\n" +
                    "y all?? donde respiran los claveles\n" +
                    "fundaremos un traje que resista\n" +
                    "la eternidad de un beso victorioso. "
        )
        bd.insert("sonetos", null, registro)
//        val cant = bd.update("articulos", registro, "codigo=${et1.text.toString()}", null)
//        }
        bd.close()
    }
}





