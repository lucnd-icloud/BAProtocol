package com.binhanh.exception;

/**
 * Định nghĩa một số mã lỗi xảy ra trong ứng dụng
 * 
 * @author lucnd
 * 
 *         GENERAL_ERROR = 0; // Các lỗi xảy ra mà không cần // phải xử lý cho
 *         người dùng // biết ERROR_CONNECTION_FOR_REQUEST = 1; // xảy ra khi có
 *         // một request // lỗi TIMEOUT_CONNECTION = 2; // xảy ra khi thời gian
 *         kết // nối vượt quá mức cho phép REFUSE_CONNECTION = 3; // xảy ra khi
 *         từ chối kết nối // mạng OPEN_CONNECT_CONNECTION = 4; // xảy ra khi
 *         không mở // được kết nối mạng OUT_OF_MEMORY_EXCEPTION = 5; // xảy ra
 *         khi tràn bộ // nhớ PARSED_JSON_EXCEPTION = 6; // xảy ra khi phần tích
 *         // lỗi json NO_RESULT_ERROR = 7; // xảy ra khi lấy kết quả ko có //
 *         từ server
 * 
 */
public enum ErrorCode {
    NONE,
    GENERAL_ERROR,
	ERROR_CONNECTION_FOR_REQUEST,
	TIMEOUT_CONNECTION,
	REFUSE_CONNECTION,
	OPEN_CONNECT_CONNECTION,
	OUT_OF_MEMORY_EXCEPTION,
	PARSED_JSON_EXCEPTION,
	NO_RESULT_ERROR,
	INVALIDE_VALUE,
	THREAD_INTERRUPTED,
	WRONG_SESSION_KEY,
	INVOKE_RELOGIN,
	NO_OPENDIS,
	NO_FIRST_PARA,
	NO_SECOND_PARA,
	NO_THIRD_PARA;
	public int getId() {
		return ordinal();
	}
}
