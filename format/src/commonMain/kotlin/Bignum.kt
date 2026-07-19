package com.juul.krayon.format

internal class Bignum private constructor(private val words: IntArray) {

    fun isZero(): Boolean = words.isEmpty()

    fun compareTo(other: Bignum): Int {
        if (words.size != other.words.size) return words.size.compareTo(other.words.size)
        for (i in words.indices.reversed()) {
            if (words[i] != other.words[i]) return words[i].compareTo(other.words[i])
        }
        return 0
    }

    fun timesSmall(multiplier: Int): Bignum {
        if (multiplier == 0 || isZero()) return ZERO
        if (multiplier == 1) return this
        val result = IntArray(words.size + 1)
        var carry = 0L
        for (i in words.indices) {
            val current = words[i].toLong() * multiplier + carry
            result[i] = (current % BASE).toInt()
            carry = current / BASE
        }
        result[words.size] = carry.toInt()
        return of(result)
    }

    fun timesPow(base: Int, exponent: Int): Bignum {
        if (exponent <= 0) return this
        val (chunk, chunkExponent) = when (base) {
            2 -> CHUNK_TWO
            5 -> CHUNK_FIVE
            10 -> CHUNK_TEN
            else -> error("Unsupported base $base")
        }
        var result = this
        var remaining = exponent
        while (remaining >= chunkExponent) {
            result = result.timesSmall(chunk)
            remaining -= chunkExponent
        }
        var tail = 1
        repeat(remaining) { tail *= base }
        if (tail != 1) result = result.timesSmall(tail)
        return result
    }

    fun plus(other: Bignum): Bignum {
        if (isZero()) return other
        if (other.isZero()) return this
        val size = maxOf(words.size, other.words.size) + 1
        val result = IntArray(size)
        var carry = 0L
        for (i in 0 until size) {
            val a = if (i < words.size) words[i].toLong() else 0L
            val b = if (i < other.words.size) other.words[i].toLong() else 0L
            val sum = a + b + carry
            result[i] = (sum % BASE).toInt()
            carry = sum / BASE
        }
        return of(result)
    }

    /** Returns `floor(this / 10^exponent)`. */
    fun divPow10(exponent: Int): Bignum {
        if (exponent <= 0 || isZero()) return this
        val wholeWords = exponent / DIGITS_PER_WORD
        val remainderDigits = exponent % DIGITS_PER_WORD
        if (wholeWords >= words.size) return ZERO
        var shifted = of(words.copyOfRange(wholeWords, words.size))
        if (remainderDigits > 0) {
            var divisor = 1
            repeat(remainderDigits) { divisor *= 10 }
            shifted = shifted.divModSmall(divisor).first
        }
        return shifted
    }

    fun divModSmall(divisor: Int): Pair<Bignum, Int> {
        val result = IntArray(words.size)
        var remainder = 0L
        for (i in words.indices.reversed()) {
            val current = remainder * BASE + words[i]
            result[i] = (current / divisor).toInt()
            remainder = current % divisor
        }
        return of(result) to remainder.toInt()
    }

    fun toDecimalString(): String {
        if (isZero()) return "0"
        return buildString {
            append(words.last())
            for (i in words.size - 2 downTo 0) {
                val chunk = words[i].toString()
                repeat(DIGITS_PER_WORD - chunk.length) { append('0') }
                append(chunk)
            }
        }
    }

    fun toStringRadix(radix: Int): String {
        if (isZero()) return "0"
        val digits = StringBuilder()
        var current = this
        while (!current.isZero()) {
            val (quotient, remainder) = current.divModSmall(radix)
            digits.append(RADIX_DIGITS[remainder])
            current = quotient
        }
        return digits.reverse().toString()
    }

    companion object {
        private const val DIGITS_PER_WORD = 9
        private const val BASE = 1_000_000_000L
        private const val RADIX_DIGITS = "0123456789abcdef"
        private val CHUNK_TWO = 536_870_912 to 29 // 2^29 < 10^9
        private val CHUNK_FIVE = 244_140_625 to 12 // 5^12 < 10^9
        private val CHUNK_TEN = 100_000_000 to 8 // 10^8 < 10^9

        val ZERO: Bignum = Bignum(IntArray(0))

        private fun of(rawWords: IntArray): Bignum {
            var size = rawWords.size
            while (size > 0 && rawWords[size - 1] == 0) size--
            if (size == 0) return ZERO
            return Bignum(if (size == rawWords.size) rawWords else rawWords.copyOf(size))
        }

        fun of(value: Long): Bignum {
            require(value >= 0) { "Bignum only represents non-negative values." }
            if (value == 0L) return ZERO
            var remaining = value
            val words = ArrayList<Int>(3)
            while (remaining > 0) {
                words.add((remaining % BASE).toInt())
                remaining /= BASE
            }
            return of(words.toIntArray())
        }

        fun tenPow(exponent: Int): Bignum = of(1L).timesPow(10, exponent)
    }
}
