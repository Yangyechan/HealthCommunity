package com.example.team18project.category.rental.chat.jpa;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    List<ChatMessageEntity> findTop5ByRoomIdOrderByIdDesc(Long id);

    List<ChatMessageEntity> findByRoomId(Long roomId);
}
