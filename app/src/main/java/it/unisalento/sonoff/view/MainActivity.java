package it.unisalento.sonoff.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import it.unisalento.sonoff.adapter.ViewPagerAdapter;
import it.unisalento.sonoff.R;
import it.unisalento.sonoff.model.User;
import it.unisalento.sonoff.service.MqttService;
import it.unisalento.sonoff.view.fragment.InputOneFragment;
import it.unisalento.sonoff.view.fragment.InputThreeFragment;
import it.unisalento.sonoff.view.fragment.InputTwoFragment;

@SuppressWarnings("FieldMayBeFinal")
public class MainActivity extends AppCompatActivity{

    private TextView tvDashboard;
    private User user;
    private Intent intent;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra("user");
        if(user != null) {
            setContentView(R.layout.activity_main);

            Intent mymqttservice_intent = new Intent(this, MqttService.class);
            startService(mymqttservice_intent);

            if(user.getRole().equals("admin")){
                intent = new Intent(this, DashboardActivity.class);
                intent.putExtra("user", user);
                tvDashboard = findViewById(R.id.tvDashboard);
                tvDashboard.setVisibility(View.VISIBLE);
                tvDashboard.setClickable(true);
                tvDashboard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(intent);
                    }
                });
            }

            InputOneFragment inputOneFragment = new InputOneFragment();
            InputTwoFragment inputTwoFragment = new InputTwoFragment();
            InputThreeFragment inputThreeFragment = new InputThreeFragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);

            inputOneFragment.setArguments(bundle);
            inputTwoFragment.setArguments(bundle);
            inputThreeFragment.setArguments(bundle);

            viewPagerAdapter = new ViewPagerAdapter(this);
            viewPagerAdapter.addFragment(inputOneFragment);
            viewPagerAdapter.addFragment(inputTwoFragment);
            viewPagerAdapter.addFragment(inputThreeFragment);

            viewPager = findViewById(R.id.viewPager);
            viewPager.setAdapter(viewPagerAdapter);

             tabLayout = findViewById(R.id.tabLayout);
            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> tab.setText("Input " + (position + 1))
            ).attach();

        }
        else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }




}