package com.team15gijo.post.domain.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "p_hashtags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HashtagV2 {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "hashtag_id", updatable = false, nullable = false)
    private UUID hashtagId;

    @Column(name = "hashtag_name", nullable = false, length = 50)
    private String hashtagName;

    /**
     * Post 엔티티와의 다대다 관계.
     * 반대편(Post)에서는 @JoinTable을 통해 연결 테이블(p_post_hashtag_map)을 정의하고,
     * 여기서는 mappedBy를 통해 "hashtags" 필드와 매핑됨을 선언합니다.
     */
    @ManyToMany(mappedBy = "hashtags")
    @JsonIgnore
    private Set<PostV2> posts = new HashSet<>();
}
