package com.max.navigation.utils;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

/**
 * @author: maker
 * @date: 2020/12/1 16:30
 * @description:
 */
public class StringConvert {
    public static String convertFeedUgc(int count){
        if(count < 10000){
            return String.valueOf(count);
        }

        return count / 10000 + "万";
    }

    public static String convertFeedTag(int num){
        if(num < 10000){
            return num + "人观看";
        } else {
            return num / 10000 + "万人观看";
        }
    }


    public static String convertSpannable(int count,String introduce){
        String countStr = String.valueOf(count);
        SpannableString ss = new SpannableString(countStr+introduce);
        ss.setSpan(new ForegroundColorSpan(Color.BLACK),0,countStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(16,true),0,countStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new StyleSpan(Typeface.BOLD),0,countStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss.toString();
    }
} 