package com.max.navigation.model;

import java.util.List;

/**
 * Created by Maker on 2020/11/25.
 * Description:
 */
public class BottomBar {
    public String activeColor;
    public String inActiveColor;
    public int selectTab;
    public List<Tabs> tabs;


    public static class Tabs {
        public int size;
        public boolean enable;
        public int index;
        public String pageUrl;
        public String title;
        public String tintColor;

    }
}
