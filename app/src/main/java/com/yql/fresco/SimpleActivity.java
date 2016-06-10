package com.yql.fresco;

import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;


public class SimpleActivity extends AppCompatActivity implements ControllerListener {
    public static final int TYPE_XML = 0;
    public static final int TYPE_CODE = 1;
    private static final String TAG = "URL_URI";
    private SimpleDraweeView simpleDraweeView;
    private int type;
    private static String IMG_URI = "http://img.ivsky.com/img/tupian/pre/201506/05/donghuapian_yingtao_xiaowanzi-006.jpg";
    private static String GIF_URI = "http://img2.duitang.com/uploads/item/201303/15/20130315134323_PMTrz.gif";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //这里记得要在setContentView之前初始化
        Fresco.initialize(this);
        setContentView(R.layout.activity_simple);
        type = getIntent().getIntExtra("type", TYPE_XML);
        simpleDraweeView = (SimpleDraweeView) findViewById(R.id.img);
        loadImg();
    }

    private void loadImg() {
        switch (type) {
            case TYPE_XML:
                loadImgXml();
                break;
            case TYPE_CODE:
                loadImgCode();
                break;
            default:
                break;
        }
    }

    private void loadImgXml() {
        ViewStub viewStub = (ViewStub) findViewById(R.id.viewstub_in_xml_opt);
        viewStub.inflate();
        //这里可以设置展示的图片比例，要在xml设置layout_width为一固定值，layout_height设为wrap_content
//        simpleDraweeView.setAspectRatio(1.33f);

        //Supported URIs: http://, https://, file://, content://, asset://, res://
        //注意:这里是指绝对路径
        //        HTTP_JPG, HTTP_GIF, RES_WEBP, RES_JPG
        getUriStr(TypeEnum.RES_WEBP);
        getUriStr(TypeEnum.RES_JPG);
        getUriStr(TypeEnum.HTTP_GIF);
        getUriStr(TypeEnum.HTTP_JPG);

        Uri uri = Uri.parse(getUriStr(TypeEnum.HTTP_GIF));
        simpleDraweeView.setImageURI(uri);
        //如果actualImageScaleType选为FOCUS_CROP，则可以指定区域裁剪，(0f,0f)为左上角，(1f,1f)为右上角
        simpleDraweeView.getHierarchy().setActualImageFocusPoint(new PointF(0.5f, 0.5f));
        simpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                Toast.makeText(SimpleActivity.this, "这里是为了触发pressedStateOverlayImage效果，与retry事件不冲突", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadImgCode() {
        ViewStub viewStub = (ViewStub) findViewById(R.id.viewstub_in_code_opt);
        viewStub.inflate();


        RoundingParams roundingParams = new RoundingParams();
        roundingParams.setRoundAsCircle(false);
        roundingParams.setBorder(R.color.red, 10);
        //RoundingMethod.BITMAP_ONLY不支持failure图片，retry图片和动画（gif）,只作用于实际图片和占位图
        roundingParams.setRoundingMethod(RoundingParams.RoundingMethod.BITMAP_ONLY);
        //覆盖层模式
        roundingParams.setRoundingMethod(RoundingParams.RoundingMethod.OVERLAY_COLOR);
        roundingParams.setOverlayColor(ContextCompat.getColor(getApplicationContext(),R.color.transparent));


        //or GenericDraweeHierarchy hierarchy = simpleDraweeView.getHierarchy();
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder.setFadeDuration(1000)
                //设置实际图片的scaleType
                .setActualImageScaleType(ScalingUtils.ScaleType.FOCUS_CROP)
                .setActualImageFocusPoint(new PointF(0.5f, 0.5f))
                .setActualImageColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.transparent), PorterDuff.Mode.DARKEN))

                //设置占位图drawable和scaleType
                .setPlaceholderImage(ContextCompat.getDrawable(getApplicationContext(), R.color.placeholder), ScalingUtils.ScaleType.CENTER_CROP)

                //设置error drawable和scaleType
                .setFailureImage(ContextCompat.getDrawable(getApplicationContext(), R.color.error), ScalingUtils.ScaleType.CENTER_CROP)

                //设置重试drawable， 记得在controller下设置setTapToRetryEnabled(true)
                .setRetryImage(ContextCompat.getDrawable(getApplicationContext(), R.color.retrying))

                //设置加载条drawable
                .setProgressBarImage(ContextCompat.getDrawable(getApplicationContext(), R.drawable.drawable_progress))

                .setOverlay(ContextCompat.getDrawable(getApplicationContext(), R.color.transparent))

                .setPressedStateOverlay(ContextCompat.getDrawable(getApplicationContext(), R.color.transparent_50))

                .setRoundingParams(roundingParams)

                .build();
        simpleDraweeView.setHierarchy(hierarchy);
        setDraweeUri(TypeEnum.HTTP_JPG);

    }

    private String getUriStr(TypeEnum type) {
        String uri = IMG_URI;
        StringBuilder builder = new StringBuilder();
        if (type.equals(TypeEnum.HTTP_JPG)) {
            uri = IMG_URI;
        } else if (type.equals(TypeEnum.HTTP_GIF)) {
            uri = GIF_URI;
        } else if (type.equals(TypeEnum.RES_WEBP)) {
            uri = builder.append(UriUtil.LOCAL_RESOURCE_SCHEME)
                    .append("://")
                    .append(getResources().getResourcePackageName(R.raw.local_img_1))
                    .append("/")
                    .append(R.raw.local_img_1)
                    .toString();
        } else if (type.equals(TypeEnum.RES_JPG)) {
            uri = builder.append(UriUtil.LOCAL_RESOURCE_SCHEME)
                    .append("://")
                    .append(getResources().getResourcePackageName(R.raw.local_img))
                    .append("/")
                    .append(R.raw.local_img)
                    .toString();
        }
        Log.d(TAG, type.toString() + " " + uri);
        return uri;
    }

    private void setDraweeUri(TypeEnum type) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                //tap-to-retry load image
                .setTapToRetryEnabled(true)
                //是否自动开启gif,webp动画,也可以在ControllerListener下手动启动动画
                .setAutoPlayAnimations(true)
                //在构建新的控制器时需要setOldController，这可以防止重新分配内存
                .setOldController(simpleDraweeView.getController())
                .setUri(Uri.parse(getUriStr(type)))
                .setControllerListener(this)
                .build();
        simpleDraweeView.setController(controller);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simple, menu);
        return type == TYPE_CODE;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_http) {
            setDraweeUri(TypeEnum.HTTP_JPG);
            return true;
        } else if (id == R.id.action_gif) {
            setDraweeUri(TypeEnum.HTTP_GIF);
            return true;
        } else if (id == R.id.action_webp) {
            setDraweeUri(TypeEnum.RES_WEBP);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSubmit(String s, Object o) {

    }

    @Override
    public void onFinalImageSet(String s, @Nullable Object o, @Nullable Animatable animatable) {
        ImageInfo imageInfo = (ImageInfo) o;
        if (imageInfo == null) {
            return;
        }
//        QualityInfo qualityInfo = imageInfo.getQualityInfo();
        Log.d(TAG, "图片获取完成>>>>" + "大小 " + imageInfo.getWidth() + " x " + imageInfo.getHeight());
        if (animatable != null) {
            animatable.start();//启动gif动画
        }

    }

    @Override
    public void onIntermediateImageSet(String s, @Nullable Object o) {

    }

    @Override
    public void onIntermediateImageFailed(String s, Throwable throwable) {

    }

    @Override
    public void onFailure(String s, Throwable throwable) {

    }

    @Override
    public void onRelease(String s) {

    }

    public enum TypeEnum {
        HTTP_JPG, HTTP_GIF, RES_WEBP, RES_JPG
    }
}
