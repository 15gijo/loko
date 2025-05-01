package com.team15gijo.follow.infrastructure.redis.lock;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockExecutor {

    private final RedissonClient redissonClient;

    private static final long LOCK_WAIT_TIME = 200L;       // 최대 대기시간 (ms)
    private static final long LOCK_LEASE_TIME = 1000L;      // 락 점유시간 (ms)

    public Optional<Long> executeWithLock(
            String lockKey,
            Supplier<Optional<Long>> cacheGetter,
            Supplier<Long> dbFallback
    ) {
        try {
            log.info("[lock] 실행 시작 - key={}", lockKey);
            Optional<Long> cached = cacheGetter.get();
            //hit
            if (cached.isPresent()) {
                log.info("[lock] Redis HIT - key={}, value={}", lockKey, cached.get());
                return cached;
            }

            RLock lock = redissonClient.getLock(lockKey);
            if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.MILLISECONDS)) {
                try {
                    log.info("[lock] Redis miss 락 획득 성공 - key={}", lockKey);
                    Optional<Long> afterLockCheck = cacheGetter.get();
                    //hit double check - 락 얻고 나서
                    if (afterLockCheck.isPresent()) {
                        log.info("[lock] Redis HIT (더블체크) - key={}, value={}", lockKey, afterLockCheck.get());
                        return afterLockCheck;
                    }

                    Long fallback = dbFallback.get();
                    log.info("[lock] DB fallback 조회 - key={}, value={}", lockKey, fallback);
                    return Optional.ofNullable(fallback);
                } finally {
                    log.info("[lock] 락 해제 - key={}", lockKey);
                    lock.unlock();
                }
            } else {
                log.warn("[Redisson] 락 획득 실패: key={}", lockKey);
            }
        } catch (Exception e) {
            log.error("[Redisson] 락 처리 중 예외 발생 - key={}", lockKey, e);
        }
        return Optional.empty();
    }

}
