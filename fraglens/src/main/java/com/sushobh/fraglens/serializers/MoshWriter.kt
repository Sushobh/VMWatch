package com.sushobh.fraglens.serializers

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Type

object SafeMoshi {

    private fun buildMoshi(maxDepth: Int): Moshi =
        Moshi.Builder()
            .add(DepthLimitingAdapterFactory(maxDepth))
            .add(KotlinJsonAdapterFactory())
            .build()

    /**
     * Serialize any object to JSON with depth limiting.
     */
    fun toJson(
        obj: Any?,
        maxDepth: Int = 5, // default limit
        pretty: Boolean = false
    ): String {
        if (obj == null) return "null"

        val moshi = buildMoshi(maxDepth)
        val adapter: JsonAdapter<Any> =
            moshi.adapter(obj.javaClass)

        return if (pretty) {
            adapter.indent("  ").toJson(obj)
        } else {
            adapter.toJson(obj)
        }
    }

    /**
     * Depth-limiting adapter factory.
     */
    private class DepthLimitingAdapterFactory(
        private val maxDepth: Int
    ) : JsonAdapter.Factory {

        override fun create(
            type: Type,
            annotations: Set<Annotation>,
            moshi: Moshi
        ): JsonAdapter<*>? {
            val delegate = moshi.nextAdapter<Any>(this, type, annotations)

            return object : JsonAdapter<Any>() {
                private var depth = 0

                override fun toJson(writer: JsonWriter, value: Any?) {
                    if (value == null) {
                        writer.nullValue()
                        return
                    }
                    if (depth >= maxDepth) {
                        // At max depth → cut off
                        writer.nullValue()
                        return
                    }

                    depth++
                    try {
                        delegate.toJson(writer, value)
                    } finally {
                        depth--
                    }
                }

                override fun fromJson(reader: JsonReader): Any? {
                    return delegate.fromJson(reader)
                }
            }
        }
    }
}
