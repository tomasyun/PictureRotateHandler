package com.yunt.picturerotatehandler;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks {
    private Button button;

    private static final String[] WRITE_STORAGE_PERMISSION =
            {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int RC_WRITE_STORAGE_PERMISSION = 100;
    private List<LocalMedia> medias = null;
    private String postFileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PictureSelector.create(MainActivity.this)
                        .openGallery(PictureMimeType.ofImage())
                        .theme(R.style.picture_default_style)
                        .maxSelectNum(1)
                        .minSelectNum(1)
                        .isCamera(true)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
            }
        });
    }

    @AfterPermissionGranted(RC_WRITE_STORAGE_PERMISSION)
    public void locationWritePhoneStateTask() {
        if (hasWriteStoragePermissions()) {

            // Have permissions, do the thing!
//            Toast.makeText(this, "TODO: Location and Contacts things", Toast.LENGTH_LONG).show();
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_location_contacts),
                    RC_WRITE_STORAGE_PERMISSION,
                    WRITE_STORAGE_PERMISSION);
        }
    }

    private boolean hasWriteStoragePermissions() {
        return EasyPermissions.hasPermissions(this, WRITE_STORAGE_PERMISSION);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.i("onPermissionsGranted:", requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.i("onPermissionsDenied:", requestCode + ":" + perms.size());
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).setTitle("获取相关权限失败").setRationale("导致部分功能无法正常使用，需要到设置页面手动授权").build().show();
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
        Log.i("onRationaleAccepted", requestCode + "");
    }

    @Override
    public void onRationaleDenied(int requestCode) {
        Log.i("onRationaleDenied", requestCode + "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
//            String yes = getString(R.string.yes);
//            String no = getString(R.string.no);
//            // Do something after user returned from app settings screen, like showing a Toast.
//            Toast.makeText(
//                    this,
//                    getString(R.string.returned_from_app_settings_to_activity,
//                            hasLocationWritePhoneStatePermissions() ? yes : no),
//                    Toast.LENGTH_LONG)
//                    .show();
        } else if (requestCode == PictureConfig.CHOOSE_REQUEST) {
            medias = PictureSelector.obtainMultipleResult(data);
            postFileName = medias.get(0).getPath();
            String path = BitmapUtils.compressImage(MainActivity.this, postFileName);
            Log.i("MainActivity", path);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
