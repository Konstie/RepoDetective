package com.app.githubdetective.data.local.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

import com.squareup.moshi.Json;

@Entity(tableName = "repositories", primaryKeys = {"id", "keyword"})
public class Repo {
    private @Json(name = "id") @ColumnInfo(name = "id") long mId;
    private @Json(name = "full_name") @ColumnInfo(name = "full_name") String mFullName;
    private @Json(name = "html_url") @ColumnInfo(name = "html_url") String mHtmlUrl;
    private @Json(name = "description") @ColumnInfo(name = "description") String mDescription;
    private @Json(name = "language") @ColumnInfo(name = "language") String mLanguage;
    private @Json(name = "stargazers_count") @ColumnInfo(name = "stargazers_count") int mStargazersCount;
    private @Json(name = "watchers_count") @ColumnInfo(name = "watchers_count") int mWatchersCount;
    private @Json(name = "forks_count") @ColumnInfo(name = "forks_count") int mForksCount;
    private transient @ColumnInfo(name = "keyword") String mKeyword;

    public void setId(long id) {
        this.mId = id;
    }

    public long getId() {
        return mId;
    }

    public void setFullName(String fullName) {
        this.mFullName = fullName;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.mHtmlUrl = htmlUrl;
    }

    public String getHtmlUrl() {
        return mHtmlUrl;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setLanguage(String language) {
        this.mLanguage = language;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setStargazersCount(int stargazersCount) {
        this.mStargazersCount = stargazersCount;
    }

    public int getStargazersCount() {
        return mStargazersCount;
    }

    public void setWatchersCount(int watchersCount) {
        this.mWatchersCount = watchersCount;
    }

    public int getWatchersCount() {
        return mWatchersCount;
    }

    public void setForksCount(int forksCount) {
        this.mForksCount = forksCount;
    }

    public int getForksCount() {
        return mForksCount;
    }

    public String getKeyword() {
        return mKeyword;
    }

    public void setKeyword(String mKeyword) {
        this.mKeyword = mKeyword;
    }

    @Override
    public String toString() {
        return "Repo {" +
                "id=" + mId +
                ", full Name='" + mFullName + '\'' +
                ", mHtmlUrl='" + mHtmlUrl + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mLanguage='" + mLanguage + '\'' +
                ", mStargazersCount=" + mStargazersCount +
                ", mWatchersCount=" + mWatchersCount +
                ", mForksCount=" + mForksCount +
                '}';
    }
}
