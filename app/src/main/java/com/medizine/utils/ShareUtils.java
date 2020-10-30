package com.medizine.utils;

public class ShareUtils {
    private static final String TAG = ShareUtils.class.getSimpleName();

    private ShareUtils() {
    }

    /*
    public static void getDynamicLink(Context context, Class className, Object object) {
        getDynamicLink(context, className, object, false, false);
    }

    public static void getDynamicLink(Context context, Class className, Object object, boolean shareOnFacebook, boolean shareOnWhatsApp) {
        String title = "";
        String description = "";
        String imageUrl = "";
        String suffix = "";
        DynamicLink.SocialMetaTagParameters.Builder socialMetaTagParametersBuilder = new DynamicLink.SocialMetaTagParameters.Builder();

        Uri link = Uri.parse(WEBSITE_LINK + suffix);

        if (Utils.isNotEmpty(title)) {
            socialMetaTagParametersBuilder.setTitle(title);
        }
        if (Utils.isNotEmpty(description)) {
            socialMetaTagParametersBuilder.setDescription(description);
        }
        if (!Utils.isNullOrEmpty(imageUrl)) {
            socialMetaTagParametersBuilder.setImageUrl(Uri.parse(imageUrl));
        }

        String finalTitle = title;
        String finalDescription = description;
        String finalImageUrl = imageUrl;

        FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setDomainUriPrefix(Configuration.DYNAMIC_LINK_PREFIX)
                .setLink(link)
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.jainam.app").setFallbackUrl(Uri.parse(WEBSITE_LINK)).build())
                .setSocialMetaTagParameters(socialMetaTagParametersBuilder.build())
                .buildShortDynamicLink()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ShortDynamicLink shortDynamicLink = task.getResult();
                        if (shortDynamicLink != null) {
                            Log.d(TAG, "previewLink : " + shortDynamicLink.getPreviewLink());
                            if (shortDynamicLink.getShortLink() != null)
                                share(context, shortDynamicLink.getShortLink().toString(), finalTitle, finalDescription, finalImageUrl, shareOnFacebook, shareOnWhatsApp);
                        }
                    } else {
                        share(context, link.toString(), finalTitle, finalDescription, finalImageUrl, shareOnFacebook, shareOnWhatsApp);
                    }
                });
    }

    public static void share(Context context, String deepLink, String title, String description, String imageUrl, boolean shareOnFacebook, boolean shareOnWhatsApp) {
        if (shareOnWhatsApp || shareOnFacebook) {
            StringBuilder builder = new StringBuilder();
            if (Utils.isNotEmpty(title)) {
                builder.append("*").append(title).append("*").append("\n");
            }
            if (Utils.isNotEmpty(description)) {
                builder.append(description).append("\n");
            }
            if (Utils.isNotEmpty(deepLink)) {
                builder.append(deepLink);
            }

            if (Utils.isNotEmpty(imageUrl)) {
                Glide.with(context)
                        .asBitmap()
                        .load(imageUrl)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                resource.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                                File imageFile = ImageUtils.getOutputMediaFile(JainamApp.getAppContext());
                                FileOutputStream outStream;
                                try {
                                    outStream = new FileOutputStream(imageFile);
                                    outStream.write(byteArrayOutputStream.toByteArray());
                                    outStream.close();
                                } catch (IOException e) {
                                    launchShareIntent(context, builder.toString(), null, shareOnWhatsApp, shareOnFacebook);
                                    e.printStackTrace();
                                }
                                launchShareIntent(context, builder.toString(), imageFile, shareOnWhatsApp, shareOnFacebook);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                launchShareIntent(context, builder.toString(), null, shareOnWhatsApp, shareOnFacebook);
                            }
                        });
            } else {
                launchShareIntent(context, builder.toString(), null, shareOnWhatsApp, shareOnFacebook);
            }
        } else {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, deepLink);
            context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.text_share)));
        }
    }

    private static void launchShareIntent(Context context, String body, @Nullable File imageFile, boolean shareOnWhatsApp, boolean shareOnFacebook) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        if (imageFile != null) {
            sharingIntent.putExtra(Intent.EXTRA_TEXT, body);
            if (shareOnWhatsApp) {
                Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + PROVIDER_PATH, imageFile);
                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                sharingIntent.setType("image/jpeg");
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        } else {
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, body);
        }


        if (shareOnWhatsApp) {
            sharingIntent.setPackage("com.whatsapp");
            if (sharingIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(sharingIntent);
            } else {
                Toast.makeText(context, R.string.whatsapp_not_found, Toast.LENGTH_SHORT).show();
            }
        } else if (shareOnFacebook) {
            sharingIntent.setPackage("com.facebook.katana");
            if (sharingIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(sharingIntent);
            } else {
                Toast.makeText(context, R.string.facebook_not_found, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void navigateThroughLink(@NonNull Context context, @NonNull Uri deeplink) {
        if (FirebaseRemoteConfigUpdate.getInstance().isForceUpdateEnabled()) {
            return;
        }

        String baseUrl = null;

        if (deeplink.toString().startsWith(WEBSITE_LINK)) {
            baseUrl = WEBSITE_LINK;
        } else if (deeplink.toString().startsWith(CLICK_ACTION_LINK)) {
            baseUrl = CLICK_ACTION_LINK;
        }

        if (!Utils.isNullOrEmpty(baseUrl)) {
            String suffix = deeplink.toString().substring(baseUrl.length());
            String[] splits = suffix.split("/");
            if (splits.length == 2) {
                switch (splits[0]) {
                    case TEMPLE_TAG:
                        AnalyticsUtils.Events.sendContentSelectionEvent(splits[0], AnalyticsConstants.Event.SHARE, splits[1], AnalyticsConstants.ScreenView.DEEPLINK);
                        openTempleDetailActivity(context, splits[1]);
                        break;
                    case DHARMSHALA_TAG:
                        AnalyticsUtils.Events.sendContentSelectionEvent(splits[0], AnalyticsConstants.Event.SHARE, splits[1], AnalyticsConstants.ScreenView.DEEPLINK);
                        openDharmashalaDetailActivity(context, splits[1]);
                        break;
                    case CONTENT_LEDGER_TAG:
                        openUpdateRequestDetailActivity(context, splits[1]);
                        break;
                    case NOTICE_TAG:
                        openNoticeDetailActivity(context, splits[1], null, false);
                        break;
                    case SPEAKER_TAG:
                        SpeakerDetailActivity.openSpeakerParichay(context, splits[1]);
                        break;
                    case Constants.Events.EVENT_TAG:
                        Intent intent = new Intent(context,WebinarActivity.class);
                        intent.putExtra(Constants.EVENT_ID,splits[1]);
                        context.startActivity(intent);
                        break;
                    case Constants.Polling.POLLING_TAG:
                        PollingDetailActivity.openPollingDetailActivity(context, splits[1], null);
                }
            }
        }
    }

     */
}
