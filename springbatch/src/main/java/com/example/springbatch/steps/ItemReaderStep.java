package com.example.springbatch.steps;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import com.example.springbatch.entities.Person;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ItemReaderStep implements Tasklet{

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("-------------> START of READER Step <----------");

        // Crear un objeto CSVReaderBuilderWithSeparator y especificar el separador como coma
        Reader reader = new FileReader(resourceLoader.getResource("classpath:files/destination/persons.csv").getFile());
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .build();
        CSVReader csvReader = new CSVReaderBuilder(reader)
                .withCSVParser(parser)
                .withSkipLines(1)   // Ignorar la primera línea como encabezado
                .build();

        // Leer cada línea del archivo CSV y convertirla a un objeto Persona
        List<Person> personList = new ArrayList<>();
        String[] linea;
        while ((linea = csvReader.readNext()) != null) {
            Person person = new Person();
            person.setName(linea[0]);
            person.setLastName(linea[1]);
            person.setAge(Integer.parseInt(linea[2]));
            personList.add(person);
        }

        // Cerrar el objeto CSVReader y el archivo
        csvReader.close();
        reader.close();

        chunkContext.getStepContext()
                    .getStepExecution()
                    .getJobExecution()
                    .getExecutionContext()
                    .put("personList", personList);

        log.info("-------------------> END of READER Step <-------------------");

        return RepeatStatus.FINISHED;
    }
    
}
