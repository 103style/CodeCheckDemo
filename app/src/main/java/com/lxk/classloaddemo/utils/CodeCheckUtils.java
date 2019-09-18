package com.lxk.classloaddemo.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;

/**
 * @author https://github.com/103style
 * @date 2019/9/18 10:49
 */
public class CodeCheckUtils {

    private static final String TAG = "CodeCheckUtils";

    /**
     * 获取 BaseDexClassLoader 中 pathList 变量的 dexElements 变量
     */
    public static Object getDexElements() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            LogUtils.e(TAG, "classLoader is null");
            return null;
        }
        LogUtils.d(TAG, "classLoader = " + classLoader.getClass().getName());
        if (!(classLoader instanceof BaseDexClassLoader)) {
            LogUtils.e(TAG, "classLoader not instanceof BaseDexClassLoader");
            return null;
        }
        BaseDexClassLoader baseDexClassLoader = (BaseDexClassLoader) classLoader;
        Class superclass = baseDexClassLoader.getClass().getSuperclass();
        if (superclass == null) {
            LogUtils.e(TAG, "baseDexClassLoader's  superclass is null");
            return null;
        }
        LogUtils.d(TAG, "baseDexClassLoader's  superclass = " + superclass.getName());

        Object declaredField = ReflectUtils.getDeclaredField(baseDexClassLoader, superclass, "pathList");
        if (declaredField == null) {
            LogUtils.e(TAG, "get pathList is null");
            return null;
        }
        return ReflectUtils.getDeclaredField(declaredField, "dexElements");
    }

    /**
     * 获取当前classloader中的DexFile列表
     */
    public static ArrayList<DexFile> getDexFileList(Object dexElements) {
        if (dexElements == null) {
            LogUtils.e(TAG, "get dexElements is null");
            return null;
        }
        int length = 0;
        try {
            length = Array.getLength(dexElements);
        } catch (Exception e) {
            LogUtils.e(TAG, "get dexElements length error");
            e.printStackTrace();
        }

        if (length == 0) {
            LogUtils.e(TAG, "dexElements length is 0");
            return null;
        }

        ArrayList<DexFile> dexFileList = new ArrayList<>();
        Field dexFileField = null;
        for (int i = 0; i < length; i++) {
            Object temp = Array.get(dexElements, i);
            if (dexFileField == null) {
                dexFileField = ReflectUtils.getClassField(temp, "dexFile");
                if (dexFileField == null) {
                    continue;
                }
            }
            Object object = ReflectUtils.getClassFieldValue(temp, dexFileField);
            if (object == null) {
                return null;
            }
            if (object instanceof DexFile) {
                dexFileList.add((DexFile) object);
            } else {
                LogUtils.e(TAG, "");
            }
        }
        return dexFileList;
    }

    /**
     * 返回 DexFile 列表中  含有 packageName 的类
     *
     * @param dexFileList DexFile 列表
     * @param packageName 返回包含此包名的类
     */
    public static List<Class> getPackageClass(ArrayList<DexFile> dexFileList, String packageName) {
        if (dexFileList == null || dexFileList.size() == 0) {
            LogUtils.e(TAG, "dexFileList is empty");
            return null;
        }
        List<Class> pckClass = new ArrayList<>();
        for (DexFile dexFile : dexFileList) {
            Enumeration<String> enumeration = dexFile.entries();
            if (enumeration == null) {
                continue;
            }
            while (enumeration.hasMoreElements()) {
                String element = enumeration.nextElement();
                if (element.contains(packageName)) {
                    try {
                        pckClass.add(Class.forName(element));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return pckClass;
    }
}
