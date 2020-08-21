package info.atiar.prnappdialer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroCustomLayoutFragment;
import com.github.appintro.AppIntroFragment;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapter.NumbersAdapter;
import adapter.WebsitesAdapter;
import bp.BP;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.LimitModdel;
import model.NumberModel;
import model.WebsitesModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
public class MainActivity extends AppIntro {
    final String tag = getClass().getSimpleName() + "Atiar - ";

    @BindView(R.id.websiteLists)    ListView _websiteLists;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseLimit;
    private FirebaseAuth auth;
    String userId,uniqueKey;

    WebsitesAdapter websitesAdapter;
    private List<WebsitesModel> websiteLists = new ArrayList<>();
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        addMobRequest();
        interestitialAdRequest();

        //Firebase stuff
        auth = FirebaseAuth.getInstance();
        userId = auth.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("websites").child(userId);
        mDatabaseLimit = FirebaseDatabase.getInstance().getReference("limit");

        websitesAdapter = new WebsitesAdapter(this, websiteLists);
        _websiteLists.setAdapter(websitesAdapter);
        websitesAdapter.notifyDataSetChanged();

        websiteDataFromDB();

        readLimit();

        showInterestitialAd();

    }

    public void visitWebsite(View view) {
        Intent intent = new Intent(MainActivity.this,WebviewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    public void allNumbersButton(View view) {
        startActivity(new Intent(MainActivity.this,NumberDialActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                showInterestitialAd();
                break;
            case R.id.menu_add_website:
                showInterestitialAd();
                if (websiteLists.size()+1>BP.getWebsiteLimit()){
                    showDialog("Limit issue","You rechead the maximum  limit. You can't add more then "+BP.getWebsiteLimit()+" websites");
                }else {
                    popUpEditText();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        finish();
    }

    private void popUpEditText() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Comments");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        input.setText("https://www.");

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (input.getText().equals("https://www.")||input.getText().equals("")){
                    Log.e(tag, "No Input added.");
                }else {
                    uniqueKey = mDatabase.push().getKey();
                    //websiteLists.add(new WebsitesModel(input.getText().toString(),4+"",uniqueKey, BP.getCurrentDateTime()));
                    WebsitesModel websitesModel = new WebsitesModel(input.getText().toString(),uniqueKey,userId, BP.getCurrentDateTime());
                    mDatabase.child(uniqueKey).setValue(websitesModel);
                }


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void websiteDataFromDB(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                websiteLists.clear();

                for (DataSnapshot websiteData : dataSnapshot.getChildren()){
                    WebsitesModel websitesModel = websiteData.getValue(WebsitesModel.class);
                    websiteLists.add(websitesModel);
                }
                websitesAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(tag, "Failed to read value.", error.toException());
            }
        });
    }

    private void readLimit(){
        mDatabaseLimit.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LimitModdel limitModdel = dataSnapshot.getValue(LimitModdel.class);
                BP.setNumberLimit(limitModdel.getNumber());
                BP.setWebsiteLimit(limitModdel.getWebsite());
                Log.e(tag, limitModdel.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(tag, "Failed to read value.", error.toException());
            }
        });
    }

    public void showDialog(String title, String message) {
        AlertDialog alert;
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    public void removeItem(int position) {
        mDatabase = FirebaseDatabase.getInstance().getReference("websites").child(userId);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are you sure to Delete this website?")
                .setMessage(websiteLists.get(position).getWebsite())
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        Query applesQuery = mDatabase.orderByChild("website").equalTo(websiteLists.get(position).getWebsite());
                        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                    if (appleSnapshot != null) {
                                        appleSnapshot.getRef().removeValue();
                                    }
                                }
                                websitesAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(tag, "onCancelled", databaseError.toException());
                            }
                        });

                        _websiteLists.invalidateViews();


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what should happen when negative button is clicked
                        Toast.makeText(getApplicationContext(), "Nothing Happened", Toast.LENGTH_LONG).show();
                    }
                })
                .show();

    }

    public void addMobRequest(){
        mAdView = (AdView) findViewById(R.id.adView);
        //mAdView.setAdSize(AdSize.BANNER);
        //mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id));

        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);
    }
    private InterstitialAd mInterstitialAd;
    public void interestitialAdRequest(){
        MobileAds.initialize(this,getResources().getString(R.string.admob_ad));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }
    public void showInterestitialAd(){

        if (BP.showInterestitialAd()){
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                AdRequest newadRequest = new AdRequest.Builder().build();
                mInterstitialAd.loadAd(newadRequest);
                Log.e("NumberDialA Atiar =  ", "The interstitial wasn't loaded yet.");
            }
        }else {
            Log.e("NumberDialA Atiar =  ","click counter is not fullfill.");
        }

    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
