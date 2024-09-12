package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findAllByItemId() {
        User user = new User(null, "mail@du.tu", "name");
        Item item = new Item(null, "item", "item descr", true, user, null);
        Comment comment = new Comment(null, "text", item, user, LocalDateTime.now());
        em.persist(user);
        em.persist(item);
        em.persist(comment);
        em.flush();

        List<Comment> comments = commentRepository.findAllByItem_IdOrderByCreated(item.getId());
        assertThat(comments.size(), equalTo(1));
        assertThat(comments.getFirst(), equalTo(comment));
    }
}
