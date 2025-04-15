package com.team15gijo.post.domain.model.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "p_hashtags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hashtag {

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
    private Set<Post> posts = new HashSet<>();
}
