package pe.edu.upc.smartmirror.backend.models;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ricardo on 5/23/17.
 */

public class Widget extends SugarRecord implements Serializable {
    private int userId;
    private boolean clock;
    private boolean weather;
    private boolean news;
    private boolean calendar;
    private boolean player;
    private boolean mail;


    public Widget() {
    }

    public Widget(int userId) {
        this.userId = userId;
        clock = true;
        weather = true;
        news = true;
        calendar = true;
        player = true;
        mail = true;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isClock() {
        return clock;
    }

    public void setClock(boolean clock) {
        this.clock = clock;
    }

    public boolean isWeather() {
        return weather;
    }

    public void setWeather(boolean weather) {
        this.weather = weather;
    }

    public boolean isNews() {
        return news;
    }

    public void setNews(boolean news) {
        this.news = news;
    }

    public boolean isCalendar() {
        return calendar;
    }

    public void setCalendar(boolean calendar) {
        this.calendar = calendar;
    }

    public boolean isPlayer() {
        return player;
    }

    public void setPlayer(boolean player) {
        this.player = player;
    }

    public boolean isMail() {
        return mail;
    }

    public void setMail(boolean mail) {
        this.mail = mail;
    }

    public static Widget findOrCreate(int userId){
        List<Widget> mWidgets = Widget.find(Widget.class, "user_id = ?", String.valueOf(userId));
        if(mWidgets.size() > 0){
           return mWidgets.get(0);
        }else{
            Widget mWidget = new Widget(userId);
            mWidget.save();
            return  mWidget;
        }
    }
}
