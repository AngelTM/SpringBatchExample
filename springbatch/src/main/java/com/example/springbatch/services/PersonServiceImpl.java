package com.example.springbatch.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springbatch.entities.Person;
import com.example.springbatch.persistence.IpersonDao;

@Service
public class PersonServiceImpl implements PersonService{

    @Autowired
    private IpersonDao ipersonDao;

    @Override
    @Transactional
    public void saveAll(List<Person> personList) {
        ipersonDao.saveAll(personList);
    }
    
}
