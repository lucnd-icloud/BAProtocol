package com.binhanh.utils;

import android.util.Log;

public class BitUtils {

	public static int getBitToInt(IEnumBitState bitState, int src){
		return (src >> bitState.getBitPosition()) & ((int)Math.pow(2, bitState.getBitNumber()) - 1);
	}
	
	/**
	 * gán giá trị vào bít cố định
	 * @param bitState: lưu thông số về vị trí bit và số lượng bit, null thì trả về giá trị 0
	 * @param dst: 
	 * @param src: giá trị để gán vào
	 * @return
	 */
	public static int copy(IEnumBitState bitState, int dst, int src){

        //Nếu không có đối tượng đẻ biết số lượng bit, và ví trí bít thì ko cập nhật
        if(bitState == null) return dst;

        //Nếu giá trị nhỏ hơn 0 thì không cần thiết lập lại bằng 0
		if(src < 0){
			Log.e("BitUtils","Giá trị truyền vào không phù hợp src = " + src + "; bitState.getBitPosition() = " + bitState.getBitPosition());
            src = 0;


        //Nếu giá trị vượt quá giá trị tối đa sơ với số lượng bit thì gán lại bằng giá trị max
		} else if(src >= Math.pow(2, bitState.getBitNumber())){
            Log.e("BitUtils","Giá trị truyền vào không phù hợp src = " + src + "; bitState.getBitPosition() = " + bitState.getBitPosition());
            src = (int)Math.pow(2, bitState.getBitNumber());
        }
		
		//gán tất cả các bít còn lại bằng 0
		for (int i = bitState.getBitPosition(); i < bitState.getBitPosition() + bitState.getBitNumber(); i++) {
			dst &= ~(1 << i);
		}

        //gán lại giá trị
		dst |= (src << bitState.getBitPosition());
		
		return dst;
	}
	
	public static int copy(IEnumBitState bitState, int dst, boolean src){
		return copy(bitState, dst, src?1:0);
	}

    /**
     * kiểm tra có cờ trong trạng thái hay không
     * @param state
     * @param mark
     * @return
     */
	public static boolean isFlag(int state, int mark){
	    return (state & mark) == mark;
    }

    /**
     * xóa cờ trong
     * @param state
     * @param mark
     * @return
     */
    public static int removeFlag(int state, int mark){
        mark = ~mark;
        return (state & mark);
    }

    /**
     * thêm cờ vào trạng thái
     * @param state
     * @param mark
     * @return
     */
    public static int addFlag(int state, int mark){
        return (state | mark);
    }

    /**
     * Lớp interaface xử lý cho bit
     */
    public interface IEnumBitState {

        /***
         * Lấy vị trí của bit
         * @return
         */
        public int getBitPosition();


        /**
         * Lấy số lượng bit
         * @return
         */
        public int getBitNumber();
    }
}
