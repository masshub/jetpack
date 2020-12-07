package com.max.navigation.exo;

import android.view.ViewGroup;

/**
 * @author: maker
 * @date: 2020/12/7 17:22
 * @description:
 */
public interface IPlayerTarget {

    ViewGroup getOwner();

    void onActive();

    void inActive();

    boolean isPlaying();

}
