plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.noarg") version "1.9.25"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.9.25"
}


group = "org.tianea"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}
val javafxVersion = "21.0.2"

dependencies {

    implementation("org.openjfx:javafx-controls:$javafxVersion:mac-aarch64")
    implementation("org.openjfx:javafx-graphics:$javafxVersion:mac-aarch64")
    implementation("org.openjfx:javafx-base:$javafxVersion:mac-aarch64")
    implementation("org.optaplanner:optaplanner-core:10.0.0")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    
    // Swagger/OpenAPI 3 의존성 추가
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
    
    // Spring Boot 3.4.4와 호환되는 Elasticsearch 의존성
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    runtimeOnly("org.postgresql:postgresql:42.7.5")
    testImplementation("org.optaplanner:optaplanner-test:10.0.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.batch:spring-batch-test")
    testRuntimeOnly("com.h2database:h2:2.3.232")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    maxParallelForks = Runtime.getRuntime().availableProcessors()
}

gradle.startParameter.isParallelProjectExecutionEnabled = true

noArg {
    annotation("org.optaplanner.core.api.domain.solution.PlanningSolution")
    annotation("org.optaplanner.core.api.domain.entity.PlanningEntity")
}

tasks.withType<JavaExec>().configureEach {
    mainClass.set("org.tianea.boxrecommend.core.MainKt")
    jvmArgs = listOf(
        "--add-opens", "java.base/java.lang=ALL-UNNAMED"
    )
}

java {
    modularity.inferModulePath.set(true)
}