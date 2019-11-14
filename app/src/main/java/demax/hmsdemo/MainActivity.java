package demax.hmsdemo;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.auth.api.signin.HuaweiIdSignIn;
import com.huawei.hms.auth.api.signin.HuaweiIdSignInClient;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button btnSignin, btnAuthSignin, btnSignout, btnSwitch;
    private TextView txtLog;
    private HuaweiIdSignInClient mSignInClient;
    private HuaweiIdSignInOptions mSignInOptions;
    private String logStr = "";

    public static final int REQUEST_SIGN_IN_LOGIN = 1002;
    public static final int REQUEST_SIGN_IN_LOGIN_CODE = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Huawei Account");

        btnSignin = (Button)findViewById(R.id.btn_signin);
        btnAuthSignin = (Button)findViewById(R.id.btn_auth_signin);
        btnSignout = (Button)findViewById(R.id.btn_signout);
        btnSwitch = (Button)findViewById(R.id.btn_switch);
        txtLog = (TextView)findViewById(R.id.txt_log);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        btnAuthSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInCode();
            }
        });
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LocationActivity.class);
                startActivity(intent);
            }
        });

        mSignInOptions = new HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("").build();
        mSignInClient = HuaweiIdSignIn.getClient(MainActivity.this, mSignInOptions);
    }

    private void signIn() {
        logMsg("start signin");
        startActivityForResult(mSignInClient.getSignInIntent(), REQUEST_SIGN_IN_LOGIN);
    }

    private void signInCode() {
        mSignInOptions = new HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN).requestServerAuthCode().build();
        mSignInClient = HuaweiIdSignIn.getClient(MainActivity.this, mSignInOptions);
        startActivityForResult(mSignInClient.getSignInIntent(), REQUEST_SIGN_IN_LOGIN_CODE);
    }

    private void logMsg(String msg) {
        logStr += msg;
        txtLog.setText(logStr);
        logStr += "\n";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGN_IN_LOGIN) {
            //login success
            //get user message by getSignedInAccountFromIntent
            Task<SignInHuaweiId> signInHuaweiIdTask = HuaweiIdSignIn.getSignedInAccountFromIntent(data);
            if (signInHuaweiIdTask.isSuccessful()) {
                SignInHuaweiId huaweiAccount = signInHuaweiIdTask.getResult();
                logMsg(huaweiAccount.getDisplayName() + " signIn success ");
                logMsg("AccessToken: " + huaweiAccount.getAccessToken());
                logMsg("Display Photo url: " + huaweiAccount.getPhotoUriString());
            } else {
                logMsg("signIn failed: " + (signInHuaweiIdTask.getException()).toString());
            }
        }
        if (requestCode == REQUEST_SIGN_IN_LOGIN_CODE) {
            //login success
            Task<SignInHuaweiId> signInHuaweiIdTask = HuaweiIdSignIn.getSignedInAccountFromIntent(data);
            if (signInHuaweiIdTask.isSuccessful()) {
                SignInHuaweiId huaweiAccount = signInHuaweiIdTask.getResult();
                logMsg("signIn get code success.");
                logMsg("ServerAuthCode: " + huaweiAccount.getServerAuthCode());
                logMsg("Display Photo url: " + huaweiAccount.getPhotoUriString());
            } else {
                logMsg("signIn get code failed: " + (signInHuaweiIdTask.getException()).toString());
            }
        }
    }

}
