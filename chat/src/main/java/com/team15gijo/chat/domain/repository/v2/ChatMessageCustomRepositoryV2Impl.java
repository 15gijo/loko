package com.team15gijo.chat.domain.repository.v2;

import static com.team15gijo.chat.infrastructure.config.RegexUtils.escapeRegex;

import com.team15gijo.chat.domain.model.v2.ChatMessageDocumentV2;
import com.team15gijo.chat.domain.model.v2.QChatMessageDocumentV2;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Slf4j
@RequiredArgsConstructor
public class ChatMessageCustomRepositoryV2Impl implements ChatMessageCustomRepositoryV2 {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<ChatMessageDocumentV2> searchMessages(
        UUID chatRoomId, LocalDateTime sentAt, String messageContent, Pageable pageable) {

        QChatMessageDocumentV2 message = QChatMessageDocumentV2.chatMessageDocumentV2;

        Criteria baseCriteria = new Criteria()
            .andOperator(
                Criteria.where(message.chatRoomId.getMetadata().getName()).is(chatRoomId),
                Criteria.where(message.deletedAt.getMetadata().getName()).is(null)
            );

        // or 조건
        List<Criteria> orConditions = new ArrayList<>();

        // sentAt 기본값이 false 면 모든 날짜로 검색
        if(sentAt != null) {
            log.info(">> sentAt={}", sentAt);
              orConditions.add(
                // ChatMessage 도큐먼트 필드 내 sentAt 필드 추출(해당 필드의 실제 데이터베이스 이름)
                Criteria.where(message.sentAt.getMetadata().getName())
                    .gte(sentAt)
            );
        }

        // messageContent 가 기본값이 아니면 검색 조건 추가
        if (messageContent != null && !messageContent.isEmpty()) {
            log.info(">> messageContent={}", messageContent);
            /*
             * 정규 표현식의 모든 메타 문자를 자동으로 이스케이프 처리
             * "^" :  URL 인코딩 시 403 Bad Request 발생으로 -> "%5E" 입력하면 "^"로 검색 처리
             * "+" : URL 인코딩 시 " "(공백 문자) 전달되어 메시지 전체 조회됨 -> "%2B" 입력하면 "+"로 검색 처리
             */
            String regexPattern = escapeRegex(messageContent);
            log.info(">> regexPattern={}", regexPattern);

            orConditions.add(
                // regex(messageContent) : 검색할 정규 표현식 패턴
                // "i" 옵션은 정규 표현식 검색 시 대소문자를 구분하지 않도록 지정
                Criteria.where(message.messageContent.getMetadata().getName()).regex(regexPattern, "i")
            );
        }
        log.info(">> orConditions={}", orConditions);

        // 최종 조건 : baseCriteria AND (sentAt OR messageContent)
        Criteria finalCriteria;

        if(orConditions.isEmpty()) {
            finalCriteria = baseCriteria;
        } else {
            finalCriteria = new Criteria().andOperator(
                baseCriteria,
                // 주어진 orConditions Criteria 객체들을 $or 연산자를 사용해 결합
                // new Criteria[0] : Criteria 타입의 배열로 변환
                new Criteria().orOperator(orConditions.toArray(new Criteria[0]))
            );
        }

        Query query = new Query(finalCriteria).with(pageable);
        List<ChatMessageDocumentV2> content = mongoTemplate.find(query, ChatMessageDocumentV2.class);
        log.info(">> content={}", content);

        Query countQuery = new Query(finalCriteria);
        long totalCount = mongoTemplate.count(countQuery, ChatMessageDocumentV2.class);
        log.info(">> totalCount={}", totalCount);

        return new PageImpl<>(content, pageable, totalCount);
    }
}
