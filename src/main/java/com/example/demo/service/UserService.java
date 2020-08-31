package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * 本示例主要介绍了常用的CRUD操作
 */
@Service
public class UserService {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserDAO userDAO;

    public List<User> findAll()
    {
        return userDAO.findAll();
    }

    public Optional<User> findOne(long id)
    {
        return userDAO.findById(id);
    }

}
