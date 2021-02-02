package in.tagteen.tagteen.MaterialEditTextUtils;

import androidx.annotation.NonNull;

import java.util.regex.Pattern;

/**
 * Created by anooj on 18/01/16.
 */
public class RegexpValidator extends METValidator {

    private Pattern pattern;

    public RegexpValidator(@NonNull String errorMessage, @NonNull String regex) {
        super(errorMessage);
        pattern = Pattern.compile(regex);
    }

    public RegexpValidator(@NonNull String errorMessage, @NonNull Pattern pattern) {
        super(errorMessage);
        this.pattern = pattern;
    }

    @Override
    public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
        return pattern.matcher(text).matches();
    }
}