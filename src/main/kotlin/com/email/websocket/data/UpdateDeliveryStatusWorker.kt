package com.email.websocket.data

import com.email.api.models.TrackingUpdate
import com.email.bgworker.BackgroundWorker
import com.email.bgworker.ProgressReporter
import com.email.db.DeliveryTypes
import com.email.db.FeedType
import com.email.db.dao.ContactDao
import com.email.db.dao.EmailDao
import com.email.db.dao.FeedItemDao
import com.email.db.dao.FileDao
import com.email.db.models.Contact
import com.email.db.models.FeedItem
import com.email.db.typeConverters.EmailDeliveryConverter
import com.email.utils.DateUtils
import java.io.File
import java.util.*

/**
 * Created by gabriel on 6/29/18.
 */

class UpdateDeliveryStatusWorker(private val dao: EmailDao,
                                 private val feedDao: FeedItemDao,
                                 private val fileDao: FileDao,
                                 private val contactDao: ContactDao,
                                 override val publishFn: (EventResult.UpdateDeliveryStatus) -> Unit,
                                 private val trackingUpdate: TrackingUpdate)
    : BackgroundWorker<EventResult.UpdateDeliveryStatus> {

    override val canBeParallelized = false

    override fun catchException(ex: Exception): EventResult.UpdateDeliveryStatus {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun work(reporter: ProgressReporter<EventResult.UpdateDeliveryStatus>)
            : EventResult.UpdateDeliveryStatus? {
        val existingEmail = dao.findEmailByMetadataKey(trackingUpdate.metadataKey)

        if (existingEmail != null) {

            dao.changeDeliveryTypeByMetadataKey(listOf(trackingUpdate.metadataKey), trackingUpdate.type)
            val update = EmailDeliveryStatusUpdate(existingEmail.id, trackingUpdate)

            if(trackingUpdate.type == DeliveryTypes.READ) {
                feedDao.insertFeedItem(FeedItem(
                        id = 0,
                        date = Date(),
                        feedType = FeedType.OPEN_EMAIL,
                        location = "",
                        seen = false,
                        emailId = existingEmail.id,
                        contactId = contactDao.getContact("${trackingUpdate.from}@${Contact.mainDomain}")!!.id,
                        fileId = null
                ))
            }

            if(trackingUpdate.type == DeliveryTypes.UNSEND) {
                dao.changeDeliveryType(existingEmail.id, DeliveryTypes.UNSEND)
                dao.unsendEmailById(existingEmail.id, "", "",
                        DateUtils.getDateFromString(DateUtils.printDateWithServerFormat(Date()), "yyyy-MM-dd HH:mm:ss"))
                fileDao.changeFileStatusByEmailid(existingEmail.id, 0)
            }

            return EventResult.UpdateDeliveryStatus.Success(update)
        }

        // nothing was updated so return null.
        return EventResult.UpdateDeliveryStatus.Success(null)
    }

    override fun cancel() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}