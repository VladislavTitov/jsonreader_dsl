package com.vlados.dsl.jsonreader

import android.util.JsonReader
import java.io.Reader

interface JsonNode<O> {
    fun parse(): O
}

fun <T> array(reader: Reader, private val block: Array<T>.() -> Unit): List<T> {
    val jsonReader = JsonReader(reader)
    val array = Array<T>(jsonReader, )
}

class Array<T : JsonNode<*>>(private val jsonReader: JsonReader, private val block: Array<T>.() -> Unit): JsonNode<List<T>> {
    private lateinit var child: JsonNode<*>

    fun <A : JsonNode<*>> array(block: Array<A>.() -> Unit) {
        child = Array(jsonReader, block)
    }

    fun `object`(block: Object<T>.() -> Unit) {
        child = Object(jsonReader, block)
    }

    override fun parse(): List<T> {
        block()
        val output = mutableListOf<T>()
        jsonReader.beginArray()
        while (jsonReader.hasNext()) {
            output.add(child.parse() as T)
        }
        jsonReader.endArray()
        return output
    }
}

class Object<T>(private val jsonReader: JsonReader, private val block: Object<T>.() -> Unit): JsonNode<T> {

    fun <A> array(name: String, parse: Array<A>.() -> Array<A>): List<A> {
        if (!jsonReader.hasNext()) {
            throw IllegalStateException("Has no more children!")
        }
        jsonReader.beginArray()
        val innerArray = Array<A>(jsonReader).parse().build()
        jsonReader.endArray()
        return innerArray
    }

    fun double(name: String): Double {
        if (jsonReader.hasNext()) {
            throw IllegalStateException("Has no more children!")
        }
        return jsonReader.nextDouble()
    }

    fun int(name: String): Int {
        if (jsonReader.hasNext()) {
            throw IllegalStateException("Has no more children!")
        }
        return jsonReader.nextInt()
    }

    fun string(name: String): String {
        if (jsonReader.hasNext()) {
            throw IllegalStateException("Has no more children!")
        }
        return jsonReader.nextString()
    }

    override fun parse() {
        TODO("Not yet implemented")
    }
}