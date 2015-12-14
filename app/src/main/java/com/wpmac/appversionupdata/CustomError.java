
package com.wpmac.appversionupdata;

/**
 * 封装服务器返回的错误结果
 */
public class CustomError extends RuntimeException {

    private static final long serialVersionUID = 1L;
    /**
     * 错误码
     */
    private int mErrorCode;

    /**
     * 原始响应URL
     */
    private String mResponse;

    public CustomError() {
        super();
    }

    public CustomError(String errorMessage) {
        super(errorMessage);
    }

    public CustomError(int errorCode, String errorMessage, String orgResponse) {
        super(errorMessage);
        this.mErrorCode = errorCode;
        this.mResponse = orgResponse;
    }

    public String getOrgResponse() {
        return mResponse;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    @Override
    public String toString() {
        return "errorCode:" + this.mErrorCode + "\nerrorMessage:"
                + this.getMessage() + "\norgResponse:" + this.mResponse;
    }

    /**
     * 将服务器返回的errorMessage转换成定义的易于理解的字符串
     *
     * @param errorCode    服务器返回的错误代码
     * @param errorMessage 服务器返回的错误字符串，和错误代码一一对应
     * @return
     */
    public static String interpretErrorMessage(int errorCode, String errorMessage) {
        switch (errorCode) {
            /**
             * 错误处理
             */
        }

        return errorMessage;
    }
}
