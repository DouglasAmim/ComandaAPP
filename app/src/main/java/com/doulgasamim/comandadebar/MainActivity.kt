package com.doulgasamim.comandadebar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doulgasamim.comandadebar.ui.theme.ComandaDeBarTheme
import java.util.Locale


data class Item(val nome: String, val preco: Double)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComandaDeBarTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ComandaScreen()
                }
            }
        }
    }
}

@Composable
fun ComandaScreen() {
    val itens = listOf(
        Item("Cerveja", 8.0),
        Item("Refrigerante", 5.0),
        Item("Batata Frita", 20.0),
        Item("Espetinho", 12.0)
    )

    val mesas = remember { mutableStateListOf("Mesa 01") }
    var mesaSelecionadaIndex by remember { mutableStateOf(0) }

    val quantidadesPorMesa = remember {
        mutableStateListOf(
            mutableStateListOf(*Array(itens.size) { 0 })
        )
    }

    fun garantirQuantidadesParaMesa(index: Int) {
        while (quantidadesPorMesa.size <= index) {
            quantidadesPorMesa.add(mutableStateListOf(*Array(itens.size) { 0 }))
        }
    }

    garantirQuantidadesParaMesa(mesaSelecionadaIndex)

    val quantidades = quantidadesPorMesa[mesaSelecionadaIndex]

    var totalFinal by remember { mutableStateOf<Double?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {
                    if (mesaSelecionadaIndex > 0) {
                        mesaSelecionadaIndex--
                        totalFinal = null
                    }
                },
                enabled = mesaSelecionadaIndex > 0
            ) {
                Text("<", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            TextField(
                value = mesas[mesaSelecionadaIndex],
                onValueChange = { novoNome ->
                    mesas[mesaSelecionadaIndex] = novoNome
                },
                singleLine = true,
                modifier = Modifier.width(150.dp),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
            )

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = {
                    if (mesaSelecionadaIndex < mesas.size - 1) {
                        mesaSelecionadaIndex++
                        totalFinal = null
                    }
                },
                enabled = mesaSelecionadaIndex < mesas.size - 1
            ) {
                Text(">", fontSize = 24.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    val novaMesaNumero = mesas.size + 1
                    mesas.add("Mesa ${novaMesaNumero.toString().padStart(2, '0')}")
                    mesaSelecionadaIndex = mesas.lastIndex
                    garantirQuantidadesParaMesa(mesaSelecionadaIndex)
                    totalFinal = null
                }
            ) {
                Text("Adicionar Mesa")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    if (mesas.size > 1) {
                        mesas.removeAt(mesaSelecionadaIndex)
                        quantidadesPorMesa.removeAt(mesaSelecionadaIndex)
                        mesaSelecionadaIndex = (mesaSelecionadaIndex - 1).coerceAtLeast(0)
                        totalFinal = null
                    }
                },
                enabled = mesas.size > 1
            ) {
                Text("Excluir Mesa")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        itens.forEachIndexed { index, item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(item.nome, fontSize = 18.sp)
                        Text("R$ %.2f".format(item.preco), fontSize = 14.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            if (quantidades[index] > 0) quantidades[index]--
                            totalFinal = null
                        }) {
                            Icon(Icons.Default.Remove, contentDescription = "Diminuir")
                        }
                        Text(
                            text = quantidades[index].toString(),
                            modifier = Modifier.width(24.dp),
                            textAlign = TextAlign.Center
                        )
                        IconButton(onClick = {
                            quantidades[index]++
                            totalFinal = null
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Aumentar")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                try {
                    val subtotal = itens.indices.sumOf { i -> itens[i].preco * quantidades[i] }
                    val taxa = subtotal * 0.20
                    totalFinal = subtotal + taxa
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Calcular Comanda", fontSize = 18.sp)
        }

        totalFinal?.let {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(
                    text = String.format(Locale("pt", "BR"), "💵 Total a pagar: R$ %.2f", it),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
