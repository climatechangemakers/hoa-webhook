@file:JvmName("Main")

package org.climatechangemakers.hoa.event

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import kotlinx.coroutines.runBlocking
import org.climatechangemakers.lambda.runtime.runLambda
import org.postgresql.ds.PGSimpleDataSource
import org.climatechangemakers.hoa.event.database.Database

fun main() = runBlocking {
  val driver = PGSimpleDataSource().apply {
    serverNames = arrayOf(getEnvironmentVariable(EnvironmentVariable.DatabaseHostname))
    portNumbers = intArrayOf(getEnvironmentVariable(EnvironmentVariable.DatabasePort).toInt())
    user = getEnvironmentVariable(EnvironmentVariable.DatabaseUser)
    password = getEnvironmentVariable(EnvironmentVariable.DatabasePassword)
    databaseName = getEnvironmentVariable(EnvironmentVariable.DatabaseName)
  }.asJdbcDriver()
  runLambda(LumaEventLambdaHandler(Database(driver)))
}