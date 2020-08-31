package com.example.demo.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtils {

    public static List<Field> getFieldList(Class clazz){
        List<Field> fieldList = new ArrayList<>();
        Class temp= clazz ;
        //递归获取该类以及其所有父类的字段
        while(temp != null){
            //若当前类为Object类，则说明已递归完毕
            if(temp == Object.class){
                break;
            }

            fieldList.addAll(Arrays.asList(temp.getDeclaredFields()));

            //得到父类，然后赋给自己
            temp = temp.getSuperclass();
        }

        return fieldList;
    }
}
