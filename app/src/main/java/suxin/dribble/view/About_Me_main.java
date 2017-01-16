package suxin.dribble.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import suxin.dribble.R;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by suxin on 9/30/16.
 */

public class About_Me_main extends Fragment {

    @BindView(R.id.venmo) TextView venmo;
    @BindView(R.id.wechat) TextView wechat;
    @BindView(R.id.alipay) TextView alipay;

    public static About_Me_main newInstance(int intType) {
        Bundle args = new Bundle();
        About_Me_main about_me_main = new About_Me_main();
        about_me_main.setArguments(args);
        return about_me_main;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aboutme, container, false);
        ButterKnife.bind(this, view);

        venmo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(getString(R.string.aboutme_venmo), venmo.getText().toString());
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(getContext(), getString(R.string.venmo_copy), Toast.LENGTH_SHORT).show();
            }
        });

        wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(getString(R.string.aboutme_wechat), wechat.getText().toString());
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(getContext(), getString(R.string.wechat_copy), Toast.LENGTH_SHORT).show();
            }
        });

        alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(getString(R.string.aboutme_Alipay), alipay.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getContext(), getString(R.string.alipay_copy), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
