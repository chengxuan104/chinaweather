package activity;
import com.chinaweather.app.R;

import android.app.Activity;  
import android.content.Context;  
import android.graphics.drawable.ColorDrawable;  
import android.view.LayoutInflater;  
import android.view.MotionEvent;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.view.View.OnTouchListener;  
import android.view.ViewGroup.LayoutParams;  
import android.widget.Button;  
import android.widget.PopupWindow;  
  
public class SelectPicPopupWindow extends PopupWindow {  
  
  
    private Button btn_duanxin, btn_qq, btn_renren, btn_qqzone,
    			   btn_pengyouquan, btn_tengxunweibo,
    			   btn_weibo, btn_weixin;  
    private View mMenuView;  
  
    public SelectPicPopupWindow(Activity context,OnClickListener itemsOnClick) {  
        super(context); 
        
        //LayoutInflater的作用类似于 findViewById(),不同点是LayoutInflater是用来找layout下xml布局文件，并且实例化！
        LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  // LAYOUT_INFLATER_SERVICE代表取得xml里定义的view
        mMenuView = inflater.inflate(R.layout.popupwindow_bottom_layout, null);  
        
        btn_duanxin = (Button) mMenuView.findViewById(R.id.btn_duanxin);  
        btn_qq = (Button) mMenuView.findViewById(R.id.btn_qq);  
        btn_renren = (Button) mMenuView.findViewById(R.id.btn_renren); 
        
        btn_pengyouquan = (Button) mMenuView.findViewById(R.id.btn_pengyouquan);  
        btn_tengxunweibo = (Button) mMenuView.findViewById(R.id.btn_tengxunweibo);  
    
        btn_qqzone = (Button) mMenuView.findViewById(R.id.btn_qqzone); 
        btn_weibo = (Button) mMenuView.findViewById(R.id.btn_weibo);  
        btn_weixin = (Button) mMenuView.findViewById(R.id.btn_weixin);
  /*      //取消按钮  
        btn_cancel.setOnClickListener(new OnClickListener() {  
  
            public void onClick(View v) {  
                //销毁弹出框  
                dismiss();  
            }  
        });  */
        //设置按钮监听  
        btn_duanxin.setOnClickListener(itemsOnClick);  
        btn_qq.setOnClickListener(itemsOnClick); 
        btn_renren.setOnClickListener(itemsOnClick);  
        btn_pengyouquan.setOnClickListener(itemsOnClick);
        btn_tengxunweibo.setOnClickListener(itemsOnClick);  
        btn_qqzone.setOnClickListener(itemsOnClick);
        btn_weibo.setOnClickListener(itemsOnClick);  
        btn_weixin.setOnClickListener(itemsOnClick);
        
        //设置SelectPicPopupWindow的View  
        this.setContentView(mMenuView);  
        //设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(LayoutParams.FILL_PARENT);  
        //设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(LayoutParams.WRAP_CONTENT);  
        //设置SelectPicPopupWindow弹出窗体可点击  
        this.setFocusable(true);  
        //设置SelectPicPopupWindow弹出窗体动画效果  
        //this.setAnimationStyle(R.style.AnimBottom);    //设置动画以后有空做  
        //实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0x60000000);  
        //设置SelectPicPopupWindow弹出窗体的背景  
        this.setBackgroundDrawable(dw);  
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框  
        mMenuView.setOnTouchListener(new OnTouchListener() {  
              
            public boolean onTouch(View v, MotionEvent event) {  
                  
                int height = mMenuView.findViewById(R.id.pop_layout).getTop();  
                int y=(int) event.getY();  
                if(event.getAction()==MotionEvent.ACTION_UP){  
                    if(y<height){  
                        dismiss();  
                    }  
                }                 
                return true;  
            }  
        });  
    }  
} 