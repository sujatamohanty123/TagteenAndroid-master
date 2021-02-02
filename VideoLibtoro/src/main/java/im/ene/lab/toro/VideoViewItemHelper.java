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

import android.media.MediaPlayer;
import android.view.View;
import android.view.ViewParent;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.CallSuper;

/**
 * Created by eneim on 2/1/16.
 *
 * A helper class to support Video's callbacks from {@link MediaPlayer} as well as {@link
 * RecyclerView.Adapter}
 */
class VideoViewItemHelper {

  /* BEGIN: Callback for View */

  /**
   * Callback from {@link RecyclerView.Adapter#onViewAttachedToWindow(RecyclerView.ViewHolder)}
   *
   * @param player the {@link ToroPlayer} which is attached to current ViewHolder
   * @param itemView main View of current ViewHolder
   * @param parent parent which holds current ViewHolder
   */
  public void onAttachedToParent(ToroPlayer player, View itemView, ViewParent parent) {

  }

  /**
   * Callback from {@link RecyclerView.Adapter#onViewDetachedFromWindow(RecyclerView.ViewHolder)}
   *
   * @param player the {@link ToroPlayer} which is attached to current ViewHolder
   * @param itemView main View of current ViewHolder
   * @param parent parent which holds current ViewHolder
   */
  public void onDetachedFromParent(ToroPlayer player, View itemView, ViewParent parent) {

  }

  /**
   * Support long press on Video, called by {@link View.OnLongClickListener#onLongClick(View)}
   *
   * @param player the {@link ToroPlayer} which is attached to current ViewHolder
   * @param itemView main View of current ViewHolder
   * @param parent parent which holds current ViewHolder
   * @return boolean response to {@link View.OnLongClickListener#onLongClick(View)}
   */
  public boolean onItemLongClick(ToroPlayer player, View itemView, ViewParent parent) {
    return false;
  }
  /* END: Callback for View */

  /* BEGIN: Callback for MediaPlayer */

  /**
   * Callback from {@link MediaPlayer.OnPreparedListener#onPrepared(MediaPlayer)}
   *
   * @param player current ToroPlayer instance
   * @param itemView main View of current ViewHolder
   * @param parent parent which holds current ViewHolder
   * @param mediaPlayer current MediaPlayer
   */
  @CallSuper public void onPrepared(ToroPlayer player, View itemView, ViewParent parent,
      MediaPlayer mediaPlayer) {
    Toro.checkNotNull();
    Toro.sInstance.onPrepared(player, itemView, parent, mediaPlayer);
  }

  /**
   * Callback from {@link MediaPlayer.OnCompletionListener#onCompletion(MediaPlayer)}
   *
   * @param player current ToroPlayer instance
   * @param mp completed MediaPlayer
   */
  @CallSuper public void onCompletion(ToroPlayer player, MediaPlayer mp) {
    Toro.checkNotNull();
    Toro.sInstance.onCompletion(player, mp);
  }

  /**
   * Callback from {@link MediaPlayer.OnErrorListener#onError(MediaPlayer, int, int)}
   *
   * @param player current ToroPlayer instance
   * @param mp current MediaPlayer
   */
  @CallSuper public boolean onError(ToroPlayer player, MediaPlayer mp, int what, int extra) {
    Toro.checkNotNull();
    return Toro.sInstance.onError(player, mp, what, extra);
  }

  /**
   * Callback from {@link MediaPlayer.OnInfoListener#onInfo(MediaPlayer, int, int)}
   *
   * @param player current ToroPlayer instance
   * @param mp current MediaPlayer
   */
  @CallSuper public boolean onInfo(ToroPlayer player, MediaPlayer mp, int what, int extra) {
    Toro.checkNotNull();
    return Toro.sInstance.onInfo(player, mp, what, extra);
  }

  /**
   * Callback from {@link MediaPlayer.OnSeekCompleteListener#onSeekComplete(MediaPlayer)}
   */
  @CallSuper public void onSeekComplete(ToroPlayer player, MediaPlayer mp) {
    Toro.checkNotNull();
    Toro.sInstance.onSeekComplete(player, mp);
  }
  /* END: Callback for MediaPlayer */
}
