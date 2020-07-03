package info.atiar.prnappdialer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import bp.BP;
import model.NumberModel;

public class WebviewActivity extends AppCompatActivity {
    final String tag = getClass().getSimpleName() + "Atiar - ";
    @BindView(R.id.addNumberEd)         EditText _addNumberEd;
    @BindView(R.id.addNumberButton)     ImageButton _addNumberButton;
    @BindView(R.id.webview)             WebView _webview;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private List<NumberModel> numberList = new ArrayList<>();


    String linkToOpen = "https://www.callingreport.net/";
    String MyUA = "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 5 Build/LMY48B; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/43.0.2357.65 Mobile Safari/537.36";
    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;
    Context mContext;
    String userId, websiteID, uniqueKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        mContext = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linkToOpen = getIntent().getStringExtra("website");
        websiteID = getIntent().getStringExtra("websiteID");

        Log.e(tag,linkToOpen);
        renderWebPage(linkToOpen);

        //Firebase stuff
        auth = FirebaseAuth.getInstance();
        userId = auth.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("allnumbers").child(userId).child(websiteID);
        numbersFromDB();
    }

    public void addNumber(View view) {
        if (numberList.size()+1>BP.getNumberLimit()){
            showDialog("Limit issue","You rechead the maximum  limit. You can't add more then "+BP.getNumberLimit()+" Numbers");
        }else {
            mDatabase = FirebaseDatabase.getInstance().getReference("allnumbers").child(userId).child(websiteID);
            if (!((_addNumberEd.getText() == null) || _addNumberEd.getText().toString().trim().equals(""))) {
                String number = "";
                Character charAt = _addNumberEd.getText().toString().trim().charAt(0);
                if (charAt.toString().equals("+")) {
                    number = _addNumberEd.getText().toString().trim();
                } else {
                    number = "+" + _addNumberEd.getText().toString().trim();
                }
                NumberModel numberModel = new NumberModel(websiteID, number, "0", userId, BP.getCurrentDateTime());
                mDatabase.child(number).setValue(numberModel);
                _addNumberEd.setText("");
            }
        }
    }

    // Custom method to render a web page
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void renderWebPage(String urlToRender){
        //WEBVIEW

        _webview.getSettings().setJavaScriptEnabled(true); // enable javascript
        _webview.getSettings().setUserAgentString(MyUA);
        _webview.getSettings().setDomStorageEnabled(true);
        _webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        _webview.getSettings().setLoadsImagesAutomatically(true);
        _webview.getSettings().setAppCacheEnabled(true);
        _webview.getSettings().setSupportZoom(true);
        _webview.getSettings().setBuiltInZoomControls(true);
        _webview.getSettings().setAppCachePath(getApplication().getCacheDir().toString());
        _webview.setWebViewClient(new WebViewClient(){


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url){
                // Do something when page loading finished
                _webview.setVisibility(View.VISIBLE);
            }

        });

        _webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        _webview.loadUrl(urlToRender);
        //mWebview.reload();
    }

    @Override
    public void onBackPressed() {
        if(_webview.canGoBack()) {
            _webview.goBack();
        } else {
            startActivity(new Intent(WebviewActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_enabled, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_home:
                startActivity(new Intent(WebviewActivity.this,MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void numbersFromDB() {
        mDatabase = FirebaseDatabase.getInstance().getReference("allnumbers").child(userId).child(websiteID);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    NumberModel numberModel = dataSnapshot1.getValue(NumberModel.class);
                    numberList.add(numberModel);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(tag, "Failed to read value.", error.toException());
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

}
