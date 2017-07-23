package com.app.githubdetective.sections.search;

import android.support.annotation.NonNull;

import com.app.githubdetective.RepoDetectiveApp;
import com.app.githubdetective.data.ReposRepository;
import com.app.githubdetective.data.local.models.Repo;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SearchPresenter implements MvpPresenter<SearchView> {
    private static final String TAG = "SearchPresenter";

    private SearchView mView;
    private Disposable mSearchDisposable;
    @Inject ReposRepository mReposRepository;

    SearchPresenter() {
        Timber.tag(TAG);
        Timber.d("Presenter created");
        RepoDetectiveApp.getAppComponent().inject(this);
    }

    void loadRepos(final String keyword) {
        if (mSearchDisposable != null && !mSearchDisposable.isDisposed()) {
            mSearchDisposable.dispose();
        }
        mSearchDisposable = mReposRepository.loadRepositories(keyword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(repos -> {
                    if (repos == null || repos.isEmpty()) {
                        handleEmptyResults();
                    } else {
                        onReposRetrieved(repos);
                    }
                }, throwable -> onErrorOccured(throwable.getMessage()));
    }

    private void handleEmptyResults() {
        if (mView != null) {
            mView.onLoadingProgressStopped();
            mView.onNoReposFound();
        }
    }

    private void onReposRetrieved(@NonNull final List<Repo> repos) {
        if (mView != null) {
            mView.onLoadingProgressStopped();
            mView.onReposLoaded(repos);
        }
    }

    private void onErrorOccured(final String errorMessage) {
        Timber.e(errorMessage);
        if (mView != null) {
            mView.onLoadingProgressStopped();
            mView.onErrorOccured(errorMessage);
        }
    }

    void stopSearch() {
        if (mSearchDisposable != null && !mSearchDisposable.isDisposed()) {
            mSearchDisposable.dispose();
        }
    }

    @Override
    public void attachView(SearchView view) {
        mView = view;
    }

    @Override
    public void detachView(boolean retainInstance) {
        mView = null;
    }
}
