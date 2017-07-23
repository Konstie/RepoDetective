package com.app.githubdetective.sections.search.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.githubdetective.R;
import com.app.githubdetective.data.local.models.Repo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RepositoriesAdapter extends RecyclerView.Adapter<RepositoriesAdapter.RepoHolder> {
    private Context mContext;
    private List<Repo> mRepos;
    private OnRepoItemClickListener mClickListener;

    public RepositoriesAdapter(Context context, List<Repo> repos, OnRepoItemClickListener clickListener) {
        mContext = context;
        mRepos = repos;
        mClickListener = clickListener;
    }

    @Override
    public RepoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_repo, parent, false);
        return new RepoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RepoHolder holder, int position) {
        Repo repository = mRepos.get(position);
        holder.titleTextView.setText(repository.getFullName());
        if (!TextUtils.isEmpty(repository.getLanguage())) {
            holder.languageTextView.setText(repository.getLanguage());
        }
        holder.stargazersTextView.setText(String.valueOf(repository.getStargazersCount()));
        holder.forksCountTextView.setText(String.valueOf(repository.getForksCount()));
        holder.watchersCountTextView.setText(String.valueOf(repository.getWatchersCount()));
        holder.rootLayout.setOnClickListener(view -> {
            if (mClickListener != null && !TextUtils.isEmpty(repository.getHtmlUrl())) {
                mClickListener.onRepoClicked(repository.getFullName(), repository.getHtmlUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRepos != null ? mRepos.size() : 0;
    }

    public void setRepos(List<Repo> newRepos) {
        mRepos = newRepos;
        notifyDataSetChanged();
    }

    public void clear() {
        mRepos.clear();
        notifyDataSetChanged();
    }

    static class RepoHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.root_layout) LinearLayout rootLayout;
        @BindView(R.id.repo_title_text_view) TextView titleTextView;
        @BindView(R.id.programming_language_text_view) TextView languageTextView;
        @BindView(R.id.stargazers_count_text_view) TextView stargazersTextView;
        @BindView(R.id.forks_count_text_view) TextView forksCountTextView;
        @BindView(R.id.watchers_count_text_view) TextView watchersCountTextView;

        RepoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnRepoItemClickListener {
        void onRepoClicked(String repoTitle, String githubRepoUrl);
    }
}
