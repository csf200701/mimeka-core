package com.igd.mimeka.io;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 类型支持类，用来存放深度泛型的
 * @param <T>
 */
public class TypeReference<T> {
    //通过new的时候指定的泛型
    private Type type;
    //关键参数,反射用的是这个
    private String typeName;
    //子泛型支持
    TypeReference<T> subTypeReference;
    protected TypeReference() {
        Type superClass = getClass().getGenericSuperclass();
        //解析泛型
        if (!superClass.getTypeName().endsWith("TypeReference")
            && !superClass.getTypeName().equals("java.lang.Object")) {
            type = ((ParameterizedType)superClass).getActualTypeArguments()[0];
            String name = type.getTypeName();
            typeName = name;
            //这里是出现了泛型内部还有泛型的情况
            if(name.contains("<")){
                typeName = name.substring(0,name.indexOf("<"));
                String subTypeClassName = name.substring(name.indexOf("<")+1,name.lastIndexOf(">"));
                subTypeReference = new TypeReference<>();
                subTypeReference.setTypeName(subTypeClassName);
            }
        }
    }
 
    /**
     * 手动set类型
     * @param typeName
     */
    public void setTypeName(String typeName) {
        //这里是出现了泛型内部还有泛型的情况
        if(typeName.contains("<")){
            this.typeName = typeName.substring(0,typeName.indexOf("<")).trim();
            String subTypeClassName = typeName.substring(typeName.indexOf("<")+1,typeName.lastIndexOf(">"));
            subTypeReference = new TypeReference<>();
            subTypeReference.setTypeName(subTypeClassName);
        }else if(typeName.contains(",")){
            //这里是出现了多个泛型情况，如 Map<String ,User> 这里取User
            typeName = typeName.substring(typeName.indexOf(",")+1).trim();
            this.typeName = typeName;
        }else{
            this.typeName = typeName.trim();
        }
    }
 
    public String getTypeName() {
        return typeName;
    }
    public TypeReference<T> getSubTypeReference() {
        return subTypeReference;
    }
    public Type getType() {
        return type;
    }
 
    @Override
    public String toString() {
        return "TypeReference{" +
            "type=" + type +
            ", typeName='" + typeName + '\'' +
            ", subTypeReference=" + subTypeReference +
            '}';
    }
}