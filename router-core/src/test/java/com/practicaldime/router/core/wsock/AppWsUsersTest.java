package com.practicaldime.router.core.wsock;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class AppWsUsersTest {

    public List<AppWsUsers.UserId> users = IntStream.range(1, 11)
            .mapToObj(i -> new AppWsUsers.UserId("name" + i, "topic" + i, "context" + i, null))
            .collect(Collectors.toList());

    @Test
    public void testAddingUsers() {
        AppWsUsers.SearchableTree tree = new AppWsUsers.SearchableTree();
        users.forEach(user -> tree.add(user));
        assertEquals("Expecting 10 entries", 10, tree.size());
    }

    @Test
    public void testSearchUser() {
        AppWsUsers.SearchableTree tree = new AppWsUsers.SearchableTree();
        users.forEach(user -> tree.add(user));
        assertEquals("Expecting 10 entries", 10, tree.size());

        AppWsUsers.UserId byName = tree.search(new AppWsUsers.Search("name" + 8, "topic" + 8, "context" + 8));
        assertEquals("Expecting 'name8'", "name8", byName.name);

        AppWsUsers.UserId byTopic = tree.search(new AppWsUsers.Search("name" + 7, "topic" + 7, "context" + 7));
        assertEquals("Expecting 'topic7'", "topic7", byTopic.topic);
    }
}
