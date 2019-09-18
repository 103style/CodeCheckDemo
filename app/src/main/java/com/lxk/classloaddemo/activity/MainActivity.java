package com.lxk.classloaddemo.activity;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.lxk.classloaddemo.R;
import com.lxk.classloaddemo.utils.CodeCheckUtils;
import com.lxk.classloaddemo.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexFile;

/**
 * @author https://github.com/103style
 * @date 2019/9/18 10:46
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.start_check).setOnClickListener(view -> startCodeCheck());
    }

    private void startCodeCheck() {
        // 获取 BaseDexClassLoader 中 pathList 变量的 dexElements 变量
        Object dexElements = CodeCheckUtils.getDexElements();

        // 获取当前classloader中的DexFile列表
        ArrayList<DexFile> dexFileList = CodeCheckUtils.getDexFileList(dexElements);

        // 获取 DexFile 列表中  含有 packageName 的类
        List<Class> pkgClass = CodeCheckUtils.getPackageClass(dexFileList, getPackageName());

        //获取不符合规则的类
        List<String> illegalList = getIllegalClass(pkgClass);
        if (illegalList == null || illegalList.size() == 0) {
            return;
        }

        //弹框提示修改
        showIllegalClassDialog(illegalList);
    }

    private void showIllegalClassDialog(List<String> illegalList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(illegalList.toArray(new String[0]), null);
        builder.setTitle("以下类不符合代码规范，请修改");
        builder.setCancelable(false);
        builder.show();
    }


    private List<String> getIllegalClass(List<Class> pkgClass) {
        if (pkgClass == null) {
            LogUtils.e(TAG, "pkgClass is null");
            return null;
        }
        List<String> illegalList = new ArrayList<>();

        // TODO: 2019/9/18  这里的检查规则 请根据具体的规则修改
        for (Class itemClass : pkgClass) {
            String name = itemClass.getName();
            LogUtils.d(TAG, name);
            String simpleName = itemClass.getSimpleName();
            if (isSystemOrInnerClass(name, simpleName)) {
                continue;
            }
            name = name.replace(getPackageName(), "");
            String[] arr = name.split("\\.");
            //文件直接在包目录下 子目录下包含文件夹  不合法
            if (arr.length != 3) {
                illegalList.add(simpleName);
                continue;
            }

            if (!arr[2].toLowerCase().endsWith(arr[1].toLowerCase())) {
                illegalList.add(simpleName);
            }
        }
        return illegalList;
    }

    /**
     * 是否是系统生成的类 或者 内部类
     */
    private boolean isSystemOrInnerClass(String name, String simpleName) {
        return name.contains("R$") //资源文件
                | "R".equals(simpleName)//R文件
                | "BuildConfig".equals(simpleName)//配置文件
                | name.contains("$");//内部类
    }


    static class TestStaticClass {

    }


    class TestClass {

    }
}
