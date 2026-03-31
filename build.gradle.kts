plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.netra"
version = "0.0.1-SNAPSHOT"
description = "Parkirin Master Service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "gitlab"
        url = uri("https://repo.yujuism.com/api/v4/projects/2/packages/maven")
        credentials(HttpHeaderCredentials::class) {
            name = "Private-Token"
            value = System.getenv("GITLAB_TOKEN")
        }
        authentication {
            create("header", HttpHeaderAuthentication::class)
        }
    }
}

dependencies {
    // Spring Data R2DBC + PostgreSQL
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    // Core Spring WebFlux
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    // Spring Security (required by common-library)
    implementation("org.springframework.boot:spring-boot-starter-security")
    // Kotlin support
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    // SpringDoc OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.0.4")
    // Flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    // PostgreSQL
    implementation("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    // common-library
    implementation("com.netra.common:library:1.0.1")
    // Jackson datetime
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
