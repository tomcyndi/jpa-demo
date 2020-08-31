package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserDAO;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(value = "/users")
public class UserController
{
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/add/{name}/{address}/{age}")
    public User addUser(@PathVariable String name, @PathVariable String address, @PathVariable Integer age)
    {
        User user = new User();
        user.setName(name);
        user.setAddress(address);
        user.setAge(age);
        userDAO.save(user);
        return user;
    }

    @RequestMapping(value = "/")
    public List<User> getUserList()
    {
        System.out.println("------收到了一个请求");
        return userService.findAll();
    }

    @RequestMapping(value = "/{id}")
    public User getUser(@PathVariable int id)
    {
        Optional<User> user = userService.findOne(id);
        return user.get();
    }

}
