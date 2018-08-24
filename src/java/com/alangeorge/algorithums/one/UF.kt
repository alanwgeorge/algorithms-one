package com.alangeorge.algorithums.one

import edu.princeton.cs.algs4.StdIn
import java.util.*

class UFkt(val n: Int) {
    val ids = Array<Int>(n) {
        it
    }

    fun union(p: Int, q: Int) {
        ids.withIndex()
                .filter {
                    it.value == ids[p] || it.value == ids[q]
                }
                .forEach {
                    ids[it.index] = p
                }
    }

    fun connected(p: Int, q: Int): Boolean =
            p < n &&
                    q < n &&
                    ids[p] == ids[q]

    override fun toString(): String {
        return "UFkt(n=$n, ids=${Arrays.toString(ids)})"
    }
}

fun main(args: Array<String>) {
    with(UFkt(StdIn.readInt())) {
        while (!StdIn.isEmpty()) {
            val p = StdIn.readInt()
            val q = StdIn.readInt()

            if (!connected(p, q)) {
                union(p, q)
                println("connecting $p $q, $this")
            } else {
                println("already connected $p $q")
            }
        }
    }
}