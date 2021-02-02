/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.lab.toro;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewParent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by eneim on 1/31/16.
 * <p/>
 * Control Application's lifecycle to properly handling callbacks, prevent Memory leak and
 * unexpected behavior;
 *
 * @<code> </code>
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) public final class Toro
    implements Application.ActivityLifecycleCallbacks {

  private static final String TAG = "Toro";

  private static final Object LOCK = new Object();

  /**
   * Stop playback strategy
   */
  private static final ToroStrategy REST = new ToroStrategy() {
    @Override public String getDescription() {
      return "Rest";
    }

    @Override public ToroPlayer findBestPlayer(List<ToroPlayer> candidates) {
      return null;
    }

    @Override public boolean allowsToPlay(ToroPlayer player, ViewParent parent) {
      return false;
    }
  };

  // Singleton, GOD object
  static volatile Toro sInstance;

  /**
   * Helper object, support RecyclerView's ViewHolder
   */
  static VideoViewItemHelper RECYCLER_VIEW_HELPER = new RecyclerViewItemHelper();

  private static ToroStrategy cachedStrategy;

  // It requires client to detach Activity/unregister View to prevent Memory leak
  // Use RecyclerView#hashCode() to sync between maps
  final Map<Integer, RecyclerView> mViews = new ConcurrentHashMap<>();
  final Map<Integer, ToroScrollListener> mListeners = new ConcurrentHashMap<>();

  // !IMPORTANT I limit this Map capacity to 3
  private LinkedStateList mStates;

  // Default strategy
  private ToroStrategy mStrategy = Strategies.MOST_VISIBLE_TOP_DOWN;

  /**
   * Attach an activity to Toro. Toro register activity's life cycle to properly handle Screen
   * visibility: free necessary resource if User doesn't need it anymore
   *
   * @param activity the Activity to which Toro gonna attach to
   */
  public static void attach(@NonNull Activity activity) {
    init(activity.getApplication());
    if (sInstance.mStates == null) {
      sInstance.mStates = new LinkedStateList(3);
    }
  }

  /**
   * Same purpose to {@link Toro#attach(Activity)}, but support overall the Application
   */
  public static void init(Application application) {
    if (sInstance == null) {
      synchronized (LOCK) {
        sInstance = new Toro();
      }
    }

    if (application != null) {
      application.registerActivityLifecycleCallbacks(sInstance);
    }
  }

  /**
   * Carefully detach current Activity from Toro. Should be coupled with {@link
   * Toro#attach(Activity)}
   */
  public static void detach(Activity activity) {
    checkNotNull();
    Application application = activity.getApplication();
    if (application != null) {
      application.unregisterActivityLifecycleCallbacks(sInstance);
    }

    if (sInstance.mStates != null) {
      sInstance.mStates.clear();
    }

    // Cleanup
    for (RecyclerView view : sInstance.mViews.values()) {
      unregister(view);
    }
  }

  public static ToroStrategy getStrategy() {
    checkNotNull();
    return sInstance.mStrategy;
  }

  /**
   * Support custom playing policy
   *
   * @param strategy requested policy from client
   */
  public static void setStrategy(@NonNull ToroStrategy strategy) {
    checkNotNull();
    if (sInstance.mStrategy == strategy) {
      // Nothing changes
      return;
    }

    sInstance.mStrategy = strategy;
    notifyStrategyChanged(strategy);
  }

  /**
   * Register a View (currently, must be one of RecyclerView) to listen to its Videos
   *
   * @param view which will be registered
   */
  public static void register(RecyclerView view) {
    checkNotNull();
    if (view == null) {
      throw new NullPointerException("Registering View must not be null");
    }

    if (sInstance.mViews.containsKey(view.hashCode())) {
      if (sInstance.mListeners.containsKey(view.hashCode())) {
        sInstance.mListeners.get(view.hashCode()).getManager().onRegistered();
        return;
      }
    }

    // 1. retrieve current TotoManager instance
    VideoPlayerManager playerManager = null;
    RecyclerView.Adapter adapter = view.getAdapter();
    // Client of this API should implement ToroManager to her Adapter.
    if (adapter instanceof VideoPlayerManager) {
      playerManager = (VideoPlayerManager) adapter;
    }

    // If no manager found, fallback to Built-in Manager
    if (playerManager == null) {
      playerManager = new VideoPlayerManagerImpl();
    }

    ToroScrollListener listener = new ToroScrollListener(playerManager);
    view.addOnScrollListener(listener);
    // Cache
    sInstance.mViews.put(view.hashCode(), view);
    sInstance.mListeners.put(view.hashCode(), listener);

    final State state;
    if (sInstance.mStates.containsKey(view.hashCode())) {
      state = sInstance.mStates.get(view.hashCode());
    } else {
      state = new State();
      sInstance.mStates.put(view.hashCode(), state);
    }

    if (state != null && state.player != null) {
      // Cold start playManager from a saved state
      playerManager.setPlayer(state.player);
      playerManager.saveVideoState(state.player.getVideoId(), state.position,
          state.player.getDuration());
    }

    playerManager.onRegistered();
  }

  /**
   * Unregister a registered View
   *
   * @param view which will be unregistered
   */
  public static void unregister(RecyclerView view) {
    checkNotNull();
    if (view == null) {
      throw new NullPointerException("Un-registering View must not be null");
    }

    if (sInstance.mViews.containsKey(view.hashCode())) {
      // Obtain listener which will be removed
      ToroScrollListener listener = sInstance.mListeners.remove(view.hashCode());
      // Process related View
      if (listener != null) {
        // Cleanup manager
        // 1. Save this state
        final State state;
        if (sInstance.mStates.containsKey(view.hashCode())) {
          state = sInstance.mStates.get(view.hashCode());
        } else {
          state = new State();
          sInstance.mStates.put(view.hashCode(), state);
        }

        if (listener.getManager().getPlayer() != null) {
          state.player = listener.getManager().getPlayer();
          state.position = listener.getManager().getPlayer().getCurrentPosition();
        }

        listener.getManager().onUnregistered();
        view.removeOnScrollListener(listener);
      }
      // Remove from cache
      sInstance.mViews.remove(view.hashCode());
    }
  }

  static void checkNotNull() {
    if (sInstance == null) {
      throw new IllegalStateException(
          "Toro has not been attached to your Activity or you Application. Please refer the doc");
    }
  }

  public static void rest(boolean willPause) {
    if (willPause) {
      cachedStrategy = getStrategy();
      setStrategy(REST);
    } else {
      // Don't allow to unrest if  Toro has not been in rested state. Be careful.
      if (getStrategy() != REST) {
        throw new IllegalStateException("Toro has already waken up.");
      }

      if (cachedStrategy != null) { // Actually, cachedStrategy would not be null here.
        setStrategy(cachedStrategy);
      }
    }
  }

  private static void notifyStrategyChanged(ToroStrategy newStrategy) {
    for (RecyclerView view : sInstance.mViews.values()) {
      int hash = view.hashCode();
      ToroScrollListener listener = sInstance.mListeners.get(hash);
      if (listener != null) {
        listener.onScrollStateChanged(view, RecyclerView.SCROLL_STATE_IDLE);
      }
    }
  }

  final void onCompletion(ToroPlayer player, MediaPlayer mediaPlayer) {
    // 1. find manager for this player
    VideoPlayerManager manager = null;
    for (ToroScrollListener listener : sInstance.mListeners.values()) {
      manager = listener.getManager();
      if (player.equals(manager.getPlayer())) {
        break;
      } else {
        manager = null;
      }
    }

    // Normally stop playback
    if (manager != null) {
      manager.saveVideoState(player.getVideoId(), 0, player.getDuration());
      manager.pausePlayback();
      player.onPlaybackStopped();
    }

    // if (sConfig.loopAble) { // It's loop-able, so restart it immediately
    if (player.isLoopAble()) {
      if (manager != null) {
        // immediately repeat
        manager.restoreVideoState(player.getVideoId());
        manager.startPlayback();
        player.onPlaybackStarted();
      }
    }
  }

  final void onPrepared(ToroPlayer player, View container, ViewParent parent,
      MediaPlayer mediaPlayer) {
    player.onVideoPrepared(mediaPlayer);
    VideoPlayerManager manager = null;
    ToroScrollListener listener;
    RecyclerView view;
    // Find correct Player manager for this player
    for (Map.Entry<Integer, ToroScrollListener> entry : sInstance.mListeners.entrySet()) {
      Integer key = entry.getKey();
      view = sInstance.mViews.get(key);
      if (view != null && view == parent) { // Found the parent view in our cache
        listener = entry.getValue();
        manager = listener.getManager();
        break;
      }
    }

    if (manager == null) {
      return;
    }

    // 1. Check if current manager wrapped this player
    if (player.equals(manager.getPlayer())) {
      if (player.wantsToPlay() && player.isAbleToPlay() // \n
          && getStrategy().allowsToPlay(player, parent)) {
        manager.restoreVideoState(player.getVideoId());
        manager.startPlayback();
        player.onPlaybackStarted();
      }
    } else {
      // There is no current player, but this guy is prepared, so let's him go ...
      if (manager.getPlayer() == null) {
        // ... if it's possible
        if (player.wantsToPlay() && player.isAbleToPlay() // \n
            && getStrategy().allowsToPlay(player, parent)) {
          manager.setPlayer(player);
          manager.restoreVideoState(player.getVideoId());
          manager.startPlayback();
          player.onPlaybackStarted();
        }
      }
    }
  }

  final boolean onError(ToroPlayer player, MediaPlayer mp, int what, int extra) {
    boolean handle = player.onPlaybackError(mp, what, extra);
    for (ToroScrollListener listener : sInstance.mListeners.values()) {
      VideoPlayerManager manager = listener.getManager();
      if (player.equals(manager.getPlayer())) {
        manager.saveVideoState(player.getVideoId(), 0, player.getDuration());
        manager.pausePlayback();
        return true;
      }
    }
    return handle;
  }

  final boolean onInfo(ToroPlayer player, MediaPlayer mp, int what, int extra) {
    player.onPlaybackInfo(mp, what, extra);
    return true;
  }

  final void onSeekComplete(ToroPlayer player, MediaPlayer mp) {
    // Do nothing
  }

  @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    if (mStates == null) {
      mStates = new LinkedStateList(3);
    }
  }

  @Override public void onActivityStarted(Activity activity) {

  }

  @Override public void onActivityResumed(Activity activity) {
    for (Map.Entry<Integer, ToroScrollListener> entry : mListeners.entrySet()) {
      ToroScrollListener listener = entry.getValue();
      State state = mStates.get(entry.getKey());
      VideoPlayerManager manager = listener.getManager();
      if (manager.getPlayer() == null) {
        if (state != null && state.player != null) {
          manager.setPlayer(state.player);
          manager.saveVideoState(state.player.getVideoId(), state.position,
              state.player.getDuration());
        }
      }

      if (manager.getPlayer() != null) {
        manager.startPlayback();
        manager.getPlayer().onActivityResumed();
      }
    }
  }

  @Override public void onActivityPaused(Activity activity) {
    for (Map.Entry<Integer, ToroScrollListener> entry : mListeners.entrySet()) {
      ToroScrollListener listener = entry.getValue();
      State state = mStates.get(entry.getKey());
      if (state == null) {
        state = new State();
        mStates.put(entry.getKey(), state);
      }

      VideoPlayerManager manager = listener.getManager();
      if (manager.getPlayer() != null) {
        // Save state
        state.player = manager.getPlayer();
        state.position = manager.getPlayer().getCurrentPosition();

        if (manager.getPlayer().isPlaying()) {
          manager.saveVideoState(manager.getPlayer().getVideoId(),
              manager.getPlayer().getCurrentPosition(), manager.getPlayer().getDuration());
          manager.pausePlayback();
          manager.getPlayer().onPlaybackPaused();
        }

        manager.getPlayer().onActivityPaused();
      }
    }
  }

  @Override public void onActivityStopped(Activity activity) {

  }

  @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

  }

  @Override public void onActivityDestroyed(Activity activity) {
    if (mStates != null) {
      for (State state : mStates.values()) {
        if (state.player != null) {
          // Release resource if there is any
          state.player.pause();
          state.player.onActivityPaused();
          // Release this player
          state.player = null;
        }
      }

      mStates.clear();
    }
  }

  public static final class Strategies {

    /**
     * Among all playable Videos, select the most visible item (Max {@link
     * ToroPlayer#visibleAreaOffset()}). In case there are more than one item, chose the first item
     * on the top
     */
    public static final ToroStrategy MOST_VISIBLE_TOP_DOWN = new ToroStrategy() {

      @Override public String getDescription() {
        return "Most visible item, top - down";
      }

      @Override public ToroPlayer findBestPlayer(List<ToroPlayer> candidates) {
        if (candidates == null || candidates.size() < 1) {
          return null;
        }

        // 1. Sort candidates by the order of player
        Collections.sort(candidates, new Comparator<ToroPlayer>() {
          @Override public int compare(ToroPlayer lhs, ToroPlayer rhs) {
            return lhs.getPlayOrder() - rhs.getPlayOrder();
          }
        });

        // 2. Sort candidates by the visible offset
        Collections.sort(candidates, new Comparator<ToroPlayer>() {
          @Override public int compare(ToroPlayer lhs, ToroPlayer rhs) {
            return Float.compare(rhs.visibleAreaOffset(), lhs.visibleAreaOffset());
          }
        });

        return candidates.get(0);
      }

      @Override public boolean allowsToPlay(ToroPlayer player, ViewParent parent) {
        Rect windowRect = new Rect();
        Rect parentRect = new Rect();
        if (parent instanceof View) {
          // 1. Get Window's vision from parent
          ((View) parent).getWindowVisibleDisplayFrame(windowRect);
          // 2. Get parent's global rect
          ((View) parent).getGlobalVisibleRect(parentRect, new Point());
        }
        // 3. Get player global rect
        Rect videoRect = new Rect();
        player.getVideoView().getGlobalVisibleRect(videoRect, new Point());

        // Condition: window contains parent, and parent contains Video or parent intersects Video
        return windowRect.contains(parentRect) && (parentRect.contains(videoRect)
            || parentRect.intersect(videoRect));
      }
    };

    /**
     * Among all playable Videos, select the most visible item (Max {@link
     * ToroPlayer#visibleAreaOffset()}). In case there are more than one item, chose the first item
     * on the top. But if current player is still playable, but not staying on the top, we still
     * keep it.
     */
    public static final ToroStrategy MOST_VISIBLE_TOP_DOWN_KEEP_LAST = new ToroStrategy() {
      @Override public String getDescription() {
        return "Most visible item, top - down. Keep last playing item.";
      }

      @Override public ToroPlayer findBestPlayer(List<ToroPlayer> candidates) {
        if (candidates == null || candidates.size() < 1) {
          return null;
        }

        // Sort candidates by the visible offset
        Collections.sort(candidates, new Comparator<ToroPlayer>() {
          @Override public int compare(ToroPlayer lhs, ToroPlayer rhs) {
            return Float.compare(rhs.visibleAreaOffset(), lhs.visibleAreaOffset());
          }
        });

        return candidates.get(0);
      }

      @Override public boolean allowsToPlay(ToroPlayer player, ViewParent parent) {
        Rect windowRect = new Rect();
        Rect parentRect = new Rect();
        if (parent instanceof View) {
          // 1. Get Window's vision from parent
          ((View) parent).getWindowVisibleDisplayFrame(windowRect);
          // 2. Get parent's global rect
          ((View) parent).getGlobalVisibleRect(parentRect, new Point());
        }
        // 3. Get player global rect
        Rect videoRect = new Rect();
        player.getVideoView().getGlobalVisibleRect(videoRect, new Point());

        // Condition: window contains parent, and parent contains Video or parent intersects Video
        return windowRect.contains(parentRect) && (parentRect.contains(videoRect)
            || parentRect.intersect(videoRect));
      }
    };

    /**
     * Scan top down of candidates, chose the first playable Video
     */
    public static final ToroStrategy FIRST_PLAYABLE_TOP_DOWN = new ToroStrategy() {
      @Override public String getDescription() {
        return "First playable item, top - down";
      }

      @Override public ToroPlayer findBestPlayer(List<ToroPlayer> candidates) {
        if (candidates == null || candidates.size() < 1) {
          return null;
        }

        // 1. Sort candidates by the order of player
        Collections.sort(candidates, new Comparator<ToroPlayer>() {
          @Override public int compare(ToroPlayer lhs, ToroPlayer rhs) {
            return lhs.getPlayOrder() - rhs.getPlayOrder();
          }
        });

        return candidates.get(0);
      }

      @Override public boolean allowsToPlay(ToroPlayer player, ViewParent parent) {
        Rect windowRect = new Rect();
        Rect parentRect = new Rect();
        if (parent instanceof View) {
          // 1. Get Window's vision from parent
          ((View) parent).getWindowVisibleDisplayFrame(windowRect);
          // 2. Get parent's global rect
          ((View) parent).getGlobalVisibleRect(parentRect, new Point());
        }
        // 3. Get player global rect
        Rect videoRect = new Rect();
        player.getVideoView().getGlobalVisibleRect(videoRect, new Point());

        // Condition: window contains parent, and parent contains Video or parent intersects Video
        return windowRect.contains(parentRect) && (parentRect.contains(videoRect)
            || parentRect.intersect(videoRect));
      }
    };

    /**
     * Scan top down (by layout direction) of candidates, chose the first playable Video. But if
     * current player is still playable, but not on the top, we keep using it
     */
    public static final ToroStrategy FIRST_PLAYABLE_TOP_DOWN_KEEP_LAST = new ToroStrategy() {

      @Override public String getDescription() {
        return "First playable item, top - down. Keep last playing item.";
      }

      @Override public ToroPlayer findBestPlayer(List<ToroPlayer> candidates) {
        if (candidates == null || candidates.size() < 1) {
          return null;
        }

        return candidates.get(0);
      }

      @Override public boolean allowsToPlay(ToroPlayer player, ViewParent parent) {
        Rect windowRect = new Rect();
        Rect parentRect = new Rect();
        if (parent instanceof View) {
          // 1. Get Window's vision from parent
          ((View) parent).getWindowVisibleDisplayFrame(windowRect);
          // 2. Get parent's global rect
          ((View) parent).getGlobalVisibleRect(parentRect, new Point());
        }
        // 3. Get player global rect
        Rect videoRect = new Rect();
        player.getVideoView().getGlobalVisibleRect(videoRect, new Point());

        // Condition: window contains parent, and parent contains Video or parent intersects Video
        return windowRect.contains(parentRect) && (parentRect.contains(videoRect)
            || parentRect.intersect(videoRect));
      }
    };
  }
}
