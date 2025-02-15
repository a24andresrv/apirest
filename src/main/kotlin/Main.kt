import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable


@Serializable
data class Meteo(
    val listDatosDiarios: List<DatosDiarios>
)


@Serializable
data class DatosDiarios(
    val `data`: String,
    val listaEstacions: List<ListaEstacions>
)


@Serializable
data class ListaEstacions(
    val concello: String,
    val estacion: String,
    val idEstacion: Int,
    val listaMedidas: List<ListaMedidas>,
    val provincia: String,
    val utmx: String,
    val utmy: String
)


@Serializable
data class ListaMedidas(
    val codigoParametro: String,
    val lnCodigoValidacion: Int,
    val nomeParametro: String,
    val unidade: String,
    val valor: Double
)




fun main() {


    val client = HttpClient.newHttpClient()


    // crear solicitud
    val request = HttpRequest.newBuilder()
        .uri(URI.create("https://servizos.meteogalicia.gal/mgrss/observacion/datosDiariosEstacionsMeteo.action"))
        .GET()
        .build()


    //  Enviar la solicitud con el cliente
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())


    // obtener string con datos
    val jsonBody = response.body()


    // Deserializar el JSON a una lista de objetos User
    val meteoData: Meteo = Json.decodeFromString(jsonBody)




    println("Consulta meteorológica")

    var concelloConsult=""
    println("Introduce o nombre do teu concello: ")
    println("(Se desexa sair escriba exit)")
    concelloConsult = readln().uppercase()
    while (concelloConsult!="EXIT") {





        if(concelloConsult=="CONCELLOS"){
            val listaConcellos = meteoData.listDatosDiarios.first().listaEstacions
                .asSequence()
                .map { it.concello }
                .sorted()
                .distinct()
                .toList()
            listaConcellos.forEach{ println(it)}
            val numerodeconcellos=listaConcellos.count()
            println("Hay $numerodeconcellos concellos")
        }
        else{
            val infoConcello = meteoData.listDatosDiarios.first().listaEstacions.asSequence().filter { it.concello == concelloConsult }.toList()

            if (infoConcello.isEmpty()) {
                println("Non se atopou o o concello seleccionado, é probable que exista algún error na escritura.")
                println("Se quere acceder ó listado de concellos, escriba concellos no campo de consulta")
            }
            else{
                println()
                println("Datos meteorolóxicos para $concelloConsult:\n")
                infoConcello.forEach { estacion ->
                    println("Estación: ${estacion.estacion}")
                    println("Provincia: ${estacion.provincia}")
                    println("Ubicación UTM: (${estacion.utmx}, ${estacion.utmy})")
                    println("Medicións:")

                    estacion.listaMedidas.forEach { medida ->
                        println("   - ${medida.nomeParametro}: ${medida.valor} ${medida.unidade}")
                    }
                    println("------------------------------------------------------") // Separador entre estaciones
                }
            }
        }
        println("Introduce o nombe do teu concello: ")
        println("(Se quere sair escriba exit)")
        concelloConsult= readln().uppercase()
    }
}