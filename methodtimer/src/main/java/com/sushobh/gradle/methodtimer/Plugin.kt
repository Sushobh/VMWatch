package com.sushobh.gradle.methodtimer

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project


class MethodTimerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.subprojects {
            if (path == ":fraglens") {
                return@subprojects
            }
            plugins.withId("com.android.application") {
                setupTransformer()
            }
            plugins.withId("com.android.library") {
                setupTransformer()
            }
        }
    }

    private fun Project.setupTransformer() {
        val androidComponents = extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants { variant ->
            variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS)
            variant.instrumentation.transformClassesWith(
                MyMetTransformerFactory::class.java,
                InstrumentationScope.PROJECT
            ) { /* no params */ }

        }
    }

}
