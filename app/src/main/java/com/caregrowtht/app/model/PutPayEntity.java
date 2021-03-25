package com.caregrowtht.app.model;

import java.io.Serializable;
import java.util.List;

public class PutPayEntity {

    private String incomePrice;
    private String outcomePrice;
    private String pageCount;

    private String inPrice;
    private String outPrice;
    private String xDate;

    private List<TableData> tableData;

    public String getIncomePrice() {
        return incomePrice;
    }

    public void setIncomePrice(String incomePrice) {
        this.incomePrice = incomePrice;
    }

    public String getOutcomePrice() {
        return outcomePrice;
    }

    public void setOutcomePrice(String outcomePrice) {
        this.outcomePrice = outcomePrice;
    }

    public String getPageCount() {
        return pageCount;
    }

    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }

    public String getInPrice() {
        return inPrice;
    }

    public void setInPrice(String inPrice) {
        this.inPrice = inPrice;
    }

    public String getOutPrice() {
        return outPrice;
    }

    public void setOutPrice(String outPrice) {
        this.outPrice = outPrice;
    }

    public String getxDate() {
        return xDate;
    }

    public void setxDate(String xDate) {
        this.xDate = xDate;
    }

    public List<TableData> getTableData() {
        return tableData;
    }

    public void setTableData(List<TableData> tableData) {
        this.tableData = tableData;
    }

    public static class TableData implements Serializable {

        /**
         * rechargeId : 2361
         * classTitle : 收入
         * price : +50000
         * rq : 2019/07/30
         * childName :
         * childType : 正式学员
         * tradeType : 新购卡
         * detail : 音乐课年卡
         * reason :
         * saleManName : 郝帅
         * agentManName : 郝帅
         * voucher :
         * isInits : 人工
         * remark :
         * isActive : 1
         */

        private String rechargeId;
        private String classTitle;
        private String price2;
        private String price;
        private String rq, create_at;
        private String childName;
        private String childType;
        private String tradeType;
        private String type;
        private String detail;
        private String reason;
        private String saleManId;
        private String agentManId;
        private String saleManName;
        private String agentManName;
        private String voucher;
        private String isInits;
        private String remark;
        private String isActive;

        public String getRechargeId() {
            return rechargeId;
        }

        public void setRechargeId(String rechargeId) {
            this.rechargeId = rechargeId;
        }

        public String getClassTitle() {
            return classTitle;
        }

        public void setClassTitle(String classTitle) {
            this.classTitle = classTitle;
        }

        public String getPrice2() {
            return price2;
        }

        public void setPrice2(String price2) {
            this.price2 = price2;
        }

        public String getRq() {
            return rq;
        }

        public void setRq(String rq) {
            this.rq = rq;
        }

        public String getCreate_at() {
            return create_at;
        }

        public void setCreate_at(String create_at) {
            this.create_at = create_at;
        }

        public String getChildName() {
            return childName;
        }

        public void setChildName(String childName) {
            this.childName = childName;
        }

        public String getChildType() {
            return childType;
        }

        public void setChildType(String childType) {
            this.childType = childType;
        }

        public String getTradeType() {
            return tradeType;
        }

        public void setTradeType(String tradeType) {
            this.tradeType = tradeType;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getSaleManName() {
            return saleManName;
        }

        public void setSaleManName(String saleManName) {
            this.saleManName = saleManName;
        }

        public String getAgentManName() {
            return agentManName;
        }

        public void setAgentManName(String agentManName) {
            this.agentManName = agentManName;
        }

        public String getVoucher() {
            return voucher;
        }

        public void setVoucher(String voucher) {
            this.voucher = voucher;
        }

        public String getIsInits() {
            return isInits;
        }

        public void setIsInits(String isInits) {
            this.isInits = isInits;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getIsActive() {
            return isActive;
        }

        public void setIsActive(String isActive) {
            this.isActive = isActive;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSaleManId() {
            return saleManId;
        }

        public void setSaleManId(String saleManId) {
            this.saleManId = saleManId;
        }

        public String getAgentManId() {
            return agentManId;
        }

        public void setAgentManId(String agentManId) {
            this.agentManId = agentManId;
        }
    }
}
