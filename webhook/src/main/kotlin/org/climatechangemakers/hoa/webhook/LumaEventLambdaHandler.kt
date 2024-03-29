package org.climatechangemakers.hoa.webhook

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.climatechangemakers.hoa.webhook.database.SelectUnsynced
import org.climatechangemakers.hoa.webhook.database.Database
import org.climatechangemakers.lambda.model.RawRequest
import org.climatechangemakers.lambda.model.RawResponse
import org.climatechangemakers.lambda.runtime.RawLambdaHandler

class LumaEventLambdaHandler(
  database: Database,
  private val lumaService: LumaService,
  private val clock: Clock,
) : RawLambdaHandler {

  private val hourOfActionEventQueries = database.hourOfActionEventQueries
  private val hourOfActionEventGuestQueries = database.hourOfActionEventGuestQueries

  override suspend fun invoke(request: RawRequest): RawResponse {
    syncEvents()
    syncGuests()
    return RawResponse("")
  }

  private suspend fun syncEvents() {
    val events = lumaService.getEvents().filter { it.secret != null }
    hourOfActionEventQueries.insertEvents(events)
    println("Inserted ${events.size} events.")
  }

  private suspend fun syncGuests() = coroutineScope {
    hourOfActionEventQueries.selectedUnsynced(clock.now()).executeAsList().forEach { unsynced ->
      launch { syncGuestsForEvent(unsynced) }
    }
  }

  private suspend fun syncGuestsForEvent(unsyncedEvent: SelectUnsynced) {
    val guests = lumaService.getGuestsForEvent(unsyncedEvent.secret)
    hourOfActionEventQueries.transaction {
      guests
        .asSequence()
        .filter { it.eventId != null }
        .forEach(hourOfActionEventGuestQueries::insertGuest)
      hourOfActionEventQueries.markSynced(unsyncedEvent.id)
    }
    println("Inserted ${guests.size} guests for event ${unsyncedEvent.id}.")
  }
}