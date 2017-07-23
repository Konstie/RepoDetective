package com.app.githubdetective.data.remote;

import com.app.githubdetective.data.local.models.Repo;
import com.squareup.moshi.Json;

import java.util.List;

public class ReposGithubResponse {
    private @Json(name = "items") List<Repo> mItems;

    List<Repo> getItems() {
        return mItems;
    }
}
