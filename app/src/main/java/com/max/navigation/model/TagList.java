package com.max.navigation.model;

import android.text.TextUtils;
import android.widget.TextView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import org.w3c.dom.Text;

import java.util.Objects;

/**
 * @author: maker
 * @date: 2021/1/12 17:21
 * @description:
 */
public class TagList extends BaseObservable {
    /**
     * id : 8
     * icon : https://p3-dy.byteimg.com/img/tos-cn-v-0000/3b1dd95af2e94225ba23bbbadef22ce2~200x200.jpeg
     * background : https://p3-dy.byteimg.com/img/tos-cn-v-0000/2eee9728d10240108d027c5e74868c0a~750x340.jpeg
     * activityIcon : https://sf1-nhcdn-tos.pstatp.com/obj/tos-cn-i-0000/51f076f662ef40c99f056a41b130c516
     * title : 纹身技术哪家强
     * intro : “你怎么纹了一个电风扇？”
     * “那叫四叶草！”
     * feedNum : 1234
     * tagId : 126137
     * enterNum : 79788
     * followNum : 79151
     * hasFollow : false
     */

    public int id;
    public String icon;
    public String background;
    public String activityIcon;
    public String title;
    public String intro;
    public int feedNum;
    public int tagId;
    public int enterNum;
    public int followNum;
    public boolean hasFollow;


    @Bindable
    public boolean isHasFollow() {
        return hasFollow;
    }

    public void setHasFollow(boolean hasFollow) {
        this.hasFollow = hasFollow;
        notifyPropertyChanged(BR._all);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagList tagList = (TagList) o;
        return id == tagList.id &&
                feedNum == tagList.feedNum &&
                tagId == tagList.tagId &&
                enterNum == tagList.enterNum &&
                followNum == tagList.followNum &&
                hasFollow == tagList.hasFollow &&
                TextUtils.equals(icon, tagList.icon) &&
                TextUtils.equals(background, tagList.background) &&
                TextUtils.equals(activityIcon, tagList.activityIcon) &&
                TextUtils.equals(title, tagList.title) &&
                TextUtils.equals(intro, tagList.intro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, icon, background, activityIcon, title, intro, feedNum, tagId, enterNum, followNum, hasFollow);
    }
}