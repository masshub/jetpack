package com.max.navigation;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.max.navigation.model.Destination;
import com.max.navigation.model.User;
import com.max.navigation.ui.login.UserManager;
import com.max.navigation.utils.AppConfig;
import com.max.navigation.utils.NavGraphBuilder;
import com.max.navigation.utils.StatusBar;
import com.max.navigation.view.AppBottomBar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private NavController navController;
    private AppBottomBar appBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Navigation);
        StatusBar.fitSystemBar(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        appBottomBar = findViewById(R.id.nav_view);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (fragment != null) {
            navController = NavHostFragment.findNavController(fragment);
            NavGraphBuilder.build(navController, this, fragment.getId());
        }


        appBottomBar.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        HashMap<String, Destination> destConfig = AppConfig.getDestConfig();
        Iterator<Map.Entry<String, Destination>> iterator = destConfig.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Destination> next = iterator.next();
            Destination value = next.getValue();
            if(value != null && !UserManager.get().isLogin() && value.isNeedLogin() && value.getId() == item.getItemId()){
                UserManager.get().login(this).observe(this, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        appBottomBar.setSelectedItemId(item.getItemId());

                    }
                });
                return false;
            }
        }
        navController.navigate(item.getItemId());
        return !TextUtils.isEmpty(item.getTitle());
    }
}