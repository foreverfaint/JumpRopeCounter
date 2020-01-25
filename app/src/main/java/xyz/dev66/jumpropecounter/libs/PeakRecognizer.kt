package xyz.dev66.jumpropecounter.libs

import xyz.dev66.jumpropecounter.models.IndexedVolume
import xyz.dev66.jumpropecounter.models.MarkedVolume
import xyz.dev66.jumpropecounter.models.Volume
import java.util.*
import kotlin.collections.ArrayList

class PeakRecognizer {
    private val window = LinkedList<Volume>()

    fun add(volume: Int, currentInMillis: Long) {
        window.addLast(Volume(volume, currentInMillis))
    }

    fun computePeaks(firstInMillis: Long, lastInMillis: Long): Iterator<IndexedVolume> {
        val markedVolumes = detectPeakAndThough(window)
        val smoothedVolumes = smooth(markedVolumes)
        val markedSmoothedVolumes = detectPeakAndThough(smoothedVolumes)

        return iterator {
            for ((index, volume) in markedSmoothedVolumes
                .filter { v -> v.flag == 1 }
                .map { v -> v.volume }
                .withIndex()) {
                if (volume.currentInMillis in firstInMillis..lastInMillis) {
                    yield(IndexedVolume(volume, index + 1))
                }
            }
        }
    }

    private fun smooth(markedVolumes: ArrayList<MarkedVolume>): ArrayList<Volume> {
        // calculate the average value
        val averageVolume = window.map { v -> v.value }.average().toLong()

        // remove all the troughs with higher value than average
        // remove all the peaks with lower value than average
        val smoothVolumes = ArrayList<Volume>()
        for ((i, markedVolume) in markedVolumes.withIndex()) {
            if (markedVolume.flag == -1) {
                if (markedVolume.volume.value > averageVolume) {
                    // fill this trough with previous value
                    val prevMarkedVolume = markedVolumes[i - 1].volume
                    smoothVolumes.add(Volume(prevMarkedVolume.value, prevMarkedVolume.currentInMillis))
                    continue
                }
            } else if (markedVolume.flag == 1) {
                if (markedVolume.volume.value < averageVolume) {
                    // cut off this peak with previous value
                    val prevMarkedVolume = markedVolumes[i - 1].volume
                    smoothVolumes.add(Volume(prevMarkedVolume.value, prevMarkedVolume.currentInMillis))
                    continue
                }
            }

            smoothVolumes.add(Volume(markedVolume.volume.value, markedVolume.volume.currentInMillis))
        }

        return smoothVolumes
    }

    private fun detectPeakAndThough(volumes: Iterable<Volume>): ArrayList<MarkedVolume> {
        // remove the consecutive same values
        val cleanVolumes = ArrayList<Volume>()
        for (volume in volumes) {
            if (cleanVolumes.size == 0) {
                cleanVolumes.add(volume)
                continue
            }

            if (cleanVolumes[cleanVolumes.size - 1].value != volume.value) {
                cleanVolumes.add(volume)
            }
        }

        // mark the peak and the trough
        // the second value of Pair indicates the type of volume
        // 0 - not peak or trough
        // 1 - peak
        // -1 - trough
        val markedVolumes = ArrayList<MarkedVolume>()
        for ((i, volume) in cleanVolumes.withIndex()) {
            when (i) {
                0 -> markedVolumes.add(MarkedVolume(volume, 0))
                cleanVolumes.size - 1 -> markedVolumes.add(MarkedVolume(volume, 0))
                else -> {
                    val prev = cleanVolumes[i -1]
                    val next = cleanVolumes[i + 1]
                    if (prev.value > volume.value && next.value > volume.value) {
                        markedVolumes.add(MarkedVolume(volume, -1))
                    } else if (prev.value < volume.value && next.value < volume.value) {
                        markedVolumes.add(MarkedVolume(volume, 1))
                    } else {
                        markedVolumes.add(MarkedVolume(volume, 0))
                    }
                }
            }
        }

        return markedVolumes
    }
}