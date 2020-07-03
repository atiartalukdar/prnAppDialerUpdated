package info.atiar.prnappdialer;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroCustomLayoutFragment;

public class AppIntroActivity extends AppIntro {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.appintro_1));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.appintro_2));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.appintro_3));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.appintro_4));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.appintro_5));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.appintro_6));
    }

    @Override
    protected void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(AppIntroActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(AppIntroActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
