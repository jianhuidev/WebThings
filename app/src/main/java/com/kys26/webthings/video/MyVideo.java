package com.kys26.webthings.video;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.SurfaceHolder;

import com.smaxe.uv.client.video.AbstractVideo;
import com.smaxe.uv.stream.MediaData;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by Lee on 2017/9/23.
 */

public final class MyVideo extends AbstractVideo {
    private UltraNetStream stream;
    private SurfaceHolder holder;
    private int height;
    private int width;
    private boolean isFirst = true;
    private MediaCodec mCodec;
    private final static int TIME_INTERNAL = 5;
    public int duration = 0;
    public int frames = 0;
    public int size = 0;
    byte[] videoData;

    public MyVideo(UltraNetStream stream, SurfaceHolder holder, int width, int height) {
        this.stream = stream;
        this.holder = holder;
        this.width = width;
        this.height = height;
        if (isFirst) {
            initDecoder();
        }
    }

    /**
     * 初始化解码
     */
    private void initDecoder() {
        try {
            //根据需要解码的类型创建解码器
            mCodec = MediaCodec.createDecoderByType("video/avc");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //初始化MediaFormat
        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc",
                width, height);
        //配置MediaFormat以及需要显示的surface
        mCodec.configure(mediaFormat, holder.getSurface(), null, 0);
        //开始解码
        mCodec.start();
        isFirst = false;

    }

    int mCount = 0;

    @Override
    public double bufferLength() {
        return stream.bufferLength();
    }

    @Override
    public long bytesLoaded() {
        return stream.bytesLoaded();
    }

    @Override
    public long bytesTotal() {
        return stream.bytesTotal();
    }

    @Override
    public double fps() {
        return stream.currentFPS();
    }

    @Override
    public double liveDelay() {
        return stream.liveDelay();
    }

    @Override
    public double time() {
        return stream.time();
    }

    @Override
    public void clear() {
        stream.close();
    }

    @Override
    public void clearPlayBuffer() {
        stream.clearBuffer();
    }

    @Override
    public void reset() {

    }

    @Override
    public void onAudioData(MediaData mediaData) {

    }

    @Override
    public void onVideoData(MediaData mediaData) {
        this.duration = (this.frames == 0 ? 0 : this.duration
                + mediaData.rtime);
        this.frames += 1;
        this.size += mediaData.size();
        try {
            InputStream mdate = mediaData.read();
            int dataLen = mdate.available() - 1;
//				Log.d("audioSize", dataLen+"");
            videoData = new byte[dataLen];
//                mdate.read(b, 0, 1);
//                mdate.read(b, 0, dataLen);
            onFrame(videoData, 0, dataLen);
//				if (dataLen > 11) {
//					if ((decsize = speexDecoder.decode(b, decoded, dataLen)) > 0) {
//						track.write(decoded, 0, decsize);
//						track.setStereoVolume(0.7f, 0.7f);
//						track.play();
//					}
//				}
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }
    }

    @Override
    public void onFlvData(MediaData mediaData) {

    }

    @Override
    public void onCuePoint(Object o) {

    }

    @Override
    public void onMetaData(Object o) {

    }

    @Override
    public void onSetDataFrame(String s, Object o) {

    }

    /**
     * 停止解码，释放解码器
     */
    public void stopCodec() {
        try {
            mCodec.stop();
            mCodec.release();
            mCodec = null;
            isFirst = true;
        } catch (Exception e) {
            e.printStackTrace();
            mCodec = null;
        }
    }

    /**
     * 每一帧
     *
     * @param buf    每一帧的数据
     * @param offset
     * @param length 长度
     * @return 是否解码成功
     */
    private boolean onFrame(byte[] buf, int offset, int length) {
        // 获取输入buffer index
        ByteBuffer[] inputBuffers = mCodec.getInputBuffers();
        //-1表示一直等待；0表示不等待；其他大于0的参数表示等待毫秒数
        int inputBufferIndex = mCodec.dequeueInputBuffer(-1);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            //清空buffer
            inputBuffer.clear();
            //put需要解码的数据
            inputBuffer.put(buf, offset, length);
            //解码
            mCodec.queueInputBuffer(inputBufferIndex, 0, length, mCount * TIME_INTERNAL, 0);
            mCount++;
        } else {
            return false;
        }
        // 获取输出buffer index
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 100);
        //循环解码，直到数据全部解码完成
        while (outputBufferIndex >= 0) {
            //logger.d("outputBufferIndex = " + outputBufferIndex);
            //true : 将解码的数据显示到surface上
            mCodec.releaseOutputBuffer(outputBufferIndex, true);
            outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
        if (outputBufferIndex < 0) {
            //logger.e("outputBufferIndex = " + outputBufferIndex);
        }
        return true;
    }
}
