package in.tagteen.tagteen.videocompressor;

import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCrypto;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.Surface;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.ByteBuffer;

import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;

public final class Compressor {
    private static final int MIN_BITRATE = 2000000;
    private static final int MIN_HEIGHT = 640;
    private static final int MIN_WIDTH = 360;
    private static final String MIME_TYPE = "video/avc";
    @NotNull
    public static final Compressor INSTANCE;

    public final boolean compressVideo(@NotNull String source, @NotNull String destination, @NotNull CompressionProgressListener listener) {
        Intrinsics.checkParameterIsNotNull(source, "source");
        Intrinsics.checkParameterIsNotNull(destination, "destination");
        Intrinsics.checkParameterIsNotNull(listener, "listener");
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(source);
        String var10000 = mediaMetadataRetriever.extractMetadata(19);
        Integer var76;
        if (var10000 != null) {
            String var6 = var10000;
            boolean var7 = false;
            var76 = Integer.parseInt(var6);
        } else {
            var76 = null;
        }

        Integer height = var76;
        var10000 = mediaMetadataRetriever.extractMetadata(18);
        if (var10000 != null) {
            String var54 = var10000;
            boolean var8 = false;
            var76 = Integer.parseInt(var54);
        } else {
            var76 = null;
        }

        Integer width = var76;
        var10000 = mediaMetadataRetriever.extractMetadata(24);
        if (var10000 != null) {
            String var56 = var10000;
            boolean var9 = false;
            var76 = Integer.parseInt(var56);
        } else {
            var76 = null;
        }

        Integer rotation = var76;
        var10000 = mediaMetadataRetriever.extractMetadata(20);
        if (var10000 != null) {
            String var58 = var10000;
            boolean var10 = false;
            var76 = Integer.parseInt(var58);
        } else {
            var76 = null;
        }

        Integer bitrate = var76;
        var10000 = mediaMetadataRetriever.extractMetadata(9);
        Long var77;
        if (var10000 != null) {
            String var60 = var10000;
            boolean var11 = false;
            var77 = Long.parseLong(var60) * (long)1000;
        } else {
            var77 = null;
        }

        Long duration = var77;
        if (bitrate == null) {
            Intrinsics.throwNpe();
        }

        if (bitrate > MIN_BITRATE) {
            if (height == null) {
                Intrinsics.throwNpe();
            }

            if (height > MIN_HEIGHT) {
                if (width == null) {
                    Intrinsics.throwNpe();
                }

                if (width > MIN_WIDTH) {
                    int newHeight;
                    int newBitrate;
                    int newWidth;
                    label817: {
                        label828: {
                            newBitrate = this.getBitrate(bitrate);
                            Pair var13 = this.generateWidthAndHeight(width, height);
                            newWidth = ((Number)var13.component1()).intValue();
                            newHeight = ((Number)var13.component2()).intValue();
                            if (rotation != null) {
                                if (rotation == 90) {
                                    break label828;
                                }
                            }

                            if (rotation != null) {
                                if (rotation == 270) {
                                    break label828;
                                }
                            }

                            if (rotation != null) {
                                if (rotation == 180) {
                                    var76 = 0;
                                    break label817;
                                }
                            }

                            var76 = rotation;
                            break label817;
                        }

                        int tempHeight = newHeight;
                        newHeight = newWidth;
                        newWidth = tempHeight;
                        var76 = 0;
                    }

                    rotation = var76;
                    File file = new File(source);
                    if (!file.canRead()) {
                        return false;
                    }

                    boolean noExceptions = true;
                    if (newWidth != 0 && newHeight != 0) {
                        File cacheFile = new File(destination);

                        String var10001;
                        try {
                            BufferInfo bufferInfo = new BufferInfo();
                            if (rotation == null) {
                                Intrinsics.throwNpe();
                            }

                            Mp4Movie movie = this.setUpMP4Movie(rotation, newWidth, newHeight, cacheFile);
                            MP4Builder mediaMuxer = (new MP4Builder()).createMovie(movie);
                            MediaExtractor extractor = new MediaExtractor();
                            extractor.setDataSource(file.toString());
                            if (newWidth == width && newHeight == height) {
                                return false;
                            }

                            int videoIndex = this.selectTrack(extractor, true);
                            extractor.selectTrack(videoIndex);
                            extractor.seekTo(0L, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
                            MediaFormat var78 = extractor.getTrackFormat(videoIndex);
                            Intrinsics.checkExpressionValueIsNotNull(var78, "extractor.getTrackFormat(videoIndex)");
                            MediaFormat inputFormat = var78;
                            var78 = MediaFormat.createVideoFormat("video/avc", newWidth, newHeight);
                            Intrinsics.checkExpressionValueIsNotNull(var78, "MediaFormat.createVideoF…YPE, newWidth, newHeight)");
                            MediaFormat outputFormat = var78;
                            MediaCodec decoder = (MediaCodec)null;
                            MediaCodec var79 = MediaCodec.createEncoderByType("video/avc");
                            Intrinsics.checkExpressionValueIsNotNull(var79, "MediaCodec.createEncoderByType(MIME_TYPE)");
                            MediaCodec encoder = var79;
                            InputSurface inputSurface = (InputSurface)null;
                            OutputSurface outputSurface = (OutputSurface)null;
                            boolean var47 = false;

                            label831: {
                                Unit var80;
                                label832: {
                                    try {
                                        var47 = true;
                                        boolean inputDone = false;
                                        boolean outputDone = false;
                                        int videoTrackIndex = -5;
                                        int colorFormat = 2130708361;
                                        this.setOutputFileParameters(outputFormat, colorFormat, newBitrate);
                                        encoder.configure(outputFormat, (Surface)null, (MediaCrypto)null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                                        inputSurface = new InputSurface(encoder.createInputSurface());
                                        inputSurface.makeCurrent();
                                        encoder.start();
                                        outputSurface = new OutputSurface();
                                        boolean encoderOutputAvailable;
                                        boolean doRender;
                                        if (inputFormat != null) {
                                            encoderOutputAvailable = false;
                                            boolean var33 = false;
                                            doRender = false;
                                            var10000 = inputFormat.getString("mime");
                                            if (var10000 == null) {
                                                Intrinsics.throwNpe();
                                            }

                                            decoder = MediaCodec.createDecoderByType(var10000);
                                            var80 = Unit.INSTANCE;
                                        } else {
                                            var10000 = null;
                                        }

                                        if (decoder == null) {
                                            Intrinsics.throwNpe();
                                        }

                                        decoder.configure(inputFormat, outputSurface.getSurface(), (MediaCrypto)null, 0);
                                        if (decoder == null) {
                                            Intrinsics.throwNpe();
                                        }

                                        decoder.start();

                                        while(!outputDone) {
                                            int decoderStatus;
                                            if (!inputDone) {
                                                int index = extractor.getSampleTrackIndex();
                                                int inputBufferIndex;
                                                if (index == videoIndex) {
                                                    if (decoder == null) {
                                                        Intrinsics.throwNpe();
                                                    }

                                                    inputBufferIndex = decoder.dequeueInputBuffer(0L);
                                                    if (inputBufferIndex >= 0) {
                                                        if (decoder == null) {
                                                            Intrinsics.throwNpe();
                                                        }

                                                        ByteBuffer inputBuffer = decoder.getInputBuffer(inputBufferIndex);
                                                        if (inputBuffer == null) {
                                                            Intrinsics.throwNpe();
                                                        }

                                                        decoderStatus = extractor.readSampleData(inputBuffer, 0);
                                                        if (decoderStatus < 0) {
                                                            if (decoder == null) {
                                                                Intrinsics.throwNpe();
                                                            }

                                                            decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0L, 4);
                                                            inputDone = true;
                                                        } else {
                                                            if (decoder == null) {
                                                                Intrinsics.throwNpe();
                                                            }

                                                            decoder.queueInputBuffer(inputBufferIndex, 0, decoderStatus, extractor.getSampleTime(), 0);
                                                            extractor.advance();
                                                        }
                                                    }
                                                } else if (index == -1) {
                                                    if (decoder == null) {
                                                        Intrinsics.throwNpe();
                                                    }

                                                    inputBufferIndex = decoder.dequeueInputBuffer(0L);
                                                    if (inputBufferIndex >= 0) {
                                                        if (decoder == null) {
                                                            Intrinsics.throwNpe();
                                                        }

                                                        decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0L, 4);
                                                        inputDone = true;
                                                    }
                                                }
                                            }

                                            boolean decoderOutputAvailable = true;
                                            encoderOutputAvailable = true;

                                            while(decoderOutputAvailable || encoderOutputAvailable) {
                                                int encoderStatus = encoder.dequeueOutputBuffer(bufferInfo, 0L);
                                                boolean var37;
                                                boolean var38;
                                                if (encoderStatus == -1) {
                                                    encoderOutputAvailable = false;
                                                } else if (encoderStatus == -2) {
                                                    var78 = encoder.getOutputFormat();
                                                    Intrinsics.checkExpressionValueIsNotNull(var78, "encoder.outputFormat");
                                                    MediaFormat newFormat = var78;
                                                    if (videoTrackIndex == -5) {
                                                        videoTrackIndex = mediaMuxer.addTrack(newFormat, false);
                                                    }
                                                } else if (encoderStatus != -3) {
                                                    if (encoderStatus < 0) {
                                                        throw(new RuntimeException("unexpected result from encoder.dequeueOutputBuffer: " + encoderStatus));
                                                    }

                                                    ByteBuffer var81 = encoder.getOutputBuffer(encoderStatus);
                                                    if (var81 == null) {
                                                        throw (new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null"));
                                                    }

                                                    Intrinsics.checkExpressionValueIsNotNull(var81, "encoder.getOutputBuffer(…$encoderStatus was null\")");
                                                    ByteBuffer encodedData = var81;
                                                    if (bufferInfo.size > 1) {
                                                        if ((bufferInfo.flags & 2) == 0) {
                                                            mediaMuxer.writeSampleData(videoTrackIndex, encodedData, bufferInfo, false);
                                                        } else if (videoTrackIndex == -5) {
                                                            byte[] csd = new byte[bufferInfo.size];
                                                            var37 = false;
                                                            var38 = false;
                                                            boolean var40 = false;
                                                            encodedData.limit(bufferInfo.offset + bufferInfo.size);
                                                            encodedData.position(bufferInfo.offset);
                                                            encodedData.get(csd);
                                                            var80 = Unit.INSTANCE;
                                                            ByteBuffer sps = (ByteBuffer)null;
                                                            ByteBuffer pps = (ByteBuffer)null;
                                                            int a = bufferInfo.size - 1;

                                                            for(boolean var39 = false; a >= 0 && a > 3; --a) {
                                                                if (csd[a] == 1 && csd[a - 1] == 0 && csd[a - 2] == 0 && csd[a - 3] == 0) {
                                                                    sps = ByteBuffer.allocate(a - 3);
                                                                    pps = ByteBuffer.allocate(bufferInfo.size - (a - 3));
                                                                    if (sps == null) {
                                                                        Intrinsics.throwNpe();
                                                                    }

                                                                    sps.put(csd, 0, a - 3).position(0);
                                                                    if (pps == null) {
                                                                        Intrinsics.throwNpe();
                                                                    }

                                                                    pps.put(csd, a - 3, bufferInfo.size - (a - 3)).position(0);
                                                                    break;
                                                                }
                                                            }

                                                            var78 = MediaFormat.createVideoFormat("video/avc", newWidth, newHeight);
                                                            Intrinsics.checkExpressionValueIsNotNull(var78, "MediaFormat.createVideoF…                        )");
                                                            MediaFormat newFormat = var78;
                                                            if (sps != null && pps != null) {
                                                                newFormat.setByteBuffer("csd-0", sps);
                                                                newFormat.setByteBuffer("csd-1", pps);
                                                            }

                                                            videoTrackIndex = mediaMuxer.addTrack(newFormat, false);
                                                        }
                                                    }

                                                    outputDone = (bufferInfo.flags & 4) != 0;
                                                    encoder.releaseOutputBuffer(encoderStatus, false);
                                                }

                                                if (encoderStatus == -1) {
                                                    if (decoder == null) {
                                                        Intrinsics.throwNpe();
                                                    }

                                                    decoderStatus = decoder.dequeueOutputBuffer(bufferInfo, 0L);
                                                    if (decoderStatus == -1) {
                                                        decoderOutputAvailable = false;
                                                    } else if (decoderStatus != -3 && decoderStatus != -2) {
                                                        if (decoderStatus < 0) {
                                                            throw (new RuntimeException("unexpected result from decoder.dequeueOutputBuffer: " + decoderStatus));
                                                        }

                                                        doRender = bufferInfo.size != 0;
                                                        if (decoder == null) {
                                                            Intrinsics.throwNpe();
                                                        }

                                                        decoder.releaseOutputBuffer(decoderStatus, doRender);
                                                        if (doRender) {
                                                            try {
                                                                outputSurface.awaitNewImage();
                                                                outputSurface.drawImage();
                                                                inputSurface.setPresentationTime(bufferInfo.presentationTimeUs * (long)1000);
                                                                if (duration != null) {
                                                                    var37 = false;
                                                                    var38 = false;
                                                                    long it = ((Number)duration).longValue();
                                                                    boolean var41 = false;
                                                                    listener.onProgressChanged((float)bufferInfo.presentationTimeUs / (float)duration * (float)100);
                                                                    var80 = Unit.INSTANCE;
                                                                } else {
                                                                    var10000 = null;
                                                                }

                                                                inputSurface.swapBuffers();
                                                            } catch (Exception var49) {
                                                                var10001 = var49.getMessage();
                                                                if (var10001 == null) {
                                                                    Intrinsics.throwNpe();
                                                                }

                                                                Log.e("Compressor", var10001);
                                                            }
                                                        }

                                                        if ((bufferInfo.flags & 4) != 0) {
                                                            decoderOutputAvailable = false;
                                                            encoder.signalEndOfInputStream();
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        var47 = false;
                                        break label832;
                                    } catch (Exception var50) {
                                        var10001 = var50.getMessage();
                                        if (var10001 == null) {
                                            Intrinsics.throwNpe();
                                        }

                                        Log.e("Compressor", var10001);
                                        noExceptions = false;
                                        var47 = false;
                                    } finally {
                                        if (var47) {
                                            extractor.unselectTrack(videoIndex);
                                            if (decoder != null) {
                                                decoder.stop();
                                                var80 = Unit.INSTANCE;
                                            } else {
                                                var10000 = null;
                                            }

                                            if (decoder != null) {
                                                decoder.release();
                                                var80 = Unit.INSTANCE;
                                            } else {
                                                var10000 = null;
                                            }

                                            encoder.stop();
                                            encoder.release();
                                            if (inputSurface != null) {
                                                inputSurface.release();
                                                var80 = Unit.INSTANCE;
                                            } else {
                                                var10000 = null;
                                            }

                                            if (outputSurface != null) {
                                                outputSurface.release();
                                                var80 = Unit.INSTANCE;
                                            } else {
                                                var10000 = null;
                                            }

                                            Intrinsics.checkExpressionValueIsNotNull(mediaMuxer, "mediaMuxer");
                                            this.processAudio(extractor, mediaMuxer, bufferInfo);
                                        }
                                    }

                                    extractor.unselectTrack(videoIndex);
                                    if (decoder != null) {
                                        decoder.stop();
                                        var80 = Unit.INSTANCE;
                                    } else {
                                        var10000 = null;
                                    }

                                    if (decoder != null) {
                                        decoder.release();
                                        var80 = Unit.INSTANCE;
                                    } else {
                                        var10000 = null;
                                    }

                                    encoder.stop();
                                    encoder.release();
                                    if (inputSurface != null) {
                                        inputSurface.release();
                                        var80 = Unit.INSTANCE;
                                    } else {
                                        var10000 = null;
                                    }

                                    if (outputSurface != null) {
                                        outputSurface.release();
                                        var80 = Unit.INSTANCE;
                                    } else {
                                        var10000 = null;
                                    }
                                    break label831;
                                }

                                extractor.unselectTrack(videoIndex);
                                if (decoder != null) {
                                    decoder.stop();
                                    var80 = Unit.INSTANCE;
                                } else {
                                    var10000 = null;
                                }

                                if (decoder != null) {
                                    decoder.release();
                                    var80 = Unit.INSTANCE;
                                } else {
                                    var10000 = null;
                                }

                                encoder.stop();
                                encoder.release();
                                inputSurface.release();
                                var80 = Unit.INSTANCE;
                                outputSurface.release();
                                var80 = Unit.INSTANCE;
                                Intrinsics.checkExpressionValueIsNotNull(mediaMuxer, "mediaMuxer");
                                this.processAudio(extractor, mediaMuxer, bufferInfo);
                            }

                            extractor.release();

                            try {
                                mediaMuxer.finishMovie();
                            } catch (Exception var48) {
                                var10001 = var48.getMessage();
                                if (var10001 == null) {
                                    Intrinsics.throwNpe();
                                }

                                Log.e("Compressor", var10001);
                            }
                        } catch (Exception var52) {
                            var10001 = var52.getMessage();
                            if (var10001 == null) {
                                Intrinsics.throwNpe();
                            }

                            Log.e("Compressor", var10001);
                        }

                        return true;
                    }

                    return false;
                }
            }
        }

        return false;
    }

    private final int getBitrate(int bitrate) {
        return bitrate >= 15000000 ? 2000000 : (bitrate >= 8000000 ? 1500000 : (bitrate >= 4000000 ? 1000000 : 750000));
    }

    private final Pair generateWidthAndHeight(int width, int height) {
        //int newWidth = false;
        //int newHeight = false;
        int newWidth;
        int newHeight;
        if (width < 1920 && height < 1920) {
            if (width < 1280 && height < 1280) {
                if (width < 960 && height < 960) {
                    newWidth = width;
                    newHeight = height;
                } else {
                    newWidth = 640;
                    newHeight = 360;
                }
            } else {
                newWidth = (int)((double)width * 0.75D);
                newHeight = (int)((double)height * 0.75D);
            }
        } else {
            newWidth = (int)((double)width * 0.5D);
            newHeight = (int)((double)height * 0.5D);
        }

        return new Pair(newWidth, newHeight);
    }

    private final Mp4Movie setUpMP4Movie(int rotation, int newWidth, int newHeight, File cacheFile) {
        Mp4Movie movie = new Mp4Movie();
        movie.setCacheFile(cacheFile);
        movie.setRotation(rotation);
        movie.setSize(newWidth, newHeight);
        return movie;
    }

    private final void setOutputFileParameters(MediaFormat outputFormat, int colorFormat, int newBitrate) {
        //int var8 = false;
        outputFormat.setInteger("color-format", colorFormat);
        outputFormat.setInteger("bitrate", newBitrate);
        outputFormat.setInteger("frame-rate", 30);
        outputFormat.setInteger("i-frame-interval", 15);
    }

    private final int selectTrack(MediaExtractor extractor, boolean isVideo) {
        int numTracks = extractor.getTrackCount();
        int i = 0;

        for(int var5 = numTracks; i < var5; ++i) {
            MediaFormat var10000 = extractor.getTrackFormat(i);
            Intrinsics.checkExpressionValueIsNotNull(var10000, "extractor.getTrackFormat(i)");
            MediaFormat format = var10000;
            String mime = format.getString("mime");
            if (isVideo) {
                if (mime != null) {
                    if (mime.startsWith("video/")) {
                        return i;
                    }
                }
            } else if (mime != null) {
                if (mime.startsWith("audio/")) {
                    return i;
                }
            }
        }

        return -5;
    }

    private final void processAudio(MediaExtractor extractor, MP4Builder mediaMuxer, BufferInfo bufferInfo) throws Exception {
        int audioIndex = this.selectTrack(extractor, false);
        if (audioIndex >= 0) {
            extractor.selectTrack(audioIndex);
            MediaFormat var10000 = extractor.getTrackFormat(audioIndex);
            Intrinsics.checkExpressionValueIsNotNull(var10000, "extractor.getTrackFormat(audioIndex)");
            MediaFormat audioFormat = var10000;
            int muxerTrackIndex = mediaMuxer.addTrack(audioFormat, true);
            int maxBufferSize = audioFormat.getInteger("max-input-size");
            boolean inputDone = false;
            extractor.seekTo(0L, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
            ByteBuffer buffer = ByteBuffer.allocateDirect(maxBufferSize);

            while(!inputDone) {
                int index = extractor.getSampleTrackIndex();
                if (index == audioIndex) {
                    bufferInfo.size = extractor.readSampleData(buffer, 0);
                    if (bufferInfo.size >= 0) {
                        boolean var12 = false;
                        boolean var13 = false;
                        //int var15 = false;
                        bufferInfo.presentationTimeUs = extractor.getSampleTime();
                        bufferInfo.offset = 0;
                        bufferInfo.flags = extractor.getSampleFlags();
                        mediaMuxer.writeSampleData(muxerTrackIndex, buffer, bufferInfo, true);
                        extractor.advance();
                    }
                } else if (index == -1) {
                    inputDone = true;
                }
            }

            extractor.unselectTrack(audioIndex);
        }

    }

    private Compressor() {
    }

    static {
        Compressor var0 = new Compressor();
        INSTANCE = var0;
    }
}
