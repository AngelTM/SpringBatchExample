package com.example.springbatch.steps;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.format.annotation.DateTimeFormat;

import com.example.springbatch.entities.Person;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemProcessorStep implements Tasklet{

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("-------------------> START of PROCESSOR Step <-------------------");
        List<Person> personList = (List<Person>) chunkContext.getStepContext()
                                .getStepExecution()
                                .getJobExecution()
                                .getExecutionContext()
                                .get("personList");
        List<Person> personFinalList = personList.stream().map(person->{

            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/mm/yyyy HH:mm:ss");
            person.setInsertionDate(format.format(LocalDateTime.now()));
            return person;
        }).collect(Collectors.toList());

        chunkContext.getStepContext()
                    .getStepExecution()
                    .getJobExecution()
                    .getExecutionContext()
                    .put("personList", personFinalList);

        
        log.info("-------------------> END of PROCESSOR Step <-------------------");
        return RepeatStatus.FINISHED;
    }
    
}
