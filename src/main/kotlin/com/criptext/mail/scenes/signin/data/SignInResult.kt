package com.criptext.mail.scenes.signin.data

import com.criptext.mail.db.models.ActiveAccount
import com.criptext.mail.scenes.settings.devices.data.DeviceItem
import com.criptext.mail.utils.UIMessage

/**
 * Created by sebas on 2/28/18.
 */

sealed class SignInResult {

    sealed class AuthenticateUser: SignInResult() {
        class Success: AuthenticateUser()
        data class Failure(
                val message: UIMessage,
                val exception: Exception): AuthenticateUser()
    }

    sealed class CheckUsernameAvailability: SignInResult() {
        data class Success(val userExists: Boolean, val username: String, val domain: String): CheckUsernameAvailability()
        data class Failure(val message: UIMessage): CheckUsernameAvailability()
    }

    sealed class ForgotPassword: SignInResult() {
        data class Success(val emailAddress: String): ForgotPassword()
        data class Failure(val message: UIMessage,
                val exception: Exception): ForgotPassword()
    }

    sealed class RecoveryCode: SignInResult() {
        data class Success(val isValidate: Boolean): RecoveryCode()
        data class Failure(val isValidate: Boolean, val message: UIMessage,
                val exception: Exception): RecoveryCode()
    }

    sealed class LinkBegin: SignInResult() {
        data class Success(val ephemeralJwt: String, val hasTwoFA: Boolean): LinkBegin()
        data class NoDevicesAvailable(val message: UIMessage): LinkBegin()
        data class NeedToRemoveDevices(val message: UIMessage): LinkBegin()
        data class Failure(val message: UIMessage): LinkBegin()
    }

    sealed class LinkAuth: SignInResult() {
        class Success: LinkAuth()
        data class Failure(val message: UIMessage,
                           val exception: Exception): LinkAuth()
    }

    sealed class CreateSessionFromLink: SignInResult() {
        data class Success(val activeAccount: ActiveAccount): CreateSessionFromLink()
        data class Failure(val message: UIMessage,
                           val exception: Exception): CreateSessionFromLink()
    }

    sealed class LinkData: SignInResult() {
        class Success: LinkData()
        data class Progress(val message: UIMessage, val progress: Int): LinkData()
        data class Failure(val message: UIMessage,
                           val exception: Exception): LinkData()
    }

    sealed class LinkStatus: SignInResult() {
        data class Success(val linkStatusData: LinkStatusData): LinkStatus()
        class Waiting: LinkStatus()
        class Denied: LinkStatus()
    }

    sealed class LinkDataReady: SignInResult() {
        data class Success(val key: String, val dataAddress: String): LinkDataReady()
        data class Failure(val message: UIMessage,
                           val exception: Exception): LinkDataReady()
    }

    sealed class FindDevices: SignInResult() {
        data class Success(val token: String, val devices: ArrayList<DeviceItem>): FindDevices()
        data class Failure(val message: UIMessage,
                           val exception: Exception): FindDevices()
    }

    sealed class RemoveDevices: SignInResult() {
        data class Success(val deviceIds: List<Int>): RemoveDevices()
        data class Failure(val message: UIMessage,
                           val exception: Exception): RemoveDevices()
    }
}
