package com.email.websocket.data

import com.email.api.EmailInsertionAPIClient
import com.email.bgworker.BackgroundWorker
import com.email.bgworker.WorkHandler
import com.email.bgworker.WorkRunner
import com.email.db.dao.EmailInsertionDao
import com.email.signal.SignalClient

/**
 * Created by gabriel on 5/1/18.
 */

class EventDataSource(override val runner: WorkRunner,
                      private val emailInsertionDao: EmailInsertionDao,
                      private val emailInsertionAPIClient: EmailInsertionAPIClient,
                      private val signalClient: SignalClient): WorkHandler<EventRequest, EventResult>() {

    override fun createWorkerFromParams(params: EventRequest, flushResults: (EventResult) -> Unit): BackgroundWorker<*> {
        return when (params) {
            is EventRequest.InsertNewEmail -> InsertNewEmailWorker(
                    emailInsertionDao = emailInsertionDao, signalClient = signalClient,
                    emailInsertionApi = emailInsertionAPIClient, metadata = params.emailMetadata,
                    publishFn = flushResults)
        }
    }
}