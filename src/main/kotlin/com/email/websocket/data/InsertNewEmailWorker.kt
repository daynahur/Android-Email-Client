package com.email.websocket.data

import com.email.R
import com.email.api.EmailInsertionAPIClient
import com.email.api.models.EmailMetadata
import com.email.bgworker.BackgroundWorker
import com.email.db.dao.EmailInsertionDao
import com.email.db.models.Email
import com.email.db.models.Label
import com.email.scenes.mailbox.data.EmailInsertionSetup
import com.email.signal.SignalClient
import com.email.utils.UIMessage
import com.github.kittinunf.result.Result

/**
 * Created by gabriel on 5/1/18.
 */
class InsertNewEmailWorker(private val emailInsertionDao: EmailInsertionDao,
                           private val emailInsertionApi: EmailInsertionAPIClient,
                           private val signalClient: SignalClient,
                           private val metadata: EmailMetadata,
                           override val publishFn: (EventResult.InsertNewEmail) -> Unit): BackgroundWorker<EventResult.InsertNewEmail> {

    override val canBeParallelized = false

    override fun catchException(ex: Exception): EventResult.InsertNewEmail {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun insertIncomingEmail() {
        val defaultLabels = Label.DefaultItems()
        val newEmailLabels = listOf(defaultLabels.inbox)

        EmailInsertionSetup.insertIncomingEmailTransaction(signalClient = signalClient,
                        dao = emailInsertionDao, apiClient = emailInsertionApi,
                        labels = newEmailLabels, metadata = metadata)
    }

    private fun loadNewEmail(): Email =
        emailInsertionDao.findEmailByBodyKey(metadata.bodyKey)

    override fun work(): EventResult.InsertNewEmail? {
        val result: Result<Email, Exception> = Result.of {
            insertIncomingEmail()
            loadNewEmail()
        }

        return when (result) {
            is Result.Success ->  EventResult.InsertNewEmail.Success(result.value)
            is Result.Failure -> {
                val errorMessage = result.error.message ?: result.error.javaClass.name
                val message = UIMessage(R.string.send_try_again_error, arrayOf(errorMessage))
                EventResult.InsertNewEmail.Failure(message)
            }
        }

    }

    override fun cancel() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}