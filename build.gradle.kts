plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4"
    id("com.github.node-gradle.node") version "3.5.1"
}

group = "dev.camunda.bpmn"
version = "1.3.1"

repositories {
    mavenCentral()
}

intellij {
    version.set("2023.2.6")
    type.set("IC")
    plugins.set(listOf("com.intellij.java"))
}

node {
    version.set("23.1.0")
    download.set(true)
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("242.*")
    }

    val buildNpm = register<com.github.gradle.node.npm.task.NpmTask>("buildNpm") {
        args.set(listOf("run", "build"))
        workingDir.set(file("${project.projectDir}"))
    }

    buildPlugin {
        dependsOn(buildNpm)
    }
}

sourceSets {
    main {
        resources {
            srcDir("${project.projectDir}/build/public")
        }
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    implementation("org.picocontainer:picocontainer:2.15")
}