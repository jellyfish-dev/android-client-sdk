package org.membraneframework.rtc.models

import org.membraneframework.rtc.TrackEncoding
import org.membraneframework.rtc.media.RemoteTrack
import org.membraneframework.rtc.utils.Metadata

fun interface OnEncodingChangedListener {
    fun onEncodingChanged(trackContext: TrackContext)
}

fun interface OnVoiceActivityChangedListener {
    fun onVoiceActivityChanged(trackContext: TrackContext)
}

/**
 * Track's context i.e. all data that can be useful when operating on track.
 *
 * @property endpoint Endpoint this track comes from.
 * @property trackId Track id. It is generated by RTC engine and takes form `endpoint_id:<random_uuidv4>`.
 * @property metadata Any info that was passed in MembraneWebRTC.createVideoTrack/MembraneWebRTC.createAudioTrack
 */
class TrackContext(track: RemoteTrack?, val endpoint: Endpoint, val trackId: String, metadata: Metadata) {
    private var onTrackEncodingChangeListener: (OnEncodingChangedListener)? = null
    private var onVadNotificationListener: (OnVoiceActivityChangedListener)? = null

    var track: RemoteTrack? = track
        internal set
    var metadata: Metadata = metadata
        internal set

    var vadStatus: VadStatus = VadStatus.SILENCE
        internal set(value) {
            field = value
            onVadNotificationListener?.let { onVadNotificationListener?.onVoiceActivityChanged(this) }
        }

    /**
     *  Encoding that is currently received. Only present for remote tracks.
     */
    var encoding: TrackEncoding? = null
        private set

    /**
     * The reason of currently selected encoding. Only present for remote tracks.
     */
    var encodingReason: EncodingReason? = null
        private set

    internal fun setEncoding(encoding: TrackEncoding, encodingReason: EncodingReason) {
        this.encoding = encoding
        this.encodingReason = encodingReason
        onTrackEncodingChangeListener?.let { onTrackEncodingChangeListener?.onEncodingChanged(this) }
    }

    /**
     * Sets listener that is called each time track encoding has changed.
     *
     * Track encoding can change in the following cases:
     * - when user requested a change
     * - when sender stopped sending some encoding (because of bandwidth change)
     * - when receiver doesn't have enough bandwidth
     * Some of those reasons are indicated in TrackContext.encodingReason
     */
    fun setOnEncodingChangedListener(listener: OnEncodingChangedListener?) {
        onTrackEncodingChangeListener = listener
    }

    /**
     * Sets listener that is called every time an update about voice activity is received from the server.
     */
    fun setOnVoiceActivityChangedListener(listener: OnVoiceActivityChangedListener?) {
        onVadNotificationListener = listener
    }
}
