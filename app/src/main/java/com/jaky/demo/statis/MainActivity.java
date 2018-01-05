package com.jaky.demo.statis;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jaky.demo.statis.databinding.ActivityMainBinding;
import com.onyx.android.sdk.data.model.StatisticsResult;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding bindingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ActivityMainModel mainModel = new ActivityMainModel(MainActivity.this);
        bindingView.setMainModel(mainModel);
        RxBus.getInstance().register(StatisticsResult.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getComsumer());
    }

    private Consumer<StatisticsResult> getComsumer() {
        return new Consumer<StatisticsResult>() {
            @Override
            public void accept(StatisticsResult res) throws Exception {
                String info = "阅读等级: "+ res.getReadingLevel() +"\n" +
                            "总阅读时间: "+ res.getTotalReadTime() +"\n" +
                            "最近阅读的书: "+ res.getRecentReadingBooks().toString() +"\n" +
                            "阅读数量: "+ res.getEventTypeAgg().getRead() +"\n" +
                            "阅读完成数量: "+ res.getEventTypeAgg().getFinish() +"\n" +
                            "阅读完成数量: "+ res.getEventTypeAgg().getTotal() +"\n" +
                            "最长阅读时间的书: "+ (res.getLongestReadTimeBook() == null ? "null":res.getLongestReadTimeBook().getName()) +"\n" +
                            "最关注的书: "+ (res.getMostCarefulBook() == null ? "null": res.getMostCarefulBook().getName()) +"\n" +
                            "每日平均阅读时间: "+ res.getDailyAvgReadTime();
                bindingView.tvInfo.setText(info);
            }
        };
    }
}
