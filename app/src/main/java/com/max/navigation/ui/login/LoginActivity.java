package com.max.navigation.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.max.navigation.R;
import com.max.navigation.model.User;
import com.max.network.ApiResponse;
import com.max.network.ApiService;
import com.max.network.JsonCallback;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: maker
 * @date: 2020/12/4 16:38
 * @description:
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mIvClose;
    private MaterialButton mMbLogin;
    private Tencent tencent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mIvClose = findViewById(R.id.iv_action_close);
        mMbLogin = findViewById(R.id.mb_action_login);

        mIvClose.setOnClickListener(this::onClick);
        mMbLogin.setOnClickListener(this::onClick);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_action_close) {
            finish();
        } else if (v.getId() == R.id.mb_action_login) {
            login();
        }

    }

    private void login() {
        if (tencent == null) {
            tencent = Tencent.createInstance("101794421", getApplicationContext());
        }

        tencent.login(this, "all", new IUiListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject response = (JSONObject) o;

                try {
                    String openid = response.getString("openid");
                    String access_token = response.getString("access_token");
                    String expires_in = response.getString("expires_in");
                    long expires_time = response.getLong("expires_time");

                    tencent.setAccessToken(access_token, expires_in);
                    tencent.setOpenId(openid);
                    QQToken qqToken = tencent.getQQToken();
                    getUserInfo(qqToken, openid, expires_time);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(LoginActivity.this, "登录失败：" + uiError.errorMessage, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "登录取消", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onWarning(int i) {

            }
        });


    }

    private void getUserInfo(QQToken qqToken, String openId, long expires_time) {
        UserInfo userInfo = new UserInfo(getApplicationContext(), qqToken);
        userInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject response = (JSONObject) o;
                try {
                    String nickname = response.getString("nickname");
                    String figureurl_2 = response.getString("figureurl_2");
                    saveUserInfo(nickname, figureurl_2, openId, expires_time);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(LoginActivity.this, "获取用户信息失败：" + uiError.errorMessage, Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "取消获取用户信息", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onWarning(int i) {

            }
        });
    }

    private void saveUserInfo(String nickname, String figureurl_2, String openId, long expires_time) {
        ApiService.get("/user/insert")
                .addParam("name", nickname)
                .addParam("avatar", figureurl_2)
                .addParam("expires_time", expires_time)
                .addParam("qqOpenId", openId)
                .execute(new JsonCallback<User>() {
                    @Override
                    public void onSuccess(ApiResponse<User> response) {
                        if (response.body != null) {
                            UserManager.get().save(response.body);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(ApiResponse<User> response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }
}