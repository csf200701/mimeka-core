package com.igd.mimeka.io;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;


@SuppressWarnings("restriction")
public class JsonReader {
	
	//基础数据类型
    private static final Set<String> BASE_CLASS_TYPE = new HashSet<>(Arrays.asList("String", "Integer", "Double", "Long", "Boolean"));
    //集合框架接口
    private static final Set<String> COLLECT_CLASS_TYPE = new HashSet<>(Arrays.asList("java.util.List", "java.util.Map"));
    //来自jdk8的ScriptEngine，js脚本处理引擎
    private static final ScriptEngine Engine = new ScriptEngineManager().getEngineByName("nashorn");
 
 
 
 
    public static <T> T resolver(String jsonStr, TypeReference<T> typeReference)
    											throws ScriptException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        jsonStr = jsonStr.replace("\n", "").replace("\t", "");
        Object obj = Engine.eval("JSON.parse('" + jsonStr + "')");
        ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror)obj;
        return parse(scriptObjectMirror, typeReference);
    }
 
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> T parse(ScriptObjectMirror scriptObjectMirror, TypeReference<T> typeReference)
        throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class classZz = Class.forName(typeReference.getTypeName());
        //如果泛型继承自集合，就走集合处理方式
        for (Class c : classZz.getInterfaces()) {
            if (COLLECT_CLASS_TYPE.contains(c.getTypeName())) {
                return (T)collectionParse(scriptObjectMirror,typeReference);
            }
        }
        //有可能泛型直接就写接口，走集合处理方式
        if (COLLECT_CLASS_TYPE.contains(typeReference.getTypeName())) {
            return (T)collectionParse(scriptObjectMirror,typeReference);
        }
        //反射获取一个对象
        Object object = classZz.newInstance();
        //遍历读取到的数据
        for (String key : scriptObjectMirror.keySet()) {
            Object value = scriptObjectMirror.get(key);
            //基础数据类型通过反射直接set
            if (BASE_CLASS_TYPE.contains(value.getClass().getSimpleName())) {
                setValue(object, key, value);
            } else if ("ScriptObjectMirror".contains(value.getClass().getSimpleName())) {
                //深度属性
                Class classZz1 = getValueClass(object, key);
                if (classZz1 != null) {
                    TypeReference typeReference1 = new TypeReference(){};
                    typeReference1.setTypeName(classZz1.getTypeName());
                    //递归，属性可能不是基础数据类型
                    setValue(object, key, parse((ScriptObjectMirror)value, typeReference1));
                }
            }else{
                //基础数据类型不知道有没有写全，反正除了ScriptObjectMirror对象，直接塞给他
                setValue(object, key, value);
            }
        }
        return (T)object;
    }
 
    /**
     * 获取object中filedName属性的类，不存在属性filedName则返回null
     * @param object
     * @param filedName
     * @return
     */
    private static Class<?> getValueClass(Object object, String filedName) {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(filedName)) {
                return field.getType();
            }
        }
        return null;
    }
 
    /**
     * 将value赋值给obj的filedName属性
     * @param object
     * @param filedName
     * @param value
     */
    private static void setValue(Object object, String filedName, Object value) {
        String methodName = "set" + filedName.substring(0, 1).toUpperCase() + filedName.substring(1);
        try {
            Method method = object.getClass().getDeclaredMethod(methodName, value.getClass());
            method.invoke(object, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
 
    /**
     * 处理map和list这种直接的数据结构
     * @param scriptObjectMirror
     * @return
     */
    @SuppressWarnings("rawtypes")
	private static Object collectionParse(ScriptObjectMirror scriptObjectMirror ,TypeReference<?> typeReference)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        //在子泛型的时候可能会出现null，这个时候用默认集合逻辑
        if(typeReference != null){
            Class<?> classZz = Class.forName(typeReference.getTypeName());
            //如果泛型进来和集合没关系，就走普通类型
            boolean temp = true;
            for (Class<?> c : classZz.getInterfaces()) {
                if (COLLECT_CLASS_TYPE.contains(c.getTypeName())) {
                    temp = false;
                    break;
                }
            }
            if(COLLECT_CLASS_TYPE.contains(typeReference.getTypeName())){
                temp = false;
            }
            if (temp) {
                //如果泛型的东西不是集合相关，那么就做普通对象处理
                return parse(scriptObjectMirror, typeReference);
            }
        }else{
            //给个空的，避免太深了遇到麻烦
            typeReference = new TypeReference(){};
        }
        if (scriptObjectMirror.isArray()) {
            List<Object> list = new ArrayList<>(scriptObjectMirror.size());
            for (int i = 0; i < scriptObjectMirror.size(); i++) {
                Object object = scriptObjectMirror.getSlot(i);
                if ("ScriptObjectMirror".contains(object.getClass().getSimpleName())) {
                	ScriptObjectMirror scriptObjectMirror1 = (ScriptObjectMirror)object;
                	//递归，集合里面的不知道是什么类型。如果泛型有给类型的话，用泛型的，没有的话就是null
                    list.add(collectionParse(scriptObjectMirror1,typeReference.getSubTypeReference()));
                } else {
                	//递归，集合里面的不知道是什么类型。如果泛型有给类型的话，用泛型的，没有的话就是null
                    list.add(object);
                }
                
            }
            return list;
        } else {
            //设置大小为 数据长度*1.25，避免因为扩容而浪费时间，map需要1.25，忘了源码里面写的啥了，他写的0.75
            Double size = scriptObjectMirror.size() * 1.25;
            int capacity = size.intValue() + 1;
            Map<String, Object> map = new HashMap<>(capacity);
            for (String key : scriptObjectMirror.keySet()) {
                Object object = scriptObjectMirror.get(key);
                if (BASE_CLASS_TYPE.contains(object.getClass().getSimpleName())) {
                    //如果是基础数据类型就直接塞给他
                    map.put(key, object);
                } else if ("ScriptObjectMirror".contains(object.getClass().getSimpleName())) {
                    //不是基础数据类型，递归处理
                    ScriptObjectMirror scriptObjectMirror1 = (ScriptObjectMirror)object;
                    //递归，集合里面的不知道是什么类型。如果泛型有给类型的话，用泛型的，没有的话就是null
                    map.put(key, collectionParse(scriptObjectMirror1,typeReference.getSubTypeReference()));
                }else{
                    //基础数据类型不知道有没有写全，反正除了ScriptObjectMirror对象，全部直接塞给他
                    map.put(key, object);
                }
            }
            return map;
        }
    }

}
