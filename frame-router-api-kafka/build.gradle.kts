import org.jsonschema2pojo.InclusionLevel
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.jsonschema2pojo")
}

dependencies {
    api("jakarta.validation:jakarta.validation-api")
    api("com.fasterxml.jackson.core:jackson-databind")
}

jsonSchema2Pojo {
    targetPackage = "ru.kotlix.frame.router.api.kafka"
    useLongIntegers = true
    setInclusionLevel(InclusionLevel.NON_NULL.name)
    includeJsr303Annotations = true
    generateBuilders = true
    useJakartaValidation = true
    useTitleAsClassname = true
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
