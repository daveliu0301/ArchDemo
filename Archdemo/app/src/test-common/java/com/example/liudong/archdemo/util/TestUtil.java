package com.example.liudong.archdemo.util;

import com.example.liudong.archdemo.vo.Contributor;
import com.example.liudong.archdemo.vo.Owner;
import com.example.liudong.archdemo.vo.Repo;
import com.example.liudong.archdemo.vo.User;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    public static User createUser(String login) {
        return new User(login, null,
                login + " name", null, null, null);
    }

    public static List<Repo> createRepos(int count, String owner, String name,
                                         String description) {
        List<Repo> repos = new ArrayList<>();
        for(int i = 0; i < count; i ++) {
            repos.add(createRepo(owner + i, name + i, description + i));
        }
        return repos;
    }

    public static Repo createRepo(String owner, String name, String description) {
        return createRepo(Repo.Companion.getUNKNOWN_ID(), owner, name, description);
    }

    public static Repo createRepo(int id, String owner, String name, String description) {
        return new Repo(id, name, owner + "/" + name,
                description, new Owner(owner, null), 3);
    }

    public static Contributor createContributor(Repo repo, String login, int contributions) {
        Contributor contributor = new Contributor(login, contributions, null);
        contributor.setRepoName(repo.getName());
        contributor.setRepoOwner(repo.getOwner().getLogin());
        return contributor;
    }
}
