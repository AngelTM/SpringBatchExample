package com.example.springbatch.services;

import java.util.List;

import com.example.springbatch.entities.Person;

public interface PersonService {
    public void saveAll(List<Person> personList );
}
