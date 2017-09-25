package com.example.liudong.archdemo.db;

import android.support.test.runner.AndroidJUnit4;

import com.example.liudong.archdemo.util.TestUtil;
import com.example.liudong.archdemo.vo.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.example.liudong.archdemo.util.LiveDataTestUtil.getValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class UserDaoTest extends DbTest {

    @Test
    public void insertAndLoad() throws InterruptedException {
        final User user = TestUtil.createUser("foo");
        db.userDao().insert(user);

        final User loaded = getValue(db.userDao().findByLogin(user.getLogin()));
        assertThat(loaded.getLogin(), is("foo"));

        final User replacement = TestUtil.createUser("foo2");
        db.userDao().insert(replacement);

        final User loadedReplacement = getValue(db.userDao().findByLogin("foo2"));
        assertThat(loadedReplacement.getLogin(), is("foo2"));
    }
}
