package com.example.springbatch.steps;

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.springbatch.entities.Person;
import com.example.springbatch.services.PersonService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemWriterStep implements Tasklet {
    
    @Autowired
    private PersonService personService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("-------------------> START of WRITER Step <-------------------");
                List<Person> personList = (List<Person>) chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("personList");

        personList.forEach( person -> {
            if(person != null){
                log.info(person.toString());
            }
        });

        personService.saveAll(personList);
        log.info("-------------------> END of WRITER Step <-------------------");
        return RepeatStatus.FINISHED;
    }
    
}
