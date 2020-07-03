package com.icandothisallday2020.ex31thread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    int num=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=findViewById(R.id.tv);
    }
    public void click(View view) {
        //오래 걸리는 작업
        for(int i=0;i<20;i++){
            num++;
            tv.setText(num+"");
            //MainThread 가 이 반복문안에서 작업중인 TextView 의
            //num 의 값을 보여주는 갱신작업 수행 불가
            //그래서 num의 값이 증가되는 모습이 보여지지 않고 반복문이 끝난 후 마지막 20만 보여짐
            //그래서 오래걸리는 작업은 MainThread 가 아닌 별도의 Thread 가 작업하도록 해야함
            try {//0.5초(500millisecond)동안 잠시 대기
                Thread.sleep(500);
            } catch (InterruptedException e) {e.printStackTrace();}
        }
    }
    public void click2(View view) {
        //직원객체(Thread) 를 생성하여 오래걸리는 작업 수행 ex)네트워크 작업,DB 작업
        MyThread t=new MyThread();
        t.start();//직원에게 작업수행 지시[ 이 thread 의 run 실행]
    }
    //오래걸리는 작업을 수행하는 스레드의 동작 설계
    class MyThread extends Thread{
        //이 객체를 start()하면 자동으로 실행되는 메소드 오버라이드
        @Override
        public void run() {
            for(int i=0;i<20;i++){
                num++;
                //****tv.setText(num+"");---UI(화면)작업은 반드시
                // Main Thread 만 할수 있도록 강제되었기에 28하위버전에서는 에러
                // 별도로 생성한 Thread 에서는 화면작업을 하면 에러
                // ->Main Thread 에 화면변경작업수행 요청
                //★방법1. Handler class 이용
                //****handler.sendEmptyMessage(0);//int what:식별번호
                //★방법2. runOnUiThread() method(Activity class's member) 이용
                //UI 변경작업수행가능권한을 Main 에게 위임받은 객체 생성
                //Thread 상속(extends)==Runnable 구현(implements)
                Runnable runnable=new Runnable() {//Runnable : interface
                    @Override
                    public void run() {tv.setText(num+"");}
                };
                runOnUiThread(runnable);//위임장을 부여하는 기능 실행
                //parameter 로 전달된 Runnable class 에게 UI 변경권한 부여
                try {Thread.sleep(500);}
                catch (InterruptedException e) {e.printStackTrace();}
            }
        }//run()
    }//MyThread...
    //★1)별도 Thread 가 MainThread 에게 UI 변경작업요청시 활용되는 객체
    Handler handler=new Handler(){//anonymous
        //handler.sendEmptyMessage()->자동으로 실행되는 method
        @Override
        public void handleMessage(@NonNull Message msg) {
            //이곳에서 UI 변경작업 가능
            tv.setText(num+"");
        }
    };
}//MainActivity class...
