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
        
        //LayoutInflater������������ findViewById(),��ͬ����LayoutInflater��������layout��xml�����ļ�������ʵ������
        LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  // LAYOUT_INFLATER_SERVICE����ȡ��xml�ﶨ���view
        mMenuView = inflater.inflate(R.layout.popupwindow_bottom_layout, null);  
        
        btn_duanxin = (Button) mMenuView.findViewById(R.id.btn_duanxin);  
        btn_qq = (Button) mMenuView.findViewById(R.id.btn_qq);  
        btn_renren = (Button) mMenuView.findViewById(R.id.btn_renren); 
        
        btn_pengyouquan = (Button) mMenuView.findViewById(R.id.btn_pengyouquan);  
        btn_tengxunweibo = (Button) mMenuView.findViewById(R.id.btn_tengxunweibo);  
    
        btn_qqzone = (Button) mMenuView.findViewById(R.id.btn_qqzone); 
        btn_weibo = (Button) mMenuView.findViewById(R.id.btn_weibo);  
        btn_weixin = (Button) mMenuView.findViewById(R.id.btn_weixin);
  /*      //ȡ����ť  
        btn_cancel.setOnClickListener(new OnClickListener() {  
  
            public void onClick(View v) {  
                //���ٵ�����  
                dismiss();  
            }  
        });  */
        //���ð�ť����  
        btn_duanxin.setOnClickListener(itemsOnClick);  
        btn_qq.setOnClickListener(itemsOnClick); 
        btn_renren.setOnClickListener(itemsOnClick);  
        btn_pengyouquan.setOnClickListener(itemsOnClick);
        btn_tengxunweibo.setOnClickListener(itemsOnClick);  
        btn_qqzone.setOnClickListener(itemsOnClick);
        btn_weibo.setOnClickListener(itemsOnClick);  
        btn_weixin.setOnClickListener(itemsOnClick);
        
        //����SelectPicPopupWindow��View  
        this.setContentView(mMenuView);  
        //����SelectPicPopupWindow��������Ŀ�  
        this.setWidth(LayoutParams.FILL_PARENT);  
        //����SelectPicPopupWindow��������ĸ�  
        this.setHeight(LayoutParams.WRAP_CONTENT);  
        //����SelectPicPopupWindow��������ɵ��  
        this.setFocusable(true);  
        //����SelectPicPopupWindow�������嶯��Ч��  
        //this.setAnimationStyle(R.style.AnimBottom);    //���ö����Ժ��п���  
        //ʵ����һ��ColorDrawable��ɫΪ��͸��  
        ColorDrawable dw = new ColorDrawable(0x60000000);  
        //����SelectPicPopupWindow��������ı���  
        this.setBackgroundDrawable(dw);  
        //mMenuView���OnTouchListener�����жϻ�ȡ����λ�������ѡ������������ٵ�����  
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