package com.hxht.testqqslidingpanel;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hxht.testqqslidingpanel.customdragview.SlidingPanelDragView;
import com.hxht.testqqslidingpanel.mylinerlayout.MyLinerlayout;
import com.nineoldandroids.view.ViewHelper;

import java.util.Random;


public class MainActivity extends Activity {

    private LinearLayout ll_leftview;
    private ImageView iv_left_icon;
    private ListView lv_left;
    private MyLinerlayout ll_mainview;
    private ImageView iv_main_icon;
    private TextView tv_main;
    private ListView lv_main;
    private static final String[] names = new String[]{
            "宋江",
            "卢俊义",
            "吴用",
            "公孙胜",
            "关胜",
            "林冲",
            "秦明",
            "呼延灼",
            "花荣",
            "柴进",
            "李应",
            "朱仝",
            "鲁智深",
            "武松",
            "董平",
            "张清",
            "杨志",
            "徐宁",
            "索超",
            "戴宗",
            "刘唐",
            "李逵",
            "史进",
            "穆弘",
            "雷横",
            "李俊",
            "阮小二",
            "张横",
            "阮小五",
            "张顺",
            "阮小七",
            "杨雄",
            "石秀",
            "解珍",
            "解宝 ",
            "燕青",
            "朱武",
            "黄信",
            "孙立",
            "宣赞",
            "郝思文",
            "韩滔",
            "彭玘",
            "单廷圭",
            "魏定国",
            "萧让",
            "裴宣",
            "欧鹏",
            "邓飞",
            "燕顺",
            "杨林",
            "凌振",
            "蒋敬",
            "吕方",
            "郭盛",
            "安道全",
            "皇甫端",
            "王英",
            "扈三娘",
            "鲍旭",
            "樊瑞",
            "孔明",
            "孔亮",
            "项充",
            "李衮",
            "金大坚",
            "马麟",
            "童威",
            "童猛",
            "孟康",
            "侯健",
            "陈达",
            "杨春",
            "郑天寿",
            "陶宗旺",
            "宋清",
            "乐和",
            "龚旺",
            "丁得孙",
            "穆春",
            "曹正",
            "宋万",
            "杜迁",
            "薛永",
            "李忠",
            "周通",
            "汤隆",
            "杜兴",
            "邹渊",
            "邹润",
            "朱贵",
            "朱富",
            "施恩",
            "蔡福",
            "蔡庆",
            "李立",
            "李云",
            "焦挺",
            "石勇",
            "孙新",
            "顾大嫂",
            "孙二娘",
            "王定六",
            "郁保四",
            "白胜",
            "时迁",
            "段景住"
    };
    private SlidingPanelDragView dragView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        initData();
    }

    private void initData() {

        ll_mainview.setDragView(dragView);

        lv_left.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, names) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(Color.WHITE);
                return tv;
            }
        });

        lv_main.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, names) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(Color.WHITE);
                return tv;
            }
        });

        dragView.setOnUpdateStatusListener(new SlidingPanelDragView.OnUpdateStatusListener() {

            @Override
            public void onOpen() {
                //Toast.makeText(MainActivity.this, "打开了侧滑面板", Toast.LENGTH_SHORT).show();
                Random random = new Random();
                int index = random.nextInt(100);
                System.out.println(index);
                lv_left.smoothScrollToPosition(index);
            }

            @Override
            public void onClose() {
                //Toast.makeText(MainActivity.this, "关闭了侧滑面板", Toast.LENGTH_SHORT).show();
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(iv_main_icon, "translationX", 15f);
                objectAnimator.setInterpolator(new CycleInterpolator(4));
                objectAnimator.setDuration(200);
                objectAnimator.start();
            }

            @Override
            public void onDraging(float percent) {
                ViewHelper.setAlpha(iv_left_icon, percent);
                ViewHelper.setAlpha(iv_main_icon, 1 - percent);
                ViewHelper.setScaleX(tv_main, 1 - percent);
                ViewHelper.setScaleY(tv_main, 1 - percent);
            }
        });
    }

    private void initViews() {
        ll_leftview = (LinearLayout) findViewById(R.id.ll_leftview);
        iv_left_icon = (ImageView) findViewById(R.id.iv_left_icon);
        lv_left = (ListView) findViewById(R.id.lv_left);
        ll_mainview = (MyLinerlayout) findViewById(R.id.ll_mainview);
        iv_main_icon = (ImageView) findViewById(R.id.iv_main_icon);
        tv_main = (TextView) findViewById(R.id.tv_main);
        lv_main = (ListView) findViewById(R.id.lv_main);
        dragView = (SlidingPanelDragView) findViewById(R.id.slidingpaneldragview);
    }
}
