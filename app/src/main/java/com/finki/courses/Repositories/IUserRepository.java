package com.finki.courses.Repositories;

import android.net.Uri;

import java.io.InputStream;

public interface IUserRepository {

    void loadProfilePictureFromFirebase();
    void addUserPictureToDocument(String email, String profilePictureUri);
    void uploadUserPictureToStorage(InputStream inputStream);

    String loadProfilePictureFromCache(String sharedPrefName);
    void saveProfilePictureToCache(String imageUrl, String sharedPrefName);
    boolean profileCacheIsEmpty(String sharedPrefName);

    void loadCoverPhotoFromFirebase();
    void uploadAndAddCoverPhotoToDocument(String email, InputStream inputStream);

    void loadNicknameAndBioFromFirebase(String email);

    void saveNicknameAndBioToFirebase(String email, String nickname, String bio);
}
