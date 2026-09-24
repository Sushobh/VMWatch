import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm") version "2.0.21"
    `kotlin-dsl`
    id("com.vanniktech.maven.publish") version "0.32.0"
}

group = "com.sushobh"
version = "1.0.0"


dependencies {
    implementation(gradleApi())
    implementation("org.ow2.asm:asm:9.5")
    implementation("org.ow2.asm:asm-commons:9.5")
    implementation("com.android.tools.build:gradle:8.9.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

gradlePlugin {

    plugins {
        create("timerPlugin") {
            id = "com.sushobh.method-timer-plugin"
            displayName = "MethodTimer"
            description = "A Gradle plugin to transform Kotlin/Java byte code to wrap timer calculation"
            implementationClass = "com.sushobh.gradle.methodtimer.MethodTimerPlugin"
        }
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}

mavenPublishing {
    coordinates("com.sushobh", "method-timer-plugin", "1.0.7")

    pom {
        name.set("Method Timer")
        description.set("Gradle plugin to add timing log statements to methods")
        inceptionYear.set("2025")
        url.set("https://github.com/Sushobh/ViewModelMethodTracer")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("sushobh")
                name.set("Sushobh")
                url.set("https://github.com/sushobh/")
            }
        }
        scm {
            url.set("https://github.com/Sushobh/ViewModelMethodTracer")
            connection.set("scm:git:git://github.com/sushobh/ViewModelMethodTracer.git")
            developerConnection.set("scm:git:ssh://git@github.com/Sushobh/ViewModelMethodTracer.git")
        }
    }
}
