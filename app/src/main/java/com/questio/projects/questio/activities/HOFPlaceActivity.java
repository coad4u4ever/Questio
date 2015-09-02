package com.questio.projects.questio.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.questio.projects.questio.R;
import com.questio.projects.questio.adepters.RewardsAdapter;
import com.questio.projects.questio.models.RewardHOF;
import com.questio.projects.questio.utilities.QuestioAPIService;
import com.questio.projects.questio.utilities.QuestioConstants;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ning jittima on 1/9/2558.
 */
public class HOFPlaceActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private final String LOG_TAG = HOFPlaceActivity.class.getSimpleName();
    Context mContext;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    long adventurerId;
    RestAdapter adapter;
    QuestioAPIService api;
    ArrayList<RewardHOF> rewards;
    RewardsAdapter rewardsAdapter;

    @Bind(R.id.hof_toolbar)
    Toolbar toolbar;

    @Bind(R.id.hof_reward_place)
    GridView hallOfFame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hof_place_activity_layout);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mContext = this;
        prefs = this.getSharedPreferences(QuestioConstants.ADVENTURER_PROFILE, Context.MODE_PRIVATE);
        editor = this.getSharedPreferences(QuestioConstants.ADVENTURER_PROFILE, Context.MODE_PRIVATE).edit();
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras == null) {
                adventurerId = 0;
            } else {
                adventurerId = extras.getLong(QuestioConstants.ADVENTURER_ID);
            }
        } else {
            adventurerId = (long) savedInstanceState.getSerializable(QuestioConstants.ADVENTURER_ID);
        }
        adventurerId = prefs.getLong(QuestioConstants.ADVENTURER_ID, 0);
        adapter = new RestAdapter.Builder()
                .setEndpoint(QuestioConstants.ENDPOINT)
                .build();
        api = adapter.create(QuestioAPIService.class);
        requestPlaceRewardsHOFData(adventurerId);
        hallOfFame.setOnItemClickListener(this);
    }

    private void requestPlaceRewardsHOFData(long id){
        api.getAllPlaceRewardsInHalloffameByAdventurerId(id, new Callback<ArrayList<RewardHOF>>() {
            @Override
            public void success(ArrayList<RewardHOF> rewardHOFs, Response response) {
                if(rewardHOFs != null){
                    rewards = rewardHOFs;
                    rewardsAdapter = new RewardsAdapter(mContext, rewardHOFs);
                    hallOfFame.setAdapter(rewardsAdapter);
                    rewardsAdapter.notifyDataSetChanged();
                }else{
                    Log.d(LOG_TAG, "place reward hof = null");
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        RewardHOF reward = rewards.get(i);
        final NiftyDialogBuilder dialog = NiftyDialogBuilder.getInstance(mContext);
        dialog
                .withTitle("Reward Description")
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessageColor("#FFFFFFFF")
                .withDialogColor("#FFE74C3C")
                .withDuration(300)
                .withEffect(Effectstype.Slidetop)
                .withButton1Text("Close")
                .isCancelableOnTouchOutside(false)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                })
                .setCustomView(R.layout.reward_description_dialog, mContext);
        TextView tvRewardName = ButterKnife.findById(dialog, R.id.dialog_reward_name);
        TextView tvRewardDesc = ButterKnife.findById(dialog, R.id.dialog_reward_desc);
        TextView tvRewardDate = ButterKnife.findById(dialog, R.id.dialog_reward_datereceived);
        ImageView rewardImage = ButterKnife.findById(dialog, R.id.dialog_reward_picture);

        String rewardName = reward.getRewardName();
        String rewardDesc = reward.getDescription();
        String rewardDate = reward.getDateReceived();

        Glide.with(mContext)
                .load(QuestioConstants.BASE_URL + reward.getRewardPic())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(rewardImage);
        tvRewardName.setText(rewardName);
        tvRewardDesc.setText(rewardDesc);
        tvRewardDate.setText(rewardDate);

        dialog.show();
    }
}