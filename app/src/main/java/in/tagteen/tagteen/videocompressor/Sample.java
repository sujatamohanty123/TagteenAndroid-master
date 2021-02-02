package in.tagteen.tagteen.videocompressor;

public final class Sample {
    private long offset;
    private long size;

    public final long getOffset() {
        return this.offset;
    }

    public final void setOffset(long var1) {
        this.offset = var1;
    }

    public final long getSize() {
        return this.size;
    }

    public final void setSize(long var1) {
        this.size = var1;
    }

    public Sample(long offset, long size) {
        this.offset = offset;
        this.size = size;
    }
}