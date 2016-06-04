package com.danxx.mdplayer.meizhi;

import java.util.List;

/**
 * Created by Zhk on 2015/12/20.
 */
public class Weather {

    /**
     * error_code : 0
     * reason : 成功
     * result : {"sk":{"temp":"8","wind_direction":"西北风","wind_strength":"2级","humidity":"94","time":"20:38"},"today":{"city":"杭州","date_y":"2015年12月20日","week":"星期日","temperature":"6~12","weather":"小雨","fa":"07","fb":"02","wind":"东风 微风","dressing_index":"较冷","dressing_advice":"建议着厚外套加毛衣等服装。年老体弱者宜着大衣、呢外套加羊毛衫。","uv_index":"最弱","comfort_index":"--","wash_index":"不宜","travel_index":"较不宜","exercise_index":"较不宜","drying_index":"--"},"future":[{"temperature":"8~12","weather":"多云","fa":"01","fb":"02","wind":"无持续风向 微风","week":"星期一","date":"20151221"},{"temperature":"9~12","weather":"中雨","fa":"08","fb":"08","wind":"东风 微风","week":"星期二","date":"20151222"},{"temperature":"9~11","weather":"小雨","fa":"07","fb":"07","wind":"东北风 微风","week":"星期三","date":"20151223"},{"temperature":"5~11","weather":"小雨","fa":"07","fb":"07","wind":"北风 微风","week":"星期四","date":"20151224"},{"temperature":"2~9","weather":"阴","fa":"02","fb":"01","wind":"西北风 微风","week":"星期五","date":"20151225"},{"temperature":"1~9","weather":"晴","fa":"00","fb":"01","wind":"东北风 微风","week":"星期六","date":"20151226"}]}
     */

    private int error_code;
    private String reason;
    /**
     * sk : {"temp":"8","wind_direction":"西北风","wind_strength":"2级","humidity":"94","time":"20:38"}
     * today : {"city":"杭州","date_y":"2015年12月20日","week":"星期日","temperature":"6~12","weather":"小雨","fa":"07","fb":"02","wind":"东风 微风","dressing_index":"较冷","dressing_advice":"建议着厚外套加毛衣等服装。年老体弱者宜着大衣、呢外套加羊毛衫。","uv_index":"最弱","comfort_index":"--","wash_index":"不宜","travel_index":"较不宜","exercise_index":"较不宜","drying_index":"--"}
     * future : [{"temperature":"8~12","weather":"多云","fa":"01","fb":"02","wind":"无持续风向 微风","week":"星期一","date":"20151221"},{"temperature":"9~12","weather":"中雨","fa":"08","fb":"08","wind":"东风 微风","week":"星期二","date":"20151222"},{"temperature":"9~11","weather":"小雨","fa":"07","fb":"07","wind":"东北风 微风","week":"星期三","date":"20151223"},{"temperature":"5~11","weather":"小雨","fa":"07","fb":"07","wind":"北风 微风","week":"星期四","date":"20151224"},{"temperature":"2~9","weather":"阴","fa":"02","fb":"01","wind":"西北风 微风","week":"星期五","date":"20151225"},{"temperature":"1~9","weather":"晴","fa":"00","fb":"01","wind":"东北风 微风","week":"星期六","date":"20151226"}]
     */

    private ResultEntity result;

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setResult(ResultEntity result) {
        this.result = result;
    }

    public int getError_code() {
        return error_code;
    }

    public String getReason() {
        return reason;
    }

    public ResultEntity getResult() {
        return result;
    }

    public static class ResultEntity {
        /**
         * temp : 8
         * wind_direction : 西北风
         * wind_strength : 2级
         * humidity : 94
         * time : 20:38
         */

        private SkEntity sk;
        /**
         * city : 杭州
         * date_y : 2015年12月20日
         * week : 星期日
         * temperature : 6~12
         * weather : 小雨
         * fa : 07
         * fb : 02
         * wind : 东风 微风
         * dressing_index : 较冷
         * dressing_advice : 建议着厚外套加毛衣等服装。年老体弱者宜着大衣、呢外套加羊毛衫。
         * uv_index : 最弱
         * comfort_index : --
         * wash_index : 不宜
         * travel_index : 较不宜
         * exercise_index : 较不宜
         * drying_index : --
         */

        private TodayEntity today;
        /**
         * temperature : 8~12
         * weather : 多云
         * fa : 01
         * fb : 02
         * wind : 无持续风向 微风
         * week : 星期一
         * date : 20151221
         */

        private List<FutureEntity> future;

        public void setSk(SkEntity sk) {
            this.sk = sk;
        }

        public void setToday(TodayEntity today) {
            this.today = today;
        }

        public void setFuture(List<FutureEntity> future) {
            this.future = future;
        }

        public SkEntity getSk() {
            return sk;
        }

        public TodayEntity getToday() {
            return today;
        }

        public List<FutureEntity> getFuture() {
            return future;
        }

        public static class SkEntity {
            private String temp;
            private String wind_direction;
            private String wind_strength;
            private String humidity;
            private String time;

            public void setTemp(String temp) {
                this.temp = temp;
            }

            public void setWind_direction(String wind_direction) {
                this.wind_direction = wind_direction;
            }

            public void setWind_strength(String wind_strength) {
                this.wind_strength = wind_strength;
            }

            public void setHumidity(String humidity) {
                this.humidity = humidity;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getTemp() {
                return temp;
            }

            public String getWind_direction() {
                return wind_direction;
            }

            public String getWind_strength() {
                return wind_strength;
            }

            public String getHumidity() {
                return humidity;
            }

            public String getTime() {
                return time;
            }
        }

        public static class TodayEntity {
            private String city;
            private String date_y;
            private String week;
            private String temperature;
            private String weather;
            private String fa;
            private String fb;
            private String wind;
            private String dressing_index;
            private String dressing_advice;
            private String uv_index;
            private String comfort_index;
            private String wash_index;
            private String travel_index;
            private String exercise_index;
            private String drying_index;

            public void setCity(String city) {
                this.city = city;
            }

            public void setDate_y(String date_y) {
                this.date_y = date_y;
            }

            public void setWeek(String week) {
                this.week = week;
            }

            public void setTemperature(String temperature) {
                this.temperature = temperature;
            }

            public void setWeather(String weather) {
                this.weather = weather;
            }

            public void setFa(String fa) {
                this.fa = fa;
            }

            public void setFb(String fb) {
                this.fb = fb;
            }

            public void setWind(String wind) {
                this.wind = wind;
            }

            public void setDressing_index(String dressing_index) {
                this.dressing_index = dressing_index;
            }

            public void setDressing_advice(String dressing_advice) {
                this.dressing_advice = dressing_advice;
            }

            public void setUv_index(String uv_index) {
                this.uv_index = uv_index;
            }

            public void setComfort_index(String comfort_index) {
                this.comfort_index = comfort_index;
            }

            public void setWash_index(String wash_index) {
                this.wash_index = wash_index;
            }

            public void setTravel_index(String travel_index) {
                this.travel_index = travel_index;
            }

            public void setExercise_index(String exercise_index) {
                this.exercise_index = exercise_index;
            }

            public void setDrying_index(String drying_index) {
                this.drying_index = drying_index;
            }

            public String getCity() {
                return city;
            }

            public String getDate_y() {
                return date_y;
            }

            public String getWeek() {
                return week;
            }

            public String getTemperature() {
                return temperature;
            }

            public String getWeather() {
                return weather;
            }

            public String getFa() {
                return fa;
            }

            public String getFb() {
                return fb;
            }

            public String getWind() {
                return wind;
            }

            public String getDressing_index() {
                return dressing_index;
            }

            public String getDressing_advice() {
                return dressing_advice;
            }

            public String getUv_index() {
                return uv_index;
            }

            public String getComfort_index() {
                return comfort_index;
            }

            public String getWash_index() {
                return wash_index;
            }

            public String getTravel_index() {
                return travel_index;
            }

            public String getExercise_index() {
                return exercise_index;
            }

            public String getDrying_index() {
                return drying_index;
            }
        }

        public static class FutureEntity {
            private String temperature;
            private String weather;
            private String fa;
            private String fb;
            private String wind;
            private String week;
            private String date;

            public void setTemperature(String temperature) {
                this.temperature = temperature;
            }

            public void setWeather(String weather) {
                this.weather = weather;
            }

            public void setFa(String fa) {
                this.fa = fa;
            }

            public void setFb(String fb) {
                this.fb = fb;
            }

            public void setWind(String wind) {
                this.wind = wind;
            }

            public void setWeek(String week) {
                this.week = week;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getTemperature() {
                return temperature;
            }

            public String getWeather() {
                return weather;
            }

            public String getFa() {
                return fa;
            }

            public String getFb() {
                return fb;
            }

            public String getWind() {
                return wind;
            }

            public String getWeek() {
                return week;
            }

            public String getDate() {
                return date;
            }
        }
    }
}
