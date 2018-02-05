package com.binhanh.protocol.serializer.model;

import java.util.Calendar;

public class TimeBySix {

    private final static int LENGTH = 6;

    private byte[] mTime;

    public TimeBySix(long milis) {
        setTime(milis);
    }

    public TimeBySix(byte[] time) {
        setBytesTime(time);
    }

    public byte[] getBytesTime() {
        if (mTime == null) {
            mTime = new byte[6];
        }
        return mTime;
    }

    /**
     * thiết lập mảng thời gian
     *
     * @param time
     */
    public void setBytesTime(byte[] time) {
        if (time.length != LENGTH) {
            throw new IllegalArgumentException(
                    "Độ dài của mảng byte phải bằng 6");
        }
        this.mTime = time;
    }

    /**
     * lấy thời gian thiết lập theo mẫu yyMMdd HHmmss
     * Với 1 số trường hợp, hộp đen sẽ trả về ngày tháng năm là 0 hết thì set mặc định thành 1-1-2000
     *
     * @return
     */
    public long getTime() {
        Calendar calendar = Calendar.getInstance();
        if (mTime[0] == 0 && mTime[1] == 0 && mTime[2] == 0) {
            calendar.set(Calendar.YEAR, mTime[0] + 2000);
            calendar.set(Calendar.MONTH, mTime[1]);
            calendar.set(Calendar.DAY_OF_MONTH, mTime[2] + 1);
        } else {
            calendar.set(Calendar.YEAR, mTime[0] + 2000);
            calendar.set(Calendar.MONTH, mTime[1] - 1);
            calendar.set(Calendar.DAY_OF_MONTH, mTime[2]);
        }
        calendar.set(Calendar.HOUR_OF_DAY, mTime[3]);
        calendar.set(Calendar.MINUTE, mTime[4]);
        calendar.set(Calendar.SECOND, mTime[5]);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * thiết lập thời gian
     *
     * @param millis
     */
    public void setTime(long millis) {
        byte ret[] = new byte[6];

        Calendar Cal = Calendar.getInstance();
        Cal.setTimeInMillis(millis);

        ret[0] = (byte) (Cal.get(Calendar.YEAR) - 2000);
        ret[1] = (byte) (Cal.get(Calendar.MONTH) + 1);
        ret[2] = (byte) (Cal.get(Calendar.DAY_OF_MONTH));
        ret[3] = (byte) (Cal.get(Calendar.HOUR_OF_DAY));
        ret[4] = (byte) (Cal.get(Calendar.MINUTE));
        ret[5] = (byte) (Cal.get(Calendar.SECOND));

        setBytesTime(ret);
    }

    @Override
    public String toString() {
        if(mTime == null) return null;
        return String.valueOf(getTime());
    }
}
