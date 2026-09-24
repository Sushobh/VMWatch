package com.sushobh.gradle.methodtimer



import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class MyMethodVisitor(
    nextClassVisitor: ClassVisitor
) : ClassVisitor(Opcodes.ASM9, nextClassVisitor) {

    private lateinit var className: String
    private var classAccess: Int = 0

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        className = name.replace('/', '.')
        classAccess = access

        super.visit(
            version,
            access,
            name,
            signature,
            superName,
            interfaces
        )
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val nextMethodVisitor = super.visitMethod(
            access,
            name,
            descriptor,
            signature,
            exceptions
        )

        if (!shouldInstrumentMethod(access, methodName = name)) {
            return nextMethodVisitor
        }

        return MethodTimerAdviceAdapter(
            methodVisitor = nextMethodVisitor,
            access = access,
            methodName = name,
            methodDescriptor = descriptor,
            className = className
        )
    }

    private fun shouldInstrumentMethod(
        access: Int,
        methodName: String
    ): Boolean {
        // Ignore interfaces and annotations.
        if ((classAccess and Opcodes.ACC_INTERFACE) != 0) return false
        if ((classAccess and Opcodes.ACC_ANNOTATION) != 0) return false

        // Constructors and static initialisers.
        if (methodName == "<init>") return false
        if (methodName == "<clinit>") return false

        // Methods without executable bytecode.
        if ((access and Opcodes.ACC_ABSTRACT) != 0) return false
        if ((access and Opcodes.ACC_NATIVE) != 0) return false

        // Compiler-generated methods.
        if ((access and Opcodes.ACC_SYNTHETIC) != 0) return false
        if ((access and Opcodes.ACC_BRIDGE) != 0) return false

        return true
    }
}