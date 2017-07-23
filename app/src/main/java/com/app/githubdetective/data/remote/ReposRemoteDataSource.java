package com.app.githubdetective.data.remote;

import com.app.githubdetective.data.local.models.Repo;
import com.app.githubdetective.data.local.models.RepoStargazersComparator;
import com.app.githubdetective.data.remote.api.SearchReposService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import timber.log.Timber;

/**
 * Data source class responsible for interaction with Github API and repositories retrieval from it
 * in 2 parallel threads (each one is responsible for repos loading from each 15-items bundle page).
 */
public class ReposRemoteDataSource {
    private static final int INDEX_FIRST_PAGE = 1;
    private static final int INDEX_SECOND_PAGE = 2;
    private static final int DEFAULT_ITEMS_PER_PAGE = 15;

    private static final String DEFAULT_SORT_CRITERIA = "stars";
    private static final String DEFAULT_SORT_ORDER = "desc";

    private final String mClientId;
    private final String mClientSecret;
    private SearchReposService mReposService;

    public ReposRemoteDataSource(SearchReposService searchReposService, final String clientId, final String clientSecret) {
        mReposService = searchReposService;
        mClientId = clientId;
        mClientSecret = clientSecret;
    }

    /**
     * Performs call to repos search API.
     *
     * @return repositories from Github API using a single thread.
     */
    private Single<ReposGithubResponse> loadReposFromNetwork(final String queryKeyword, final int pageNumber) {
        return mReposService
                .repositories(queryKeyword, DEFAULT_SORT_CRITERIA, DEFAULT_SORT_ORDER, pageNumber, DEFAULT_ITEMS_PER_PAGE, mClientId, mClientSecret)
                .doOnSuccess(reposGithubResponse -> Timber.d("Thread " + pageNumber + " finished repos search!"));
    }

    /**
     * Executes 2 parallel Github-repos search requests and zips their results into a single observable with
     * sorted repo instances inside.
     *
     * @return single observable with sorted repo instances inside.
     */
    public Single<List<Repo>> loadSortedReposFromNetwork(final String queryKeyword) {
        final Single<ReposGithubResponse> firstPageResponse = loadReposFromNetwork(queryKeyword, INDEX_FIRST_PAGE);
        final Single<ReposGithubResponse> secondPageResponse = loadReposFromNetwork(queryKeyword, INDEX_SECOND_PAGE);

        return Single.zip(firstPageResponse, secondPageResponse,
                (reposFromFirstPage, reposFromSecondPage) -> {
            List<Repo> allRepos = new ArrayList<>();
            if (reposFromFirstPage != null && reposFromFirstPage.getItems() != null) {
                allRepos.addAll(reposFromFirstPage.getItems());
            }
            if (reposFromSecondPage != null && reposFromSecondPage.getItems() != null) {
                allRepos.addAll(reposFromSecondPage.getItems());
            }
            return allRepos;
        }).map(repos -> {
            Collections.sort(repos, new RepoStargazersComparator());
            return repos;
        });
    }


}
