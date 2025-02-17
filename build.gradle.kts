import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.7"
}

// Library versions
val junit = property("junit") as String
val lombok = property("lombok") as String
// Project version and group
version = property("projectVersion") as String
group = "kz.hxncus.mc"

allprojects {
    apply(plugin = "java")
    tasks {
        processResources {
            filesMatching("**/plugin.yml") {
                expand("version" to rootProject . version, "name" to rootProject . name)
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            filteringCharset = Charsets.UTF_8.name()
        }
    }
    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/") // Paper
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://libraries.minecraft.net/") // Minecraft repo
        maven("https://maven.enginehub.org/repo/")
        maven("https://jitpack.io") // JitPack
        mavenLocal()
    }
    dependencies {
        compileOnly("org.projectlombok:lombok:$lombok")
        compileOnly(fileTree("../libs/compileOnly/"))

        implementation(fileTree("../libs/implementation/"))

        annotationProcessor("org.projectlombok:lombok:$lombok")
        testAnnotationProcessor("org.projectlombok:lombok:$lombok")
        testCompileOnly("org.projectlombok:lombok:$lombok")
        testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
    }
    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(project(path = ":bukkit"))
    implementation(project(path = ":1_20_4"))
}

tasks {
    compileJava.get().options.encoding = Charsets.UTF_8.name()
    javadoc.get().options.encoding = Charsets.UTF_8.name()
    processResources {
        filesNotMatching(listOf("**/*.png", "**/*.ogg", "**/models/**", "**/textures/**", "**/font/**.json", "**/plugin.yml")) {
            expand(mapOf(project.version.toString() to version))
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        filteringCharset = Charsets.UTF_8.name()
    }
    shadowJar {
        archiveClassifier.set("")
        relocate("org.bstats", rootProject.group.toString() + "." + rootProject.name.lowercase(Locale.ENGLISH) + ".metrics")
        relocate("ch.qos", rootProject.group.toString() + "." + rootProject.name.lowercase(Locale.ENGLISH) + ".libs.ch.qos")
        relocate("com", rootProject.group.toString() + "." + rootProject.name.lowercase(Locale.ENGLISH) + ".libs.com")
        relocate("it.unimi.dsi", rootProject.group.toString() + "." + rootProject.name.lowercase(Locale.ENGLISH) + ".libs.it.unimi.dsi")
        relocate("javax", rootProject.group.toString() + "." + rootProject.name.lowercase(Locale.ENGLISH) + ".libs.javax")
        relocate("org.apache", rootProject.group.toString() + "." + rootProject.name.lowercase(Locale.ENGLISH) + ".libs.org.apache")
        relocate("org.checkerframe", rootProject.group.toString() + "." + rootProject.name.lowercase(Locale.ENGLISH) + ".libs.org.checkerframe")
        relocate("org.codehaus", rootProject.group.toString() + "." + rootProject.name.lowercase(Locale.ENGLISH) + ".libs.org.codehaus")
        relocate("org.jooq", rootProject.group.toString() + "." + rootProject.name.lowercase(Locale.ENGLISH) + ".libs.org.jooq")
        relocate("org.reactivestreams", rootProject.group.toString() + "." + rootProject.name.lowercase(Locale.ENGLISH) + ".libs.org.reactivestreams")
        relocate("org.slf4j", rootProject.group.toString() + "." + rootProject.name.lowercase(Locale.ENGLISH) + ".libs.org.slf4j")
        relocate("xsd", rootProject.group.toString() + "." + rootProject.name.lowercase(Locale.ENGLISH) + ".libs.xsd")
        manifest {
            attributes(mapOf(
                "Built-By" to System . getProperty("user.name"),
                "Version" to version,
                "Build-Timestamp" to SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSSZ") . format(Date.from(Instant.now())),
                "Created-By" to "Gradle ${gradle.gradleVersion}",
                "Build-Jdk" to "${System.getProperty("java.version")} ${System.getProperty("java.vendor")} ${System.getProperty("java.vm.version")}",
                "Build-OS" to "${System.getProperty("os.name")} ${System.getProperty("os.arch")} ${System.getProperty("os.version")}",
                "Compiled" to(project.findProperty("compiled")?.toString() ?: "true").toBoolean()
            ))
        }
        archiveFileName.set(rootProject.name + "-${version}.jar")
        archiveClassifier.set("")
    }
    compileJava.get().dependsOn(clean)
    build.get().dependsOn(shadowJar)
}
