
plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
}

group = "com.spireprod"
version = "0.1.20-Jyggalag"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    maven {
    	url = uri("https://jitpack.io")
    }
}

dependencies {
	implementation("com.googlecode.lanterna:lanterna:3.1.2")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    
    withJavadocJar()
    withSourcesJar()
}

