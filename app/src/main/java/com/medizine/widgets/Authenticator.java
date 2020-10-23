package com.medizine.widgets;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import javax.annotation.Nullable;

public class Authenticator extends AbstractAccountAuthenticator {

    public Authenticator(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public Bundle editProperties(@NonNull AccountAuthenticatorResponse response, @NonNull String accountType) {
        throw new UnsupportedOperationException();
    }

    @androidx.annotation.Nullable
    @Override
    public Bundle addAccount(@NonNull AccountAuthenticatorResponse response,
                             @NonNull String accountType, @NonNull String authTokenType,
                             @NonNull String[] requiredFeatures, @NonNull Bundle options) throws NetworkErrorException {
        return null;
    }

    @androidx.annotation.Nullable
    @Override
    public Bundle confirmCredentials(@NonNull AccountAuthenticatorResponse response,
                                     @NonNull Account account,
                                     @NonNull Bundle options) throws NetworkErrorException {
        return null;
    }

    @NonNull
    @Override
    public Bundle getAuthToken(@NonNull AccountAuthenticatorResponse response,
                               @NonNull Account account,
                               @NonNull String authTokenType,
                               @NonNull Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public String getAuthTokenLabel(@NonNull String authTokenType) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Bundle updateCredentials(@NonNull AccountAuthenticatorResponse response,
                                    @NonNull Account account,
                                    @NonNull String authTokenType,
                                    @NonNull Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Bundle hasFeatures(@NonNull AccountAuthenticatorResponse response,
                              @NonNull Account account,
                              @Nullable String[] features) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }
}
