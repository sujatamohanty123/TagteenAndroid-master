package in.tagteen.tagteen.profile;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import android.view.View;

public abstract class BaseFragment extends Fragment {

    protected <V extends View> V findView(@IdRes int id) {
        return findView(getView(), id);
    }

    protected <V extends View> V findView(View view, @IdRes int id) {
        final V out;
        if (view == null) {
            out = null;
        } else {
            //noinspection unchecked
            out = (V) view.findViewById(id);
        }
        return out;
    }
}
