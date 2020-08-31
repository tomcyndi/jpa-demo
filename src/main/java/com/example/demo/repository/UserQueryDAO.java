package com.example.demo.repository;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.entity.querydsl.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * 介绍更多关于querydsl灵活多变的查询方式
 * 更多可参考：https://www.jianshu.com/p/69dcb1b85bbb
 */
@Component
public class UserQueryDAO {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * 常见的查询方式
     */
    public void query(){
        QUser qUser = QUser.user;
        JPAQueryFactory factory = new JPAQueryFactory(entityManager);

        //查询字段-select()
        List<String> nameList = factory.select(qUser.name).from(qUser).fetch();

        //查询实体-selectFrom()
        List<User> userList = factory.selectFrom(qUser).fetch();

        //排序
        List<User> orderList = factory.selectFrom(qUser).orderBy(qUser.name.asc()).fetch();

        //查询并将结果封装至dto中
        List<UserDTO> dtoList = factory.select(Projections.constructor(UserDTO.class,qUser.name,qUser.address)).from(qUser).fetch();

        //去重查询-selectDistinct()
        List<String> distinctNameList = factory.selectDistinct(qUser.name).from(qUser).fetch();

        //获取首个查询结果-fetchFirst()
        User firstUser = factory.selectFrom(qUser).fetchFirst();

        //获取唯一查询结果-fetchOne()
        //当fetchOne()根据查询条件从数据库中查询到多条匹配数据时，会抛`NonUniqueResultException`。
        User anotherFirstMember = factory.selectFrom(qUser).fetchOne();

    }

    /**
     * 子查询
     */
    public void subQuery(){
        QUser qUser = QUser.user;
        JPAQueryFactory factory = new JPAQueryFactory(entityManager);

        //查询条件示例
        List<User> memberConditionList = factory.selectFrom(qUser)
            .where(
                    //like示例
                    qUser.name.like('%'+"Jack"+'%')
                    //contain示例
                    .and(qUser.address.contains("厦门"))
                    //between示例
                    .and(qUser.age.between(20, 30))
                    )
            .fetch();

        //如果觉得上面的写法不够优雅，可以考虑下面这种方式
        BooleanBuilder builder = new BooleanBuilder();
        //like
        builder.and(qUser.name.like('%'+"Jack"+'%'));
        //contain
        builder.and(qUser.address.contains("厦门"));
        //between
        builder.and(qUser.age.between(20, 30));
        List<User> list = factory.selectFrom(qUser).where(builder).fetch();

        //子查询示例
        List<User> subList = factory.selectFrom(qUser).where(qUser.address.in(JPAExpressions.select(qUser.address).from(qUser))).fetch();
    }

    /**
     * 使用数据库的聚合函数
     */
    public void dbFuncQuery(){

        QUser qUser = QUser.user;
        JPAQueryFactory factory = new JPAQueryFactory(entityManager);

        //聚合函数-avg()
        Double averageAge = factory.select(qUser.age.avg()).from(qUser).fetchOne();

        //聚合函数-concat()
        String concat = factory.select(qUser.name.concat(qUser.address)).from(qUser).fetchOne();

        //聚合函数-date_format()
        String date = factory.select(Expressions.stringTemplate("DATE_FORMAT({0},'%Y-%m-%d')", qUser.createTime)).from(qUser).fetchOne();
    }

    /**
     * 多表查询
     */
    public void joinTableQuery(){

        QUser qUser = QUser.user;
        JPAQueryFactory factory = new JPAQueryFactory(entityManager);

        //以左关联为例-left join
        List<User> leftJoinList = factory.selectFrom(qUser).leftJoin(qUser).where(qUser.address.eq("0721")).fetch();
    }

}
