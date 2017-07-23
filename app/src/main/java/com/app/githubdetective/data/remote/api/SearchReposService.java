package com.app.githubdetective.data.remote.api;

import com.app.githubdetective.data.remote.ReposGithubResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchReposService {
    /**
     * Retrieves repositories by a given keyword.
     * Client key and secret are also passed on order not to spend overhead time on authorization
     * flow, as this part is not esseintial for the current project. Just needed to increase
     * the requests to Github API rate limit.
     *
     * @param clientId in real life i would NEVER ever pass this
     * @param clientSecret in real life i would NEVER ever pass this as well
     */
    @GET("/search/repositories")
    Single<ReposGithubResponse> repositories(
            @Query(encoded = true, value = "q") String query,
            @Query("sort") String sortType, @Query("order") String order,
            @Query("page") int page, @Query("per_page") int itemsPerPage,
            @Query("client_id") String clientId, @Query("client_secret") String clientSecret
    );
}
