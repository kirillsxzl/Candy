package cf.ga2mer.candy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import cf.ga2mer.candy.database.Account;
import cf.ga2mer.candy.database.AccountDatabaseHandler;

public class LoginActivity extends ActionBarActivity {
    long captchaSid;
    String captcha_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        final MaterialEditText login = (MaterialEditText) findViewById(R.id.login);
        final MaterialEditText password = (MaterialEditText) findViewById(R.id.password);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login.getText().toString().equals("")){
                    login.setError("Fill field");
                }
                else if (password.getText().toString().equals("")){
                    password.setError("Fill field");
                }
                else {
                    doAuth(login.getText().toString(), password.getText().toString(), false);
                }
            }
        });
    }

    void doAuth(final String login, final String password, boolean captcha){
        RequestParams params = new RequestParams();
        params.put("grant_type", "password");
        params.put("scope", "nohttps,offline");
        params.put("client_id", "2274003");
        params.put("client_secret", "hHbZxrka2uZ6jB1inYsH");
        params.put("username", login);
        params.put("password", password);
        if (captcha){
            params.put("captcha_sid", captchaSid);
            params.put("captcha_key", captcha_key);
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("https://oauth.vk.com/token", params, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.out.println("no work " + responseString);
                if(responseString != null) {
                    if (responseString.contains("need_validation")) {
                        try {
                            JSONObject response = new JSONObject(responseString);
                            final Dialog dialog = new Dialog(LoginActivity.this);
                            String redirect_uri = response.getString("redirect_uri");
                            LinearLayout ll = new LinearLayout(LoginActivity.this);
                            ll.setOrientation(LinearLayout.VERTICAL);
                            WebView webView = new WebView(LoginActivity.this);
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.loadUrl(redirect_uri);
                            webView.setWebViewClient(new WebViewClient() {

                                public void onPageFinished(WebView view, String url) {
                                    if (url.contains("oauth.vk.com")) {
                                        StringTokenizer stringTokenizer = new StringTokenizer(url);
                                        String accessToken = "";
                                        String userID = "";
                                        String secret = "";
                                        while (stringTokenizer.hasMoreTokens()) {
                                            String tokenizer = stringTokenizer.nextToken("&");
                                            if (tokenizer.contains("access_token")) {
                                                tokenizer = tokenizer.replace("access_token=", "");
                                                accessToken = tokenizer;
                                            }
                                            if (tokenizer.contains("user_id")) {
                                                tokenizer = tokenizer.replace("user_id=", "");
                                                userID = tokenizer;
                                            }
                                            if (tokenizer.contains("secret")) {
                                                tokenizer = tokenizer.replace("secret=", "");
                                                secret = tokenizer;
                                            }
                                        }
                                        dialog.dismiss();
                                        addAccount(accessToken, Long.parseLong(userID), secret);
                                    }
                                }
                            });
                            dialog.setTitle("Двухфакторная аутентификация");
                            ll.addView(webView);
                            dialog.setContentView(ll);
                            dialog.show();
                        } catch (JSONException e) {

                        }
                    }
                    if (responseString.contains("invalid_client")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("Произошла ошибка!")
                                .setMessage("" + responseString)
                                .setCancelable(false)
                                .setNegativeButton("ОК",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    if (responseString.contains("need_captcha")) {
                        try {
                            final Dialog dialog = new Dialog(LoginActivity.this);
                            JSONObject response = new JSONObject(responseString);
                            captchaSid = response.getLong("captcha_sid");
                            System.out.println(captchaSid);
                            final MaterialEditText captchaKey = new MaterialEditText(LoginActivity.this);
                            captchaKey.setHint("Капча");
                            ImageView captcha = new ImageView(LoginActivity.this);
                            Picasso.with(LoginActivity.this).load("https://api.vk.com/captcha.php?sid=" + captchaSid).into(captcha);
                            Button button = new Button(LoginActivity.this);
                            button.setText("OK");
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    captcha_key = captchaKey.getText().toString();
                                    doAuth(login, password, true);
                                    dialog.dismiss();
                                }
                            });
                            LinearLayout captchaLayout = new LinearLayout(LoginActivity.this);
                            captchaLayout.setOrientation(LinearLayout.VERTICAL);
                            captchaLayout.addView(captcha);
                            captchaLayout.addView(captchaKey);
                            captchaLayout.addView(button);
                            dialog.setTitle("Введите капчу");
                            dialog.setContentView(captchaLayout);
                            dialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject response = new JSONObject(responseString);
                    System.out.println("token " + response.getString("access_token"));
                    addAccount(response.getString("access_token"), Long.parseLong(response.getString("user_id")), response.getString("secret"));
                }
                catch (JSONException e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Важное сообщение!")
                            .setMessage(""+responseString)
                            .setCancelable(false)
                            .setNegativeButton("ОК",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }
    void addAccount(String accessToken, long user_id, String secret){
        AccountDatabaseHandler db = new AccountDatabaseHandler(this);

        System.out.println("Inserting ..");
        System.out.println("token " + accessToken);
        db.addAccount(new Account(user_id, accessToken, secret, "", "", "", ""));
        db.close();
        startActivity(new Intent(this, ChooseAccountActivity.class));
        finish();
    }

}
