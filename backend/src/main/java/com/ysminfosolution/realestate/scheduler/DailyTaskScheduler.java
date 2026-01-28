package com.ysminfosolution.realestate.scheduler;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ysminfosolution.realestate.model.FollowUp;
import com.ysminfosolution.realestate.model.Task;
import com.ysminfosolution.realestate.repository.FollowUpRepository;
import com.ysminfosolution.realestate.repository.TaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyTaskScheduler {

    private final FollowUpRepository followUpRepository;
    private final TaskRepository taskRepository;

    // Runs every night at 23:59 (you can adjust cron)
    // @Scheduled(cron = "0 */1 * * * *") // ^ For testing: runs every minute
    @Scheduled(cron = "0 00 01 * * *")
    @Transactional
    public void generateTasksForToday() {
        LocalDate today = LocalDate.now();
        log.info("Running daily task generation for date: {}", today);

        taskRepository.deleteAllInBatch(); // ✅ executes immediately
        taskRepository.flush(); // Ensure deletion is flushed before proceeding

        Set<FollowUp> dueFollowUps = followUpRepository.findByFollowUpNextDateAndIsDeletedFalse(today);

        Set<Task> tasks = new HashSet<>(dueFollowUps.stream()
                .map(f -> {
                    Task t = new Task();
                    t.setFollowUp(f);
                    return t;
                })
                .toList());

        taskRepository.saveAll(tasks);
        log.info("Generated {} new tasks for today", tasks.size());
    }

}
