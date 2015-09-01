package com.questio.projects.questio.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.questio.projects.questio.R;
import com.questio.projects.questio.adepters.InventoryAdapter;
import com.questio.projects.questio.models.Avatar;
import com.questio.projects.questio.models.Item;
import com.questio.projects.questio.models.ItemInInventory;
import com.questio.projects.questio.utilities.QuestioAPIService;
import com.questio.projects.questio.utilities.QuestioConstants;
import com.questio.projects.questio.utilities.QuestioHelper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class AvatarActivity extends AppCompatActivity {
    private static final String LOG_TAG = AvatarActivity.class.getSimpleName();
    QuestioAPIService api;
    RestAdapter adapter;
    long adventurerId;
    Context mContext;
    InventoryAdapter inventoryAdapter = null;
    ArrayList<ItemInInventory> itemsEquip;
    GridView equipLayout;

    @Bind(R.id.avatar_toolbar)
    Toolbar toolbar;

    @Bind(R.id.avatar_head)
    ImageView avatarHead;

    @Bind(R.id.avatar_background)
    ImageView avatarBackground;

    @Bind(R.id.avatar_neck)
    ImageView avatarNeck;

    @Bind(R.id.avatar_top)
    ImageView avatarBody;

    @Bind(R.id.avatar_handleft)
    ImageView avatarHandLeft;

    @Bind(R.id.avatar_handright)
    ImageView avatarHandRight;

    @Bind(R.id.avatar_arms)
    ImageView avatarArms;

    @Bind(R.id.avatar_bottom)
    ImageView avatarLegs;

    @Bind(R.id.avatar_feet)
    ImageView avatarFoot;

    @Bind(R.id.avatar_aura)
    ImageView avatarSpecial;

    @Bind(R.id.button_head)
    ImageButton buttonHead;

    @Bind(R.id.button_background)
    ImageButton buttonBackground;

    @Bind(R.id.button_neck)
    ImageButton buttonNeck;

    @Bind(R.id.button_top)
    ImageButton buttonBody;

    @Bind(R.id.button_handleft)
    ImageButton buttonHandLeft;

    @Bind(R.id.button_handright)
    ImageButton buttonHandRight;

    @Bind(R.id.button_arms)
    ImageButton buttonArms;

    @Bind(R.id.button_bottom)
    ImageButton buttonBottom;

    @Bind(R.id.button_feet)
    ImageButton buttonFoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new RestAdapter.Builder().setEndpoint(QuestioConstants.ENDPOINT).build();
        api = adapter.create(QuestioAPIService.class);
        setContentView(R.layout.activity_avatar);
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
        handleInstanceState(savedInstanceState);

        api.getAvatarCountByAvatarId(adventurerId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                String avatarCountStr = QuestioHelper.getJSONStringValueByTag("avatarcount", response);
                int avatarCount = Integer.parseInt(avatarCountStr);
                boolean hasAvatar = (avatarCount == 1);
                if (hasAvatar) {
                    populateAvatar();
                } else {
                    insertNewAvatar();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        setButtonClick();

    }

    private void handleInstanceState(Bundle savedInstanceState) {
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
        Log.d(LOG_TAG, QuestioConstants.ADVENTURER_ID + ": " + adventurerId);
        SharedPreferences prefs = getSharedPreferences(QuestioConstants.ADVENTURER_PROFILE, MODE_PRIVATE);
        adventurerId = prefs.getLong(QuestioConstants.ADVENTURER_ID, 0);
        mContext = this;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Your Avatar");
        }
    }

    private void insertNewAvatar() {
        api.insertNewAvatar(adventurerId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.d(LOG_TAG, "insert new avatar successfully");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(LOG_TAG, "insert new avatar failure");
            }
        });
    }

    private void populateAvatar() {
        api.getAvatarByAvatarId(adventurerId, new Callback<Avatar[]>() {
            @Override
            public void success(Avatar[] avatars, Response response) {
                Log.d(LOG_TAG, "get avatar successfully");
                Log.d(LOG_TAG, avatars[0].toString());
                setImageSpike(
                        avatars[0].getHeadId(),
                        avatars[0].getBackgroundId(),
                        avatars[0].getNeckId(),
                        avatars[0].getBodyId(),
                        avatars[0].getHandleftId(),
                        avatars[0].getHandrightId(),
                        avatars[0].getArmId(),
                        avatars[0].getLegId(),
                        avatars[0].getFootId(),
                        avatars[0].getSpecialId()
                );
                Log.d(LOG_TAG, "end of populateAvatar");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(LOG_TAG, "get avatar failure");
            }
        });
    }

    private void setImageSpike(
            long headId,
            long backgroundId,
            long neckId,
            long bodyId,
            long handleftId,
            long handrightId,
            long armId,
            long legId,
            long footId,
            long specialId
    ) {
        Log.d(LOG_TAG, "setImageSpike called");
        api.getItemsBySetOfItemId(
                headId,
                backgroundId,
                neckId,
                bodyId,
                handleftId,
                handrightId,
                armId,
                legId,
                footId,
                specialId,
                new Callback<Item[]>() {
                    @Override
                    public void success(Item[] items, Response response) {
                        Log.d(LOG_TAG, "setImageSpike load successfully");
                        if (items != null) {
                            for (Item item : items) {
                                Log.d(LOG_TAG, item.toString());
                                switch (item.getPositionId()) {
                                    case QuestioConstants.POSITION_HEAD:
                                        Glide.with(AvatarActivity.this)
                                                .load(QuestioConstants.BASE_URL + item.getEquipSpritePath())
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(avatarHead);
                                        break;
                                    case QuestioConstants.POSITION_BACKGROUND:
                                        Glide.with(AvatarActivity.this)
                                                .load(QuestioConstants.BASE_URL + item.getEquipSpritePath())
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(avatarBackground);
                                        break;
                                    case QuestioConstants.POSITION_NECK:
                                        Glide.with(AvatarActivity.this)
                                                .load(QuestioConstants.BASE_URL + item.getEquipSpritePath())
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(avatarNeck);
                                        break;
                                    case QuestioConstants.POSITION_BODY:
                                        Glide.with(AvatarActivity.this)
                                                .load(QuestioConstants.BASE_URL + item.getEquipSpritePath())
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(avatarBody);
                                        break;
                                    case QuestioConstants.POSITION_HANDLEFT:
                                        Glide.with(AvatarActivity.this)
                                                .load(QuestioConstants.BASE_URL + item.getEquipSpritePath())
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(avatarHandLeft);
                                        break;
                                    case QuestioConstants.POSITION_HANDRIGHT:
                                        Glide.with(AvatarActivity.this)
                                                .load(QuestioConstants.BASE_URL + item.getEquipSpritePath())
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(avatarHandRight);
                                        break;
                                    case QuestioConstants.POSITION_ARMS:
                                        Glide.with(AvatarActivity.this)
                                                .load(QuestioConstants.BASE_URL + item.getEquipSpritePath())
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(avatarArms);
                                        break;
                                    case QuestioConstants.POSITION_LEGS:
                                        Glide.with(AvatarActivity.this)
                                                .load(QuestioConstants.BASE_URL + item.getEquipSpritePath())
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(avatarLegs);
                                        break;
                                    case QuestioConstants.POSITION_FOOT:
                                        Glide.with(AvatarActivity.this)
                                                .load(QuestioConstants.BASE_URL + item.getEquipSpritePath())
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(avatarFoot);
                                        break;
                                    case QuestioConstants.POSITION_AURA:
                                        Glide.with(AvatarActivity.this)
                                                .load(QuestioConstants.BASE_URL + item.getEquipSpritePath())
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(avatarSpecial);
                                        break;
                                }
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(LOG_TAG, "setImageSpike load failed");
                        Log.d(LOG_TAG, error.getUrl());
                    }
                }

        );
    }

    public void showEquipDialog(int position){
        final NiftyDialogBuilder dialog = NiftyDialogBuilder.getInstance(this);
        dialog
                .withTitle("Equip")
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessageColor("#FFFFFFFF")
                .withDialogColor("#FFE74C3C")
                .withDuration(300)
                .withEffect(Effectstype.Slidetop)
                .withButton1Text("Close")
                .isCancelableOnTouchOutside(true)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                })
                .setCustomView(R.layout.inventory_equip_layout, this);
        equipLayout = ButterKnife.findById(dialog, R.id.item_inventory_equip);
        api.getAllItemInInventoryByAdventurerIdAndPositionId(adventurerId, position, new Callback<ArrayList<ItemInInventory>>() {

                    @Override
                    public void success(ArrayList<ItemInInventory> itemInInventories, Response response) {
                        if (itemInInventories != null) {
                            if (!itemInInventories.isEmpty()) {
                                itemsEquip = itemInInventories;
                                inventoryAdapter = new InventoryAdapter(mContext, itemsEquip);
                                equipLayout.setAdapter(inventoryAdapter);
                                equipLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    }
                                });
                                inventoryAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

            dialog.show();
    }

    public void setButtonClick(){
        buttonHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEquipDialog(QuestioConstants.POSITION_HEAD);
            }
        });
        buttonBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEquipDialog(QuestioConstants.POSITION_BACKGROUND);
            }
        });
        buttonNeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEquipDialog(QuestioConstants.POSITION_NECK);
            }
        });
        buttonBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEquipDialog(QuestioConstants.POSITION_BODY);
            }
        });
        buttonHandLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEquipDialog(QuestioConstants.POSITION_HANDLEFT);
            }
        });
        buttonHandRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEquipDialog(QuestioConstants.POSITION_HANDRIGHT);
            }
        });
        buttonArms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEquipDialog(QuestioConstants.POSITION_ARMS);
            }
        });
        buttonBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEquipDialog(QuestioConstants.POSITION_LEGS);
            }
        });
        buttonFoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEquipDialog(QuestioConstants.POSITION_FOOT);
            }
        });
    }
}