package xyz.dev66.jumpropecounter.models

data class Volume(val value: Int, val currentInMillis: Long)

data class MarkedVolume(val volume: Volume, val flag: Int)

data class IndexedVolume(val volume: Volume, val index: Int)