package com.app.githubdetective.injection;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.res.Resources;

import com.app.githubdetective.RepoDetectiveApp;
import com.app.githubdetective.R;
import com.app.githubdetective.data.ReposRepository;
import com.app.githubdetective.data.local.database.GithubDetectiveDatabase;
import com.app.githubdetective.data.local.database.RepositoriesDao;
import com.app.githubdetective.data.remote.ReposRemoteDataSource;
import com.app.githubdetective.data.remote.api.SearchReposService;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import timber.log.Timber;

@Module
public class AppModule {
    private static final int CONNECTION_TIMEOUT = 60;

    private static final String DB_NAME = "repodetective.db";
    private static final String GITHUB_API_BASE_URL = "http://api.github.com/";

    private RepoDetectiveApp mApplication;

    public AppModule(RepoDetectiveApp application) {
        mApplication = application;
    }

    @Inject
    @Provides
    @Singleton
    Context provideAppContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    Resources provideResources() {
        return mApplication.getResources();
    }

    @Provides
    @Singleton
    Interceptor provideInterceptor() {
        return chain -> {
            Request request = chain.request();

            long startTime = System.currentTimeMillis();
            Timber.i(String.format("Sending request by url %s with params %s", request.url(), request.body()));

            Response response = chain.proceed(request);
            long endTime = System.currentTimeMillis();

            Timber.d(String.format(Locale.US, "Retrieved response for %s in %d ms", response.request().url(), endTime - startTime));

            return response;
        };
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Interceptor interceptor) {
        return new OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(GITHUB_API_BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
    }

    @Provides
    @Singleton
    SearchReposService provideSearchReposService(Retrofit retrofit) {
        return retrofit.create(SearchReposService.class);
    }

    @Provides
    @Singleton
    GithubDetectiveDatabase provideDatabase(Context appContext) {
        return Room.databaseBuilder(appContext, GithubDetectiveDatabase.class, DB_NAME)
                .build();
    }

    @Provides
    @Singleton
    RepositoriesDao provideRepositoriesDao(GithubDetectiveDatabase database) {
        return database.repositoriesDao();
    }

    @Provides
    @Singleton
    ReposRemoteDataSource provideReposRemoteDataSource(SearchReposService reposService, Resources resources) {
        final String clientId = resources.getString(R.string.github_client_id);
        final String clientSecret = resources.getString(R.string.github_client_secret);
        return new ReposRemoteDataSource(reposService, clientId, clientSecret);
    }

    @Provides
    @Singleton
    ReposRepository provideReposRepository(GithubDetectiveDatabase database, RepositoriesDao repositoriesDao, ReposRemoteDataSource reposRemoteDataSource) {
        return new ReposRepository(database, repositoriesDao, reposRemoteDataSource);
    }
}
