package com.evervc.saznexpressstaff;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            Log.d("TOKEN_FCM", token);//cdUGEwaYTi-3ChdeDtagBV:APA91bHCMGfM7na-ovVffzok4SvwPUiXd22nqjdsQtc-8VfNZmar0tzelbHVrme7osQBhycvjIMLmFZKgI8JpUSk0lz556-E14GLaVcUuo5L_LZp-6m9W-I
        });

    }
}