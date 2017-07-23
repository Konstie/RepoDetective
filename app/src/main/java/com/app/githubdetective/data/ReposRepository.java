package com.app.githubdetective.data;

import com.app.githubdetective.data.local.database.GithubDetectiveDatabase;
import com.app.githubdetective.data.local.database.RepositoriesDao;
import com.app.githubdetective.data.local.models.Repo;
import com.app.githubdetective.data.remote.ReposRemoteDataSource;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Class responsible for Github-repositories retrieval by using cache-than-network strategy.
 */
public class ReposRepository {
    private GithubDetectiveDatabase mDb;
    private RepositoriesDao mReposDao;
    private ReposRemoteDataSource mRemoteDataSource;
    private Disposable mRepoSavingDisposable;

    public ReposRepository(GithubDetectiveDatabase database, RepositoriesDao dao, ReposRemoteDataSource remoteDataSource) {
        mDb = database;
        mReposDao = dao;
        mRemoteDataSource = remoteDataSource;
    }

    /**
     * Retrieves repositories from cache: if there are none with a given keyword, they will be
     * loaded from network and then inserted to the database.
     *
     * If Maybe were working with Room in an appropriate way and could fire empty observable as expected,
     * it would be much better do something like this:
     *
     * Maybe.concat(mReposDao.loadReposByKeyword(queryKeyword),
     *      loadRepositoriesFromNetwork(queryKeyword).toMaybe()).firstElement();
     *
     * @return Github-repositories observable.
     */
    public Maybe<List<Repo>> loadRepositories(final String queryKeyword) {
        return mReposDao.loadReposByKeyword(queryKeyword)
                .subscribeOn(Schedulers.io())
                // As we can't use Maybe with Room yet, let's intercept database query results and return
                // repos from the remote source observable of they are empty.
                .flatMapSingleElement(new Function<List<Repo>, Single<List<Repo>>>() {
                    @Override
                    public Single<List<Repo>> apply(@NonNull List<Repo> repos) throws Exception {
                        if (repos == null || repos.isEmpty()) {
                            return loadRepositoriesFromNetwork(queryKeyword);
                        } else {
                            return Single.fromCallable(() -> repos);
                        }
                    }
                }).cache();
    }

    private Single<List<Repo>> loadRepositoriesFromNetwork(final String queryKeyword) {
        return mRemoteDataSource.loadSortedReposFromNetwork(queryKeyword)
                        .doOnSuccess(repos -> saveRepositoriesToDatabase(queryKeyword, repos));
    }

    /**
     * Associates repos with the keyword and stores the mutated repos to the database.
     */
    private void saveRepositoriesToDatabase(final String keyword, final List<Repo> repos) {
        mRepoSavingDisposable = Flowable.fromIterable(repos)
                .subscribeOn(Schedulers.io())
                .doOnNext(repo -> repo.setKeyword(keyword))
                .toList()
                .subscribe(this::saveRepositoriesToDatabase, throwable ->
                        Timber.e("Could not save repositories. Probably, ran into some connectivity issues!"));
    }

    /**
     * Performs repos saving transaction.
     */
    private void saveRepositoriesToDatabase(final List<Repo> repos) {
        Timber.d("Repos count: " + repos.size());
        try {
            mDb.beginTransaction();
            mReposDao.insertRepos(repos);
        } finally {
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
        }
    }
}
