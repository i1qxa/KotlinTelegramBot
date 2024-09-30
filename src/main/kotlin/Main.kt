package org.example

import java.io.File

const val FILE_NAME = "words.txt"

fun main() {

    File(FILE_NAME).readLines().forEach {
        println(it)
    }

}