package com.beautifullife.core.util;

import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * Created by zhy on 15/8/11.
 */
public class ImageUtils {

    public static ImageSize getImageSize(InputStream imageStream) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageStream, null, options);
        return new ImageSize(options.outWidth, options.outHeight);
    }

    public static class ImageSize {
        int width;
        int height;

        public ImageSize() {
        }

        public ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString() {
            return "ImageSize{" +
                    "width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

    public static int calculateInSampleSize(ImageSize srcSize, ImageSize targetSize) {
        int width = srcSize.width;
        int height = srcSize.height;
        int inSampleSize = 1;

        int reqWidth = targetSize.width;
        int reqHeight = targetSize.height;

        if (width > reqWidth && height > reqHeight) {
            int widthRatio = Math.round((float) width / (float) reqWidth);
            int heightRatio = Math.round((float) height / (float) reqHeight);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        return inSampleSize;
    }

    public static int computeImageSampleSize(ImageSize srcSize, ImageSize targetSize, ImageView imageView) {
        final int srcWidth = srcSize.width;
        final int srcHeight = srcSize.height;
        final int targetWidth = targetSize.width;
        final int targetHeight = targetSize.height;

        int scale = 1;

        if (imageView == null) {
            scale = Math.max(srcWidth / targetWidth, srcHeight / targetHeight); // max
        } else {
            switch (imageView.getScaleType()) {
                case FIT_CENTER:
                case FIT_XY:
                case FIT_START:
                case FIT_END:
                case CENTER_INSIDE:
                    scale = Math.max(srcWidth / targetWidth, srcHeight / targetHeight); // max
                    break;
                case CENTER:
                case CENTER_CROP:
                case MATRIX:
                    scale = Math.max(srcWidth / targetWidth, srcHeight / targetHeight); // min
                    break;
                default:
                    scale = Math.max(srcWidth / targetWidth, srcHeight / targetHeight); // max
                    break;
            }
        }

        if (scale < 1) {
            scale = 1;
        }

        return scale;
    }

    public static ImageSize getImageViewSize(View view) {

        ImageSize imageSize = new ImageSize();

        imageSize.width = getExpectWidth(view);
        imageSize.height = getExpectHeight(view);

        return imageSize;
    }

    private static int getExpectHeight(View view) {

        int height = 0;
        if (view == null) return 0;

        final ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null && params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
            height = view.getWidth();
        }
        if (height <= 0 && params != null) {
            height = params.height;
        }

        if (height <= 0) {
            height = getImageViewFieldValue(view, "mMaxHeight");
        }

        if (height <= 0) {
            DisplayMetrics displayMetrics = view.getContext().getResources()
                    .getDisplayMetrics();
            height = displayMetrics.heightPixels;
        }

        return height;
    }

    private static int getExpectWidth(View view) {
        int width = 0;
        if (view == null) return 0;

        final ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
            width = view.getWidth();
        }
        if (width <= 0 && params != null) {
            width = params.width;
        }

        if (width <= 0)

        {
            width = getImageViewFieldValue(view, "mMaxWidth");
        }
        if (width <= 0)

        {
            DisplayMetrics displayMetrics = view.getContext().getResources()
                    .getDisplayMetrics();
            width = displayMetrics.widthPixels;
        }

        return width;
    }

    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
        }
        return value;

    }
}
