package in.tagteen.tagteen.MaterialEditTextUtils;

import androidx.annotation.NonNull;

/**
 * Created by anooj on 18/01/16.
 */
public abstract class METValidator {
    protected String errorMessage;

    public METValidator(@NonNull String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setErrorMessage(@NonNull String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @NonNull
    public String getErrorMessage() {
        return this.errorMessage;
    }


    public abstract boolean isValid(@NonNull CharSequence text, boolean isEmpty);
}
