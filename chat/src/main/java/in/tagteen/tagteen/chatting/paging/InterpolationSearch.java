package in.tagteen.tagteen.chatting.paging;

import androidx.annotation.NonNull;
import android.util.Log;

import in.tagteen.tagteen.chatting.room.Message;

import java.util.List;

/**
 * Created by tony00 on 3/30/2019.
 * <p>
 * mid = Lo + ((Hi - Lo) * (X - A[Lo]) / (A[Hi] - A[Lo]))
 * * <p>
 * * where −
 * * A    = list
 * * Lo   = Lowest index of the list
 * * Hi   = Highest index of the list
 * * A[n] = Value stored at index n in the list
 */

public class InterpolationSearch {

    private static final String TAG = InterpolationSearch.class.getCanonicalName();

    private InterpolationSearch() {
    }

    public static Integer findMessagePosition(@NonNull List<Integer> collection,
                                              @NonNull Integer target) {
        if (collection.size() == 0) {
            Log.d(TAG, "findMessagePosition: Target not found");
            return -1;
        } else if (collection.size() == 1) {
            if (collection.get(0) == target) {
                Log.d(TAG, "findMessagePosition: Target found at : 0");
                return 0;
            } else {
                Log.d(TAG, "findMessagePosition: Target not found");
                return -1;
            }
        }

        int size = collection.size();
        Integer[] arr = collection.toArray(new Integer[size]);
        int mid = -1;
        int lIndex = 0;
        int hIndex = size - 1;
        int X = target;

        while ((arr[hIndex] != arr[lIndex]) && (X >= arr[lIndex]) && (X <= arr[hIndex])) {
            mid = lIndex + ((hIndex - lIndex) * (X - arr[lIndex]) / (arr[hIndex] - arr[lIndex]));

            if (arr[mid] == X) {
                Log.d(TAG, "findMessagePosition: Target found at : " + mid);
                return mid;
            } else {
                if (arr[mid] < X)
                    lIndex = mid + 1;
                else if (arr[mid] > X)
                    hIndex = mid - 1;
            }
        }

        Log.d(TAG, "findMessagePosition: Target not found");
        return -1;
    }

    public static Integer findMessageAdapterPosition(@NonNull List<Message> collection,
                                                     @NonNull int target) {
        if (collection.size() == 0) {
            Log.d(TAG, "findMessageAdapterPosition: Target not found");
            return -1;
        } else if (collection.size() == 1) {
            if (collection.get(0).getId() == target) {
                Log.d(TAG, "findMessageAdapterPosition: Target found at : 0");
                return 0;
            } else {
                Log.d(TAG, "findMessageAdapterPosition: Target not found");
                return -1;
            }
        }

        int size = collection.size();
        Integer[] arr = new Integer[size];
        int mid = -1;
        int lIndex = 0;
        int hIndex = size - 1;
        int X = target;

        for (int i = 0; i < size; i++) {
            arr[i] = collection.get(i).getId();
        }

        while ((arr[hIndex] != arr[lIndex]) && (X >= arr[lIndex]) && (X <= arr[hIndex])) {
            mid = lIndex + ((hIndex - lIndex) * (X - arr[lIndex]) / (arr[hIndex] - arr[lIndex]));

            if (arr[mid] == X) {
                Log.d(TAG, "findMessageAdapterPosition: Target found at : " + mid);
                return mid;
            } else {
                if (arr[mid] < X)
                    lIndex = mid + 1;
                else if (arr[mid] > X)
                    hIndex = mid - 1;
            }
        }

        Log.d(TAG, "findMessageAdapterPosition: Target not found");
        return -1;
    }
}
