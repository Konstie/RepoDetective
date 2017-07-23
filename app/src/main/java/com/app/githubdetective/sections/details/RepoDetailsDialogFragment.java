package com.app.githubdetective.sections.details;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.githubdetective.R;

import timber.log.Timber;

public class RepoDetailsDialogFragment extends DialogFragment {
    private static final String EXTRA_REPO_URL = "repo_url";

    public static RepoDetailsDialogFragment newInstance(final String repoUrl) {
        RepoDetailsDialogFragment dialogFragment = new RepoDetailsDialogFragment();

        Bundle args = new Bundle();
        args.putString(EXTRA_REPO_URL, repoUrl);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String repoUrl = getArguments().getString(EXTRA_REPO_URL);

        Timber.d("Using context: " + getContext());

        final WebView repoWebView = new WebView(getContext());
        repoWebView.getSettings().setJavaScriptEnabled(true);
        repoWebView.loadUrl(repoUrl);
        repoWebView.setWebViewClient(getWebViewClient(repoUrl));

        return new AlertDialog.Builder(getContext())
                .setView(repoWebView)
                .setNegativeButton(R.string.details_dialog_close, (dialog, which) -> dialog.dismiss())
                .create();
    }

    private WebViewClient getWebViewClient(final String url) {
        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(url);
                return true;
            }
        };
    }
}
