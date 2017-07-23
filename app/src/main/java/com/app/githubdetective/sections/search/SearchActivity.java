package com.app.githubdetective.sections.search;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.githubdetective.R;
import com.app.githubdetective.data.local.models.Repo;
import com.app.githubdetective.sections.details.RepoDetailsDialogFragment;
import com.app.githubdetective.sections.search.adapter.RepositoriesAdapter;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class SearchActivity extends MvpActivity<SearchView, SearchPresenter> implements SearchView {
    private static final String TAG = "SearchActivity";

    private RepositoriesAdapter mAdapter;
    private Disposable mTextChangeSubscription;

    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.search_edit_text) EditText mSearchEditText;
    @BindView(R.id.repos_recycler_view) RecyclerView mReposListView;
    @BindView(R.id.error_placeholder_text_view) TextView mPlaceholderTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        Timber.tag(TAG);

        mProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.primary_light), PorterDuff.Mode.SRC_ATOP);

        setupReposListView();
        setupSearchField();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_stop_search:
                getPresenter().stopSearch();
                mProgressBar.setVisibility(View.GONE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupReposListView() {
        mAdapter = new RepositoriesAdapter(this, new ArrayList<>(), this::showRepositoryInWebView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReposListView.setLayoutManager(layoutManager);
        mReposListView.setAdapter(mAdapter);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void showRepositoryInWebView(final String repositoryTitle, final String repositoryUrl) {
        final RepoDetailsDialogFragment detailsDialog = RepoDetailsDialogFragment.newInstance(repositoryUrl);
        detailsDialog.show(getSupportFragmentManager(), "details_dialog");
    }

    private void setupSearchField() {
        mTextChangeSubscription = RxTextView.textChanges(mSearchEditText)
                .debounce(250, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    final String searchQuery = s.toString();
                    if (!searchQuery.isEmpty()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        getPresenter().loadRepos(s.toString());
                    } else if (searchQuery.isEmpty()) {
                        mAdapter.clear();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        getPresenter().attachView(this);
    }

    @Override
    public void onReposLoaded(List<Repo> repos) {
        if (repos != null && repos.size() > 0) {
            mPlaceholderTextView.setVisibility(View.GONE);
            mAdapter.setRepos(repos);
        }
    }

    @Override
    public void onNoReposFound() {
        mAdapter.clear();
        showPlaceholder(getString(R.string.search_no_results));
    }

    @Override
    public void onErrorOccured(String errorMessage) {
        mAdapter.clear();
        showPlaceholder(getString(R.string.search_error, errorMessage));
    }

    @Override
    public void onLoadingProgressStopped() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void showPlaceholder(String errorMessage) {
        mPlaceholderTextView.setVisibility(View.VISIBLE);
        mPlaceholderTextView.setText(errorMessage);
    }

    @Override
    protected void onStop() {
        getPresenter().detachView(true);

        super.onStop();
    }

    @NonNull
    @Override
    public SearchPresenter createPresenter() {
        return new SearchPresenter();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (mTextChangeSubscription != null && !mTextChangeSubscription.isDisposed()) {
            mTextChangeSubscription.dispose();
        }
        super.onDestroy();
    }
}
