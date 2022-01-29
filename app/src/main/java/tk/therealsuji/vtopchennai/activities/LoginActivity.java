package tk.therealsuji.vtopchennai.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import tk.therealsuji.vtopchennai.BuildConfig;
import tk.therealsuji.vtopchennai.R;
import tk.therealsuji.vtopchennai.helpers.SettingsRepository;
import tk.therealsuji.vtopchennai.helpers.VTOPHelper;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences encryptedSharedPreferences, sharedPreferences;
    VTOPHelper vtopHelper;

    public void signIn() {
        hideKeyboard(this.getCurrentFocus());

        EditText usernameView = findViewById(R.id.edit_text_username);
        EditText passwordView = findViewById(R.id.edit_text_password);

        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        encryptedSharedPreferences.edit().putString("username", username).apply();
        encryptedSharedPreferences.edit().putString("password", password).apply();

        this.vtopHelper.bind();
        this.vtopHelper.start();
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ConstraintLayout loginLayout = findViewById(R.id.constraint_layout_login);
        loginLayout.setOnApplyWindowInsetsListener((view, windowInsets) -> {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            layoutParams.setMargins(
                    windowInsets.getSystemWindowInsetLeft(),
                    windowInsets.getSystemWindowInsetTop(),
                    windowInsets.getSystemWindowInsetRight(),
                    windowInsets.getSystemWindowInsetBottom()
            );
            view.setLayoutParams(layoutParams);

            return windowInsets.consumeSystemWindowInsets();
        });

        this.encryptedSharedPreferences = SettingsRepository.getEncryptedSharedPreferences(getApplicationContext());
        this.sharedPreferences = SettingsRepository.getSharedPreferences(getApplicationContext());

        findViewById(R.id.button_sign_in).setOnClickListener(view -> signIn());
        findViewById(R.id.button_privacy).setOnClickListener(view -> SettingsRepository.openWebViewActivity(
                this,
                getString(R.string.privacy),
                SettingsRepository.APP_PRIVACY_URL
        ));

        /*
            Locally check for a new version (The actually checking is done in the LauncherActivity)
         */
        int versionCode = BuildConfig.VERSION_CODE;
        int latestVersion = this.sharedPreferences.getInt("latest", versionCode);

        if (versionCode < latestVersion) {
            new MaterialAlertDialogBuilder(this)
                    .setMessage(R.string.update_message)
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton(R.string.update, (dialogInterface, i) -> SettingsRepository.openDownloadPage(this))
                    .setTitle(R.string.update_title)
                    .show();
        }

        this.vtopHelper = new VTOPHelper(this, new VTOPHelper.Initiator() {
            @Override
            public void onLoading(boolean isLoading) {
                if (isLoading) {
                    findViewById(R.id.text_view_sign_in).setVisibility(View.INVISIBLE);
                    findViewById(R.id.progress_bar_loading).setVisibility(View.VISIBLE);

                    findViewById(R.id.button_sign_in).setEnabled(false);
                    findViewById(R.id.edit_text_username).setEnabled(false);
                    findViewById(R.id.edit_text_password).setEnabled(false);
                } else {
                    findViewById(R.id.text_view_sign_in).setVisibility(View.VISIBLE);
                    findViewById(R.id.progress_bar_loading).setVisibility(View.INVISIBLE);

                    findViewById(R.id.button_sign_in).setEnabled(true);
                    findViewById(R.id.edit_text_username).setEnabled(true);
                    findViewById(R.id.edit_text_password).setEnabled(true);
                }
            }

            @Override
            public void onComplete() {
                startMainActivity();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.vtopHelper.bind();

        if (!this.vtopHelper.isBound() && this.sharedPreferences.getBoolean("isSignedIn", false)) {
            this.startMainActivity();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.vtopHelper.unbind();
    }
}