package com.app.githubdetective.data.local.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.app.githubdetective.data.local.models.Repo;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface RepositoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRepos(List<Repo> repos);

    @Query("SELECT * FROM repositories WHERE keyword LIKE :keyword ORDER BY stargazers_count DESC")
    Maybe<List<Repo>> loadReposByKeyword(String keyword);
}
