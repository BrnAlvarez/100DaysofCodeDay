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
                    "en cuyo estío estalla la luz de los limones.\n" +
                    "En ese nombre corren navíos de madera\n" +
                    "rodeados por enjambres de fuego azul marino,\n" +
                    "y esas letras son el agua de un río\n" +
                    "que desemboca en mi corazón calcinado.\n" +
                    "Oh nombre descubierto bajo una enredadera\n" +
                    "como la puerta de un túnel desconocido\n" +
                    "que comunica con la fragancia del mundo!\n" +
                    "Oh invádeme con tu boca abrasadora,\n" +
                    "indágame, si quieres, con tus ojos nocturnos,\n" +
                    "pero en tu nombre déjame navegar y dormir."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 2)
        registro.put(
            "descripcion", "Amor, cuántos caminos hasta llegar a un beso,\n" +
                    "qué soledad errante hasta tu compañía!\n" +
                    "Siguen los trenes solos rodando con la lluvia.\n" +
                    "En Taltal no amanece aún la primavera.\n" +
                    "Pero tú y yo, amor mío, estamos juntos,\n" +
                    "juntos desde la ropa a las raíces,\n" +
                    "juntos de otoño, de agua, de caderas,\n" +
                    "hasta ser sólo tú, sólo yo juntos.\n" +
                    "Pensar que costó tantas piedras que lleva el río,\n" +
                    "la desembocadura del agua de Boroa,\n" +
                    "pensar que separados por trenes y naciones\n" +
                    "tú y yo teníamos que simplemente amarnos,\n" +
                    "con todos confundidos, con hombres y mujeres,\n" +
                    "con la tierra que implanta y educa los claveles. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 3)
        registro.put(
            "descripcion", "Aspero amor, violeta coronada de espinas,\n" +
                    "matorral entre tantas pasiones erizado,\n" +
                    "lanza de los dolores, corola de la cólera,\n" +
                    "por qué caminos y cómo te dirigiste a mi alma?\n" +
                    "Por qué precipitaste tu fuego doloroso,\n" +
                    "de pronto, entre las hojas frías de mi camino?\n" +
                    "Quién te enseñó los pasos que hasta mí te llevaron?\n" +
                    "Qué flor, qué piedra, qué humo mostraron mi morada?\n" +
                    "Lo cierto es que tembló la noche pavorosa,\n" +
                    "el alba llenó todas las copas con su vino\n" +
                    "y el sol estableció su presencia celeste,\n" +
                    "mientras que el cruel amor me cercaba sin tregua\n" +
                    "hasta que lacerándome con espadas y espinas\n" +
                    "abrió en mi corazón un camino quemante."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 4)
        registro.put(
            "descripcion", "Recordarás aquella quebrada caprichosa\n" +
                    "a donde los aromas palpitantes treparon,\n" +
                    "de cuando en cuando un pájaro vestido\n" +
                    "con agua y lentitud: traje de invierno.\n" +
                    "Recordarás los dones de la tierra:\n" +
                    "irascible fragancia, barro de oro,\n" +
                    "hierbas del matorral, locas raíces,\n" +
                    "sortílegas espinas como espadas.\n" +
                    "Recordarás el ramo que trajiste,\n" +
                    "ramo de sombra y agua con silencio,\n" +
                    "ramo como una piedra con espuma.\n" +
                    "Y aquella vez fue como nunca y siempre:\n" +
                    "vamos allí donde no espera nada\n" +
                    "y hallamos todo lo que está esperando."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 5)
        registro.put(
            "descripcion", "No te toque la noche ni el aire ni la aurora,\n" +
                    "sólo la tierra, la virtud de los racimos,\n" +
                    "las manzanas que crecen oyendo el agua pura,\n" +
                    "el barro y las resinas de tu país fragante.\n" +
                    "Desde Quinchamalí donde hicieron tus ojos\n" +
                    "hasta tus pies creados para mí en la Frontera\n" +
                    "eres la greda oscura que conozco:\n" +
                    "en tus caderas toco de nuevo todo el trigo.\n" +
                    "Tal vez tú no sabías, araucana,\n" +
                    "que cuando antes de amarte me olvidé de tus besos\n" +
                    "mi corazón quedó recordando tu boca\n" +
                    "y fui como un herido por las calles\n" +
                    "hasta que comprendí que había encontrado,\n" +
                    "amor, mi territorio de besos y volcanes."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 6)
        registro.put(
            "descripcion", "En los bosques, perdido, corté una rama oscura\n" +
                    "y a los labios, sediento, levanté su susurro:\n" +
                    "era tal vez la voz de la lluvia llorando,\n" +
                    "una campana rota o un corazón cortado.\n" +
                    "Algo que desde tan lejos me parecía\n" +
                    "oculto gravemente, cubierto por la tierra,\n" +
                    "un grito ensordecido por inmensos otoños,\n" +
                    "por la entreabierta y húmeda tiniebla de las hojas.\n" +
                    "Pero allí, despertando de los sueños del bosque,\n" +
                    "la rama de avellano cantó bajo mi boca\n" +
                    "y su errabundo olor trepó por mi criterio\n" +
                    "como si me buscaran de pronto las raíces\n" +
                    "que abandoné, la tierra perdida con mi infancia,\n" +
                    "y me detuve herido por el aroma errante."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 7)
        registro.put(
            "descripcion", "Vendrás conmigo dije -sin que nadie supiera\n" +
                    "dónde y cómo latía mi estado doloroso,\n" +
                    "y para mí no había clavel ni barcarola,\n" +
                    "nada sino una herida por el amor abierta.\n" +
                    "Repetí: ven conmigo, como si me muriera,\n" +
                    "y nadie vio en mi boca la luna que sangraba,\n" +
                    "nadie vio aquella sangre que subía al silencio.\n" +
                    "Oh amor ahora olvidemos la estrella con espinas!\n" +
                    "Por eso cuando oí que tu voz repetía\n" +
                    "Vendrás conmigo -fue como si desataras\n" +
                    "dolor, amor, la furia del vino encarcelado\n" +
                    "que desde su bodega sumergida subiera\n" +
                    "y otra vez en mi boca sentí un sabor de llama,\n" +
                    "de sangre y de claveles, de piedra y quemadura."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 8)
        registro.put(
            "descripcion", "Si no fuera porque tus ojos tienen color de luna,\n" +
                    "de día con arcilla, con trabajo, con fuego,\n" +
                    "y aprisionada tienes la agilidad del aire,\n" +
                    "si no fuera porque eres una semana de ámbar,\n" +
                    "si no fuera porque eres el momento amarillo\n" +
                    "en que el otoño sube por las enredaderas\n" +
                    "y eres aún el pan que la luna fragante\n" +
                    "elabora paseando su harina por el cielo,\n" +
                    "oh, bienamada, yo no te amaría!\n" +
                    "En tu abrazo yo abrazo lo que existe,\n" +
                    "la arena, el tiempo, el árbol de la lluvia,\n" +
                    "y todo vive para que yo viva:\n" +
                    "sin ir tan lejos puedo verlo todo:\n" +
                    "veo en tu vida todo lo viviente. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 9)
        registro.put(
            "descripcion", "Al golpe de la ola contra la piedra indócil\n" +
                    "la claridad estalla y establece su rosa\n" +
                    "y el círculo del mar se reduce a un racimo,\n" +
                    "a una sola gota de sal azul que cae.\n" +
                    "Oh radiante magnolia desatada en la espuma,\n" +
                    "magnética viajera cuya muerte florece\n" +
                    "y eternamente vuelve a ser y a no ser nada:\n" +
                    "sal rota, deslumbrante movimiento marino.\n" +
                    "Juntos tú y yo, amor mío, sellamos el silencio,\n" +
                    "mientras destruye el mar sus constantes estatuas\n" +
                    "y derrumba sus torres de arrebato y blancura,\n" +
                    "porque en la trama de estos tejidos invisibles\n" +
                    "del agua desbocada, de la incesante arena,\n" +
                    "sostenemos la única y acosada ternura."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 10)
        registro.put(
            "descripcion", "Suave es la bella como si música y madera,\n" +
                    "ágata, telas, trigo, duraznos transparentes,\n" +
                    "hubieran erigido la fugitiva estatua.\n" +
                    "Hacia la ola dirige su contraria frescura.\n" +
                    "El mar moja bruñidos pies copiados\n" +
                    "a la forma recién trabajada en la arena\n" +
                    "y es ahora su fuego femenino de rosa\n" +
                    "una sola burbuja que el sol y el mar combaten.\n" +
                    "Ay, que nada te toque sino la sal del frío!\n" +
                    "Que ni el amor destruya la primavera intacta.\n" +
                    "Hermosa, reverbero de la indeleble espuma,\n" +
                    "deja que tus caderas impongan en el agua\n" +
                    "una medida nueva de cisne o de nenúfar\n" +
                    "y navegue tu estatua por el cristal eterno. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 11)
        registro.put(
            "descripcion", "Suave es la bella como si música y madera,\n" +
                    "ágata, telas, trigo, duraznos transparentes,\n" +
                    "hubieran erigido la fugitiva estatua.\n" +
                    "Hacia la ola dirige su contraria frescura.\n" +
                    "El mar moja bruñidos pies copiados\n" +
                    "a la forma recién trabajada en la arena\n" +
                    "y es ahora su fuego femenino de rosa\n" +
                    "una sola burbuja que el sol y el mar combaten.\n" +
                    "Ay, que nada te toque sino la sal del frío!\n" +
                    "Que ni el amor destruya la primavera intacta.\n" +
                    "Hermosa, reverbero de la indeleble espuma,\n" +
                    "deja que tus caderas impongan en el agua\n" +
                    "una medida nueva de cisne o de nenúfar\n" +
                    "y navegue tu estatua por el cristal eterno."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 12)
        registro.put(
            "descripcion", "Plena mujer, manzana carnal, luna caliente,\n" +
                    "espeso aroma de algas, lodo y luz machacados,\n" +
                    "qué oscura claridad se abre entre tus columnas?\n" +
                    "Qué antigua noche el hombre toca con sus sentidos?\n" +
                    "Ay, amar es un viaje con agua y con estrellas,\n" +
                    "con aire ahogado y bruscas tempestades de harina:\n" +
                    "amar es un combate de relámpagos\n" +
                    "y dos cuerpos por una sola miel derrotados.\n" +
                    "Beso a beso recorro tu pequeño infinito,\n" +
                    "tus márgenes, tus ríos, tus pueblos diminutos,\n" +
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
                    "no es de nácar marino, nunca de plata fría:\n" +
                    "eres de pan, de pan amado por el fuego.\n" +
                    "La harina levantó su granero contigo\n" +
                    "y creció incrementada por la edad venturosa,\n" +
                    "cuando los cereales duplicaron tu pecho\n" +
                    "mi amor era el carbón trabajando en la tierra.\n" +
                    "Oh, pan tu frente, pan tus piernas, pan tu boca,\n" +
                    "pan que devoro y nace con luz cada mañana,\n" +
                    "bienamada, bandera de las panaderías,\n" +
                    "una lección de sangre te dio el fuego,\n" +
                    "de la harina aprendiste a ser sagrada,\n" +
                    "y del pan el idioma y el aroma."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 14)
        registro.put(
            "descripcion", "Me falta tiempo para celebrar tus cabellos.\n" +
                    "Uno por uno debo contarlos y alabarlos:\n" +
                    "otros amantes quieren vivir con ciertos ojos,\n" +
                    "yo sólo quiero ser tu peluquero.\n" +
                    "En Italia te bautizaron Medusa\n" +
                    "por la encrespada y alta luz de tu cabellera.\n" +
                    "Yo te llamo chascona mía y enmarañada:\n" +
                    "mi corazón conoce las puertas de tu pelo.\n" +
                    "Cuando tú te extravíes en tus propios cabellos,\n" +
                    "no me olvides, acuérdate que te amo,\n" +
                    "no me dejes perdido ir sin tu cabellera\n" +
                    "por el mundo sombrío de todos los caminos\n" +
                    "que sólo tiene sombra, transitorios dolores,\n" +
                    "hasta que el sol sube a la torre de tu pelo. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 15)
        registro.put(
            "descripcion", "Desde hace mucho tiempo la tierra te conoce:\n" +
                    "eres compacta como el pan o la madera,\n" +
                    "eres cuerpo, racimo de segura substancia,\n" +
                    "tienes peso de acacia, de legumbre dorada.\n" +
                    "Sé que existes no sólo porque tus ojos vuelan\n" +
                    "y dan luz a las cosas como ventana abierta,\n" +
                    "sino porque de barro te hicieron y cocieron\n" +
                    "en Chillán, en un horno de adobe estupefacto.\n" +
                    "Los seres se derraman como aire o agua o frío\n" +
                    "y vagos son, se borran al contacto del tiempo,\n" +
                    "como si antes de muertos fueran desmenuzados.\n" +
                    "Tú caerás conmigo como piedra en la tumba\n" +
                    "y así por nuestro amor que no fue consumido\n" +
                    "continuará viviendo con nosotros la tierra."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 16)
        registro.put(
            "descripcion", "Amo el trozo de tierra que tú eres,\n" +
                    "porque de las praderas planetarias\n" +
                    "otra estrella no tengo. Tú repites\n" +
                    "la multiplicación del universo.\n" +
                    "Tus anchos ojos son la luz que tengo\n" +
                    "de las constelaciones derrotadas,\n" +
                    "tu piel palpita como los caminos\n" +
                    "que recorre en la lluvia el meteoro.\n" +
                    "De tanta luna fueron para mí tus caderas,\n" +
                    "de todo el sol tu boca profunda y su delicia,\n" +
                    "de tanta luz ardiente como miel en la sombra\n" +
                    "tu corazón quemado por largos rayos rojos,\n" +
                    "y así recorro el fuego de tu forma besándote,\n" +
                    "pequeña y planetaria, paloma y geografía. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 17)
        registro.put(
            "descripcion", "No te amo como si fueras rosa de sal, topacio \n" +
                    "o flecha de claveles que propagan el fuego: \n" +
                    "te amo como se aman ciertas cosas oscuras, \n" +
                    "secretamente, entre la sombra y el alma. \n" +
                    "Te amo como la planta que no florece y lleva \n" +
                    "dentro de sí, escondida, la luz de aquellas flores, \n" +
                    "y gracias a tu amor vive oscuro en mi cuerpo \n" +
                    "el apretado aroma que ascendió de la tierra. \n" +
                    "Te amo sin saber cómo, ni cuándo, ni de dónde, \n" +
                    "te amo directamente sin problemas ni orgullo: \n" +
                    "así te amo porque no sé amar de otra manera, \n" +
                    "sino así de este modo en que no soy ni eres, \n" +
                    "tan cerca que tu mano sobre mi pecho es mía, \n" +
                    "tan cerca que se cierran tus ojos con mi sueño."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 18)
        registro.put(
            "descripcion", "Por las montañas vas como viene la brisa\n" +
                    "o la corriente brusca que baja de la nieve\n" +
                    "o bien tu cabellera palpitante confirma\n" +
                    "los altos ornamentos del sol en la espesura.\n" +
                    "Toda la luz del Cáucaso cae sobre tu cuerpo\n" +
                    "como en una pequeña vasija interminable\n" +
                    "en que el agua se cambia de vestido y de canto\n" +
                    "a cada movimiento transparente del río.\n" +
                    "Por los montes el viejo camino de guerreros\n" +
                    "y abajo enfurecida brilla como una espada\n" +
                    "el agua entre murallas de manos minerales,\n" +
                    "hasta que tú recibes de los bosques de pronto\n" +
                    "el ramo o el relámpago de unas flores azules\n" +
                    "y la insólita flecha de un aroma salvaje."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 19)
        registro.put(
            "descripcion", "Mientras la magna espuma de Isla Negra,\n" +
                    "la sal azul, el sol en las olas te mojan,\n" +
                    "yo miro los trabajos de la avispa\n" +
                    "empeñada en la miel de su universo.\n" +
                    "Va y viene equilibrando su recto y rubio vuelo\n" +
                    "como si deslizara de un alambre invisible\n" +
                    "la elegancia del baile, la sed de su cintura,\n" +
                    "y los asesinatos del aguijón maligno.\n" +
                    "De petróleo y naranja es su arco iris,\n" +
                    "busca como un avión entre la hierba,\n" +
                    "con un rumor de espiga vuela, desaparece,\n" +
                    "mientras que tú sales del mar, desnuda,\n" +
                    "y regresas al mundo llena de sal y sol,\n" +
                    "reverberante estatua y espada de la arena."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 20)
        registro.put(
            "descripcion", "Mi fea, eres una castaña despeinada,\n" +
                    "mi bella, eres hermosa como el viento,\n" +
                    "mi fea, de tu boca se pueden hacer dos,\n" +
                    "mi bella, son tus besos frescos como sandías.\n" +
                    "Mi fea, dónde están escondidos tus senos?\n" +
                    "Son mínimos como dos copas de trigo.\n" +
                    "Me gustaría verte dos lunas en el pecho:\n" +
                    "las gigantescas torres de tu soberanía.\n" +
                    "Mi fea, el mar no tiene tus uñas en su tienda,\n" +
                    "mi bella, flor a flor, estrella por estrella,\n" +
                    "ola por ola, amor, he contado tu cuerpo:\n" +
                    "mi fea, te amo por tu cintura de oro,\n" +
                    "mi bella, te amo por una arruga en tu frente,\n" +
                    "amor, te amo por clara y por oscura. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 21)
        registro.put(
            "descripcion", "Oh que todo el amor propague en mí su boca,\n" +
                    "que no sufra un momento más sin primavera,\n" +
                    "yo no vendí sino mis manos al dolor,\n" +
                    "ahora, bienamada, déjame con tus besos.\n" +
                    "Cubre la luz del mes abierto con tu aroma,\n" +
                    "cierra las puertas con tu cabellera,\n" +
                    "y en cuanto a mí no olvides que si despierto y lloro\n" +
                    "es porque en sueños sólo soy un niño perdido\n" +
                    "que busca entre las hojas de la noche tus manos,\n" +
                    "el contacto del trigo que tú me comunicas,\n" +
                    "un rapto centelleante de sombra y energía.\n" +
                    "Oh, bienamada, y nada más que sombra\n" +
                    "por donde me acompañes en tus sueños\n" +
                    "y me digas la hora de la luz."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 22)
        registro.put(
            "descripcion", "Cuántas veces, amor, te amé sin verte y tal vez sin recuerdo,\n" +
                    "sin reconocer tu mirada, sin mirarte, centaura,\n" +
                    "en regiones contrarias, en un mediodía quemante:\n" +
                    "eras sólo el aroma de los cereales que amo.\n" +
                    "Tal vez te vi, te supuse al pasar levantando una copa\n" +
                    "en Angol, a la luz de la luna de Junio,\n" +
                    "o eras tú la cintura de aquella guitarra\n" +
                    "que toqué en las tinieblas y sonó como el mar desmedido.\n" +
                    "Te amé sin que yo lo supiera, y busqué tu memoria.\n" +
                    "En las casas vacías entré con linterna a robar tu retrato.\n" +
                    "Pero yo ya sabía cómo era. De pronto\n" +
                    "mientras ibas conmigo te toqué y se detuvo mi vida:\n" +
                    "frente a mis ojos estabas, reinándome, y reinas.\n" +
                    "Como hoguera en los bosques el fuego es tu reino."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 23)
        registro.put(
            "descripcion", "Fue luz el fuego y pan la luna rencorosa,\n" +
                    "el jazmín duplicó su estrellado secreto,\n" +
                    "y del terrible amor las suaves manos puras\n" +
                    "dieron paz a mis ojos y sol a mis sentidos.\n" +
                    "Oh amor, cómo de pronto, de las desgarraduras\n" +
                    "hiciste el edificio de la dulce firmeza,\n" +
                    "derrotaste las uñas malignas y celosas\n" +
                    "y hoy frente al mundo somos como una sola vida.\n" +
                    "Así fue, así es y así será hasta cuando,\n" +
                    "salvaje y dulce amor, bienamada Matilde,\n" +
                    "el tiempo nos señale la flor final del día.\n" +
                    "Sin ti, sin mí, sin luz ya no seremos:\n" +
                    "entonces más allá del la tierra y la sombra\n" +
                    "el resplandor de nuestro amor seguirá vivo."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 24)
        registro.put(
            "descripcion", "Amor, amor, las nubes a la torre del cielo\n" +
                    "subieron como triunfantes lavanderas,\n" +
                    "y todo ardió en azul, todo fue estrella:\n" +
                    "el mar, la nave, el día se desterraron juntos.\n" +
                    "Ven a ver los cerezos del agua constelada\n" +
                    "y la clave redonda del rápido universo,\n" +
                    "ven a tocar el fuego del azul instantáneo,\n" +
                    "ven antes de que sus pétalos se consuman.\n" +
                    "No hay aquí sino luz, cantidades, racimos,\n" +
                    "espacio abierto por las virtudes del viento\n" +
                    "hasta entregar los últimos secretos de la espuma.\n" +
                    "Y entre tantos azules celestes, sumergidos,\n" +
                    "se pierden nuestros ojos adivinando apenas\n" +
                    "los poderes del aire, las llaves submarinas."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 25)
        registro.put(
            "descripcion", "Antes de amarte, amor, nada era mío:\n" +
                    "vacilé por las calles y las cosas:\n" +
                    "nada contaba ni tenía nombre:\n" +
                    "el mundo era del aire que esperaba.\n" +
                    "Yo conocí salones cenicientos,\n" +
                    "túneles habitados por la luna,\n" +
                    "hangares crueles que se despedían,\n" +
                    "preguntas que insistían en la arena.\n" +
                    "Todo estaba vacío, muerto y mudo,\n" +
                    "caído, abandonado y decaído,\n" +
                    "todo era inalienablemente ajeno,\n" +
                    "todo era de los otros y de nadie,\n" +
                    "hasta que tu belleza y tu pobreza\n" +
                    "llenaron el otoño de regalos."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 26)
        registro.put(
            "descripcion", "Ni el color de las dunas terribles en Iquique,\n" +
                    "ni el estuario del Río Dulce de Guatemala,\n" +
                    "cambiaron tu perfil conquistado en el trigo,\n" +
                    "tu estilo de uva grande, tu boca de guitarra.\n" +
                    "Oh corazón, oh mía desde todo el silencio,\n" +
                    "desde las cumbres donde reinó la enredadera\n" +
                    "hasta las desoladas planicies del platino,\n" +
                    "en toda patria pura te repitió la tierra.\n" +
                    "Pero ni huraña mano de montes minerales,\n" +
                    "ni nieve tibetana, ni piedra de Polonia,\n" +
                    "nada alteró tu forma de cereal viajero,\n" +
                    "como si greda o trigo, guitarras o racimos\n" +
                    "de Chillán defendieran en ti su territorio\n" +
                    "imponiendo el mandato de la luna silvestre. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 27)
        registro.put(
            "descripcion", "Desnuda eres tan simple como una de tus manos,\n" +
                    "lisa, terrestre, mínima, redonda, transparente,\n" +
                    "tienes líneas de luna, caminos de manzana,\n" +
                    "desnuda eres delgada como el trigo desnudo.\n" +
                    "Desnuda eres azul como la noche en Cuba,\n" +
                    "tienes enredaderas y estrellas en el pelo,\n" +
                    "desnuda eres enorme y amarilla\n" +
                    "como el verano en una iglesia de oro.\n" +
                    "Desnuda eres pequeña como una de tus uñas,\n" +
                    "curva, sutil, rosada hasta que nace el día\n" +
                    "y te metes en el subterráneo del mundo\n" +
                    "como en un largo túnel de trajes y trabajos:\n" +
                    "tu claridad se apaga, se viste, se deshoja\n" +
                    "y otra vez vuelve a ser una mano desnuda."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 28)
        registro.put(
            "descripcion", "Amor, de grano a grano, de planeta a planeta,\n" +
                    "la red del viento con sus países sombríos,\n" +
                    "la guerra con sus zapatos de sangre,\n" +
                    "o bien el día y la noche de la espiga.\n" +
                    "Por donde fuimos, islas o puentes o banderas,\n" +
                    "violines del fugaz otoño acribillado,\n" +
                    "repitió la alegría los labios de la copa,\n" +
                    "el dolor nos detuvo con su lección de llanto.\n" +
                    "En todas las repúblicas desarrollaba el viento\n" +
                    "su pabellón impune, su glacial cabellera\n" +
                    "y luego regresaba la flor a sus trabajos.\n" +
                    "Pero en nosotros nunca se calcinó el otoño.\n" +
                    "Y en nuestra patria inmóvil germinaba y crecía\n" +
                    "el amor con los derechos del rocío. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 29)
        registro.put(
            "descripcion", "Vienes de la pobreza de las casas del Sur,\n" +
                    "de las regiones duras con frío y terremoto\n" +
                    "que cuando hasta sus dioses rodaron a la muerte\n" +
                    "nos dieron la lección de la vida en la greda.\n" +
                    "Eres un caballito de greda negra, un beso\n" +
                    "de barro oscuro, amor, amapola de greda,\n" +
                    "paloma del crepúsculo que voló en los caminos,\n" +
                    "alcancía con lágrimas de nuestra pobre infancia.\n" +
                    "Muchacha, has conservado tu corazón de pobre,\n" +
                    "tus pies de pobre acostumbrados a las piedras,\n" +
                    "tu boca que no siempre tuvo pan o delicia.\n" +
                    "Eres del pobre Sur, de donde viene mi alma:\n" +
                    "en su cielo tu madre sigue lavando ropa\n" +
                    "con mi madre. Por eso te escogí, compañera."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 30)
        registro.put(
            "descripcion", "Tienes del archipiélago las hebras del alerce,\n" +
                    "la carne trabajada por los siglos del tiempo,\n" +
                    "venas que conocieron el mar de las maderas,\n" +
                    "sangre verde caída del cielo a la memoria.\n" +
                    "Nadie recogerá mi corazón perdido\n" +
                    "entre tantas raíces, en la amarga frescura\n" +
                    "del sol multiplicado por la furia del agua,\n" +
                    "allí vive la sombra que no viaja conmigo.\n" +
                    "Por eso tú saliste del Sur como una isla\n" +
                    "poblada y coronada por plumas y maderas\n" +
                    "y yo sentí el aroma de los bosques errantes,\n" +
                    "hallé la miel oscura que conocí en la selva,\n" +
                    "y toqué en tus caderas los pétalos sombríos\n" +
                    "que nacieron conmigo y construyeron mi alma. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 31)
        registro.put(
            "descripcion", "Con laureles del Sur y orégano de Lota\n" +
                    "te corono, pequeña monarca de mis huesos,\n" +
                    "y no puede faltarte esa corona\n" +
                    "que elabora la tierra con bálsamo y follaje.\n" +
                    "Eres, como el que te ama, de las provincias verdes:\n" +
                    "de allá trajimos barro que nos corre en la sangre,\n" +
                    "en la ciudad andamos, como tantos, perdidos,\n" +
                    "temerosos de que cierren el mercado.\n" +
                    "Bienamada, tu sombra tiene olor a ciruela,\n" +
                    "tus ojos escondieron en el Sur sus raíces,\n" +
                    "tu corazón es una paloma de alcancía,\n" +
                    "tu cuerpo es liso como las piedras en el agua,\n" +
                    "tus besos son racimos con rocío,\n" +
                    "y yo a tu lado vivo con la tierra."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 32)
        registro.put(
            "descripcion", "La casa en la mañana con la verdad revuelta\n" +
                    "de sábanas y plumas, el origen del día\n" +
                    "sin dirección, errante como una pobre barca,\n" +
                    "entre los horizontes del orden y del sueño.\n" +
                    "Las cosas quieren arrastrar vestigios,\n" +
                    "adherencias sin rumbo, herencias frías,\n" +
                    "los papeles esconden vocales arrugadas\n" +
                    "y en la botella el vino quiere seguir su ayer.\n" +
                    "Ordenadora, pasas vibrando como abeja\n" +
                    "tocando las regiones perdidas por la sombra,\n" +
                    "conquistando la luz con tu blanca energía.\n" +
                    "Y se construye entonces la claridad de nuevo:\n" +
                    "obedecen las cosas al viento de la vida\n" +
                    "y el orden establece su pan y su paloma. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 33)
        registro.put(
            "descripcion", "Amor, ahora nos vamos a la casa\n" +
                    "donde la enredadera sube por las escalas:\n" +
                    "antes que llegues tú llegó a tu dormitorio\n" +
                    "el verano desnudo con pies de madreselva.\n" +
                    "Nuestros besos errantes recorrieron el mundo:\n" +
                    "Armenia, espesa gota de miel desenterrada,\n" +
                    "Ceylán, paloma verde, y el Yang Tsé separando\n" +
                    "con antigua paciencia los días de las noches.\n" +
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
            "descripcion", "Eres hija del mar y prima del orégano,\n" +
                    "nadadora, tu cuerpo es de agua pura,\n" +
                    "cocinera, tu sangre es tierra viva\n" +
                    "y tus costumbres son floridas y terrestres.\n" +
                    "Al agua van tus ojos y levantan las olas,\n" +
                    "a la tierra tus manos y saltan las semillas,\n" +
                    "en agua y tierra tienes propiedades profundas\n" +
                    "que en ti se juntan como las leyes de la greda.\n" +
                    "Náyade, corta tu cuerpo la turquesa\n" +
                    "y luego resurrecto florece en la cocina\n" +
                    "de tal modo que asumes cuanto existe\n" +
                    "y al fin duermes rodeada por mis brazos que apartan\n" +
                    "de la sormbra sombría, para que tú descanses,\n" +
                    "legumbres, algas, hierbas: la espuma de tus sueños. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 35)
        registro.put(
            "descripcion", "Tu mano fue volando de mis ojos al día.\n" +
                    "Entró la luz como un rosal abierto.\n" +
                    "Arena y cielo palpitaban como una\n" +
                    "culminante colmena cortada en las turquesas.\n" +
                    "Tu mano tocó sílabas que tintineaban, copas,\n" +
                    "alcuzas con aceites amarillos,\n" +
                    "corolas, manantiales y, sobre todo, amor,\n" +
                    "amor: tu mano pura preservó las cucharas.\n" +
                    "La tarde fue. La noche deslizó sigilosa\n" +
                    "sobre el sueño del hombre su cápsula celeste.\n" +
                    "Un triste olor salvaje soltó la madreselva.\n" +
                    "Y tu mano volvió de su vuelo volando\n" +
                    "a cerrar su plumaje que yo creí perdido\n" +
                    "sobre mis ojos devorados por la sombra."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 36)
        registro.put(
            "descripcion", "Corazón mío, reina del apio y de la artesa:\n" +
                    "pequeña leoparda del hilo y la cebolla:\n" +
                    "me gusta ver brillar tu imperio diminuto,\n" +
                    "las armas de la cera, del vino, del aceite,\n" +
                    "del ajo, de la tierra por tus manos abierta\n" +
                    "de la sustancia azul encendida en tus manos,\n" +
                    "de la transmigración del sueño a la ensalada,\n" +
                    "del reptil enrollado en la manguera.\n" +
                    "Tú con tu podadora levantando el perfume,\n" +
                    "tú, con la dirección del jabón en la espuma,\n" +
                    "tú, subiendo mis locas escalas y escaleras,\n" +
                    "tú, manejando el síntoma de mi caligrafía\n" +
                    "y encontrando en la arena del cuaderno\n" +
                    "las letras extraviadas que buscaban tu boca."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 37)
        registro.put(
            "descripcion", "Oh amor, oh rayo loco y amenaza purpúrea,\n" +
                    "me visitas y subes por tu fresca escalera\n" +
                    "el castillo que el tiempo coronó de neblinas,\n" +
                    "las pálidas paredes del corazón cerrado.\n" +
                    "Nadie sabrá que sólo fue la delicadeza\n" +
                    "construyendo cristales duros como ciudades\n" +
                    "y que la sangre abría túneles desdichados\n" +
                    "sin que su monarquía derribara el invierno.\n" +
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
            "descripcion", "Tu casa suena como un tren a mediodía,\n" +
                    "zumban las avispas, cantan las cacerolas,\n" +
                    "la cascada enumera los hechos del rocío,\n" +
                    "tu risa desarrolla su trino de palmera.\n" +
                    "La luz azul del muro conversa con la piedra,\n" +
                    "llega como un pastor silbando un telegrama\n" +
                    "y entre las dos higueras de voz verde\n" +
                    "Homero sube con zapatos sigilosos.\n" +
                    "Sólo aquí la ciudad no tiene voz ni llanto,\n" +
                    "ni sin fin, ni sonatas, ni labios, ni bocina\n" +
                    "sino un discurso de cascada y de leones,\n" +
                    "y tú que subes, cantas, corres, caminas, bajas,\n" +
                    "plantas, coses, cocinas, clavas, escribes, vuelves,\n" +
                    "o te has ido y se sabe que comenzó el invierno. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 39)
        registro.put(
            "descripcion", "Pero olvidé que tus manos satisfacían\n" +
                    "las raíces, regando rosas enmarañadas,\n" +
                    "hasta que florecieron tus huellas digitales\n" +
                    "en la plenaria paz de la naturaleza.\n" +
                    "El azadón y el agua como animales tuyos\n" +
                    "te acompañan, mordiendo y lamiendo la tierra,\n" +
                    "y es así cómo, trabajando, desprendes\n" +
                    "fecundidad, fogosa frescura de claveles.\n" +
                    "Amor y honor de abejas pido para tus manos\n" +
                    "que en la tierra confunden su estirpe transparente,\n" +
                    "y hasta en mi corazón abren su agricultura,\n" +
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
                    "Matilde, atravesaste el mediodía.\n" +
                    "Ibas cargada de flores ferruginosas,\n" +
                    "algas que el viento sur atormenta y olvida,\n" +
                    "aún blancas, agrietadas por la sal devorante,\n" +
                    "tus manos levantaban las espigas de arena.\n" +
                    "Amo tus dones puros, tu piel de piedra intacta,\n" +
                    "tus uñas ofrecidas en el sol de tus dedos,\n" +
                    "tu boca derramada por toda la alegría,\n" +
                    "pero, para mi casa vecina del abismo,\n" +
                    "dame el atormentado sistema del silencio,\n" +
                    "el pabellón del mar olvidado en la arena. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 41)
        registro.put(
            "descripcion", "Desdichas del mes de Enero cuando el indiferente\n" +
                    "mediodía establece su ecuación en el cielo,\n" +
                    "un oro duro como el vino de una copa colmada\n" +
                    "llena la tierra hasta sus límites azules.\n" +
                    "Desdichas de este tiempo parecidas a uvas\n" +
                    "pequeñas que agruparon verde amargo,\n" +
                    "confusas, escondidas lágrimas de los días\n" +
                    "hasta que la intemperie publicó sus racimos.\n" +
                    "Sí, gérmenes, dolores, todo lo que palpita\n" +
                    "aterrado, a la luz crepitante de Enero,\n" +
                    "madurará, arderá como ardieron los frutos.\n" +
                    "Divididos serán los pesares: el alma\n" +
                    "dará un golpe de viento, y la morada\n" +
                    "quedará limpia con el pan fresco en la mesa."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 42)
        registro.put(
            "descripcion", "Radiantes días balanceados por el agua marina,\n" +
                    "concentrados como el interior de una piedra amarilla\n" +
                    "cuyo esplendor de miel no derribó el desorden:\n" +
                    "preservó su pureza de rectángulo.\n" +
                    "Crepita, sí, la hora como fuego o abejas\n" +
                    "y es verde la tarea de sumergirse en hojas,\n" +
                    "hasta que hacia la altura es el follaje\n" +
                    "un mundo centelleante que se apaga y susurra.\n" +
                    "Sed del fuego, abrasadora multitud del estío\n" +
                    "que construye un Edén con unas cuantas hojas,\n" +
                    "porque la tierra de rostro oscuro no quiere sufrimientos\n" +
                    "sino frescura o fuego, agua o pan para todos,\n" +
                    "y nada debería dividir a los hombres\n" +
                    "sino el sol o la noche, la luna o las espigas."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 43)
        registro.put(
            "descripcion", "Un signo tuyo busco en todas las otras,\n" +
                    "en el brusco, ondulante río de las mujeres,\n" +
                    "trenzas, ojos apenas sumergidos,\n" +
                    "pies claros que resbalan navegando en la espuma.\n" +
                    "De pronto me parece que diviso tus uñas\n" +
                    "oblongas, fugitivas, sobrinas de un cerezo,\n" +
                    "y otra vez es tu pelo que pasa y me parece\n" +
                    "ver arder en el agua tu retrato de hoguera.\n" +
                    "Miré, pero ninguna llevaba tu latido,\n" +
                    "tu luz, la greda oscura que trajiste del bosque,\n" +
                    "ninguna tuvo tus diminutas orejas.\n" +
                    "Tú eres total y breve, de todas eres una,\n" +
                    "y así contigo voy recorriendo y amando\n" +
                    "un ancho Mississippi de estuario femenino."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 44)
        registro.put(
            "descripcion", "Sabrás que no te amo y que te amo\n" +
                    "puesto que de dos modos es la vida,\n" +
                    "la palabra es un ala del silencio,\n" +
                    "el fuego tiene una mitad de frío.\n" +
                    "Yo te amo para comenzar a amarte,\n" +
                    "para recomenzar el infinito\n" +
                    "y para no dejar de amarte nunca:\n" +
                    "por eso no te amo todavía.\n" +
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
            "descripcion", "No estés lejos de mí un solo día, porque cómo,\n" +
                    "porque, no sé decirlo, es largo el día,\n" +
                    "y te estaré esperando como en las estaciones\n" +
                    "cuando en alguna parte se durmieron los trenes.\n" +
                    "No te vayas por una hora porque entonces\n" +
                    "en esa hora se juntan las gotas del desvelo\n" +
                    "y tal vez todo el humo que anda buscando casa\n" +
                    "venga a matar aún mi corazón perdido.\n" +
                    "Ay que no se quebrante tu silueta en la arena,\n" +
                    "ay que no vuelen tus párpados en la ausencia:\n" +
                    "no te vayas por un minuto, bienamada,\n" +
                    "porque en ese minuto te habrás ido tan lejos\n" +
                    "que yo cruzaré toda la tierra preguntando\n" +
                    "si volverás o si me dejarás muriendo."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 46)
        registro.put(
            "descripcion", "De las estrellas que admiré, mojadas\n" +
                    "por ríos y rocíos diferentes,\n" +
                    "yo no escogí sino la que yo amaba\n" +
                    "y desde entonces duermo con la noche.\n" +
                    "De la ola, una ola y otra ola,\n" +
                    "verde mar, verde frío, rama verde,\n" +
                    "yo no escogí sino una sola ola:\n" +
                    "la ola indivisible de tu cuerpo.\n" +
                    "Todas las gotas, todas las raíces,\n" +
                    "todos los hilos de la luz vinieron,\n" +
                    "me vinieron a ver tarde o temprano.\n" +
                    "Yo quise para mí tu cabellera.\n" +
                    "Y de todos los dones de mi patria\n" +
                    "sólo escogí tu corazón salvaje. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 47)
        registro.put(
            "descripcion", "Detrás de mí en la rama quiero verte.\n" +
                    "Poco a poco te convertiste en fruto.\n" +
                    "No te costó subir de las raíces\n" +
                    "cantando con tu sílaba de savia.\n" +
                    "Y aquí estarás primero en flor fragante,\n" +
                    "en la estatua de un beso convertida,\n" +
                    "hasta que sol y tierra, sangre y cielo,\n" +
                    "te otorguen la delicia y la dulzura.\n" +
                    "En la rama veré tu cabellera,\n" +
                    "tu signo madurando en el follaje,\n" +
                    "acercando las hojas a mi sed,\n" +
                    "y llenará mi boca tu substancia,\n" +
                    "el beso que subió desde la tierra\n" +
                    "con tu sangre de fruta enamorada."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 48)
        registro.put(
            "descripcion", "Dos amantes dichosos hacen un solo pan,\n" +
                    "una sola gota de luna en la hierba,\n" +
                    "dejan andando dos sombras que se reúnen,\n" +
                    "dejan un solo sol vacío en una cama.\n" +
                    "De todas las verdades escogieron el día:\n" +
                    "no se ataron con hilos sino con un aroma,\n" +
                    "y no despedazaron la paz ni las palabras.\n" +
                    "La dicha es una torre transparente.\n" +
                    "El aire, el vino van con los dos amantes,\n" +
                    "la noche les regala sus pétalos dichosos,\n" +
                    "tienen derecho a todos los claveles.\n" +
                    "Dos amantes dichosos no tienen fin ni muerte,\n" +
                    "nacen y mueren muchas veces mientras viven,\n" +
                    "tienen la eternidad de la naturaleza."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 49)
        registro.put(
            "descripcion", "Es hoy: todo el ayer se fue cayendo\n" +
                    "entre dedos de luz y ojos de sueño,\n" +
                    "mañana llegará con pasos verdes:\n" +
                    "nadie detiene el río de la aurora.\n" +
                    "Nadie detiene el río de tus manos,\n" +
                    "los ojos de tu sueño, bienamada,\n" +
                    "eres temblor del tiempo que transcurre\n" +
                    "entre luz vertical y sol sombrío,\n" +
                    "y el cielo cierra sobre ti sus alas\n" +
                    "llevándote y trayéndote a mis brazos\n" +
                    "con puntual, misteriosa cortesía:\n" +
                    "Por eso canto al día y a la luna,\n" +
                    "al mar, al tiempo, a todos los planetas,\n" +
                    "a tu voz diurna y a tu piel nocturna."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 50)
        registro.put(
            "descripcion", "Cotapos dice que tu risa cae\n" +
                    "como un halcón desde una brusca torre\n" +
                    "y, es verdad, atraviesas el follaje del mundo\n" +
                    "con un solo relámpago de tu estirpe celeste\n" +
                    "que cae, y corta, y saltan las lenguas del rocío,\n" +
                    "las aguas del diamante, la luz con sus abejas\n" +
                    "y allí donde vivía con su barba el silencio\n" +
                    "estallan las granadas del sol y las estrellas,\n" +
                    "se viene abajo el cielo con la noche sombría,\n" +
                    "arden a plena luna campanas y claveles,\n" +
                    "y corren los caballos de los talabarteros:\n" +
                    "porque tú siendo tan pequeñita como eres\n" +
                    "dejas caer la risa desde tu meteoro\n" +
                    "electrizando el nombre de la naturaleza."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 51)
        registro.put(
            "descripcion", "Tu risa pertenece a un árbol entreabierto\n" +
                    "por un rayo, por un relámpago plateado\n" +
                    "que desde el cielo cae quebrándose en la copa,\n" +
                    "partiendo en dos el árbol con una sola espada.\n" +
                    "Sólo en las tierras altas del follaje con nieve\n" +
                    "nace una risa como la tuya, bienamante,\n" +
                    "es la risa del aire desatado en la altura,\n" +
                    "costumbres de araucaria, bienamada.\n" +
                    "Cordillerana mía, chillaneja evidente,\n" +
                    "corta con los cuchillos de tu risa la sombra,\n" +
                    "la noche, la mañana, la miel del mediodía,\n" +
                    "y que salten al cielo las aves del follaje\n" +
                    "cuando como una luz derrochadora\n" +
                    "rompe tu risa el árbol de la vida."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 52)
        registro.put(
            "descripcion", "Cantas y a sol y a cielo con tu canto\n" +
                    "tu voz desgrana el cereal del día,\n" +
                    "hablan los pinos con su lengua verde:\n" +
                    "trinan todas las aves del invierno.\n" +
                    "El mar llena sus sótanos de pasos,\n" +
                    "de campanas, cadenas y gemidos,\n" +
                    "tintinean metales y utensilios,\n" +
                    "suenan las ruedas de la caravana.\n" +
                    "Pero sólo tu voz escucho y sube\n" +
                    "tu voz con vuelo y precisión de flecha,\n" +
                    "baja tu voz con gravedad de lluvia,\n" +
                    "tu voz esparce altísimas espadas,\n" +
                    "vuelve tu voz cargada de violetas\n" +
                    "y luego me acompaña por el cielo. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 53)
        registro.put(
            "descripcion", "Aquí está el pan, el vino, la mesa, la morada:\n" +
                    "el menester del hombre, la mujer y la vida:\n" +
                    "a este sitio corría la paz vertiginosa,\n" +
                    "por esta luz ardió la común quemadura.\n" +
                    "Honor a tus dos manos que vuelan preparando\n" +
                    "los blancos resultados del canto y la cocina,\n" +
                    "salve! la integridad de tus pies corredores,\n" +
                    "viva! la bailarina que baila con la escoba.\n" +
                    "Aquellos bruscos ríos con aguas y amenazas,\n" +
                    "aquel atormentado pabellón de la espuma,\n" +
                    "aquellos incendiaron panales y arrecifes\n" +
                    "son hoy este reposo de tu sangre en la mía,\n" +
                    "este cauce estrellado y azul como la noche,\n" +
                    "esta simplicidad sin fin de la ternura."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 54)
        registro.put(
            "descripcion", "Espléndida razón, demonio claro\n" +
                    "del racimo absoluto, del recto mediodía,\n" +
                    "aquí estamos al fin, sin soledad y solos,\n" +
                    "lejos del desvarío de la ciudad salvaje.\n" +
                    "Cuando la línea pura rodea su paloma\n" +
                    "y el fuego condecora la paz con su alimento\n" +
                    "tú y yo erigimos este celeste resultado!\n" +
                    "Razón y amor desnudos viven en esta casa.\n" +
                    "Sueños furiosos, ríos de amarga certidumbre\n" +
                    "decisiones más duras que el sueño de un martillo\n" +
                    "cayeron en la doble copa de los amantes.\n" +
                    "Hasta que en la balanza se elevaron, gemelos,\n" +
                    "la razón y el amor como dos alas.\n" +
                    "Así se construyó la transparencia. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 55)
        registro.put(
            "descripcion", "Espinas, vidrios rotos, enfermedades, llanto\n" +
                    "asedian día y noche la miel de los felices\n" +
                    "y no sirve la torre, ni el viaje, ni los muros:\n" +
                    "la desdicha atraviesa la paz de los dormidos,\n" +
                    "el dolor sube y baja y acerca sus cucharas\n" +
                    "y no hay hombre sin este movimiento,\n" +
                    "no hay natalicio, no hay techo ni cercado:\n" +
                    "hay que tomar en cuenta este atributo.\n" +
                    "Y en el amor no valen tampoco ojos cerrados,\n" +
                    "profundos lechos lejos del pestilente herido,\n" +
                    "o del que paso a paso conquista su bandera.\n" +
                    "Porque la vida pega como cólera o río\n" +
                    "y abre un túnel sangriento por donde nos vigilan\n" +
                    "los ojos de una inmensa familia de dolores."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 56)
        registro.put(
            "descripcion", "Acostúmbrate a ver detrás de mí la sombra\n" +
                    "y que tus manos salgan del rencor, transparentes,\n" +
                    "como si en la mañana del mar fueran creadas:\n" +
                    "la sal te dio, amor mío, proporción cristalina.\n" +
                    "La envidia sufre, muere, se agota con mi canto.\n" +
                    "Uno a uno agonizan sus tristes capitanes.\n" +
                    "Yo digo amor, y el mundo se puebla de palomas.\n" +
                    "Cada sílaba mía trae la primavera.\n" +
                    "Entonces tú, florida, corazón, bienamada,\n" +
                    "sobre mis ojos como los follajes del cielo\n" +
                    "eres, y yo te miro recostada en la tierra.\n" +
                    "Veo el sol trasmigrar racimos a tu rostro,\n" +
                    "mirando hacia la altura reconozco tus pasos.\n" +
                    "Matilde, bienamada, diadema, bienvenida!"
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 57)
        registro.put(
            "descripcion", "Mienten los que dijeron que yo perdí la luna,\n" +
                    "los que profetizaron mi porvenir de arena,\n" +
                    "aseveraron tantas cosas con lenguas frías:\n" +
                    "quisieron prohibir la flor del universo.\n" +
                    "«Ya no cantará más el ámbar insurgente\n" +
                    "de la sirena, no tiene sino pueblo.»\n" +
                    "Y masticaban sus incesantes papeles\n" +
                    "patrocinando para mi guitarra el olvido.\n" +
                    "Yo les lancé a los ojos las lanzas deslumbrantes\n" +
                    "de nuestro amor clavando tu corazón y el mío,\n" +
                    "yo reclamé el jazmín que dejaban tus huellas,\n" +
                    "yo me perdí de noche sin luz bajo tus párpados\n" +
                    "y cuando me envolvió la claridad\n" +
                    "nací de nuevo, dueño de mi propia tiniebla."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 58)
        registro.put(
            "descripcion", "Entre los espadones de fierro literario\n" +
                    "paso yo como un marinero remoto\n" +
                    "que no conoce las esquinas y que canta\n" +
                    "porque sí, porque cómo si no fuera por eso.\n" +
                    "De los atormentados archipiélagos traje\n" +
                    "mi acordeón con borrascas, rachas de lluvia loca,\n" +
                    "y una costumbre lenta de cosas naturales:\n" +
                    "ellas determinaron mi corazón silvestre.\n" +
                    "Así cuando los dientes de la literatura\n" +
                    "trataron de morder mis honrados talones,\n" +
                    "yo pasé, sin saber, cantando con el viento\n" +
                    "hacia los almacenes lluviosos de mi infancia,\n" +
                    "hacia los bosques fríos del Sur indefinible,\n" +
                    "hacia donde mi vida se llenó con tu aroma. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 59)
        registro.put(
            "descripcion", "(G.M.)\n" +
                    "Pobres poetas a quienes la vida y la muerte\n" +
                    "persiguieron con la misma tenacidad sombría\n" +
                    "y luego son cubiertos por impasible pompa\n" +
                    "entregados al rito y al diente funerario.\n" +
                    "Ellos -oscuros como piedrecitas- ahora\n" +
                    "detrás de los caballos arrogantes, tendidos\n" +
                    "van, gobernados al fin por los intrusos,\n" +
                    "entre los edecanes, a dormir sin silencio.\n" +
                    "Antes y ya seguros de que está muerto el muerto\n" +
                    "hacen de las exequias un festín miserable\n" +
                    "con pavos, puercos y otros oradores.\n" +
                    "Acecharon su muerte y entonces la ofendieron:\n" +
                    "sólo porque su boca está cerrada\n" +
                    "y ya no puede contestar su canto."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 60)
        registro.put(
            "descripcion", "A ti te hiere aquel que quiso hacerme daño,\n" +
                    "y el golpe del veneno contra mí dirigido\n" +
                    "como por una red pasa entre mis trabajos\n" +
                    "y en ti deja una mancha de óxido y desvelo.\n" +
                    "No quiero ver, amor, en la luna florida\n" +
                    "de tu frente cruzar el odio que me acecha.\n" +
                    "No quiero que en tu sueño deje el rencor ajeno\n" +
                    "olvidada su inútil corona de cuchillos.\n" +
                    "Donde voy van detrás de mí pasos amargos,\n" +
                    "donde río una mueca de horror copia mi cara,\n" +
                    "donde canto la envidia maldice, ríe y roe.\n" +
                    "Y es ésa, amor, la sombra que la vida me ha dado:\n" +
                    "es un traje vacío que me sigue cojeando\n" +
                    "como un espantapájaros de sonrisa sangrienta."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 61)
        registro.put(
            "descripcion", "Trajo el amor su cola de dolores,\n" +
                    "su largo rayo estático de espinas\n" +
                    "y cerramos los ojos porque nada,\n" +
                    "porque ninguna herida nos separe.\n" +
                    "No es culpa de tus ojos este llanto:\n" +
                    "tus manos no clavaron esta espada:\n" +
                    "no buscaron tus pies este camino:\n" +
                    "llegó a tu corazón la miel sombría.\n" +
                    "Cuando el amor como una inmensa ola\n" +
                    "nos estrelló contra la piedra dura,\n" +
                    "nos amasó con una sola harina,\n" +
                    "cayó el dolor sobre otro dulce rostro\n" +
                    "y así en la luz de la estación abierta\n" +
                    "se consagró la primavera herida."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 62)
        registro.put(
            "descripcion", "Ay de mí, ay de nosotros, bienamada,\n" +
                    "sólo quisimos sólo amor, amarnos,\n" +
                    "y entre tantos dolores se dispuso\n" +
                    "sólo nosotros dos ser malheridos.\n" +
                    "Quisimos el tú y yo para nosotros,\n" +
                    "el tú del beso, el yo del pan secreto,\n" +
                    "y así era todo, eternamente simple,\n" +
                    "hasta que el odio entró por la ventana.\n" +
                    "Odian los que no amaron nuestro amor,\n" +
                    "ni ningún otro amor, desventurados\n" +
                    "como las sillas de un salón perdido,\n" +
                    "hasta que se enredaron en ceniza\n" +
                    "y el rostro amenazante que tuvieron\n" +
                    "se apagó en el crepúsculo apagado."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 63)
        registro.put(
            "descripcion", "No sólo por las tierras desiertas donde la piedra salina\n" +
                    "es como la única rosa, la flor por el mar enterrada,\n" +
                    "anduve, sino por la orilla de ríos que cortan la nieve.\n" +
                    "Las amargas alturas de las cordilleras conocen mis pasos.\n" +
                    "Enmarañada, silbante región de mi patria salvaje,\n" +
                    "lianas cuyo beso mortal se encadena en la selva,\n" +
                    "lamento mojado del ave que surge lanzando sus escalofríos,\n" +
                    "oh región de perdidos dolores y llanto inclemente!\n" +
                    "No sólo son míos la piel venenosa del cobre\n" +
                    "o el salitre extendido como estatua yacente y nevada,\n" +
                    "sino la viña, el cerezo premiado por la primavera,\n" +
                    "son míos, y yo pertenezco como átomo negro\n" +
                    "a las áridas tierras y a la luz del otoño en las uvas,\n" +
                    "a esta patria metálica elevada por torres de nieve."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 64)
        registro.put(
            "descripcion", "De tanto amor mi vida se tiñó de violeta\n" +
                    "y fui de rumbo en rumbo como las aves ciegas\n" +
                    "hasta llegar a tu ventana, amiga mía:\n" +
                    "tú sentiste un rumor de corazón quebrado\n" +
                    "y allí de la tinieblas me levanté a tu pecho,\n" +
                    "sin ser y sin saber fui a la torre del trigo,\n" +
                    "surgí para vivir entre tus manos,\n" +
                    "me levanté del mar a tu alegría.\n" +
                    "Nadie puede contar lo que te debo, es lúcido\n" +
                    "lo que te debo, amor, y es como una raíz\n" +
                    "natal de Araucanía, lo que te debo, amada.\n" +
                    "Es sin duda estrellado todo lo que te debo,\n" +
                    "lo que te debo es como el pozo de una zona silvestre\n" +
                    "en donde guardó el tiempo relámpagos errantes. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 65)
        registro.put(
            "descripcion", "Matilde, dónde estás? Noté, hacia abajo,\n" +
                    "entre corbata y corazón, arriba,\n" +
                    "cierta melancolía intercostal:\n" +
                    "era que tú de pronto eras ausente.\n" +
                    "Me hizo falta la luz de tu energía\n" +
                    "y miré devorando la esperanza,\n" +
                    "miré el vacío que es sin ti una casa,\n" +
                    "no quedan sino trágicas ventanas.\n" +
                    "De puro taciturno el techo escucha\n" +
                    "caer antiguas lluvias deshojadas,\n" +
                    "plumas, lo que la noche aprisionó:\n" +
                    "y así te espero como casa sola\n" +
                    "y volverás a verme y habitarme.\n" +
                    "De otro modo me duelen las ventanas."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 66)
        registro.put(
            "descripcion", "No te quiero sino porque te quiero\n" +
                    "y de quererte a no quererte llego\n" +
                    "y de esperarte cuando no te espero\n" +
                    "pasa mi corazón del frío al fuego.\n" +
                    "Te quiero sólo porque a ti te quiero,\n" +
                    "te odio sin fin, y odiándote te ruego,\n" +
                    "y la medida de mi amor viajero\n" +
                    "es no verte y amarte como un ciego.\n" +
                    "Tal vez consumirá la luz de Enero,\n" +
                    "su rayo cruel, mi corazón entero,\n" +
                    "robándome la llave del sosiego.\n" +
                    "En esta historia sólo yo me muero\n" +
                    "y moriré de amor porque te quiero,\n" +
                    "porque te quiero, amor, a sangre y fuego."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 67)
        registro.put(
            "descripcion", "La gran lluvia del sur cae sobre Isla Negra\n" +
                    "como una sola gota transparente y pesada,\n" +
                    "el mar abre sus hojas frías y la recibe,\n" +
                    "la tierra aprende el húmedo destino de una copa.\n" +
                    "Alma mía, dame en tus besos el agua\n" +
                    "salobre de estos mares, la miel del territorio,\n" +
                    "la fragancia mojada por mil labios del cielo,\n" +
                    "la paciencia sagrada del mar en el invierno.\n" +
                    "Algo nos llama, todas las puertas se abren solas,\n" +
                    "relata el agua un largo rumor a las ventanas,\n" +
                    "crece el cielo hacia abajo tocando las raíces,\n" +
                    "y así teje y desteje su red celeste el día\n" +
                    "con tiempo, sal, susurros, crecimientos, caminos,\n" +
                    "una mujer, un hombre, y el invierno en la tierra."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 68)
        registro.put(
            "descripcion", "(Mascarón de Proa)\n" +
                    "La niña de madera no llegó caminando:\n" +
                    "allí de pronto estuvo sentada en los ladrillos,\n" +
                    "viejas flores del mar cubrían su cabeza,\n" +
                    "su mirada tenía tristeza de raíces.\n" +
                    "Allí quedó mirando nuestras vidas abiertas,\n" +
                    "el ir y ser y andar y volver por la tierra,\n" +
                    "el día destiñendo sus pétalos graduales.\n" +
                    "Vigilaba sin vernos la niña de madera.\n" +
                    "La niña coronada por las antiguas olas,\n" +
                    "allí miraba con sus ojos derrotados:\n" +
                    "sabía que vivimos en una red remota\n" +
                    "de tiempo y agua y olas y sonidos y lluvia,\n" +
                    "sin saber si existimos o si somos su sueño.\n" +
                    "Ésta es la historia de la muchacha de madera. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 69)
        registro.put(
            "descripcion", "Tal vez no ser es ser sin que tú seas,\n" +
                    "sin que vayas cortando el mediodía\n" +
                    "como una flor azul, sin que camines\n" +
                    "más tarde por la niebla y los ladrillos,\n" +
                    "sin esa luz que llevas en la mano\n" +
                    "que tal vez otros no verán dorada,\n" +
                    "que tal vez nadie supo que crecía\n" +
                    "como el origen rojo de la rosa,\n" +
                    "sin que seas, en fin, sin que vinieras\n" +
                    "brusca, incitante, a conocer mi vida,\n" +
                    "ráfaga de rosal, trigo del viento,\n" +
                    "y desde entonces soy porque tú eres,\n" +
                    "y desde entonces eres, soy y somos,\n" +
                    "y por amor seré, serás, seremos."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 70)
        registro.put(
            "descripcion", "Tal vez herido voy sin ir sangriento\n" +
                    "por uno de los rayos de tu vida\n" +
                    "y a media selva me detiene el agua:\n" +
                    "la lluvia que se cae con su cielo.\n" +
                    "Entonces toco el corazón llovido:\n" +
                    "allí sé que tus ojos penetraron\n" +
                    "por la región extensa de mi duelo\n" +
                    "y un susurro de sombra surge solo:\n" +
                    "Quién es? Quién es? Pero no tuvo nombre\n" +
                    "la hoja o el agua oscura que palpita\n" +
                    "a media selva, sorda, en el camino,\n" +
                    "y así, amor mío, supe que fui herido\n" +
                    "y nadie hablaba allí sino la sombra,\n" +
                    "la noche errante, el beso de la lluvia. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 71)
        registro.put(
            "descripcion", "De pena en pena cruza sus islas el amor\n" +
                    "y establece raíces que luego riega el llanto,\n" +
                    "y nadie puede, nadie puede evadir los pasos\n" +
                    "del corazón que corre callado y carnicero.\n" +
                    "Así tú y yo buscamos un hueco, otro planeta\n" +
                    "en donde no tocara la sal tu cabellera,\n" +
                    "en donde no crecieran dolores por mi culpa,\n" +
                    "en donde viva el pan sin agonía.\n" +
                    "Un planeta enredado por distancia y follajes,\n" +
                    "un páramo, una piedra cruel y deshabitada,\n" +
                    "con nuestras propias manos hacer un nido duro,\n" +
                    "queríamos, sin daño ni herida ni palabra,\n" +
                    "y no fue así el amor, sino una ciudad loca\n" +
                    "donde la gente palidece en los balcones."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 72)
        registro.put(
            "descripcion", "Amor mío, el invierno regresa a sus cuarteles,\n" +
                    "establece la tierra sus dones amarillos\n" +
                    "y pasamos la mano sobre un país remoto,\n" +
                    "sobre la cabellera de la geografía.\n" +
                    "Irnos! Hoy! Adelante, ruedas, naves, campanas,\n" +
                    "aviones acerados por el diurno infinito\n" +
                    "hacia el olor nupcial del archipiélago,\n" +
                    "por longitudinales harinas de usufructo!\n" +
                    "Vamos, levántate, y endiadémate y sube\n" +
                    "y baja y corre y trina con el aire y conmigo\n" +
                    "vámonos a los trenes de Arabia o Tocopilla,\n" +
                    "sin más que trasmigrar hacia el polen lejano,\n" +
                    "a pueblos lancinantes de harapos y gardenias\n" +
                    "gobernados por pobres monarcas sin zapatos. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 73)
        registro.put(
            "descripcion", "Recordarás tal vez aquel hombre afilado\n" +
                    "que de la oscuridad salió como un cuchillo\n" +
                    "y antes de que supiéramos, sabía:\n" +
                    "vio el humo y decidió que venía del fuego.\n" +
                    "La pálida mujer de cabellera negra\n" +
                    "surgió como un pescado del abismo\n" +
                    "y entre los dos alzaron en contra del amor\n" +
                    "una máquina armada de dientes numerosos.\n" +
                    "Hombre y mujer talaron montañas y jardines,\n" +
                    "bajaron a los ríos, treparon por los muros,\n" +
                    "subieron por los montes su atroz artillería.\n" +
                    "El amor supo entonces que se llamaba amor.\n" +
                    "Y cuando levanté mis ojos a tu nombre\n" +
                    "tu corazón de pronto dispuso mi camino."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 74)
        registro.put(
            "descripcion", "El camino mojado por el agua de Agosto\n" +
                    "brilla como si fuera cortado en plena luna,\n" +
                    "en plena claridad de la manzana,\n" +
                    "en mitad de la fruta del otoño.\n" +
                    "Neblina, espacio o cielo, la vaga red del día\n" +
                    "crece con fríos sueños, sonidos y pescados,\n" +
                    "el vapor de las islas combate la comarca,\n" +
                    "palpita el mar sobre la luz de Chile.\n" +
                    "Todo se reconcentra como el metal, se esconden\n" +
                    "las hojas, el invierno enmascara su estirpe\n" +
                    "y sólo ciegos somos, sin cesar, solamente.\n" +
                    "Solamente sujetos al cauce sigiloso\n" +
                    "del movimiento, adiós, del viaje, del camino:\n" +
                    "adiós, caen las lágrimas de la naturaleza. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 75)
        registro.put(
            "descripcion", "Ésta es la casa, el mar y la bandera.\n" +
                    "Errábamos por otros largos muros.\n" +
                    "No hallábamos la puerta ni el sonido\n" +
                    "desde la ausencia, como desde muertos.\n" +
                    "Y al fin la casa abre su silencio,\n" +
                    "entramos a pisar el abandono,\n" +
                    "las ratas muertas, el adiós vacío,\n" +
                    "el agua que lloró en las cañerías.\n" +
                    "Lloró, lloró la casa noche y día,\n" +
                    "gimió con las arañas, entreabierta,\n" +
                    "se desgranó desde sus ojos negros,\n" +
                    "y ahora de pronto la volvemos viva,\n" +
                    "la poblamos y no nos reconoce:\n" +
                    "tiene que florecer, y no se acuerda."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 76)
        registro.put(
            "descripcion", "Diego Rivera con la paciencia del oso\n" +
                    "buscaba la esmeralda del bosque en la pintura\n" +
                    "o el bermellón, la flor súbita de la sangre\n" +
                    "recogía la luz del mundo en tu retrato.\n" +
                    "Pintaba el imperioso traje de tu nariz,\n" +
                    "la centella de tus pupilas desbocadas,\n" +
                    "tus uñas que alimentan la envidia de la luna,\n" +
                    "y en tu piel estival, tu boca de sandía.\n" +
                    "Te puso dos cabezas de volcán encendidas\n" +
                    "por fuego, por amor, por estirpe araucana,\n" +
                    "y sobre los dos rostros dorados de la greda\n" +
                    "te cubrió con el casco de un incendio bravío\n" +
                    "y allí secretamente quedaron enredados\n" +
                    "mis ojos en su torre total: tu cabellera. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 77)
        registro.put(
            "descripcion", "Hoy es hoy con el peso de todo el tiempo ido,\n" +
                    "con las alas de todo lo que será mañana,\n" +
                    "hoy es el Sur del mar, la vieja edad del agua\n" +
                    "y la composición de un nuevo día.\n" +
                    "A tu boca elevada a la luz o a la luna\n" +
                    "se agregaron los pétalos de un día consumido,\n" +
                    "y ayer viene trotando por su calle sombría\n" +
                    "para que recordemos su rostro que se ha muerto.\n" +
                    "Hoy, ayer y mañana se comen caminando,\n" +
                    "consumimos un día como una vaca ardiente,\n" +
                    "nuestro ganado espera con sus días contados,\n" +
                    "pero en tu corazón el tiempo echó su harina,\n" +
                    "mi amor construyó un horno con barro de Temuco:\n" +
                    "tú eres el pan de cada día para mi alma."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 78)
        registro.put(
            "descripcion", "No tengo nunca más, no tengo siempre. En la arena\n" +
                    "la victoria dejó sus pies perdidos.\n" +
                    "Soy un pobre hombre dispuesto a amar a sus semejantes.\n" +
                    "No sé quién eres. Te amo. No doy, no vendo espinas.\n" +
                    "Alguien sabrá tal vez que no tejí coronas\n" +
                    "sangrientas, que combatí la burla,\n" +
                    "y que en verdad llené la pleamar de mi alma.\n" +
                    "Yo pagué la vileza con palomas.\n" +
                    "Yo no tengo jamás porque distinto\n" +
                    "fui, soy, seré. Y en nombre\n" +
                    "de mi cambiante amor proclamo la pureza.\n" +
                    "La muerte es sólo piedra del olvido.\n" +
                    "Te amo, beso en tu boca la alegría.\n" +
                    "Traigamos leña. Haremos fuego en la montaña."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 79)
        registro.put(
            "descripcion", "De noche, amada, amarra tu corazón al mío\n" +
                    "y que ellos en el sueño derroten las tinieblas\n" +
                    "como un doble tambor combatiendo en el bosque\n" +
                    "contra el espeso muro de las hojas mojadas.\n" +
                    "Nocturna travesía, brasa negra del sueño\n" +
                    "interceptando el hilo de las uvas terrestres\n" +
                    "con la puntualidad de un tren descabellado\n" +
                    "que sombra y piedras frías sin cesar arrastrara.\n" +
                    "Por eso, amor, amárrame el movimiento puro,\n" +
                    "a la tenacidad que en tu pecho golpea\n" +
                    "con las alas de un cisne sumergido,\n" +
                    "para que a las preguntas estrelladas del cielo\n" +
                    "responda nuestro sueño con una sola llave,\n" +
                    "con una sola puerta cerrada por la sombra."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 80)
        registro.put(
            "descripcion", "De viajes y dolores yo regresé, amor mío,\n" +
                    "a tu voz, a tu mano volando en la guitarra,\n" +
                    "al fuego que interrumpe con besos el otoño,\n" +
                    "a la circulación de la noche en el cielo.\n" +
                    "Para todos los hombres pido pan y reinado,\n" +
                    "pido tierra para el labrador sin ventura,\n" +
                    "que nadie espere tregua de mi sangre o mi canto.\n" +
                    "Pero a tu amor no puedo renunciar sin morirme.\n" +
                    "Por eso toca el vals de la serena luna,\n" +
                    "la barcarola en el agua de la guitarra\n" +
                    "hasta que se doblegue mi cabeza soñando:\n" +
                    "que todos los desvelos de mi vida tejieron\n" +
                    "esta enramada en donde tu mano vive y vuela\n" +
                    "custodiando la noche del viajero dormido."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 81)
        registro.put(
            "descripcion", "Ya eres mía. Reposa con tu sueño en mi sueño.\n" +
                    "Amor, dolor, trabajos, deben dormir ahora.\n" +
                    "Gira la noche sobre sus invisibles ruedas\n" +
                    "y junto a mí eres pura como el ámbar dormido.\n" +
                    "Ninguna más, amor, dormirá con mis sueños.\n" +
                    "Irás, iremos juntos por las aguas del tiempo.\n" +
                    "Ninguna viajará por la sombra conmigo,\n" +
                    "sólo tú, siempreviva, siempre sol, siempre luna.\n" +
                    "Ya tus manos abrieron los puños delicados\n" +
                    "y dejaron caer suaves signos sin rumbo,\n" +
                    "tus ojos se cerraron como dos alas grises,\n" +
                    "mientras yo sigo el agua que llevas y me lleva:\n" +
                    "la noche, el mundo, el viento devanan su destino,\n" +
                    "y ya no soy sin ti sino sólo tu sueño."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 82)
        registro.put(
            "descripcion", "Amor mío, al cerrar esta puerta nocturna\n" +
                    "te pido, amor, un viaje por oscuro recinto:\n" +
                    "cierra tus sueños, entra con tu cielo en mis ojos,\n" +
                    "extiéndete en mi sangre como en un ancho río.\n" +
                    "Adiós, adiós, cruel claridad que fue cayendo\n" +
                    "en el saco de cada día del pasado,\n" +
                    "adiós a cada rayo de reloj o naranja,\n" +
                    "salud oh sombra, intermitente compañera!\n" +
                    "En esta nave o agua o muerte o nueva vida,\n" +
                    "una vez más unidos, dormidos, resurrectos,\n" +
                    "somos el matrimonio de la noche en la sangre.\n" +
                    "No sé quién vive o muere, quién reposa o despierta,\n" +
                    "pero es tu corazón el que reparte\n" +
                    "en mi pecho los dones de la aurora. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 83)
        registro.put(
            "descripcion", "Es bueno, amor, sentirte cerca de mí en la noche,\n" +
                    "invisible en tu sueño, seriamente nocturna,\n" +
                    "mientras yo desenredo mis preocupaciones\n" +
                    "como si fueran redes confundidas.\n" +
                    "Ausente, por los sueños tu corazón navega,\n" +
                    "pero tu cuerpo así abandonado respira\n" +
                    "buscándome sin verme, completando mi sueño\n" +
                    "como una planta que se duplica en la sombra.\n" +
                    "Erguida, serás otra que vivirá mañana,\n" +
                    "pero de las fronteras perdidas en la noche,\n" +
                    "de este ser y no ser en que nos encontramos\n" +
                    "algo queda acercándonos en la luz de la vida\n" +
                    "como si el sello de la sombra señalara\n" +
                    "con fuego sus secretas criaturas."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 84)
        registro.put(
            "descripcion", "Una vez más, amor, la red del día extingue\n" +
                    "trabajos, ruedas, fuegos, estertores, adioses,\n" +
                    "y a la noche entregamos el trigo vacilante\n" +
                    "que el mediodía obtuvo de la luz y la tierra.\n" +
                    "Sólo la luna en medio de su página pura\n" +
                    "sostiene las columnas del estuario del cielo,\n" +
                    "la habitación adopta la lentitud del oro\n" +
                    "y van y van tus manos preparando la noche.\n" +
                    "Oh amor, oh noche, oh cúpula cerrada por un río\n" +
                    "de impenetrables aguas en la sombra del cielo\n" +
                    "que destaca y sumerge sus uvas tempestuosas,\n" +
                    "hasta que sólo somos un solo espacio oscuro,\n" +
                    "una copa en que cae la ceniza celeste,\n" +
                    "una gota en el pulso de un lento y largo río."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 85)
        registro.put(
            "descripcion", "Del mar hacia las calles corre la vaga niebla\n" +
                    "como el vapor de un buey enterrado en el frío,\n" +
                    "y largas lenguas de agua se acumulan cubriendo\n" +
                    "el mes que a nuestras vidas prometió ser celeste.\n" +
                    "Adelantado otoño, panal silbante de hojas,\n" +
                    "cuando sobre los pueblos palpita tu estandarte\n" +
                    "cantan mujeres locas despidiendo a los ríos,\n" +
                    "los caballos relinchan hacia la Patagonia.\n" +
                    "Hay una enredadera vespertina en tu rostro\n" +
                    "que crece silenciosa por el amor llevada\n" +
                    "hasta las herraduras crepitantes del cielo.\n" +
                    "Me inclino sobre el fuego de tu cuerpo nocturno\n" +
                    "y no sólo tus senos amo sino el otoño\n" +
                    "que esparce por la niebla su sangre ultramarina."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 86)
        registro.put(
            "descripcion", "Oh Cruz del Sur, oh trébol de fósforo fragante,\n" +
                    "con cuatro besos hoy penetró tu hermosura\n" +
                    "y atravesó la sombra y mi sombrero:\n" +
                    "la luna iba redonda por el frío.\n" +
                    "Entonces con mi amor, con mi amada, oh diamantes\n" +
                    "de escarcha azul, serenidad del cielo,\n" +
                    "espejo, apareciste y se llenó la noche\n" +
                    "con tus cuatro bodegas temblorosas de vino.\n" +
                    "Oh palpitante plata de pez pulido y puro,\n" +
                    "cruz verde, perejil de la sombra radiante,\n" +
                    "luciérnaga a la unidad del cielo condenada,\n" +
                    "descansa en mí, cerremos tus ojos y los míos.\n" +
                    "Por un minuto duerme con la noche del hombre.\n" +
                    "Enciende en mí tus cuatro números constelados. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 87)
        registro.put(
            "descripcion", "Las tres aves del mar, tres rayos, tres tijeras\n" +
                    "cruzaron por el cielo frío hacia Antofagasta,\n" +
                    "por eso quedó el aire tembloroso,\n" +
                    "todo tembló como bandera herida.\n" +
                    "Soledad, dame el signo de tu incesante origen,\n" +
                    "el apenas camino de los pájaros crueles,\n" +
                    "y la palpitación que sin duda precede\n" +
                    "a la miel, a la música, al mar, al nacimiento.\n" +
                    "(Soledad sostenida por un constante rostro\n" +
                    "como una grave flor sin cesar extendida\n" +
                    "hasta abarcar la pura muchedumbre del cielo.)\n" +
                    "Volaban alas frías del mar, del Archipiélago,\n" +
                    "hacia la arena del Noroeste de Chile.\n" +
                    "Y la noche cerró su celeste cerrojo."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 88)
        registro.put(
            "descripcion", "El mes de Marzo vuelve con su luz escondida\n" +
                    "y se deslizan peces inmensos por el cielo,\n" +
                    "vago vapor terrestre progresa sigiloso,\n" +
                    "una por una caen al silencio las cosas.\n" +
                    "Por suerte en esta crisis de atmósfera errabunda\n" +
                    "reuniste las vidas del mar con las del fuego,\n" +
                    "el movimiento gris de la nave de invierno,\n" +
                    "la forma que el amor imprimió a la guitarra.\n" +
                    "Oh amor, rosa mojada por sirenas y espumas,\n" +
                    "fuego que baila y sube la invisible escalera\n" +
                    "y despierta en el túnel del insomnio a la sangre\n" +
                    "para que se consuman las olas en el cielo,\n" +
                    "olvide el mar sus bienes y leones\n" +
                    "y caiga el mundo adentro de las redes oscuras. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 89)
        registro.put(
            "descripcion", "Cuando yo muera quiero tus manos en mis ojos:\n" +
                    "quiero la luz y el trigo de tus manos amadas\n" +
                    "pasar una vez más sobre mí su frescura:\n" +
                    "sentir la suavidad que cambió mi destino.\n" +
                    "Quiero que vivas mientras yo, dormido, te espero,\n" +
                    "quiero que tus oídos sigan oyendo el viento,\n" +
                    "que huelas el aroma del mar que amamos juntos\n" +
                    "y que sigas pisando la arena que pisamos.\n" +
                    "Quiero que lo que amo siga vivo\n" +
                    "y a ti te amé y canté sobre todas las cosas,\n" +
                    "por eso sigue tú floreciendo, florida,\n" +
                    "para que alcances todo lo que mi amor te ordena,\n" +
                    "para que se pasee mi sombra por tu pelo,\n" +
                    "para que así conozcan la razón de mi canto."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 90)
        registro.put(
            "descripcion", "Pensé morir, sentí de cerca el frío,\n" +
                    "y de cuanto viví sólo a ti te dejaba:\n" +
                    "tu boca eran mi día y mi noche terrestres\n" +
                    "y tu piel la república fundada por mis besos.\n" +
                    "En ese instante se terminaron los libros,\n" +
                    "la amistad, los tesoros sin tregua acumulados,\n" +
                    "la casa transparente que tú y yo construimos:\n" +
                    "todo dejó de ser, menos tus ojos.\n" +
                    "Porque el amor, mientras la vida nos acosa,\n" +
                    "es simplemente una ola alta sobre las olas,\n" +
                    "pero ay cuando la muerte viene a tocar a la puerta\n" +
                    "hay sólo tu mirada para tanto vacío,\n" +
                    "sólo tu claridad para no seguir siendo,\n" +
                    "sólo tu amor para cerrar la sombra. \n"
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 91)
        registro.put(
            "descripcion", "La edad nos cubre como la llovizna,\n" +
                    "interminable y árido es el tiempo,\n" +
                    "una pluma de sal toca tu rostro,\n" +
                    "una gotera carcomió mi traje:\n" +
                    "el tiempo no distingue entre mis manos\n" +
                    "o un vuelo de naranjas en las tuyas:\n" +
                    "pica con nieve y azadón la vida:\n" +
                    "la vida tuya que es la vida mía.\n" +
                    "La vida mía que te di se llena\n" +
                    "de años, como el volumen de un racimo.\n" +
                    "Regresarán las uvas a la tierra.\n" +
                    "Y aún allá abajo el tiempo sigue siendo,\n" +
                    "esperando, lloviendo sobre el polvo,\n" +
                    "ávido de borrar hasta la ausencia."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 92)
        registro.put(
            "descripcion", "Amor mío, si muero y tú no mueres,\n" +
                    "no demos al dolor más territorio:\n" +
                    "amor mío, si mueres y no muero,\n" +
                    "no hay extensión como la que vivimos.\n" +
                    "Polvo en el trigo, arena en las arenas\n" +
                    "el tiempo, el agua errante, el viento vago\n" +
                    "nos llevó como grano navegante.\n" +
                    "Pudimos no encontrarnos en el tiempo.\n" +
                    "Esta pradera en que nos encontramos,\n" +
                    "oh pequeño infinito! devolvemos.\n" +
                    "Pero este amor, amor, no ha terminado,\n" +
                    "y así como no tuvo nacimiento\n" +
                    "no tiene muerte, es como un largo río,\n" +
                    "sólo cambia de tierras y de labios. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 93)
        registro.put(
            "descripcion", "Si alguna vez tu pecho se detiene,\n" +
                    "si algo deja de andar ardiendo por tus venas,\n" +
                    "si tu voz en tu boca se va sin ser palabra,\n" +
                    "si tus manos se olvidan de volar y se duermen,\n" +
                    "Matilde, amor, deja tus labios entreabiertos\n" +
                    "porque ese último beso debe durar conmigo,\n" +
                    "debe quedar inmóvil para siempre en tu boca\n" +
                    "para que así también me acompañe en mi muerte.\n" +
                    "Me moriré besando tu loca boca fría,\n" +
                    "abrazando el racimo perdido de tu cuerpo,\n" +
                    "y buscando la luz de tus ojos cerrados.\n" +
                    "Y así cuando la tierra reciba nuestro abrazo\n" +
                    "iremos confundidos en una sola muerte\n" +
                    "a vivir para siempre la eternidad de un beso."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 94)
        registro.put(
            "descripcion", "Si muero sobrevíveme con tanta fuerza pura\n" +
                    "que despiertes la furia del pálido y del frío,\n" +
                    "de sur a sur levanta tus ojos indelebles,\n" +
                    "de sol a sol que suene tu boca de guitarra.\n" +
                    "No quiero que vacilen tu risa ni tus pasos,\n" +
                    "no quiero que se muera mi herencia de alegría,\n" +
                    "no llames a mi pecho, estoy ausente.\n" +
                    "Vive en mi ausencia como en una casa.\n" +
                    "Es una casa tan grande la ausencia\n" +
                    "que pasarás en ella a través de los muros\n" +
                    "y colgarás los cuadros en el aire.\n" +
                    "Es una casa tan transparente la ausencia\n" +
                    "que yo sin vida te veré vivir\n" +
                    "y si sufres, mi amor, me moriré otra vez. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 95)
        registro.put(
            "descripcion", "Quiénes se amaron como nosotros? Busquemos\n" +
                    "las antiguas cenizas del corazón quemado\n" +
                    "y allí que caigan uno por uno nuestros besos\n" +
                    "hasta que resucite la flor deshabitada.\n" +
                    "Amemos el amor que consumió su fruto\n" +
                    "y descendió a la tierra con rostro y poderío:\n" +
                    "tú y yo somos la luz que continúa,\n" +
                    "su inquebrantable espiga delicada.\n" +
                    "Al amor sepultado por tanto tiempo frío,\n" +
                    "por nieve y primavera, por olvido y otoño,\n" +
                    "acerquemos la luz de una nueva manzana,\n" +
                    "de la frescura abierta por una nueva herida,\n" +
                    "como el amor antiguo que camina en silencio\n" +
                    "por una eternidad de bocas enterradas."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 96)
        registro.put(
            "descripcion", "Pienso, esta época en que tú me amaste\n" +
                    "se irá por otra azul sustituida,\n" +
                    "será otra piel sobre los mismos huesos,\n" +
                    "otros ojos verán la primavera.\n" +
                    "Nadie de los que ataron esta hora,\n" +
                    "de los que conversaron con el humo,\n" +
                    "gobiernos, traficantes, transeúntes,\n" +
                    "continuarán moviéndose en sus hilos.\n" +
                    "Se irán los crueles dioses con anteojos,\n" +
                    "los peludos carnívoros con libro,\n" +
                    "los pulgones y los pipipasseyros.\n" +
                    "Y cuando esté recién lavado el mundo\n" +
                    "nacerán otros ojos en el agua\n" +
                    "y crecerá sin lágrimas el trigo. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 97)
        registro.put(
            "descripcion", "Hay que volar en este tiempo, a dónde?\n" +
                    "Sin alas, sin avión, volar sin duda:\n" +
                    "ya los pasos pasaron sin remedio,\n" +
                    "no elevaron los pies del pasajero.\n" +
                    "Hay que volar a cada instante como\n" +
                    "las águilas, las moscas y los días,\n" +
                    "hay que vencer los ojos de Saturno\n" +
                    "y establecer allí nuevas campanas.\n" +
                    "Ya no bastan zapatos ni caminos,\n" +
                    "ya no sirve la tierra a los errantes,\n" +
                    "ya cruzaron la noche las raíces,\n" +
                    "y tú aparecerás en otra estrella\n" +
                    "determinadamente transitoria\n" +
                    "convertida por fin en amapola."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 98)
        registro.put(
            "descripcion", "Y esta palabra, este papel escrito\n" +
                    "por las mil manos de una sola mano,\n" +
                    "no queda en ti, no sirve para sueños,\n" +
                    "cae a la tierra: allí se continúa.\n" +
                    "No importa que la luz o la alabanza\n" +
                    "se derramen y salgan de la copa\n" +
                    "si fueron un tenaz temblor del vino,\n" +
                    "si se tiñó tu boca de amaranto.\n" +
                    "No quiere más la sílaba tardía,\n" +
                    "lo que trae y retrae el arrecife\n" +
                    "de mis recuerdos, la irritada espuma,\n" +
                    "no quiere más sino escribir tu nombre.\n" +
                    "Y aunque lo calle mi sombrío amor\n" +
                    "más tarde lo dirá la primavera. "
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 99)
        registro.put(
            "descripcion", "Otros días vendrán, será entendido\n" +
                    "el silencio de plantas y planetas\n" +
                    "y cuántas cosas puras pasarán!\n" +
                    "Tendrán olor a luna los violines!\n" +
                    "El pan será tal vez como tú eres:\n" +
                    "tendrá tu voz, tu condición de trigo,\n" +
                    "y hablarán otras cosas con tu voz:\n" +
                    "los caballos perdidos del Otoño.\n" +
                    "Aunque no sea como está dispuesto\n" +
                    "el amor llenará grandes barricas\n" +
                    "como la antigua miel de los pastores,\n" +
                    "y tú en el polvo de mi corazón\n" +
                    "(en donde habrán inmensos almacenes)\n" +
                    "irás y volverás entre sandías."
        )
        bd.insert("sonetos", null, registro)
        registro.put("codigo", 100)
        registro.put(
            "descripcion", "En medio de la tierra apartaré\n" +
                    "las esmeraldas para divisarte\n" +
                    "y tú estarás copiando las espigas\n" +
                    "con una pluma de agua mensajera.\n" +
                    "Qué mundo! Qué profundo perejil!\n" +
                    "Qué nave navegando en la dulzura!\n" +
                    "Y tú tal vez y yo tal vez topacio!\n" +
                    "Ya no habrá división en las campanas.\n" +
                    "Ya no habrá sino todo el aire libre,\n" +
                    "las manzanas llevadas por el viento,\n" +
                    "el suculento libro en la enramada,\n" +
                    "y allí donde respiran los claveles\n" +
                    "fundaremos un traje que resista\n" +
                    "la eternidad de un beso victorioso. "
        )
        bd.insert("sonetos", null, registro)
//        val cant = bd.update("articulos", registro, "codigo=${et1.text.toString()}", null)
//        }
        bd.close()
    }
}





