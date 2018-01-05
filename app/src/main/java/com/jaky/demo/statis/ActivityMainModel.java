package com.jaky.demo.statis;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;

import com.onyx.android.sdk.data.model.BaseStatisticsModel;
import com.onyx.android.sdk.data.model.Book;
import com.onyx.android.sdk.data.model.EventTypeAggBean;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.model.StatisticsResult;
import com.onyx.android.sdk.data.utils.StatisticsUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by jaky on 2018/1/5 0005.
 */

public class ActivityMainModel {

    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    public final static int RECENT_BOOK_MAX_COUNT = 5;


    public static final String BASE_CONTENT_URI = "content://";
    public static final String AUTHORITY = "com.onyx.kreader.statistics.provider";
    public static final String ENDPOINT = "OnyxStatisticsModel";
    public static final Uri CONTENT_URI = Uri.parse(BASE_CONTENT_URI + AUTHORITY + "/" + ENDPOINT);

    private StatisticsResult statisticsResult;
    private Context context;
    private Object task;
    private final ContentResolver cr;
    private List<ReadingStatisticsBean> datas;

    public ActivityMainModel(Context context) {
        this.context = context;
        cr = context.getContentResolver();
    }

    public StatisticsResult getStatisticsResult() {
        return statisticsResult;
    }

    public void setStatisticsResult(StatisticsResult statisticsResult) {
        this.statisticsResult = statisticsResult;
    }

    public void onDoMain(View v) {
        RxBus.getInstance().post(readLocalData());
    }

    private StatisticsResult readLocalData() {
        String[] selections = new String[]{"type=0"};
        datas = queryReadingStatisticsData(selections);

        StatisticsResult res = new StatisticsResult();
        EventTypeAggBean eventTypeAggBean = res.getEventTypeAgg();
        res.setTotalReadTime(getTotalReadTime());
        eventTypeAggBean.setRead(getReadCount());
        eventTypeAggBean.setFinish(getFinishCount());
        res.setMyEventHourlyAgg(getSelfReadTimeDis());
        res.setDailyAvgReadTime(getReadTimeEveryDay());
        res.setLongestReadTimeBook(getLongestBook());
        res.setMostCarefulBook(getMostCarefullyBook());
        res.setRecentReadingBooks(getRecentBooks());
        return res;
    }

    private long getTotalReadTime() {
        long readTimes = 0;
        for (ReadingStatisticsBean statisticsModel : datas) {
            readTimes += statisticsModel.getDurationTime();
        }
        return readTimes;
    }

    public Collection<OnyxStatisticsModel> loadStatisticsList(Context context, int type) {



        return null;
    }

    private List<ReadingStatisticsBean> queryReadingStatisticsData(String[] selections) {
        List<ReadingStatisticsBean> list = new ArrayList<ReadingStatisticsBean>();
        Cursor cursor = cr.query(CONTENT_URI, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ReadingStatisticsBean bean = new ReadingStatisticsBean();
            bean.setType(cursor.getInt(cursor.getColumnIndex("type")));
            bean.setMd5(cursor.getString(cursor.getColumnIndex("md5")));
            bean.setMd5short(cursor.getString(cursor.getColumnIndex("md5short")));
            bean.setMd5(cursor.getString(cursor.getColumnIndex("path")));
            bean.setMd5(cursor.getString(cursor.getColumnIndex("name")));
            bean.setMd5(cursor.getString(cursor.getColumnIndex("author")));
            bean.setEventTime(cursor.getLong(cursor.getColumnIndex("eventTime")));
            bean.setEventTime(cursor.getLong(cursor.getColumnIndex("durationTime")));
            bean.setType(cursor.getInt(cursor.getColumnIndex("score")));
            list.add(bean);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    private int getReadCount() {
        Set<String> readCount = new HashSet<>();
        for (ReadingStatisticsBean statisticsModel : datas) {
            readCount.add(statisticsModel.getMd5short());
        }
        return readCount.size();
    }

    private int getFinishCount() {
        Set<String> finishCount = new HashSet<>();
        for (ReadingStatisticsBean statisticsModel : datas) {
            finishCount.add(statisticsModel.getMd5short());
        }
        return finishCount.size();
    }

    private int getLookupDicCount() {
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_LOOKUP_DIC);
        return statisticsModels.size();
    }

    private int getAnnotationCount() {
        List<OnyxStatisticsModel> annotationStatistics = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_ANNOTATION);
        return annotationStatistics.size();
    }

    private int getSelectTextCount() {
        List<OnyxStatisticsModel> highLightStatistics = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_TEXT_SELECTED);
        return highLightStatistics.size();
    }

    private List<Integer> getSelfReadTimeDis() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date date = calendar.getTime();
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, date);
        return StatisticsUtils.getEventHourlyAgg(statisticsModels, 24);
    }

    private long getReadTimeEveryDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date date = calendar.getTime();
        Set<String> days = new HashSet<>();
        long readTimes = 0;
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, date, BaseStatisticsModel.DATA_TYPE_PAGE_CHANGE);
        for (OnyxStatisticsModel statisticsModel : statisticsModels) {
            days.add(formatDate(statisticsModel.getEventTime(), DATE_FORMAT_YYYYMMDD));
            readTimes += statisticsModel.getDurationTime();
        }
        if (days.size() == 0) {
            return 0;
        }
        long readTimeEveryDay = readTimes / days.size();
        if (readTimeEveryDay > 10 * 60 * 60 * 1000) {

        }
        return readTimeEveryDay;
    }

    private Book getLongestBook() {
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_PAGE_CHANGE);
        Book book = StatisticsUtils.getLongestBook(statisticsModels);
        if (book == null) {
            return null;
        }
        String md5short = book.getMd5short();

        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(context, md5short, BaseStatisticsModel.DATA_TYPE_OPEN, true);
        if (statisticsModels != null && statisticsModels.size() > 0) {
            book.setBegin(statisticsModels.get(0).getEventTime());
        }
        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(context, md5short, true);
        if (statisticsModels != null && statisticsModels.size() > 0) {
            book.setEnd(statisticsModels.get(statisticsModels.size() - 1).getEventTime());
        }
        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListByMd5short(context, md5short);
        if (statisticsModels.size() > 0) {
            book.setName(statisticsModels.get(0).getName());
        }
        return book;
    }

    private Book getMostCarefullyBook() {
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_LOOKUP_DIC);
        statisticsModels.addAll(StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_ANNOTATION));
        statisticsModels.addAll(StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_TEXT_SELECTED));
        Book book = StatisticsUtils.getMostCarefullyBook(statisticsModels);
        if (book == null) {
            return null;
        }
        String md5short = book.getMd5short();

        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, md5short, BaseStatisticsModel.DATA_TYPE_LOOKUP_DIC);
        book.setLookupDic(statisticsModels.size());
        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, md5short, BaseStatisticsModel.DATA_TYPE_ANNOTATION);
        book.setAnnotation(statisticsModels.size());
        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, md5short, BaseStatisticsModel.DATA_TYPE_TEXT_SELECTED);
        book.setTextSelect(statisticsModels.size());
        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListByMd5short(context, md5short);
        if (statisticsModels.size() > 0) {
            book.setName(statisticsModels.get(0).getName());
        }
        return book;
    }

    public List<Book> getRecentBooks() {
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(context, BaseStatisticsModel.DATA_TYPE_OPEN, false);
        List<Book> recentBooks = StatisticsUtils.getRecentBooks(statisticsModels, RECENT_BOOK_MAX_COUNT);

        for (Book book : recentBooks) {
            String md5short = book.getMd5short();
            statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListByMd5short(context, md5short);
            if (statisticsModels.size() > 0) {
                book.setName(statisticsModels.get(0).getName());
            }

            Date beginTime = null;
            Date endTime = null;
            statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(context, md5short, BaseStatisticsModel.DATA_TYPE_OPEN, false);
            if (statisticsModels != null && statisticsModels.size() > 0) {
                beginTime = statisticsModels.get(0).getEventTime();
                book.setBegin(beginTime);
            }
            statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(context, md5short, false);
            if (statisticsModels != null && statisticsModels.size() > 0) {
                endTime = statisticsModels.get(0).getEventTime();
                book.setEnd(endTime);
            }

            long useTime = 0;
            if (beginTime != null) {
                statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, md5short, BaseStatisticsModel.DATA_TYPE_PAGE_CHANGE, beginTime);
                for (OnyxStatisticsModel statisticsModel : statisticsModels) {
                    useTime += statisticsModel.getDurationTime();
                }
            }

            if (useTime <= 0 && beginTime != null & endTime != null) {
                useTime = endTime.getTime() - beginTime.getTime();
            }

            book.setReadingTime(Math.max(useTime, 0));
        }

        return recentBooks;
    }

    public static String formatDate(Date date, SimpleDateFormat simpleDateFormat) {
        if (date == null) {
            return "";
        }
        return simpleDateFormat.format(date);
    }
}
