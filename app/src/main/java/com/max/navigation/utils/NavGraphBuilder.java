package com.max.navigation.utils;

import android.content.ComponentName;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;

import com.max.common.AppGlobals;
import com.max.libnavannotation.FragmentDestination;
import com.max.navigation.model.Destination;

import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * Created by Maker on 2020/11/25.
 * Description:
 */
public class NavGraphBuilder {
    public static void build(NavController controller, FragmentActivity activity,int containerId) {
        NavigatorProvider provider = controller.getNavigatorProvider();

//        FragmentNavigator fragmentNavigator = provider.getNavigator(FragmentNavigator.class);

        NavGraph navGraph = new NavGraph(new NavGraphNavigator(provider));

        FixFragmentNavigator fragmentNavigator = new FixFragmentNavigator(activity,activity.getSupportFragmentManager(),containerId);
        provider.addNavigator(fragmentNavigator);

        ActivityNavigator activityNavigator = provider.getNavigator(ActivityNavigator.class);

        HashMap<String, Destination> destConfig = AppConfig.getDestConfig();


        for (Destination value : destConfig.values()) {
            if (value.isFragment()) {
                FragmentNavigator.Destination destination = fragmentNavigator.createDestination();
                destination.setClassName(value.getClazName());
                destination.setId(value.getId());
                destination.addDeepLink(value.getPageUrl());
                navGraph.addDestination(destination);

            } else {
                ActivityNavigator.Destination destination = activityNavigator.createDestination();
                destination.setId(value.getId());
                destination.addDeepLink(value.getPageUrl());
                destination.setComponentName(new ComponentName(AppGlobals.getApplication().getPackageName(), value.getClazName()));
                navGraph.addDestination(destination);
            }

            if(value.isAsStarter()){
                navGraph.setStartDestination(value.getId());
            }
        }

        controller.setGraph(navGraph);

    }
}
