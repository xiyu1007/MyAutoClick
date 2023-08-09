package com.example.yu;

// ShowToast.java
import android.content.Context;
import android.widget.Toast;


/**
 * @author ASUS
 */
public class ShowToast {

    public static void show(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}



//构造函数，创建实例对象调用
//    final ShowToast showToast = new ShowToast(MainActivity.this);

//public class ShowToast {
//    private Context context;
//
//    public ShowToast(Context context) {
//        this.context = context;
//    }
//
//    public void showToastWithCustomLayout(String message) {
//        // 加载自定义布局
//        View toastLayout = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
//
//        // 获取文本视图并设置文本内容
//        TextView textView = toastLayout.findViewById(R.id.toast_text);
//        textView.setText(message);
//
//        // 创建并显示自定义Toast
//        Toast customToast = new Toast(context);
//        customToast.setDuration(Toast.LENGTH_SHORT);
//        customToast.setView(toastLayout);
//        customToast.show();
//    }
//}






