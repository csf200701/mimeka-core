package com.igd.mimeka.io;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * ����֧���࣬���������ȷ��͵�
 * @param <T>
 */
public class TypeReference<T> {
    //ͨ��new��ʱ��ָ���ķ���
    private Type type;
    //�ؼ�����,�����õ������
    private String typeName;
    //�ӷ���֧��
    TypeReference<T> subTypeReference;
    protected TypeReference() {
        Type superClass = getClass().getGenericSuperclass();
        //��������
        if (!superClass.getTypeName().endsWith("TypeReference")
            && !superClass.getTypeName().equals("java.lang.Object")) {
            type = ((ParameterizedType)superClass).getActualTypeArguments()[0];
            String name = type.getTypeName();
            typeName = name;
            //�����ǳ����˷����ڲ����з��͵����
            if(name.contains("<")){
                typeName = name.substring(0,name.indexOf("<"));
                String subTypeClassName = name.substring(name.indexOf("<")+1,name.lastIndexOf(">"));
                subTypeReference = new TypeReference<>();
                subTypeReference.setTypeName(subTypeClassName);
            }
        }
    }
 
    /**
     * �ֶ�set����
     * @param typeName
     */
    public void setTypeName(String typeName) {
        //�����ǳ����˷����ڲ����з��͵����
        if(typeName.contains("<")){
            this.typeName = typeName.substring(0,typeName.indexOf("<")).trim();
            String subTypeClassName = typeName.substring(typeName.indexOf("<")+1,typeName.lastIndexOf(">"));
            subTypeReference = new TypeReference<>();
            subTypeReference.setTypeName(subTypeClassName);
        }else if(typeName.contains(",")){
            //�����ǳ����˶������������� Map<String ,User> ����ȡUser
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