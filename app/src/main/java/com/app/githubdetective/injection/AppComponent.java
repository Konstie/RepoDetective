package com.app.githubdetective.injection;

import com.app.githubdetective.sections.search.SearchPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(SearchPresenter presenter);
}
