package com.sushobh.gradle.methodtimer

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.commons.Method

internal class MethodTimerAdviceAdapter(
    methodVisitor: MethodVisitor,
    access: Int,
    methodName: String,
    methodDescriptor: String,
    private val className: String
) : AdviceAdapter(
    Opcodes.ASM9,
    methodVisitor,
    access,
    methodName,
    methodDescriptor
) {

    private val targetMethodName = methodName

    private var startTimeLocalIndex: Int = -1

    override fun onMethodEnter() {
        /*
         * Generates:
         *
         * long startTime = System.nanoTime();
         */

        invokeStatic(
             SYSTEM_TYPE,
            NANO_TIME_METHOD
        )

        startTimeLocalIndex = newLocal(Type.LONG_TYPE)
        storeLocal(startTimeLocalIndex, Type.LONG_TYPE)
    }

    override fun onMethodExit(opcode: Int) {
        /*
         * Generates:
         *
         * long durationNanos = System.nanoTime() - startTime;
         */

        invokeStatic(
             SYSTEM_TYPE,
            NANO_TIME_METHOD
        )

        loadLocal(startTimeLocalIndex, Type.LONG_TYPE)

        math(
            SUB,
            Type.LONG_TYPE
        )

        val durationLocalIndex = newLocal(Type.LONG_TYPE)
        storeLocal(durationLocalIndex, Type.LONG_TYPE)

        /*
         * Generates:
         *
         * Log.d(
         *     "SushobhMethodTimer",
         *     "com.example.MyClass.methodName -> 2.45 ms"
         * );
         */

        push( LOG_TAG)

        newInstance(STRING_BUILDER_TYPE)
        dup()

        invokeConstructor(
            STRING_BUILDER_TYPE,
             STRING_BUILDER_CONSTRUCTOR
        )

        push( "$className.$targetMethodName -> ")

        invokeVirtual(
            STRING_BUILDER_TYPE,
            APPEND_STRING_METHOD
        )

        loadLocal(durationLocalIndex, Type.LONG_TYPE)

        cast(
            Type.LONG_TYPE,
            Type.DOUBLE_TYPE
        )

        push( NANOSECONDS_PER_MILLISECOND)

        math(
            DIV,
            Type.DOUBLE_TYPE
        )

        invokeVirtual(
            STRING_BUILDER_TYPE,
            APPEND_DOUBLE_METHOD
        )

        push(" ms")

        invokeVirtual(
             STRING_BUILDER_TYPE,
            APPEND_STRING_METHOD
        )

        invokeVirtual(
            STRING_BUILDER_TYPE,
            TO_STRING_METHOD
        )

        invokeStatic(
            ANDROID_LOG_TYPE,
            LOG_DEBUG_METHOD
        )

        // Log.d() returns an Int, which must be removed from the stack.
        pop()
    }

    private companion object {

        private const val LOG_TAG = "SushobhMethodTimer"

        private const val NANOSECONDS_PER_MILLISECOND = 1_000_000.0

        private val SYSTEM_TYPE: Type =
            Type.getObjectType( "java/lang/System")

        private val STRING_BUILDER_TYPE: Type =
            Type.getObjectType("java/lang/StringBuilder")

        private val ANDROID_LOG_TYPE: Type =
            Type.getObjectType( "android/util/Log")

        private val NANO_TIME_METHOD =
            Method(
                 "nanoTime",
                 "()J"
            )

        private val STRING_BUILDER_CONSTRUCTOR =
            Method(
                 "<init>",
                "()V"
            )

        private val APPEND_STRING_METHOD =
            Method(
                 "append",
                 "(Ljava/lang/String;)Ljava/lang/StringBuilder;"
            )

        private val APPEND_DOUBLE_METHOD =
            Method(
                 "append",
                "(D)Ljava/lang/StringBuilder;"
            )

        private val TO_STRING_METHOD =
            Method(
                 "toString",
                 "()Ljava/lang/String;"
            )

        private val LOG_DEBUG_METHOD =
            Method(
                 "d",
                 "(Ljava/lang/String;Ljava/lang/String;)I"
            )
    }
}