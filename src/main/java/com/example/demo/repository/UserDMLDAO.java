package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.entity.querydsl.QUser;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class UserDMLDAO {

    @PersistenceContext
    EntityManager entityManager;

    public void addUser(User user){

    }

    /**
     * 通过querydsl方式来更新数据
     *
     *  优点：只会update，即只与DB交互一次，比起save（先去select再update），只update无疑性能更高，
     *
     *  适用场景：
     *     1）update操作较为频繁或update数据范围较大时，尤其是在高并发高吞吐场景时，建议使用本方式 或者 直接写hql\native sql来更新
     *     2）update的条件不固定，即update的where条件较为灵活时
     */
    @Transactional
    public long modifyUser(User user,User dto){
        JPAQueryFactory factory = new JPAQueryFactory(entityManager);
        QUser qUser = QUser.user;
        if(user != null){
            boolean flag = false;
            JPAUpdateClause action = factory.update(qUser);

            if(user.getName() != null && !"".equals(user.getName())){
                action.set(qUser.name,user.getName());
                flag = true;
            }
            if(user.getAddress() != null && !"".equals(user.getAddress())){
                action.set(qUser.address,user.getAddress());
                flag = true;
            }

            if(flag){
                if(dto.getId() != null){
                    action.where(qUser.id.eq(dto.getId()));
                }
                if(dto.getName() != null && !"".equals(dto.getName())){
                    action.where(qUser.name.like("%"+dto.getName()));
                }
                if(dto.getAddress() != null && !"".equals(dto.getAddress())){
                    action.where(qUser.address.like("%"+dto.getAddress()));
                }
                return action.execute();
            }
        }

        return 0;
    }

    /**
     * 通过querydsl方式来删除数据
     *
     *  优点：只会delete，即只与DB交互一次，比起delete（先去select再delete），只delete无疑性能更高，
     *  但相比@Query注解里直接写hql或native sql，querydsl的delelte并没有多大的优势
     *
     *  适用场景：
     *     1）delete操作较为频繁或delete的数据范围较大时，尤其是在高并发高吞吐场景时，建议使用本方式 或者 直接写hql\native sql来删除
     *     2）delete的条件不固定，即delete的where条件较为灵活时
     */
    @Transactional
    public long removeUser(User dto){
        JPAQueryFactory factory = new JPAQueryFactory(entityManager);
        QUser qUser = QUser.user;
        if(dto != null){
            boolean flag = false;
            JPADeleteClause action = factory.delete(qUser);

            if(dto.getId() != null){
                action.where(qUser.id.eq(dto.getId()));
                flag = true;
            }
            if(dto.getName() != null && !"".equals(dto.getName())){
                action.where(qUser.name.like("%"+dto.getName()));
                flag = true;
            }
            if(dto.getAddress() != null && !"".equals(dto.getAddress())){
                action.where(qUser.address.like("%"+dto.getAddress()));
                flag = true;
            }

            if(flag){
                return action.execute();
            }
        }

        return 0;
    }



}
