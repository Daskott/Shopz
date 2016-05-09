package com.pear.shopz.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;

import com.pear.shopz.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CardView shareButtonCard = (CardView)findViewById(R.id.share_button_card);
        assert shareButtonCard != null;

        shareButtonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Shopizy is an app that enhances your\n" +
                                                        "grocery shopping experience. Get it free for Android."+
                                                        "\nhttps://play.google.com/store/apps" +
                                                        "/details?id=com.pear.shopz");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share Via"));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
