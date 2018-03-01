package com.email.scenes.mailbox

import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.*
import android.widget.ImageView
import com.email.IHostActivity
import com.email.R
import com.email.R.id.fab
import com.email.androidui.mailthread.ThreadListView
import com.email.androidui.mailthread.ThreadRecyclerView
import com.email.scenes.LabelChooser.LabelChooserDialog
import com.email.scenes.LabelChooser.LabelDataSourceHandler
import com.email.scenes.composer.ui.ComposerUIObserver
import com.email.scenes.mailbox.data.EmailThread
import com.email.scenes.mailbox.holders.ToolbarHolder
import com.email.scenes.mailbox.ui.DrawerMenuView
import com.email.scenes.mailbox.ui.EmailThreadAdapter
import com.email.scenes.mailbox.ui.MailboxUIObserver
import com.email.utils.VirtualList

/**
 * Created by sebas on 1/23/18.
 */

interface MailboxScene: ThreadListView {

    var observer: MailboxUIObserver?
    fun initDrawerLayout()
    fun initNavHeader(fullName: String)
    fun onBackPressed(): Boolean
    fun attachView(threadEventListener: EmailThreadAdapter.OnThreadEventListener)
    fun refreshToolbarItems()
    fun showMultiModeBar(selectedThreadsQuantity : Int)
    fun hideMultiModeBar()
    fun updateToolbarTitle(title: String)
    fun showDialogLabelsChooser(labelDataSourceHandler: LabelDataSourceHandler)
    fun showDialogMoveTo(onMoveThreadsListener: OnMoveThreadsListener)
    fun setToolbarNumberOfEmails(emailsSize: Int)
    fun openNotificationFeed()

    class MailboxSceneView(private val mailboxView: View,
                           val hostActivity: IHostActivity,
                           val threadList: VirtualList<EmailThread>)
        : MailboxScene {

        private val context = mailboxView.context

        override var observer: MailboxUIObserver? = null

        private val labelChooserDialog = LabelChooserDialog(context)
        private val moveToDialog = MoveToDialog(context)

        private lateinit var drawerMenuView: DrawerMenuView

        private val recyclerView: RecyclerView by lazy {
            mailboxView.findViewById<RecyclerView>(R.id.mailbox_recycler)
        }

        private val toolbarHolder: ToolbarHolder by lazy {
            val view = mailboxView.findViewById<Toolbar>(R.id.mailbox_toolbar)
            ToolbarHolder(view)
        }

        private val drawerLayout: DrawerLayout by lazy {
            mailboxView.findViewById<DrawerLayout>(R.id.drawer_layout) as DrawerLayout
        }

        private val leftNavigationView: NavigationView by lazy {
            mailboxView.findViewById<NavigationView>(R.id.nav_left_view)
        }

        private val navButton: ImageView by lazy {
            mailboxView.findViewById<ImageView>(R.id.mailbox_nav_button)
        }

        private val openComposerButton: View by lazy {
            mailboxView.findViewById<View>(R.id.fab)
        }

        private lateinit var threadRecyclerView: ThreadRecyclerView

        private var threadListener: EmailThreadAdapter.OnThreadEventListener? = null
            set(value) {
                threadRecyclerView.setThreadListener(value)
                field = value
            }

        override fun attachView(threadEventListener: EmailThreadAdapter.OnThreadEventListener){
            threadRecyclerView = ThreadRecyclerView(recyclerView, threadEventListener, threadList)
            this.threadListener = threadEventListener

            drawerMenuView = DrawerMenuView(leftNavigationView)
            openComposerButton.setOnClickListener {
                observer?.onOpenComposerButtonClicked()
            }
        }

        override fun initDrawerLayout() {
            navButton.setOnClickListener({
                drawerLayout.openDrawer(GravityCompat.START)
            })
        }

        override fun onBackPressed(): Boolean {
            return when {
                drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                    drawerLayout.closeDrawer(Gravity.LEFT)
                    false
                }
                drawerLayout.isDrawerOpen(GravityCompat.END) -> {
                    drawerLayout.closeDrawer(Gravity.RIGHT)
                    false
                }
                else -> true
            }
        }

        override fun initNavHeader(fullName: String) {
            drawerMenuView.initNavHeader(fullName)
        }

        override fun notifyThreadSetChanged() {
            threadRecyclerView.notifyThreadSetChanged()
        }

        override fun notifyThreadRemoved(position: Int) {
            threadRecyclerView.notifyThreadRemoved(position)
        }

        override fun notifyThreadChanged(position: Int) {
            threadRecyclerView.notifyThreadChanged(position)
        }

        override fun notifyThreadRangeInserted(positionStart: Int, itemCount: Int) {
            threadRecyclerView.notifyThreadRangeInserted(positionStart, itemCount)
        }

        override fun changeMode(multiSelectON: Boolean, silent: Boolean) {
            threadRecyclerView.changeMode(multiSelectON, silent)
        }

        override fun refreshToolbarItems() {
            hostActivity.refreshToolbarItems()
        }

        override fun showMultiModeBar(selectedThreadsQuantity : Int) {
            toolbarHolder.showMultiModeBar(selectedThreadsQuantity)
        }

        override fun hideMultiModeBar() {
            toolbarHolder.hideMultiModeBar()
        }

        override fun updateToolbarTitle(title: String) {
            toolbarHolder.updateToolbarTitle(title)
        }

        override fun showDialogLabelsChooser( labelDataSourceHandler:
                                              LabelDataSourceHandler ) {
            labelChooserDialog.showDialogLabelsChooser(dataSource = labelDataSourceHandler)
        }

        override fun openNotificationFeed(){
            drawerLayout.openDrawer(GravityCompat.END)
        }

        override fun showDialogMoveTo(onMoveThreadsListener: OnMoveThreadsListener) {
            moveToDialog.showMoveToDialog(
                    onMoveThreadsListener = onMoveThreadsListener)
        }

        override fun setToolbarNumberOfEmails(emailsSize: Int) {
            toolbarHolder.updateNumberOfMails(emailsSize)
        }

    }


}