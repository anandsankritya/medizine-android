package com.medizine.event;

import androidx.annotation.NonNull;

public class Event {

    public static class OTPFragmentEvent {
        private String userName, userMobile;

        public OTPFragmentEvent(@NonNull String userName, @NonNull String userMobile) {
            this.userName = userName;
            this.userMobile = userMobile;
        }

        public String getUserName() {
            return userName;
        }

        public String getUserMobile() {
            return userMobile;
        }
    }

    public static class AudioDownloadEvent {
        private String name;
        private String url;

        public AudioDownloadEvent(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }

    public static class ItemSelectedEvent {
        private String type;
        private String id;

        public ItemSelectedEvent(@NonNull String id, @NonNull String type) {
            this.id = id;
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }
    }

    public static class ZoomMeetingEvent {
        private String moduleId;
        private String moduleType;

        public ZoomMeetingEvent(String moduleId, String moduleType) {
            this.moduleId = moduleId;
            this.moduleType = moduleType;
        }

        public String getModuleId() {
            return moduleId;
        }

        public String getModuleType() {
            return moduleType;
        }
    }

    public static class RetryButtonEvent {

    }

    /*
        Event used to indicate : 1 ) if it's force or normal update
                                 2)
         */
    public static class UpdateCheckerEvent {
        @NonNull
        private final String updateType;
        @NonNull
        private final String updateBodyText;


        public UpdateCheckerEvent(@NonNull String updateType, @NonNull String updateBodyText) {
            this.updateType = updateType;
            this.updateBodyText = updateBodyText;
        }

        @NonNull
        public String getUpdateType() {
            return updateType;
        }

        @NonNull
        public String getUpdateBodyText() {
            return updateBodyText;
        }
    }

}

