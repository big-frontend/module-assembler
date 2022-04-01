package com.rnbundle;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.image.ImageResizeMode;

import java.util.Map;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Mar/13/2022  Sun
 */
public class MyImageViewManager extends SimpleViewManager<MyImageView> {

   ReactApplicationContext mCallerContext;

   public MyImageViewManager(ReactApplicationContext reactContext) {
      mCallerContext = reactContext;
   }

   @Override
   public String getName() {
      return "MyImageView";
   }

   @Override
   public MyImageView createViewInstance(ThemedReactContext context) {
      return new MyImageView(context);
   }


   @ReactProp(name = "src")
   public void setSrc(MyImageView view, @Nullable ReadableArray sources) {
      view.setSource(sources);
   }

   @ReactProp(name = "borderRadius", defaultFloat = 0f)
   public void setBorderRadius(MyImageView view, float borderRadius) {
      view.setBorderRadius(borderRadius);
   }

   @ReactProp(name = ViewProps.RESIZE_MODE)
   public void setResizeMode(MyImageView view, @Nullable String resizeMode) {
      view.setScaleType(ImageResizeMode.toScaleType(resizeMode));
   }

   @Override
   public Map getExportedCustomBubblingEventTypeConstants() {
      //java层的topChange 与 js层的onChange进行映射
      return MapBuilder.builder().put(
              "topChange",
              MapBuilder.of(
                      "phasedRegistrationNames",
                      MapBuilder.of("bubbled", "onChange")
              )
      ).build();
   }

}