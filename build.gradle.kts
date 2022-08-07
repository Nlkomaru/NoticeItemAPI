/*
 *     Written in 2022  by Nikomaru <nikomaru@nikomaru.dev>
 *
 *     To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide.
 *     This software is distributed without any warranty.
 *
 *     You should have received a copy of the CC0 Public Domain Dedication along with this software.
 *     If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default

plugins {
    id("java")
    id("eclipse")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.0.1"
    kotlin("jvm") version "1.6.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    kotlin("plugin.serialization") version "1.6.10"
    id("maven-publish")
}

group = "com.noticemc"     // need to change
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://plugins.gradle.org/m2/")
    maven("https://repo.incendo.org/content/repositories/snapshots")
}

val cloudVersion = "1.7.0"
dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")

    implementation("cloud.commandframework:cloud-annotations:$cloudVersion")
    implementation("cloud.commandframework:cloud-core:$cloudVersion")
    implementation("cloud.commandframework:cloud-kotlin-extensions:$cloudVersion")
    implementation("cloud.commandframework:cloud-paper:$cloudVersion")

    implementation("com.github.guepardoapps:kulid:2.0.0.0")

    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.4.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.4.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10-RC")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}


tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
        kotlinOptions.javaParameters = true
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "17"
    }
    shadowJar {
        relocate("cloud.commandframework", "com.noticemc.noticeitemapi.shaded.cloud")
        relocate("io.leangen.geantyref", "com.noticemc.noticeitemapi.shaded.typetoken")
    }
    build {
        dependsOn(shadowJar)
    }
}

tasks {
    runServer {
        minecraftVersion("1.18.2")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = "noticeitemapi"
            version = "miencraft_plugin_version"

            from(components["java"])
        }
    }
}



bukkit {
    name = "NoticeItem" // need to change
    version = "miencraft_plugin_version"
    website = "https://github.com/Nlkomaru/NoticeItemAPI"

    main = "com.noticemc.noticeitemapi.NoticeItem"

    apiVersion = "1.18"

    libraries = listOf("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.4.0", "com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.4.0")

    permissions {
        register("noticeitemapi.admin") {
            children =
                listOf("noticeitemapi.command.remove", "noticeitemapi.command.open", "noticeitemapi.command.copy", "noticeitemapi.command.give")
            default = Default.OP
        }
        register("noticeitemapi.player") {
            children = listOf("noticeitemapi.command.open", "noticeitemapi.command.give")
            default = Default.TRUE
        }

    }
}