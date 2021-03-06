package com.criptext.mail.scenes.settings.data

sealed class SettingsRequest{
    class ResetPassword: SettingsRequest()
    class SyncBegin: SettingsRequest()
    data class UpdateSignature(val signature: String): SettingsRequest()
}