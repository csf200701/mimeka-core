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
	
	//������������
    private static final Set<String> BASE_CLASS_TYPE = new HashSet<>(Arrays.asList("String", "Integer", "Double", "Long", "Boolean"));
    //���Ͽ�ܽӿ�
    private static final Set<String> COLLECT_CLASS_TYPE = new HashSet<>(Arrays.asList("java.util.List", "java.util.Map"));
    //����jdk8��ScriptEngine��js�ű���������
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
        //������ͼ̳��Լ��ϣ����߼��ϴ���ʽ
        for (Class c : classZz.getInterfaces()) {
            if (COLLECT_CLASS_TYPE.contains(c.getTypeName())) {
                return (T)collectionParse(scriptObjectMirror,typeReference);
            }
        }
        //�п��ܷ���ֱ�Ӿ�д�ӿڣ��߼��ϴ���ʽ
        if (COLLECT_CLASS_TYPE.contains(typeReference.getTypeName())) {
            return (T)collectionParse(scriptObjectMirror,typeReference);
        }
        //�����ȡһ������
        Object object = classZz.newInstance();
        //������ȡ��������
        for (String key : scriptObjectMirror.keySet()) {
            Object value = scriptObjectMirror.get(key);
            //������������ͨ������ֱ��set
            if (BASE_CLASS_TYPE.contains(value.getClass().getSimpleName())) {
                setValue(object, key, value);
            } else if ("ScriptObjectMirror".contains(value.getClass().getSimpleName())) {
                //�������
                Class classZz1 = getValueClass(object, key);
                if (classZz1 != null) {
                    TypeReference typeReference1 = new TypeReference(){};
                    typeReference1.setTypeName(classZz1.getTypeName());
                    //�ݹ飬���Կ��ܲ��ǻ�����������
                    setValue(object, key, parse((ScriptObjectMirror)value, typeReference1));
                }
            }else{
                //�����������Ͳ�֪����û��дȫ����������ScriptObjectMirror����ֱ��������
                setValue(object, key, value);
            }
        }
        return (T)object;
    }
 
    /**
     * ��ȡobject��filedName���Ե��࣬����������filedName�򷵻�null
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
     * ��value��ֵ��obj��filedName����
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
     * ����map��list����ֱ�ӵ����ݽṹ
     * @param scriptObjectMirror
     * @return
     */
    @SuppressWarnings("rawtypes")
	private static Object collectionParse(ScriptObjectMirror scriptObjectMirror ,TypeReference<?> typeReference)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        //���ӷ��͵�ʱ����ܻ����null�����ʱ����Ĭ�ϼ����߼�
        if(typeReference != null){
            Class<?> classZz = Class.forName(typeReference.getTypeName());
            //������ͽ����ͼ���û��ϵ��������ͨ����
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
                //������͵Ķ������Ǽ�����أ���ô������ͨ������
                return parse(scriptObjectMirror, typeReference);
            }
        }else{
            //�����յģ�����̫���������鷳
            typeReference = new TypeReference(){};
        }
        if (scriptObjectMirror.isArray()) {
            List<Object> list = new ArrayList<>(scriptObjectMirror.size());
            for (int i = 0; i < scriptObjectMirror.size(); i++) {
                Object object = scriptObjectMirror.getSlot(i);
                if ("ScriptObjectMirror".contains(object.getClass().getSimpleName())) {
                	ScriptObjectMirror scriptObjectMirror1 = (ScriptObjectMirror)object;
                	//�ݹ飬��������Ĳ�֪����ʲô���͡���������и����͵Ļ����÷��͵ģ�û�еĻ�����null
                    list.add(collectionParse(scriptObjectMirror1,typeReference.getSubTypeReference()));
                } else {
                	//�ݹ飬��������Ĳ�֪����ʲô���͡���������и����͵Ļ����÷��͵ģ�û�еĻ�����null
                    list.add(object);
                }
                
            }
            return list;
        } else {
            //���ô�СΪ ���ݳ���*1.25��������Ϊ���ݶ��˷�ʱ�䣬map��Ҫ1.25������Դ������д��ɶ�ˣ���д��0.75
            Double size = scriptObjectMirror.size() * 1.25;
            int capacity = size.intValue() + 1;
            Map<String, Object> map = new HashMap<>(capacity);
            for (String key : scriptObjectMirror.keySet()) {
                Object object = scriptObjectMirror.get(key);
                if (BASE_CLASS_TYPE.contains(object.getClass().getSimpleName())) {
                    //����ǻ����������;�ֱ��������
                    map.put(key, object);
                } else if ("ScriptObjectMirror".contains(object.getClass().getSimpleName())) {
                    //���ǻ����������ͣ��ݹ鴦��
                    ScriptObjectMirror scriptObjectMirror1 = (ScriptObjectMirror)object;
                    //�ݹ飬��������Ĳ�֪����ʲô���͡���������и����͵Ļ����÷��͵ģ�û�еĻ�����null
                    map.put(key, collectionParse(scriptObjectMirror1,typeReference.getSubTypeReference()));
                }else{
                    //�����������Ͳ�֪����û��дȫ����������ScriptObjectMirror����ȫ��ֱ��������
                    map.put(key, object);
                }
            }
            return map;
        }
    }

}
