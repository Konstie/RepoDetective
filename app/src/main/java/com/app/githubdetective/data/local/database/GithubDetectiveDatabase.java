package com.app.githubdetective.data.local.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.app.githubdetective.data.local.models.Repo;

@Database(entities = {Repo.class}, version = 1, exportSchema = false)
public abstract class GithubDetectiveDatabase extends RoomDatabase {
    abstract public RepositoriesDao repositoriesDao();
}
