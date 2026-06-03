package ro.ubbcluj.assignment.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ro.ubbcluj.assignment.model.ChatMessage;
import java.util.List;

public interface ChatRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findTop50ByOrderByTimestampAsc();
}
