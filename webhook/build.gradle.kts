import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.shadow)
  alias(libs.plugins.sqldelight)
  application
}

group = "org.climatechangemakers"
version = "0.0.1"

application {
  mainClass.set("org.climatechangemakers.hoa.webhook.Main")
}

tasks {
  named<ShadowJar>("shadowJar") {
    manifest {
      attributes["Main-Class"] = "org.climatechangemakers.hoa.webhook.Main"
    }
  }
}

sqldelight {
  database("Database") {
    packageName = "org.climatechangemakers.hoa.webhook.database"
    dialect(libs.sqldelight.postgresql.dialect.get().toString())
    deriveSchemaFromMigrations = false
    verifyMigrations = false
  }
}

dependencies {
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.postgresql)
  implementation(libs.sqldelight.jdbc.driver)
  implementation(libs.ktor.client.cio)
  implementation(libs.ktor.client.okhttp)
  implementation(project(":multiplatform-aws-lambda-runtime"))

  testImplementation(libs.kotlin.test)
  testImplementation(libs.testcontainers.postgresql)
}