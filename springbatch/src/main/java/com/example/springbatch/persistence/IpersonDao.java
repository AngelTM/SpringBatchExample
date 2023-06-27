package com.example.springbatch.persistence;

import org.springframework.data.repository.CrudRepository;

import com.example.springbatch.entities.Person;

public interface IpersonDao extends CrudRepository<Person,Long>{
    
}
