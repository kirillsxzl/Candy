package cf.ga2mer.candy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cf.ga2mer.candy.database.Account;
import cf.ga2mer.candy.database.AccountDatabaseHandler;

public class ChooseAccountActivity extends ActionBarActivity {
    List<Fragment> accountList = new ArrayList<Fragment>();
    AccountPagerAdapter adapter;
    AsyncHttpClient client;
    List<Long> ids = new ArrayList<>();
    List<Account> account = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_account);
        client = new AsyncHttpClient();
        adapter = new AccountPagerAdapter(getSupportFragmentManager(), accountList);
        ImageView addUser = (ImageView) findViewById(R.id.addUser);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseAccountActivity.this, LoginActivity.class));
            }
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.choosePager);
        viewPager.setAdapter(adapter);
        CirclePageIndicator cpi = (CirclePageIndicator) findViewById(R.id.titles);
        cpi.setViewPager(viewPager);
        final AccountDatabaseHandler db = new AccountDatabaseHandler(this);
        List<Account> contacts = db.getAllAccounts();
        getSupportActionBar().hide();
        for (Account cn : contacts) {
            String log = "Id: "+cn.getUserId()+" ,Name: " + cn.getAccessToken() + " , status: " + cn.getStatus();
            System.out.println(log);
            if (cn.getStatus().equals("")){
                ids.add(cn.getUserId());
                account.add(cn);
            }
        }
        if (!ids.isEmpty()){
            System.out.println("run");
            RequestParams params = new RequestParams();
            String formatedString = ids.toString()
                    .replace("[", "")
                    .replace("]", "")
                    .trim();
            params.put("user_ids", formatedString);
            params.put("fields", "photo_big");
            client.post("https://api.vk.com/method/users.get", params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONObject response = new JSONObject(responseString);
                        JSONArray array = response.getJSONArray("response");
                        for (int i = 0; i < array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String avatar = object.getString("photo_big");
                            account.get(i).setFirstName(first_name);
                            account.get(i).setLastName(last_name);
                            account.get(i).setAvatarURL(avatar);
                            account.get(i).setStatus("" + i);
                            System.out.println(account.get(i).getId());
                            System.out.println(account.get(i).getLastName());
                            System.out.println(account.get(i).getAvatarURL());
                            db.updateAccount(account.get(i));
                        }
                        addAccounts();
                        System.out.println("Успешно");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        else{
            addAccounts();
        }
        db.close();
    }
    void addAccounts(){
        final AccountDatabaseHandler db = new AccountDatabaseHandler(this);
        List<Account> contacts = db.getAllAccounts();
        for (Account cn : contacts) {
            accountList.add(AccountFragment.newInstance(String.format("%s %s", cn.getFirstName(), cn.getLastName()), cn.getAvatarURL(), cn.getId()));
        }
        adapter.notifyDataSetChanged();
        db.close();
    }

    private class AccountPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public AccountPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int pos) {
            return this.fragments.get(pos);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }

    public static class AccountFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.account_spinner_item, container, false);

            TextView flname = (TextView) v.findViewById(R.id.nickname);
            flname.setText(getArguments().getString("nickname"));
            ImageView avatar = (ImageView) v.findViewById(R.id.avatar);
            Picasso.with(getActivity()).load(getArguments().getString("avatar")).transform(new CropSquareTransformation()).into(avatar);

            return v;
        }

        public static AccountFragment newInstance(String flname, String avatar, long id) {

            AccountFragment f = new AccountFragment();
            Bundle b = new Bundle();
            b.putString("nickname", flname);
            b.putString("avatar", avatar);

            f.setArguments(b);

            return f;
        }
    }
}
