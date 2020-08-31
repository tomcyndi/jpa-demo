package com.example.demo.repository;

import com.example.demo.BaseTest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

public class UserDAOTest extends BaseTest {

    @Autowired
    UserDAO userDAO;
    @Autowired
    UserDMLDAO userDMLDAO;
    @Autowired
    UserPageDAO userPageDAO;
    @Autowired
    UserService userService;
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 如果未显式设置主键id，则主键id属性需要配置生成策略，比如：
     *   @GeneratedValue(strategy = GenerationType.IDENTITY) 指主键id由数据生成
     */
    @Test
    void testSave() {
        for(int i=1 ; i<=10 ; i++){
            User user = new User();
            user.setName("敖丙-"+i);
            user.setAddress("深圳");
            user.setAge(i);
            userDAO.save(user);
        }
    }

    /**
     * 如果未显式设置主键id，则主键id属性需要配置生成策略，比如：
     *   @GeneratedValue(strategy = GenerationType.IDENTITY) 指主键id由数据生成
     */
    @Test
    void testSaveAll() {
        List<User> list = new ArrayList<>();
        for(int i=0 ; i<=3 ; i++){
            User user = new User();
            user.setName("易建联-"+i);
            user.setAddress("深圳");
            user.setAge(i);
            list.add(user);
        }
        userDAO.saveAll(list);
    }

    /**
     * persist的最佳实践：
     *  1、persist只会新增，不会更新，且不会select，与DB只有一次交互，适用于并发吞吐要求较高的场景
     *  2、实体的主键id属性不可配置@GeneratedValue(strategy = GenerationType.IDENTITY)注解
     *  3、要在事务内执行该方法，否则会报错
     *  4、不会返回所保存实体的主键id
     */
    @Test
    @Transactional
    void testPersist() {
        for(int i=1 ; i<=10 ; i++){
            User user = new User();
            user.setId(i*11L);
            user.setName("敖丙-"+i);
            user.setAddress("上海");
            entityManager.persist(user);
        }
    }

    @Test
    void testRemoveAll() {
        List<User> list = userDAO.findAll();
        System.out.println("------需要删除的记录数 = "+list.size());
        userDAO.deleteAll();
    }

    @Test
    void testRemoveInBatch() {
        List<User> list = userDAO.findByAddress("深圳");
        System.out.println("------需要删除的记录数 = "+list.size());
        userDAO.deleteInBatch(list);
    }

    @Test
    void testRemoveByAddress() {
        String address = "上海";
        List<User> list = userDAO.findByAddress(address);
        System.out.println("------需要删除的记录数 = "+list.size());
        int rows = userDAO.remove(address);
        System.out.println("------成功删除的记录数 = "+list.size());
        List<User> newList = userDAO.findByAddress(address);
        System.out.println("------当前剩余的记录数 = "+newList.size());
    }

    @Test
    void testFindByAddress() {
        List<User> list = userDAO.findByAddress("北京");
        System.out.println("------user size = "+list.size());
    }

    @Test
    void testPageByJPAQuery(){
        int pageNo = 1 ;
        int pageSize = 15 ;
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        String address = "上海";
        List list = userDAO.findByAddress(address);
        System.out.println("------totalCount:"+list.size());

        User vo = new User();
        vo.setAddress(address);
        Page<User> pageResult = userPageDAO.pageByJPAQuery(pageable,vo);
        System.out.println("------result size:"+pageResult.getTotalElements());
    }

    @Test
    void testModifyByQSDL(){
        User dto = new User();
        dto.setAddress("深圳");

        User user = new User();
        user.setName("阿里巴巴");
        long rows = userDMLDAO.modifyUser(user,dto);
        System.out.println("------更改记录数："+rows);
    }

    @Test
    void testRemoveByQSDL(){
        User dto = new User();
        dto.setAddress("深圳");
        long rows = userDMLDAO.removeUser(dto);
        System.out.println("------删除记录数："+rows);
    }

    @Test
    void testFindOne(){
        Long id = 10L;
        User user = userDAO.findById(id).get();
    }

}
