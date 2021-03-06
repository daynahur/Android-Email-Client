package com.criptext.mail.scenes.settings.privacy

import android.view.View
import android.widget.*
import com.criptext.mail.R
import com.criptext.mail.api.models.DeviceInfo
import com.criptext.mail.scenes.settings.Settings2FADialog
import com.criptext.mail.utils.KeyboardManager
import com.criptext.mail.utils.UIMessage
import com.criptext.mail.utils.getLocalizedUIMessage
import com.criptext.mail.utils.ui.*
import com.criptext.mail.utils.ui.data.DialogType
import com.criptext.mail.utils.uiobserver.UIObserver


interface PrivacyScene{

    fun attachView(uiObserver: PrivacyUIObserver,
                   keyboardManager: KeyboardManager, model: PrivacyModel)
    fun showMessage(message: UIMessage)
    fun showLinkDeviceAuthConfirmation(untrustedDeviceInfo: DeviceInfo.UntrustedDeviceInfo)
    fun showSyncDeviceAuthConfirmation(trustedDeviceInfo: DeviceInfo.TrustedDeviceInfo)
    fun dismissLinkDeviceDialog()
    fun dismissSyncDeviceDialog()
    fun showConfirmPasswordDialog(observer: UIObserver)
    fun dismissConfirmPasswordDialog()
    fun setConfirmPasswordError(message: UIMessage)
    fun showForgotPasswordDialog(email: String)
    fun updateReadReceipts(isChecked: Boolean)
    fun enableReadReceiptsSwitch(isEnabled: Boolean)
    fun showTwoFADialog(hasRecoveryEmailConfirmed: Boolean)
    fun enableTwoFASwitch(isEnabled: Boolean)
    fun updateTwoFa(isChecked: Boolean)
    fun showAccountSuspendedDialog(observer: UIObserver, email: String, dialogType: DialogType)
    fun dismissAccountSuspendedDialog()


    class Default(val view: View): PrivacyScene{
        private lateinit var privacyUIObserver: PrivacyUIObserver

        private val context = view.context

        private val twoFASwitch: Switch by lazy {
            view.findViewById<Switch>(R.id.privacy_two_fa_switch)
        }

        private val readReceiptsSwitch: Switch by lazy {
            view.findViewById<Switch>(R.id.privacy_read_receipts_switch)
        }

        private val twoFALoading: ProgressBar by lazy {
            view.findViewById<ProgressBar>(R.id.two_fa_loading)
        }

        private val readReceiptsLoading: ProgressBar by lazy {
            view.findViewById<ProgressBar>(R.id.read_receipts_loading)
        }

        private val backButton: ImageView by lazy {
            view.findViewById<ImageView>(R.id.mailbox_back_button)
        }

        private val confirmPasswordDialog = ConfirmPasswordDialog(context)
        private val linkAuthDialog = LinkNewDeviceAlertDialog(context)
        private val syncAuthDialog = SyncDeviceAlertDialog(context)
        private val twoFADialog = Settings2FADialog(context)
        private val accountSuspended = AccountSuspendedDialog(context)

        override fun attachView(uiObserver: PrivacyUIObserver, keyboardManager: KeyboardManager,
                                model: PrivacyModel) {

            privacyUIObserver = uiObserver

            backButton.setOnClickListener {
                privacyUIObserver.onBackButtonPressed()
            }

            setSwitchListener()
        }

        override fun showConfirmPasswordDialog(observer: UIObserver) {
            confirmPasswordDialog.showDialog(observer)
        }

        override fun dismissConfirmPasswordDialog() {
            confirmPasswordDialog.dismissDialog()
        }

        override fun setConfirmPasswordError(message: UIMessage) {
            confirmPasswordDialog.setPasswordError(message)
        }

        override fun showForgotPasswordDialog(email: String) {
            ForgotPasswordDialog(context, email).showForgotPasswordDialog()
        }

        override fun showTwoFADialog(hasRecoveryEmailConfirmed: Boolean) {
            twoFADialog.showLogoutDialog(hasRecoveryEmailConfirmed)
        }

        override fun enableTwoFASwitch(isEnabled: Boolean) {
            twoFASwitch.isEnabled = isEnabled
            if(isEnabled){
                twoFASwitch.visibility = View.VISIBLE
                twoFALoading.visibility = View.GONE
            } else {
                twoFASwitch.visibility = View.INVISIBLE
                twoFALoading.visibility = View.VISIBLE
            }
        }

        override fun updateTwoFa(isChecked: Boolean) {
            twoFASwitch.setOnCheckedChangeListener { _, _ ->  }
            twoFASwitch.isChecked = isChecked
            setSwitchListener()
        }

        override fun enableReadReceiptsSwitch(isEnabled: Boolean) {
            readReceiptsSwitch.isEnabled = isEnabled
            if(isEnabled){
                readReceiptsSwitch.visibility = View.VISIBLE
                readReceiptsLoading.visibility = View.GONE
            } else {
                readReceiptsSwitch.visibility = View.INVISIBLE
                readReceiptsLoading.visibility = View.VISIBLE
            }
        }

        override fun updateReadReceipts(isChecked: Boolean) {
            readReceiptsSwitch.setOnCheckedChangeListener { _, _ ->  }
            readReceiptsSwitch.isChecked = isChecked
            setSwitchListener()
        }

        override fun dismissAccountSuspendedDialog() {
            accountSuspended.dismissDialog()
        }

        override fun showAccountSuspendedDialog(observer: UIObserver, email: String, dialogType: DialogType) {
            accountSuspended.showDialog(observer, email, dialogType)
        }


        private fun setSwitchListener(){
            readReceiptsSwitch.setOnCheckedChangeListener { _, isChecked ->
                privacyUIObserver.onReadReceiptsSwitched(isChecked)
            }
            twoFASwitch.setOnCheckedChangeListener { _, isChecked ->
                privacyUIObserver.onTwoFASwitched(isChecked)
            }
        }

        override fun showLinkDeviceAuthConfirmation(untrustedDeviceInfo: DeviceInfo.UntrustedDeviceInfo) {
            if(linkAuthDialog.isShowing() != null && linkAuthDialog.isShowing() == false)
                linkAuthDialog.showLinkDeviceAuthDialog(privacyUIObserver, untrustedDeviceInfo)
            else if(linkAuthDialog.isShowing() == null)
                linkAuthDialog.showLinkDeviceAuthDialog(privacyUIObserver, untrustedDeviceInfo)
        }

        override fun showSyncDeviceAuthConfirmation(trustedDeviceInfo: DeviceInfo.TrustedDeviceInfo) {
            if(syncAuthDialog.isShowing() != null && syncAuthDialog.isShowing() == false)
                syncAuthDialog.showLinkDeviceAuthDialog(privacyUIObserver, trustedDeviceInfo)
            else if(syncAuthDialog.isShowing() == null)
                syncAuthDialog.showLinkDeviceAuthDialog(privacyUIObserver, trustedDeviceInfo)
        }

        override fun dismissLinkDeviceDialog() {
            linkAuthDialog.dismiss()
        }

        override fun dismissSyncDeviceDialog() {
            syncAuthDialog.dismiss()
        }

        override fun showMessage(message: UIMessage) {
            val duration = Toast.LENGTH_LONG
            val toast = Toast.makeText(
                    context,
                    context.getLocalizedUIMessage(message),
                    duration)
            toast.show()
        }
    }

}