package io.agora.openlive.utils

import android.util.Log
import org.phoenixframework.Channel
import org.phoenixframework.Message
import org.phoenixframework.Socket
import java.io.IOException

open class BroadcastingTrackSocket(
        private val mAppId: String, userId: String
) {
    private val msocketConnectionParams: MutableMap<String, String> = HashMap()
    private var socket: Socket? = null
    private var channel: Channel? = null
    private var mTrackEventsCallback: TrackEventsCallback? = null

    companion object {
        val TAG = "Socket"
        val mSocketUrl = "http://13.235.167.76:4005/socket/websocket"
        val mChannelName = "visitor:"
        val mPushCommentEvent = "push_comment"
        val mPushUserOnline = "push_online"
        val mRecieveUserOnline = "receive_online"
        val mReceiveCommentEvent = "receive_comment"
        val mUserLeavesChannel = "presence_diff"
        val mResponseOk = "ok"
        val mIgnored = "ignore"
    }

    init {
        msocketConnectionParams["user_id"] = userId
    }

    fun setCallback(callBack: TrackEventsCallback) {
        this.mTrackEventsCallback = callBack
    }

    fun initSocketConnection(channelId: String) {
        try {
            socket = Socket(mSocketUrl, msocketConnectionParams)
            socket?.connect()
            socket?.onOpen {

            }

            socket?.onClose {
            }

            socket?.onError { throwable, response ->

            }


            if (socket != null) {
                initJoinChannel(channelId)
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun initJoinChannel(channelId: String) {
        channel = socket?.channel(mChannelName + channelId, mapOf("status" to "joining"))
        try {
            channel!!.join().receive(mResponseOk) { envelope ->
                pushOnline(channelId)
                mTrackEventsCallback?.onJoinChannelSuccess(envelope)
            }.receive(mIgnored) { envelope ->
                mTrackEventsCallback?.onJoinChannelIgnored(envelope)
            }
            channel!!.on(
                    mReceiveCommentEvent
            ) { envelope ->
                mTrackEventsCallback?.onReceiveComment(envelope)
            }

            channel!!.on(
                    mRecieveUserOnline
            ) { envelope ->
                mTrackEventsCallback?.onRecieveUserCount(envelope)
            }

            channel!!.on(
                    mUserLeavesChannel
            ) { envelope ->
                mTrackEventsCallback?.onUserLeavesChannel(envelope)
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    open fun pushComment(
            userId: String,
            userName: String,
            comment: String,
            channelId: String
    ) {
        if (socket == null || !socket!!.isConnected) {
            initSocketConnection(channelId)
        }
        val payload =
                mapOf("id" to userId, "name" to userName, "comment" to comment, "channel_id" to channelId)
        if (channel != null && !channel?.isJoined!!) {
            initJoinChannel(channelId)
        }
        channel?.push(mPushCommentEvent, payload)
    }

    open fun pushOnline(
            channelId: String
    ) {
        if (socket == null || !socket!!.isConnected) {
            initSocketConnection(channelId)
        }
        val payload =
                mapOf("channel_id" to channelId)
        if (channel != null && !channel?.isJoined!!) {
            initJoinChannel(channelId)
        }
        this.channel?.push(mPushUserOnline, payload)
    }

    fun disconnectAllSockets() {
        if (socket != null) {
            channel?.leave(0)
            socket?.remove(channel!!)
            socket?.disconnect {
            }
            socket = null
        }
    }

    interface TrackEventsCallback {
        fun onReceiveComment(message: Message)

        fun onRecieveUserCount(message: Message)

        fun onJoinChannelSuccess(message: Message)

        fun onJoinChannelIgnored(message: Message)

        fun onUserLeavesChannel(message: Message)
    }

}