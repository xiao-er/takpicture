package com.xiaoxiao.takepicture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView takePicture, photoAlbum;
    private SimpleDraweeView headImg;
    private String pictureStr;
    private File pictureFile;
    private Uri pictureUri;
    private int SELECT = 0;//1：相机拍照后剪裁,0：相册选择照片后剪裁

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }


    private void initView() {
        photoAlbum = findViewById(R.id.photoAlbum);
        takePicture = findViewById(R.id.takePicture);
        headImg = findViewById(R.id.headImg);
        takePicture.setOnClickListener(this);
        photoAlbum.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photoAlbum:
                SELECT = 0;
                if (Build.VERSION.SDK_INT >= 23) {
                    if (PermissionUtils.isPowerRequest(MainActivity.this, PermissionUtils.takePhoto, 101)) {
                        openAblum();
                    } else {
                        PermissionUtils.isPowerRequest(MainActivity.this, PermissionUtils.takePhoto, 101);
                    }
                }

                break;
            case R.id.takePicture:
                SELECT = 1;
                if (Build.VERSION.SDK_INT >= 23) {
                    if (PermissionUtils.isPowerRequest(MainActivity.this, PermissionUtils.takePhoto, 101)) {
                        openCamera();
                    } else {
                        PermissionUtils.isPowerRequest(MainActivity.this, PermissionUtils.takePhoto, 101);
                    }
                }
                break;
        }
    }


    /**
     * 拍照
     */
    private void openCamera() {
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        pictureStr = FileUtil.getPhotopath();
        pictureFile = new File(pictureStr);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //  大于等于24即为7.0及以上执行内容
            pictureUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".fileprovider", pictureFile);
        } else {
            //  低于24即为7.0以下执行内容
            pictureUri = Uri.fromFile(pictureFile);
        }
        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        camera_intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(camera_intent, AppCode.requestCode.RESULT_CAPTURE);
    }

    /**
     * 相册
     */
    public void openAblum() {
        Intent photo_album_i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(photo_album_i, "请选择图片"), AppCode.requestCode.RESULT_PICK);
    }

    /**
     * 拍照后,
     */
    private void takePictureResult() {
        if (pictureUri != null) {
            starCropPhoto(pictureUri);
        }
    }

    /**
     * 打开裁剪界面
     */
    public void starCropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (SELECT == 1) {//拍照
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//系統>7.0
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {//系統<7.0

            }
            intent.putExtra("scale", true);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureFile));
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        } else {//相册
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureFile = new File(FileUtil.getPhotopath())));
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, AppCode.requestCode.CROP_PHOTO);

//        intent.putExtra("scale", true);
//        intent.putExtra("scaleUpIfNeeded", true);
//        intent.setDataAndType(uri, "image/*");
//        // crop为true是设置在开启的intent中设置显示的view可以剪裁
//        intent.putExtra("crop", "true");
//        //剪裁比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        // outputX outputY 是裁剪图片宽高
//        intent.putExtra("outputX", 200);
//        intent.putExtra("outputY", 200);
//        intent.putExtra("noFaceDetection", true);
//        startActivityForResult(intent, CROP_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppCode.requestCode.RESULT_CAPTURE:
                    takePictureResult();
                    break;
                case AppCode.requestCode.CROP_PHOTO:
                    if (SELECT == 1) {//拍照剪裁
                        if (pictureStr != null && !pictureStr.isEmpty()) {
                            Bitmap loacalBitmap = ImageUtil.getLoacalBitmap(pictureStr);
                            headImg.setImageBitmap(loacalBitmap);
                        }
                    } else {//相册剪裁
                        if (data != null) {
                            Bundle extras = data.getExtras();
                            if (extras != null) {
                                // 取得SDCard图片路径做显示
                                pictureStr = FileUtil.getPhotopath();
                                Bitmap photo = ImageUtil.getLoacalBitmap(FileUtil.getPhotopath());
                                pictureStr = FileUtil.saveFile(this, "imageTest.jpg", photo);
                                Drawable drawable = new BitmapDrawable(null, photo);
                                headImg.setImageDrawable(drawable);
                                //将图片上传到服务器
                                if (pictureStr != null && !pictureStr.isEmpty()) {

                                }

                            }

                        }
                    }
                    break;
                case AppCode.requestCode.RESULT_PICK:
                    if (data != null) {
                        Uri uri = data.getData();
                        starCropPhoto(uri);
                    }
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101:
                if (PermissionUtils.hasAllPermissionsGranted(grantResults)) {
                    //权限申请成功
                    if (SELECT == 1) {
                        openCamera();
                    } else {
                        openAblum();
                    }
                } else {
                    //权限申请失败
                    Toast.makeText(this, "授权失败！", Toast.LENGTH_LONG).show();
                }
                break;


        }
    }
}
