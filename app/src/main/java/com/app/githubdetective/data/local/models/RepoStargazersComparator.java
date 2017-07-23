package com.app.githubdetective.data.local.models;

import java.util.Comparator;

public class RepoStargazersComparator implements Comparator<Repo> {
    @Override
    public int compare(Repo o1, Repo o2) {
        return o2.getStargazersCount() - o1.getStargazersCount();
    }
}
