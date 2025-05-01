package com.team15gijo.user.infrastructure.scheduler.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FollowCountEventQueue {

    private final Queue<FollowCountDeltaDto> eventQueue = new ConcurrentLinkedQueue<>();

    public void push(FollowCountDeltaDto delta) {
        eventQueue.offer(delta);
    }

    public List<FollowCountDeltaDto> drainAll() {
        List<FollowCountDeltaDto> drained = new ArrayList<>();
        FollowCountDeltaDto event;
        while ((event = eventQueue.poll()) != null) {
            drained.add(event);
        }
        return drained;
    }

    public boolean isEmpty() {
        return eventQueue.isEmpty();
    }
}
