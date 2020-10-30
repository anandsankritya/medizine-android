package com.medizine.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.medizine.Constants;
import com.medizine.GlideApp;
import com.medizine.MedizineApp;
import com.medizine.R;
import com.medizine.model.MediaLink;
import com.medizine.model.MediaObject;
import com.medizine.model.ThumbImage;
import com.medizine.model.enums.AspectRatio;
import com.medizine.widgets.TextDrawable;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation;

import static com.medizine.Constants.MAX_ALLOWED_RESOLUTION;
import static com.medizine.Constants.MIN_ALLOWED_RESOLUTION;
import static com.medizine.Constants.THUMB_SIZE;
import static com.medizine.Constants.TRANSFORMATION;

public class ImageUtils {

    public static final int REQUEST_CAMERA = 101;
    public static final int SELECT_FILE = 102;
    public static final String PROVIDER_PATH = ".provider.GenericFileProvider";
    public static final String ALL_PATH = "all_path";
    public static final String ACTION_MULTIPLE_PICK = Constants.APP_NAME + "ACTION_MULTIPLE_PICK";
    // Gallery directory name to store the images
    private static final String GALLERY_DIRECTORY_NAME = Constants.APP_NAME;
    private static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    private static final String IMAGE_NAME = "IMG_";
    private static final String IMAGE_EXTENSION = ".jpg";
    @NonNull
    private static String TAG = ImageUtils.class.getSimpleName();

    public static File showPhotoEditDialog(@Nullable final Activity activity, @NonNull RxPermissions rxPermissions, @Nullable Integer cameraRequestCode,
                                           @Nullable Integer gallaryRequestCode, @Nullable OnRemoveButtonClickListener onRemoveButtonClickListener) {
        if (activity == null) {
            return null;
        }

        File cameraFile = ImageUtils.getOutputMediaFile(activity);
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        List<String> itemList = new ArrayList<>();
                        if (cameraRequestCode != null && cameraFile != null) {
                            itemList.add(activity.getString(R.string.take_photo));
                        }
                        if (gallaryRequestCode != null) {
                            itemList.add(activity.getString(R.string.choose_from_gallery));
                        }
                        if (onRemoveButtonClickListener != null) {
                            itemList.add(activity.getString(R.string.remove_photo));
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setCancelable(true);
                        builder.setItems(itemList.toArray(new String[]{}), (dialog, item) -> {
                            dialog.dismiss();
                            if (itemList.get(item).equals(activity.getString(R.string.take_photo))) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                Uri photoURI = null;
                                if (cameraFile != null) {
                                    photoURI = FileProvider.getUriForFile(activity, activity.getPackageName() + PROVIDER_PATH, cameraFile);
                                }
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                if (cameraRequestCode != null) {
                                    activity.startActivityForResult(intent, cameraRequestCode);
                                }
                            } else if (itemList.get(item).equals(activity.getString(R.string.choose_from_gallery))) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                String[] mimeTypes = {"image/jpeg", "image/png"};
                                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                                if (gallaryRequestCode != null) {
                                    activity.startActivityForResult(intent, gallaryRequestCode);
                                }
                            } else if (itemList.get(item).equals(activity.getString(R.string.remove_photo))) {
                                if (onRemoveButtonClickListener != null) {
                                    onRemoveButtonClickListener.onRemoveClicked();
                                }
                            }
                        });
                        builder.show();
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.generic_permission_message), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> Utils.logException(TAG, throwable));
        return cameraFile;
    }

    @Nullable
    public static String getPathFromGallaryResult(Activity activity, @Nullable Uri uri) {
        String imagePath = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        if (uri != null) {
            // Get the cursor
            Cursor cursor = activity.getContentResolver().query(uri, filePathColumn, null, null, null);
            if (cursor != null) {
                // Move to first row
                cursor.moveToFirst();
                //Get the column index of MediaStore.Images.Media.DATA
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                //Gets the String value in the column
                imagePath = cursor.getString(columnIndex);
                cursor.close();
            } else {
                Utils.logException(TAG, uri.toString(), new Throwable("Cursor is null"));
            }
        }
        return imagePath;
    }

    /**
     * Creates and returns the image or video file before opening the camera
     */
    public static File getOutputMediaFile(Context context) {
        File mediaStorageDir;
        String timeStamp = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date());

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), GALLERY_DIRECTORY_NAME);
        } else {
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), GALLERY_DIRECTORY_NAME);
        }

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "Failed to create directory");
            return null;
        }

        return new File(mediaStorageDir.getPath() + File.separator + IMAGE_NAME + timeStamp + IMAGE_EXTENSION);
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

    /**
     * Refreshes gallery on adding new image/video. Gallery won't be refreshed
     * on older devices until device is rebooted
     */
    public static void refreshGallery(Context context, String path) {
        MediaScannerConnection.scanFile(context, new String[]{path}, null, null);
    }

    public static void loadPicInView(Context context, Uri drawable, ImageView imageView) {
        GlideApp.with(context)
                .asBitmap()
                .load(drawable)
                .thumbnail(THUMB_SIZE)
                .centerCrop()
                .into(imageView);
    }

    public static void loadPicInView(Context context, ImageView imageView, int resourceId) {
        loadPicInView(context, null, imageView, resourceId);
    }

    public static void loadPicInView(Context context, String url, ImageView imageView) {
        loadPicInView(context, url, imageView, 0);
    }

    public static void loadPicInView(Context context, @Nullable String url, ImageView imageView, @DrawableRes int resourceId) {
        GlideApp.with(context)
                .asBitmap()
                .load(url)
                .thumbnail(THUMB_SIZE)
                .centerCrop()
                .placeholder(resourceId)
                .into(imageView);
    }

    public static void loadPicInCircularView(Context context, @Nullable String url, ImageView imageView, @DrawableRes int resourceId) {
        GlideApp.with(context)
                .asBitmap()
                .load(url)
                .thumbnail(THUMB_SIZE)
                .placeholder(resourceId)
                .centerCrop()
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    public static void loadPicInRoundedRectangle(Context context, @Nullable String url, ImageView imageView, @DrawableRes int resourceId, int radius) {
        GlideApp.with(context)
                .load(url)
                .thumbnail(THUMB_SIZE)
                .placeholder(resourceId)
                .transform(new CenterCrop(), new RoundedCorners(radius))
                .into(imageView);
    }

    public static void loadPicInCircularView(Context context, @Nullable String url, ImageView imageView) {
        GlideApp.with(context)
                .asBitmap()
                .load(url)
                .thumbnail(THUMB_SIZE)
                .centerCrop()
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    public static void loadPicInBorderedCircularView(Context context, @Nullable String url, ImageView imageView, @DrawableRes int resourceId, int width, @ColorInt int color) {
        GlideApp.with(context)
                .asBitmap()
                .load(url)
                .thumbnail(THUMB_SIZE)
                .placeholder(resourceId)
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(new CropCircleWithBorderTransformation(width, color)))
                .into(imageView);
    }

    public static void loadPicInBorderedCircularView(Context context, @Nullable Uri uri, ImageView imageView, @DrawableRes int resourceId, int width, @ColorInt int color) {
        GlideApp.with(context)
                .asBitmap()
                .load(uri)
                .thumbnail(THUMB_SIZE)
                .placeholder(resourceId)
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(new CropCircleWithBorderTransformation(width, color)))
                .into(imageView);
    }

    public static void renderCircleImageOrInitials(@NonNull Context context, @NonNull ImageView imageView, @NonNull String initials, @Nullable ThumbImage thumbImage, int position) {
        if (thumbImage == null) {
            TextDrawable drawable = TextDrawable.builder().buildRound(initials, ColorGenerator.MATERIAL.getColor(position % ColorGenerator.MATERIAL.getSize()));
            imageView.setBackground(drawable);
        } else {
            loadPicInCircularView(context, thumbImage.getS3Links().get(0).getLink(), imageView);
        }
    }

    public static void renderCircleUserPicOrInitials(@NonNull Context context, @Nullable String url, @Nullable String name, @NonNull ImageView imageView) {

        loadPicInCircularView(context, url, imageView, R.drawable.profile_pic_circle);
    }

    public static void renderSpeakerPicOrInitials(@NonNull Context context, @Nullable String url, @NonNull String name, @NonNull ImageView imageView) {
//            String initials = Utils.getInitialsOfName(name);
//            TextDrawable drawable = TextDrawable.builder().buildRound(initials, ColorGenerator.MATERIAL.getColor(initials.charAt(0) % ColorGenerator.DEFAULT.getSize()));
//            imageView.setBackground(drawable);

        loadPicInView(context, url, imageView, R.drawable.profile_pic);
    }

    public static void renderCircleSpeakerPicOrInitials(@NonNull Context context, @Nullable String url, @NonNull String name, @NonNull ImageView imageView) {

        loadPicInCircularView(context, url, imageView, R.drawable.profile_pic_circle);
    }

    public static void renderBorderedCircleSpeakerPicOrInitials(@NonNull Context context, @Nullable String url, @NonNull String name, @NonNull ImageView imageView, int width, @ColorInt int color) {

        loadPicInBorderedCircularView(context, url, imageView, R.drawable.profile_pic_white_border_circle, width, color);
    }

    @Nullable
    public static String getImageUrlByTransformation(String transformation, @Nullable MediaLink thumbnail, List<MediaLink> photos) {
        String imageUrl = null;
        if (thumbnail != null) {
            imageUrl = getImageUrl(thumbnail.getS3Links(), transformation);
        } else if (!Utils.isListEmpty(photos)) {
            imageUrl = getImageUrl(photos.get(0).getS3Links(), transformation);
        }
        return imageUrl;
    }

    @Nullable
    public static String getImageUrl(@Nullable List<MediaObject> s3Links, String transformation) {
        if (Utils.isListEmpty(s3Links)) {
            return null;
        }

        for (MediaObject s3Link : s3Links) {
            Map<String, String> objMetadata = null;
            if (s3Link != null) {
                objMetadata = s3Link.getObjectMetadata();
            }
            if (objMetadata != null && !Utils.isNullOrEmpty(objMetadata.get(TRANSFORMATION)) && Objects.requireNonNull(objMetadata.get(TRANSFORMATION)).equals(transformation)) {
                return s3Link.getLink();
            }
        }
        if (s3Links.get(0) != null) {
            return s3Links.get(0).getLink();
        } else {
            return null;
        }
    }

    @Nullable
    public static String getImageUrl(@Nullable ThumbImage thumbImage, String transformation) {
        if (thumbImage == null) {
            return null;
        }
        return getImageUrl(thumbImage.getS3Links(), transformation);
    }

    public static File processImageBeforeUpload(@NonNull String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        Log.d(TAG, "Input file size :: " + android.text.format.Formatter.formatShortFileSize(MedizineApp.getAppContext(), imageFile.length()));

        Bitmap inputBitmap = BitmapFactory.decodeFile(imageFile.getPath());
        if (inputBitmap == null) {
            return imageFile;
        }

        Log.d(TAG, "Input bitmap width :: " + inputBitmap.getWidth());
        Log.d(TAG, "Input bitmap height :: " + inputBitmap.getHeight());

        Bitmap croppedBitmap = rotateIfRequired(getCroppedBitmap(inputBitmap), imagePath);

        Log.d(TAG, "Output bitmap width :: " + croppedBitmap.getWidth());
        Log.d(TAG, "Output bitmap height :: " + croppedBitmap.getHeight());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);

        imageFile = getOutputCacheMediaFile(MedizineApp.getAppContext());
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(imageFile);
            outStream.write(byteArrayOutputStream.toByteArray());
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Output file size :: " + android.text.format.Formatter.formatShortFileSize(MedizineApp.getAppContext(), imageFile.length()));

        return imageFile;

    }

    private static Bitmap rotateIfRequired(Bitmap bitmap, String imagePath) throws IOException {

        // Find out if the picture needs rotating by looking at its Exif data
        ExifInterface exifInterface = new ExifInterface(imagePath);
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        int rotationDegrees = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotationDegrees = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotationDegrees = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotationDegrees = 270;
                break;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private static Bitmap getCroppedBitmap(Bitmap bitmap) {
        AspectRatio aspectRatio = getAspectRatio(bitmap);
        if (aspectRatio == null) {
            return cropBitmapWithoutAspectRatio(bitmap);
        } else {
            return cropBitmapUsingAspectRatio(aspectRatio, bitmap);
        }
    }

    private static Bitmap cropBitmapUsingAspectRatio(AspectRatio aspectRatio, Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, aspectRatio.getWidth(), aspectRatio.getHeight(), false);
    }

    private static Bitmap cropBitmapWithoutAspectRatio(Bitmap bitmap) {
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();

        if (inWidth >= inHeight) {
            if (inWidth > MAX_ALLOWED_RESOLUTION) {
                outWidth = MAX_ALLOWED_RESOLUTION;
                outHeight = (inHeight * MAX_ALLOWED_RESOLUTION) / inWidth;
            } else if (inWidth < MIN_ALLOWED_RESOLUTION) {
                outWidth = (inWidth * MIN_ALLOWED_RESOLUTION) / inHeight;
                outHeight = MIN_ALLOWED_RESOLUTION;
            } else {
                outWidth = inWidth;
                outHeight = inHeight;
            }
        } else {
            if (inHeight > MAX_ALLOWED_RESOLUTION) {
                outHeight = MAX_ALLOWED_RESOLUTION;
                outWidth = (inWidth * MAX_ALLOWED_RESOLUTION) / inHeight;
            } else if (inHeight < MIN_ALLOWED_RESOLUTION) {
                outHeight = (inHeight * MIN_ALLOWED_RESOLUTION) / inWidth;
                outWidth = MIN_ALLOWED_RESOLUTION;
            } else {
                outWidth = inWidth;
                outHeight = inHeight;
            }
        }
        return Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
    }

    private static AspectRatio getAspectRatio(Bitmap bitmap) {
        float error = 0.00001f;
        float aspectRatioOfBitmap = (float) bitmap.getWidth() / bitmap.getHeight();
        for (AspectRatio ratio : AspectRatio.values()) {
            if (Math.abs(aspectRatioOfBitmap - ratio.getAspectRatio()) < error) {
                return ratio;
            }
        }
        return null;
    }

    public static File getOutputCacheMediaFile(Context context) {
        File mediaCacheDir = new File(context.getCacheDir(), "/image_cache");
        if (!mediaCacheDir.exists()) {
            if (!mediaCacheDir.mkdirs()) {
                return context.getCacheDir();
            }
        }
        String timeStamp = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date());
        return new File(mediaCacheDir.getPath() + File.separator + IMAGE_NAME + timeStamp + "_" + System.nanoTime() + IMAGE_EXTENSION);
    }

    public interface OnRemoveButtonClickListener {
        void onRemoveClicked();
    }

}