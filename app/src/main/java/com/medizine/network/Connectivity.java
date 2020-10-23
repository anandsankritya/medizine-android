package com.medizine.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.medizine.MedizineApp;

public class Connectivity {
    private NetworkInfo.State state;
    private NetworkInfo.DetailedState detailedState;
    private int type;
    private int subType;
    private boolean available;
    private boolean failover;
    private boolean roaming;
    private String typeName;
    private String subTypeName;
    private String reason;
    private String extraInfo;

    protected Connectivity() {
    }

    protected Connectivity(@NonNull Builder builder) {
        state = builder.state;
        detailedState = builder.detailedState;
        type = builder.type;
        subType = builder.subType;
        available = builder.available;
        failover = builder.failover;
        roaming = builder.roaming;
        typeName = builder.typeName;
        subTypeName = builder.subTypeName;
        reason = builder.reason;
        extraInfo = builder.extraInfo;
    }

    @NonNull
    public static Connectivity create() {
        final NetworkInfo networkInfo = getNetworkInfo();
        return (networkInfo == null) ? new Builder().build() : create(networkInfo);
    }

    @NonNull
    private static Connectivity create(@NonNull NetworkInfo networkInfo) {
        return new Builder().state(networkInfo.getState())
                .detailedState(networkInfo.getDetailedState())
                .type(networkInfo.getType())
                .subType(networkInfo.getSubtype())
                .available(networkInfo.isAvailable())
                .failover(networkInfo.isFailover())
                .roaming(networkInfo.isRoaming())
                .typeName(networkInfo.getTypeName())
                .subTypeName(networkInfo.getSubtypeName())
                .reason(networkInfo.getReason())
                .extraInfo(networkInfo.getExtraInfo())
                .build();
    }

    private static NetworkInfo getNetworkInfo() {
        final String service = Context.CONNECTIVITY_SERVICE;
        final ConnectivityManager manager = (ConnectivityManager) MedizineApp.getAppContext().getSystemService(service);
        return manager.getActiveNetworkInfo();
    }

    public NetworkInfo.State getState() {
        return state;
    }

    public NetworkInfo.DetailedState getDetailedState() {
        return detailedState;
    }

    public int getType() {
        return type;
    }

    public int getSubType() {
        return subType;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isFailover() {
        return failover;
    }

    public boolean isRoaming() {
        return roaming;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getSubTypeName() {
        return subTypeName;
    }

    public String getReason() {
        return reason;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Connectivity connectivity = (Connectivity) o;

        if (type != connectivity.type) {
            return false;
        }
        if (subType != connectivity.subType) {
            return false;
        }
        if (available != connectivity.available) {
            return false;
        }
        if (failover != connectivity.failover) {
            return false;
        }
        if (roaming != connectivity.roaming) {
            return false;
        }
        if (state != connectivity.state) {
            return false;
        }
        if (detailedState != connectivity.detailedState) {
            return false;
        }
        if (!typeName.equals(connectivity.typeName)) {
            return false;
        }
        if (subTypeName != null ? !subTypeName.equals(connectivity.subTypeName) : connectivity.subTypeName != null) {
            return false;
        }
        if (reason != null ? !reason.equals(connectivity.reason) : connectivity.reason != null) {
            return false;
        }

        return extraInfo != null ? extraInfo.equals(connectivity.extraInfo) : connectivity.extraInfo == null;
    }

    @Override
    public int hashCode() {
        int result = state.hashCode();
        result = 31 * result + (detailedState != null ? detailedState.hashCode() : 0);
        result = 31 * result + type;
        result = 31 * result + subType;
        result = 31 * result + (available ? 1 : 0);
        result = 31 * result + (failover ? 1 : 0);
        result = 31 * result + (roaming ? 1 : 0);
        result = 31 * result + typeName.hashCode();
        result = 31 * result + (subTypeName != null ? subTypeName.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (extraInfo != null ? extraInfo.hashCode() : 0);
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "Connectivity{"
                + "state="
                + state
                + ", detailedState="
                + detailedState
                + ", type="
                + type
                + ", subType="
                + subType
                + ", available="
                + available
                + ", failover="
                + failover
                + ", roaming="
                + roaming
                + ", typeName='"
                + typeName
                + '\''
                + ", subTypeName='"
                + subTypeName
                + '\''
                + ", reason='"
                + reason
                + '\''
                + ", extraInfo='"
                + extraInfo
                + '\''
                + '}';
    }

    public static class Builder {

        // disabling PMD for builder class attributes
        // because we want to have the same method names as names of the attributes for builder

        private NetworkInfo.State state = NetworkInfo.State.DISCONNECTED;
        private NetworkInfo.DetailedState detailedState = NetworkInfo.DetailedState.IDLE;
        private int type = -1;
        private int subType = -1;
        private boolean available = false;
        private boolean failover = false;
        private boolean roaming = false;
        private String typeName = "NONE";
        private String subTypeName = "NONE";
        private String reason = "";
        private String extraInfo = "";

        @NonNull
        public Builder state(NetworkInfo.State state) {
            this.state = state;
            return this;
        }

        @NonNull
        public Builder detailedState(NetworkInfo.DetailedState detailedState) {
            this.detailedState = detailedState;
            return this;
        }

        @NonNull
        public Builder type(int type) {
            this.type = type;
            return this;
        }

        @NonNull
        public Builder subType(int subType) {
            this.subType = subType;
            return this;
        }

        @NonNull
        public Builder available(boolean available) {
            this.available = available;
            return this;
        }

        @NonNull
        public Builder failover(boolean failover) {
            this.failover = failover;
            return this;
        }

        @NonNull
        public Builder roaming(boolean roaming) {
            this.roaming = roaming;
            return this;
        }

        @NonNull
        public Builder typeName(String name) {
            this.typeName = name;
            return this;
        }

        @NonNull
        public Builder subTypeName(String subTypeName) {
            this.subTypeName = subTypeName;
            return this;
        }

        @NonNull
        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        @NonNull
        public Builder extraInfo(String extraInfo) {
            this.extraInfo = extraInfo;
            return this;
        }

        @NonNull
        public Connectivity build() {
            return new Connectivity(this);
        }
    }
}
