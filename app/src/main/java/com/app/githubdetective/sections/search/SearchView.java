package com.app.githubdetective.sections.search;

import com.app.githubdetective.data.local.models.Repo;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import java.util.List;

interface SearchView extends MvpView {
    void onReposLoaded(final List<Repo> repos);
    void onNoReposFound();
    void onErrorOccured(String errorMessage);
    void onLoadingProgressStopped();
}
