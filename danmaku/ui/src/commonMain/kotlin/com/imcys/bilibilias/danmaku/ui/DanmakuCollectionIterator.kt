package com.imcys.bilibilias.danmaku.ui

class DanmakuCollectionIterator<T : SizeSpecifiedDanmaku, D>(
    private val tracks: List<DanmakuTrack<T, D>>
) : Iterator<D>, Iterable<D> {
    private var currentTrackIndex = 0
    private var currentTrackIterator: Iterator<D>? = null

    init {
        advanceToNextNonEmptyTrack()
    }

    private fun advanceToNextNonEmptyTrack() {
        currentTrackIterator = null

        while (currentTrackIndex < tracks.size) {
            val iterator = tracks[currentTrackIndex].iterator()
            if (iterator.hasNext()) {
                currentTrackIterator = iterator
                return
            }
            currentTrackIndex++
        }
    }

    override fun hasNext(): Boolean {
        // If current iterator is done, try to find next track with elements
        if (currentTrackIterator?.hasNext() != true && currentTrackIndex < tracks.size) {
            currentTrackIndex++
            advanceToNextNonEmptyTrack()
        }

        return currentTrackIterator?.hasNext() == true
    }

    override fun next(): D {
        if (!hasNext()) {
            throw NoSuchElementException("No more elements in danmaku collection")
        }

        val next = currentTrackIterator!!.next()

        // If current iterator is exhausted, prepare the next track's iterator
        if (!currentTrackIterator!!.hasNext()) {
            currentTrackIndex++
            advanceToNextNonEmptyTrack()
        }

        return next
    }

    override fun iterator(): Iterator<D> {
        return this
    }
}