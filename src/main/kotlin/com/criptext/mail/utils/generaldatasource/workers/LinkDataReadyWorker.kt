package com.criptext.mail.utils.generaldatasource.workers

import com.criptext.mail.R
import com.criptext.mail.api.HttpClient
import com.criptext.mail.api.ServerErrorException
import com.criptext.mail.api.models.Event
import com.criptext.mail.bgworker.BackgroundWorker
import com.criptext.mail.bgworker.ProgressReporter
import com.criptext.mail.db.models.ActiveAccount
import com.criptext.mail.scenes.signin.data.SignInAPIClient
import com.criptext.mail.utils.ServerCodes
import com.criptext.mail.utils.UIMessage
import com.criptext.mail.utils.generaldatasource.data.GeneralResult
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.flatMap
import org.json.JSONObject


class LinkDataReadyWorker(private val activeAccount: ActiveAccount,
                          val httpClient: HttpClient,
                          override val publishFn: (GeneralResult) -> Unit)
    : BackgroundWorker<GeneralResult.LinkDataReady> {

    private val apiClient = SignInAPIClient(httpClient)

    override val canBeParallelized = false

    override fun catchException(ex: Exception): GeneralResult.LinkDataReady {
        return GeneralResult.LinkDataReady.Failure(createErrorMessage(ex), ex)
    }

    override fun work(reporter: ProgressReporter<GeneralResult.LinkDataReady>): GeneralResult.LinkDataReady? {
        val result = Result.of { apiClient.isLinkDataReady(activeAccount.jwt) }
                .flatMap { Result.of { Event.fromJSON(it.body) } }
                .flatMap { Result.of {
                    Pair(Pair(
                            JSONObject(it.params).getString("key"),
                            JSONObject(it.params).getString("dataAddress")
                    ), it.cmd)
                } }
                .flatMap { Result.of { apiClient.acknowledgeEvents(listOf(it.second.toLong()), activeAccount.jwt)
                it.first} }

        return when (result) {
            is Result.Success ->{

                GeneralResult.LinkDataReady.Success(key = result.value.first,
                        dataAddress = result.value.second)
            }
            is Result.Failure -> catchException(result.error)
        }
    }

    override fun cancel() {
        TODO("not implemented") //To change body of created functions use CRFile | Settings | CRFile Templates.
    }

    private val createErrorMessage: (ex: Exception) -> UIMessage = { ex ->
        when(ex){
            is ServerErrorException -> {
                when(ex.errorCode){
                    ServerCodes.BadRequest -> UIMessage(resId = R.string.no_devices_available)
                    ServerCodes.TooManyRequests -> UIMessage(resId = R.string.too_many_login_attempts)
                    ServerCodes.TooManyDevices -> UIMessage(resId = R.string.too_many_devices)
                    else -> UIMessage(resId = R.string.server_bad_status, args = arrayOf(ex.errorCode))
                }
            }
            else -> UIMessage(resId = R.string.unknown_error, args = arrayOf(ex.toString()))
        }
    }

}