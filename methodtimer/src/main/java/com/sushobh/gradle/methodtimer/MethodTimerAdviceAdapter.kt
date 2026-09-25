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
         * onMethodEnded(
         *     "com.example.MyClass.methodName",
         *     durationNanos / 1_000_000.0
         * )
         */

        push("$className.$targetMethodName")

        loadLocal(durationLocalIndex, Type.LONG_TYPE)

        cast(
            Type.LONG_TYPE,
            Type.DOUBLE_TYPE
        )

        push(NANOSECONDS_PER_MILLISECOND)

        math(
            DIV,
            Type.DOUBLE_TYPE
        )

        invokeStatic(
            METHOD_TIMER_TYPE,
            ON_METHOD_ENDED_METHOD
        )
    }

    private companion object {


        private const val NANOSECONDS_PER_MILLISECOND = 1_000_000.0

        private val SYSTEM_TYPE =
            Type.getObjectType("java/lang/System")

        private val METHOD_TIMER_TYPE =
            Type.getObjectType(
                "com/sushobh/fraglens/methodtimer/MethodTimerKt"
            )

        private val NANO_TIME_METHOD =
            Method(
                "nanoTime",
                "()J"
            )

        private val ON_METHOD_ENDED_METHOD =
            Method(
                "onMethodEnded9898",
                "(Ljava/lang/String;D)V"
            )
    }
}