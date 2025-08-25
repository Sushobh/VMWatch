package com.sushobh.fraglens.serializers

import com.google.gson.Gson
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class SafeGson private constructor(
    private val gson: Gson,
    private val maxDepth: Int,
    private val maxSize: Int,
    private val timeoutMs: Long
) {
    companion object {
        fun toJson(
            obj: Any?,
            maxDepth: Int = 10,
            maxSize: Int = 10000,
            timeoutMs: Long = 300
        ): String {
            val safe = SafeGson(Gson(), maxDepth, maxSize, timeoutMs)
            return safe.serialize(obj)
        }
    }

    private fun serialize(obj: Any?): String {
        return runWithTimeout(timeoutMs) {
            val stringWriter = StringWriter()
            val limitingWriter = SizeLimitingWriter(stringWriter, maxSize)
            val depthWriter = DepthLimitingJsonWriter(limitingWriter, maxDepth)

            try {
                gson.toJson(obj, Any::class.java, depthWriter)
                depthWriter.flush()
                stringWriter.toString()
            } catch (e: IOException) {
                // size limit reached — just return whatever has been serialized
                stringWriter.toString()
            } catch (e: Exception) {
                SafeMoshi.toJson(obj)
            }
        }
    }

    private fun runWithTimeout(timeoutMs: Long, block: () -> String): String {
        val executor = Executors.newSingleThreadExecutor()
        return try {
            val future = executor.submit(Callable { block() })
            future.get(timeoutMs, TimeUnit.MILLISECONDS)
        } catch (e: TimeoutException) {
            "\"<Serialization timed out>\""
        } catch (e: Exception) {
            "\"<Serialization failed: ${e.message}>\""
        } finally {
            executor.shutdownNow()
        }
    }

    // Writer that stops writing after maxSize but doesn't throw
    private class SizeLimitingWriter(
        private val out: Writer,
        private val maxSize: Int
    ) : Writer() {
        private var count = 0

        override fun write(cbuf: CharArray, off: Int, len: Int) {
            val allowed = maxSize - count
            if (allowed <= 0) return // ignore extra chars
            val toWrite = len.coerceAtMost(allowed)
            out.write(cbuf, off, toWrite)
            count += toWrite
        }

        override fun flush() = out.flush()
        override fun close() = out.close()
    }

    // JsonWriter that stops recursion after maxDepth but keeps already serialized content
    private class DepthLimitingJsonWriter(
        out: Writer,
        private val maxDepth: Int
    ) : JsonWriter(out) {
        private var depth = 0

        override fun beginObject(): JsonWriter {
            if (depth >= maxDepth) return this // skip deeper levels
            depth++
            return super.beginObject()
        }

        override fun endObject(): JsonWriter {
            if (depth > 0) depth--
            return super.endObject()
        }

        override fun beginArray(): JsonWriter {
            if (depth >= maxDepth) return this // skip deeper levels
            depth++
            return super.beginArray()
        }

        override fun endArray(): JsonWriter {
            if (depth > 0) depth--
            return super.endArray()
        }

    }
}
