package com.gsabr.assetscare
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Patrimonio(

    val localizacao : String,
    val codfilial : String,
    val endereco : String,
    val dtcadastro : String,
    val dtultmanutencao : String,
    val codequip : String,
    val fabricante : String,
    val modelo : String,
    val descricao : String,
    val codfabrica : String,
    val numpatrimonio : String,
    val horimetro : String,
    val codfunccad : String,
    val dtcompra : String,
    val codlocal : String,
    val responsavel : String,
    val dtfimgarantia : String,
    val tagequip : String,
    val obs : String,
    val tipo : String,
    val codfuncultmanutencao : String,
    val nome_responsavel : String,
    val custo : String
)
