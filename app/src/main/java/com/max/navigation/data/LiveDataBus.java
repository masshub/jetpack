package com.max.navigation.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: maker
 * @date: 2020/12/16 17:34
 * @description:
 */
public class LiveDataBus {
    public static LiveDataBus get() {
        return Lazy.sLiveDataBus;
    }

    private static class Lazy {
        static LiveDataBus sLiveDataBus = new LiveDataBus();

    }

    private ConcurrentHashMap<String, StickyLiveData> mHashMap = new ConcurrentHashMap<String, StickyLiveData>();

    public StickyLiveData with(String eventName) {
        StickyLiveData liveData = mHashMap.get(eventName);
        if (liveData == null) {
            liveData = new StickyLiveData(eventName);
            mHashMap.put(eventName, liveData);
        }
        return liveData;
    }


    public class StickyLiveData<T> extends LiveData<T> {
        private String mEventName;
        private T mStickyData;
        private int mVersion = 0;

        public StickyLiveData(String eventName) {
            mEventName = eventName;
        }

        @Override
        public void setValue(T value) {
            mVersion++;
            super.setValue(value);
        }

        @Override
        public void postValue(T value) {
            mVersion++;
            super.postValue(value);
        }

        public void setStickyData(T stickyData) {
            this.mStickyData = stickyData;
            setValue(stickyData);
        }

        public void postStickyData(T stickyData) {
            this.mStickyData = stickyData;
            postValue(stickyData);
        }



        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            stickyObserve(owner, observer, false);
        }

        public void stickyObserve(LifecycleOwner owner, Observer<? super T> observer, boolean sticky) {
            super.observe(owner, new WrapperObserver(this, observer, sticky));
            owner.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        mHashMap.remove(mEventName);
                    }
                }
            });

        }

        private class WrapperObserver implements Observer<T> {

            private StickyLiveData<T> mStickyLiveData;
            private Observer<? super T> mObserver;
            private boolean mSticky;
            private int lastVersion;

            public WrapperObserver(StickyLiveData<T> stickyLiveData, Observer<? super T> observer, boolean sticky) {
                mStickyLiveData = stickyLiveData;
                mObserver = observer;
                mSticky = sticky;
                lastVersion = stickyLiveData.mVersion;
            }

            @Override
            public void onChanged(T t) {
                if (lastVersion >= mStickyLiveData.mVersion) {
                    if (mSticky && mStickyLiveData.mStickyData != null) {
                        mObserver.onChanged(mStickyLiveData.mStickyData);
                    }
                    return;
                }

                lastVersion = mStickyLiveData.mVersion;
                mObserver.onChanged(t);

            }
        }
    }
}