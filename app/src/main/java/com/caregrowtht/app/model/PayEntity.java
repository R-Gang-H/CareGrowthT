package com.caregrowtht.app.model;

/**
 * Date: 2019/5/9
 * Author: haoruigang
 * Description: com.caregrowtht.app.model
 */
public class PayEntity {


    /**
     * alipay_trade_app_pay_response : {"code":"10000","msg":"Success","app_id":"2019050564362891","auth_app_id":"2019050564362891","charset":"UTF-8","timestamp":"2019-05-09 20:22:32","out_trade_no":"215574045404075","total_amount":"0.01","trade_no":"2019050922001473281032644650","seller_id":"2088431238274125"}
     * sign : V7WiTc+LD/conoZlTR6vz5UKuiIrwgenIazq+AqdGVFIiMlOjuIgIA7PxyxkuCTQccxr62NkSrC7SU1ubLmuTFIsiF9Zi7tGk/05H75bNbsRP3b2n0APUL/qXX9fSYEoMy8vCjOA3IfLNim/9sd8SsYrS/4wVFeeL52f2uOUw72KCqKSeZa/uFl/EAQM1PxINvmlodctF4pxbpNaPg9F03Se3/YB41ePv6Sdrpl460gAcq8Z3rBFlRJnIL2sh/Ohr59ZxPqXkhZ7UC6D7pI2OUJ29gyu2Kxc+CDu6zYRpJSNpgF4Ubc0nT7DrbQ3ohGAYeuoqfqOWD0/ydj+ZICPhA==
     * sign_type : RSA2
     */

    private AlipayTradeAppPayResponseBean alipay_trade_app_pay_response;
    private String sign;
    private String sign_type;

    public AlipayTradeAppPayResponseBean getAlipay_trade_app_pay_response() {
        return alipay_trade_app_pay_response;
    }

    public void setAlipay_trade_app_pay_response(AlipayTradeAppPayResponseBean alipay_trade_app_pay_response) {
        this.alipay_trade_app_pay_response = alipay_trade_app_pay_response;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public static class AlipayTradeAppPayResponseBean {
        /**
         * code : 10000
         * msg : Success
         * app_id : 2019050564362891
         * auth_app_id : 2019050564362891
         * charset : UTF-8
         * timestamp : 2019-05-09 20:22:32
         * out_trade_no : 215574045404075
         * total_amount : 0.01
         * trade_no : 2019050922001473281032644650
         * seller_id : 2088431238274125
         */

        private String code;
        private String msg;
        private String app_id;
        private String auth_app_id;
        private String charset;
        private String timestamp;
        private String out_trade_no;
        private String total_amount;
        private String trade_no;
        private String seller_id;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getApp_id() {
            return app_id;
        }

        public void setApp_id(String app_id) {
            this.app_id = app_id;
        }

        public String getAuth_app_id() {
            return auth_app_id;
        }

        public void setAuth_app_id(String auth_app_id) {
            this.auth_app_id = auth_app_id;
        }

        public String getCharset() {
            return charset;
        }

        public void setCharset(String charset) {
            this.charset = charset;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getOut_trade_no() {
            return out_trade_no;
        }

        public void setOut_trade_no(String out_trade_no) {
            this.out_trade_no = out_trade_no;
        }

        public String getTotal_amount() {
            return total_amount;
        }

        public void setTotal_amount(String total_amount) {
            this.total_amount = total_amount;
        }

        public String getTrade_no() {
            return trade_no;
        }

        public void setTrade_no(String trade_no) {
            this.trade_no = trade_no;
        }

        public String getSeller_id() {
            return seller_id;
        }

        public void setSeller_id(String seller_id) {
            this.seller_id = seller_id;
        }
    }
}
