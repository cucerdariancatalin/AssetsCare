package com.gsabr.assetscare
import kotlinx.serialization.Serializable

@Serializable
data class Order(
//    val cliente: String,

    val numos: String,
    val data: String,
    val numpatrimonio: String,
    val funcionario: String?,
    val codcli: String,
    val posos: String,
    val status: String?,

//    val datafim: String,

//    val numpatrimonio: String,
//    val solicitante: String,
//    val status: String,
//    val tipoatendimento: String
)