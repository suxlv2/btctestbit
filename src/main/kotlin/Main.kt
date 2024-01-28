package com.suxlv2.btcbit

import com.google.gson.Gson
import com.google.gson.JsonArray
import java.io.File
import java.io.FileInputStream

data class Note(val octave: Int, val note: Int)

data class MusicalPiece(val notes: List<Note>)

fun main(args: Array<String>) {
    val change = args[1].toInt()

    val input = load(args)
    val transposed = transpose(input, change)
    validate(transposed)
    save(transposed)
}

private fun validate(musicalPiece: MusicalPiece) {
    musicalPiece.notes.forEach {
        if (it.octave < -3
            || (it.octave == -3 && it.note < 10)
            || it.octave > 5
            || (it.octave == 5 && it.note > 1)
        ) throw Exception("input is out of keyboard range")
    }
}

private fun load(args: Array<String>): MusicalPiece {
    FileInputStream(File(args[0]))
        .use {
            val input = Gson().fromJson(String(it.readAllBytes()), JsonArray::class.java)
            val notes = input.map { note -> Note(note.asJsonArray[0].asInt, note.asJsonArray[1].asInt) }

            return MusicalPiece(notes)
        }
}

private fun transpose(musicalPiece: MusicalPiece, change: Int): MusicalPiece {
    val tns = musicalPiece.notes.map {
        val changedNote = it.note + change

        val note = calculateNote(changedNote)
        val octave = calculateOctave(changedNote, it.octave)

        Note(octave, note)
    }

    return MusicalPiece(tns)
}

private fun save(transposed: MusicalPiece) {
    val ja = JsonArray(transposed.notes.size)
    transposed.notes.forEach {
        val jan = JsonArray(2)
        jan.add(it.octave)
        jan.add(it.note)
        ja.add(jan)
    }

    File("output.json")
        .writeText(ja.toString())
}

private fun calculateNote(changedNote: Int): Int {
    return if (changedNote < 0) {
        12 + changedNote
    } else if (changedNote > 12) {
        changedNote - 12
    } else {
        changedNote
    }
}

private fun calculateOctave(changedNote: Int, octave: Int): Int {
    return if (changedNote < 0) {
        octave - 1
    } else if (changedNote > 12) {
        octave + 1
    } else {
        octave
    }
}
