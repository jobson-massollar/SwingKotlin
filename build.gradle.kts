plugins {
    id("org.jetbrains.kotlin.jvm") version "2.4.0"
    id("application")
    id("com.dorongold.task-tree") version "4.0.1"
}

application {
    mainClass = "main.MainKt"
}

group = "com.massollar.lib"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    runtimeOnly("com.formdev:flatlaf:3.5.4")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_23
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_4)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_4)
    }
}

tasks.jar {
    manifest {
        attributes(mapOf(
            "Main-Class" to "main.MainKt",
            "Class-Path" to configurations.runtimeClasspath.get().map { it -> it.name }.joinToString(" ")
        ))
    }
}

tasks.register<Copy>("copyDependencies") {
    from(configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("libs"))
}

tasks.named("build") {
    finalizedBy("copyDependencies")
}