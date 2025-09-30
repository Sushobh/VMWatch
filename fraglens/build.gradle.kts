import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.vanniktech.maven.publish")
}
val nav_version = "2.9.3"
android {
    namespace = "com.sushobh.fraglens"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(kotlin("reflect"))
    implementation("com.sushobh:androidrestserver:1.0.0")
    implementation(libs.gson.v2131)
    implementation("androidx.navigation:navigation-fragment:${nav_version}")
    implementation("androidx.navigation:navigation-ui:${nav_version}")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.2")
}


mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}

mavenPublishing {
    coordinates("com.sushobh", "fraglens", "0.1.14")

    pom {
        name.set("Frag Lens")
        description.set("Library to trace viewmodel properties using reflection")
        inceptionYear.set("2025")
        url.set("https://github.com/Sushobh/VMWatch")
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
            url.set("https://github.com/Sushobh/VMWatch")
            connection.set("scm:git:git://github.com/sushobh/VMWatch.git")
            developerConnection.set("scm:git:ssh://git@github.com/Sushobh/VMWatch.git")
        }
    }
}