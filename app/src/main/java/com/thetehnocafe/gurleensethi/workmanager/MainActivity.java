package com.thetehnocafe.gurleensethi.workmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.textView);

        Data data = new Data.Builder()
                .putString(MyWorker.EXTRA_TITLE, "Message from Activity!")
                .putString(MyWorker.EXTRA_TEXT, "Hi! I have come from activity.")
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();

        final OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                .setInputData(data)
                .setConstraints(constraints)
                .addTag("simple_work")
                .build();

        final PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(MyWorker.class, 12, TimeUnit.HOURS)
                .addTag("periodic_work")
                .build();

        final UUID workId = simpleRequest.getId();

        findViewById(R.id.simpleWorkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkManager.getInstance().enqueue(simpleRequest);
            }
        });

        findViewById(R.id.cancelWorkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //WorkManager.getInstance().cancelAllWorkByTag("simple_work");
                WorkManager.getInstance().cancelWorkById(workId);
            }
        });

        findViewById(R.id.periodicWorkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkManager.getInstance().enqueue(periodicWorkRequest);
            }
        });

        findViewById(R.id.cancelPeriodicWorkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkManager.getInstance().cancelWorkById(periodicWorkRequest.getId());
            }
        });

        WorkManager instance = WorkManager.getInstance();
        LiveData<WorkInfo> workInfoByIdLiveData = instance.getWorkInfoByIdLiveData(simpleRequest.getId());
        workInfoByIdLiveData.observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(@Nullable WorkInfo workStatus) {
                if (workStatus != null) {
                    mTextView.append("SimpleWorkRequest: " + workStatus.getState().name() + "\n");
                }

                if (workStatus != null && workStatus.getState().isFinished()) {
                    String message = workStatus.getOutputData().getString(MyWorker.EXTRA_OUTPUT_MESSAGE);
                    mTextView.append("SimpleWorkRequest (Data): " + message);
                }
            }
        });


    }
}