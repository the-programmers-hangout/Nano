import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "me.elliott"
version = "0.0.1"
description = "A Minimalistic Q&A Bot."

plugins {
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

repositories {
    mavenCentral()
    jcenter()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation("me.jakejmattson:DiscordKt:${Versions.DISCORDKT}")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    shadowJar {
        archiveFileName.set("Nano.jar")
        manifest {
            attributes(
                    "Main-Class" to "me.elliott.nano.MainAppKt"
            )
        }
    }
}

object Versions {
    const val BOT = "1.0.0"
    const val DISCORDKT = "0.21.0-SNAPSHOT"
}