package com.thetehnocafe.gurleensethi.workmanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView tv_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_status = findViewById(R.id.textView);

        scheduleOneTime(oneTimeWorkRequest());
        schedulePeriodicWork(new PeriodicWorkRequest.Builder(SampleWorker.class, 12, TimeUnit.HOURS)
                .addTag("periodic_work")
                .build());


    }

    private void schedulePeriodicWork(WorkRequest periodicWorkRequest) {
        findViewById(R.id.periodicWorkButton).setOnClickListener(view -> WorkManager.getInstance(this).enqueue(periodicWorkRequest));
        findViewById(R.id.cancelPeriodicWorkButton).setOnClickListener(view -> WorkManager.getInstance(this).cancelWorkById(periodicWorkRequest.getId()));
    }

    private void scheduleOneTime(OneTimeWorkRequest simpleRequest) {
        final UUID workId = simpleRequest.getId();
        LiveData<WorkInfo> workInfoByIdLiveData = WorkManager.getInstance(this).getWorkInfoByIdLiveData(simpleRequest.getId());
        workInfoByIdLiveData.observe(this, this::updateWorkInfo);
        findViewById(R.id.cancelWorkButton).setOnClickListener(view -> WorkManager.getInstance(this).cancelWorkById(workId));
        findViewById(R.id.simpleWorkButton).setOnClickListener(view -> WorkManager.getInstance(this).enqueue(simpleRequest));
    }

    private OneTimeWorkRequest oneTimeWorkRequest() {
        return new OneTimeWorkRequest.Builder(SampleWorker.class)
                .setInputData(new Data.Builder()
                        .putString(SampleWorker.EXTRA_TITLE, "Message from Activity!")
                        .putString(SampleWorker.EXTRA_TEXT, "Hi! I have come from activity.")
                        .build())
                .setConstraints(new Constraints.Builder()
                        .setRequiresCharging(true)
                        .build())
                .addTag("simple_work")
                .build();
    }

    private void updateWorkInfo(WorkInfo workStatus) {
        if (workStatus != null) {
            tv_status.append("SimpleWorkRequest: " + workStatus.getState().name() + "\n");
        }
        if (workStatus != null && workStatus.getState().isFinished()) {
            String message = workStatus.getOutputData().getString(SampleWorker.EXTRA_OUTPUT_MESSAGE);
            tv_status.append("SimpleWorkRequest (Data): " + message);
        }
    }
}