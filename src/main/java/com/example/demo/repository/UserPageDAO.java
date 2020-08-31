package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.entity.querydsl.QUser;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * 介绍了几种分页方式
 */

@Component
public class UserPageDAO {

    @Autowired
    UserDAO userDAO;
    @PersistenceContext
    EntityManager entityManager;

    /**
     * 基于querydsl的分页 - 推荐方式 (JPAQuery)
     *  优点：可维护性好，尤其是表结构有变动时，可通过工具一键同步更新Q类
     *  缺点：需要引入第三方的依赖和插件，初次使用时，验证配置的时间可能会较多
     */
    public Page<User> pageByJPAQuery(Pageable pageable, final User dto) {
        JPAQuery jpaQuery = new JPAQuery(entityManager);

        QUser qUser = QUser.user;
        jpaQuery.from(qUser);
        this.setCondition(dto,jpaQuery,qUser);
        jpaQuery.offset(pageable.getOffset());
        jpaQuery.limit(pageable.getPageSize());

        //先查询符合条件的总记录数，总记录数大于0才执行具体的查询
        long count = jpaQuery.fetchCount();
        List<User> list = new ArrayList<>();
        if(count > 0){
            list = jpaQuery.fetch();
        }

        return new PageImpl<>(list);
    }

    /**
     * 基于querydsl的分页 - 推荐方式 (JPAQueryFactory)
     *  优点：可维护性好，尤其是表结构有变动时，可通过工具一键同步更新Q类
     *  缺点：需要引入第三方的依赖和插件，初次使用时，验证配置的时间可能会较多
     */
    public void pageByJPAQueryFactory(){
        QUser qUser = QUser.user;
        JPAQueryFactory factory = new JPAQueryFactory(entityManager);
        //写法一
        JPAQuery<User> query = factory.selectFrom(qUser).orderBy(qUser.id.asc());
        long total = query.fetchCount();//hfetchCount的时候上面的orderBy不会被执行
        List<User> result = query.offset(2).limit(5).fetch();
        //写法二
        QueryResults<User> results = factory.selectFrom(qUser).orderBy(qUser.id.asc()).offset(2).limit(5).fetchResults();
        List<User> list = results.getResults();
    }

    /**
     * 设置查询条件 - querydsl方式
     */
    private void setCondition(User dto,JPAQuery jpaQuery,QUser qUser){
        if(dto == null){
            return;
        }

        if(dto.getName() != null && !"".equals(dto.getName())){
            //等于
            jpaQuery.where(qUser.name.eq(dto.getName()));
        }
        if(dto.getAge() != null){
            //大于等于
            jpaQuery.where(qUser.age.goe(dto.getAge()));
        }
        if(dto.getCreateTime() != null){
            //小于等于
            jpaQuery.where(qUser.createTime.loe(dto.getCreateTime()));
        }
        if(dto.getAddress() != null && !"".equals(dto.getAddress())){
            //模糊匹配
            jpaQuery.where(qUser.address.like("%"+dto.getAddress()));
        }
    }

    /**
     * 不带任何查询条件的分页 - 不推荐，因为很少有分页不带条件的
     *
     */
    public Page<User> pageAll(Pageable pageable) {
        return userDAO.findAll(pageable);
    }

    /**
     * 指定了固定查询条件的分页 - 不推荐，因为很少有分页的查询条件是固定的
     */
    public Page<User> pageByAddress(Pageable pageable, String address){
        return userDAO.findByAddress(address,pageable);
    }

    /**
     * 动态查询条件的分页 - Predicate方式
     * 1、前提：Repository类需要继承JpaSpecificationExecutor类，比如：
     *  public interface UserRepository extends JpaRepository<User,Long>,JpaSpecificationExecutor<User> {
     *      .........
     *  }
     *
     * 2、优缺点：
     *  优点：JPA自带的方式，不需要引入第三方库
     *  缺点：
     *      1) 要求Repository类必须多继承一个额外的接口-JpaSpecificationExecutor
     *      2) 查询条件里需要对实体属性名进行硬编码，可维护性不足，尤其是当表结构有变动时若Predicate实现没有同步更改，则不易发现
     */
    public Page<User> pageByPredicate(Pageable pageable, final User vo) {

        Page<User> page = userDAO.findAll(getSpCondition(vo), pageable);

        return page;
    }

    /**
     * 生成查询条件 - Predicate方式
     */
    private Specification<User> getSpCondition(User vo){
        Specification<User> sp = new Specification<User>() {
            /**
             * @param root root参数是我们用来对应实体的信息的。
             * @param query
             * @param cb criteriaBuilder可以帮助我们制作查询信息。
             * @return
             */
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 创建 Predicate
                Predicate predicate = cb.conjunction();
                // 组装条件
                if(vo.getAddress()!= null && !"".equals(vo.getAddress())){
                    predicate.getExpressions().add(cb.equal(root.get("address"), vo.getName()));
                }
                return predicate;
            }

        };
        return sp;
    }

    /**
     * 动态查询条件的分页 - Example方式
     * 优缺点：
     *  优点：JPA自带的方式，不需要引入第三方库，Repository类也不需要多继承某个额外的接口
     *  缺点：查询条件里需要对实体属性名进行硬编码，可维护性不足，尤其是当表结构有变动时若Example实现没有同步更改，则不易发现
     */
    public Page<User> pageByExample(Pageable pageable, final User vo) {

        Page<User> page = userDAO.findAll(getExCondition(vo), pageable);

        return page;
    }

    /**
     * 生成查询条件 - Example方式
     * @param vo
     * @return
     */
    private Example<User> getExCondition(User vo){
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //模糊查询
                .withIgnoreCase(true) //忽略大小写
                .withMatcher("address", ExampleMatcher.GenericPropertyMatchers.startsWith()); //地址采用“开始匹配”的方式查询

        //创建实例
        return Example.of(vo, matcher);
    }

}
