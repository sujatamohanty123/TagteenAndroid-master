package in.tagteen.tagteen.videocompressor;

public interface CompressionListener {
    void onStart();

    void onSuccess();

    void onFailure();

    void onProgress(float var1);
}
