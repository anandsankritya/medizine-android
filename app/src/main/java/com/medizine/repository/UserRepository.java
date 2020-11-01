package com.medizine.repository;

import com.medizine.model.entity.User;

import javax.inject.Singleton;

@Singleton
public class UserRepository extends BaseRepository<User> {
    private volatile static UserRepository INSTANCE = null;

    public static UserRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (UserRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserRepository();
                }
            }
        }
        return INSTANCE;
    }

//    public Resource<User> getUser(@NonNull String userId, @NonNull FetchResource.TYPE type) {
//        return fetch(new FetchResource<User>() {
//            @NonNull
//            @Override
//            public TYPE fetchType() {
//                return type;
//            }
//
//            @NonNull
//            @Override
//            public LiveData<User> getStorage() {
//                return StorageService.getInstance().getMedizineDatabase().userDao().getUser(userId);
//            }
//
//            @Nullable
//            @Override
//            public Single<Boolean> exists() {
//                return StorageService.getInstance().getMedizineDatabase().userDao().exists(userId);
//            }
//
//            @NonNull
//            @Override
//            public Completable storageCall(User user) {
//                return StorageService.getInstance().getMedizineDatabase().userDao().insertOrUpdate(user);
//            }
//
//            @NonNull
//            @Override
//            public Single<Response<User>> networkCall() {
//                return NetworkService.getInstance().getUserProfile(userId);
//            }
//        });
//    }

    @Override
    public void destroy() {
        INSTANCE = null;
    }
}
