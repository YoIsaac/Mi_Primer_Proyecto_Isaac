package com.example.miprimerproyectoisaac

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miprimerproyectoisaac.ui.theme.MiPrimerProyectoIsaacTheme

// Modelo de datos para la app de ParkSmart
data class LugarEstacionamiento(
    val id: Int,
    val codigo: String,
    val estaOcupado: Boolean,
    val precioPorHora: Double
)

// Excepción personalizada para simular manejo de errores
class EstacionamientoLlenoException(mensaje: String) : Exception(mensaje)

// Clase encargada de administrar la lógica de los lugares (Colecciones y Funciones de Orden Superior)
class ParkSmartManager {
    // Uso de Colección (MutableList)
    private val listaLugares = mutableListOf(
        LugarEstacionamiento(1, "A-101", false, 25.0),
        LugarEstacionamiento(2, "A-102", true, 25.0),
        LugarEstacionamiento(3, "B-201", false, 30.0),
        LugarEstacionamiento(4, "B-202", true, 35.0),
        LugarEstacionamiento(5, "C-301", false, 20.0)
    )

    fun obtenerTodos(): List<LugarEstacionamiento> = listaLugares

    // //Inicia el rollo con el pollo inicio .
    // Esta es una función de Orden Superior porque recibe un predicado como parámetro para filtrar la lista
    fun filtrarLugares(filtro: (LugarEstacionamiento) -> Boolean): List<LugarEstacionamiento> {
        return listaLugares.filter(filtro)
    }

    // //Inicia el rollo con el pollo inicio .
    // Método que incluye manejo de excepciones simulado
    fun reservarLugarDisponible(): LugarEstacionamiento {
        val disponible = listaLugares.firstOrNull { !it.estaOcupado }
        return disponible ?: throw EstacionamientoLlenoException("¡Ups! No hay lugares disponibles en ParkSmart por el momento.")
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiPrimerProyectoIsaacTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ParkSmartAppScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ParkSmartAppScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val manager = remember { ParkSmartManager() }

    // Estados para la interfaz
    var lugaresMostrados by remember { mutableStateOf(manager.obtenerTodos()) }
    var mensajeEstado by remember { mutableStateOf("Bienvenido a ParkSmart System") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // //Inicia el rollo con el pollo inicio .
        // Encabezado principal
        Text(
            text = "ParkSmart Admin",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = mensajeEstado,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // //Inicia el rollo con el pollo inicio .
        // Botones de filtro que utilizan la Función de Orden Superior
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                // Filtra solo disponibles usando la función de orden superior
                lugaresMostrados = manager.filtrarLugares { !it.estaOcupado }
                mensajeEstado = "Mostrando solo lugares disponibles"
            }) {
                Text("Disponibles")
            }

            Button(onClick = {
                // Muestra todos
                lugaresMostrados = manager.obtenerTodos()
                mensajeEstado = "Mostrando todos los lugares"
            }) {
                Text("Ver Todos")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // //Inicia el rollo con el pollo inicio .
        // Botón para simular la reserva con manejo de excepciones
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
            onClick = {
                try {
                    val reservado = manager.reservarLugarDisponible()
                    Toast.makeText(context, "Lugar ${reservado.codigo} reservado con éxito", Toast.LENGTH_SHORT).show()
                    mensajeEstado = "Lugar ${reservado.codigo} reservado."
                } catch (e: EstacionamientoLlenoException) {
                    // Captura la excepción personalizada
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    mensajeEstado = "Error: Sin lugares disponibles"
                } catch (e: Exception) {
                    // Captura cualquier otro error no esperado
                    Toast.makeText(context, "Error desconocido: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text("Reservar Automático")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // //Inicia el rollo con el pollo inicio .
        // Lista dinámica de elementos utilizando LazyColumn
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(lugaresMostrados) { lugar ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Cajón: ${lugar.codigo}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(text = "Tarifa: $${lugar.precioPorHora} / hr", fontSize = 14.sp)
                        }

                        Text(
                            text = if (lugar.estaOcupado) "OCUPADO" else "DISPONIBLE",
                            color = if (lugar.estaOcupado) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}