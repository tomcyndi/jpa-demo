package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 基本的JPA Repository 用法
 */
public interface UserDAO extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {

    List<User> findByAddress(String address);

    Page<User> findByAddress(String address, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "delete from User u where u.address = ?1")
    int remove(String address);



}
