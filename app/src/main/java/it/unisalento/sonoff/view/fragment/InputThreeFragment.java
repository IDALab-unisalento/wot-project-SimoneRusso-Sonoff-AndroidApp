package it.unisalento.sonoff.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import it.unisalento.sonoff.R;
import it.unisalento.sonoff.model.User;
import it.unisalento.sonoff.restService.RestService;

public class InputThreeFragment extends Fragment {
    private static final String INPUT_THREE = "1";
    private TextView tvAccess;
    private Button buttonAccess;
    private ToggleButton toggleButton;
    private RestService restService;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_input_three, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvAccess = view.findViewById(R.id.tvAccessInput3);
        buttonAccess = view.findViewById(R.id.btnAccessInput3);
        toggleButton = view.findViewById(R.id.toggleBtnInput3);
        user = (User) getArguments().getSerializable("user");

        restService = new RestService(this.getContext());
        restService.getStatus(toggleButton, user);

        toggleButton.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isPressed()){
                tvAccess.setText("");
                if(compoundButton.isChecked())
                    restService.changeStatusON(compoundButton, user);
                else
                    restService.changeStatusOFF(compoundButton, user);

            }
        });

        buttonAccess.setOnClickListener(view1 -> restService.getStatus(tvAccess, user));

        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(mMessageReceiver, new IntentFilter(INPUT_THREE));

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("status");
            Log.d("receiver", "Got message: " + status);
            toggleButton.setChecked(status.equals("ON"));
        }

    };
}