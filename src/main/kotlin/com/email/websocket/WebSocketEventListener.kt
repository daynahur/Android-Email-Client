package com.email.websocket

import com.email.api.models.*
import com.email.db.models.Email
import com.email.utils.UIMessage

/**
 * Objects  that implement this interface can subscribe to the web socket and react to events emitted
 * by the web socket.
 * Created by gabriel on 9/15/17.
 */
interface WebSocketEventListener{
    /**
     * Invoked when an email has been received. Subscribers should show the email
     * in the UI to notify the user about it.
     * @param token the token of the opened email
     * @param message the message that should be shown in the UI.
     */
    fun onNewEmail(email: Email)

    /**
     * Invoked when a new tracking update been received. Subscribers should try to add the update
     * to the list of notifications in the UI.
     * @param emailId id of the email whose delivery status has been updated.
     * @param update the received update
     */
    fun onNewTrackingUpdate(emailId: Long, update: TrackingUpdate)

    /**
     * Invoked when a new tracking update been received. Subscribers should try to add the update
     * to the list of notifications in the UI.
     * @param emailId id of the email whose delivery status has been updated.
     * @param update the received update
     */
    fun onNewPeerReadEmailUpdate(emailIds: List<Long>, update: PeerReadEmailStatusUpdate)

    /**
     * Invoked when a new tracking update been received. Subscribers should try to add the update
     * to the list of notifications in the UI.
     * @param emailId id of the email whose delivery status has been updated.
     * @param update the received update
     */
    fun onNewPeerUnsendEmailUpdate(emailId: Long, update: PeerUnsendEmailStatusUpdate)

    /**
     * Invoked when a new tracking update been received. Subscribers should try to add the update
     * to the list of notifications in the UI.
     * @param emailId id of the email whose delivery status has been updated.
     * @param update the received update
     */
    fun onNewPeerReadThreadUpdate(update: PeerReadThreadStatusUpdate)

    /**
     * Invoked when a new tracking update been received. Subscribers should try to add the update
     * to the list of notifications in the UI.
     * @param emailId id of the email whose delivery status has been updated.
     * @param update the received update
     */
    fun onNewPeerEmailDeletedUpdate(emailIds: List<Long>, update: PeerEmailDeletedStatusUpdate)

    /**
     * Invoked when a new tracking update been received. Subscribers should try to add the update
     * to the list of notifications in the UI.
     * @param emailId id of the email whose delivery status has been updated.
     * @param update the received update
     */
    fun onNewPeerThreadDeletedUpdate(update: PeerThreadDeletedStatusUpdate)
    /**
     * Invoked when a new tracking update been received. Subscribers should try to add the update
     * to the list of notifications in the UI.
     * @param emailId id of the email whose delivery status has been updated.
     * @param update the received update
     */
    fun onNewPeerEmailLabelsChangedUpdate(update: PeerEmailLabelsChangedStatusUpdate)
    /**
     * Invoked when a new tracking update been received. Subscribers should try to add the update
     * to the list of notifications in the UI.
     * @param emailId id of the email whose delivery status has been updated.
     * @param update the received update
     */
    fun onNewPeerThreadLabelsChangedUpdate(update: PeerThreadLabelsChangedStatusUpdate)

    /**
     * Invoked when a new tracking update been received. Subscribers should try to add the update
     * to the list of notifications in the UI.
     * @param emailId id of the email whose delivery status has been updated.
     * @param update the received update
     */
    fun onNewPeerLabelCreatedUpdate(update: PeerLabelCreatedStatusUpdate)

    /**
     * Invoked when a new tracking update been received. Subscribers should try to add the update
     * to the list of notifications in the UI.
     * @param emailId id of the email whose delivery status has been updated.
     * @param update the received update
     */
    fun onNewPeerUsernameChangedUpdate(update: PeerUsernameChangedStatusUpdate)

    /**
     * Called when something went wrong processing the event. Subscribers may want to display an
     * error message.
     * @param uiMessage: Object with localized error message
     */
    fun onError(uiMessage: UIMessage)
}
