package org.rechargeplanrecommender.com.rechargeplanrecommender;

/**
 * Created by NOKIA ASHA on 11/04/2015.
 */
public class TimeValue {
    private  String my_num;
    private  String my_operator;
    private String plan;
    private Double local_inter_minute;
    private Double local_inter_sec;
    private  Double std_inter_minute;
    private Double std_inter_sec;
    private Double local_intra_minute;
    private Double local_intra_sec;
    private  Double std_intra_minute;
    private Double std_intra_sec;
    private int period;

    public String getMy_num() {
        return my_num;
    }

    public int getPeriod(){ return period; }

    public void setPeriod(int period){this.period=period;}

    public String getMy_operator() {
        return my_operator;
    }

    public void setMy_operator(String my_operator) {
        this.my_operator = my_operator;
    }

    public void setMy_num(String my_num) {
        this.my_num = my_num;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public Double getLocal_inter_minute() {
        return local_inter_minute;
    }

    public void setLocal_inter_minute(Double local_inter_minute) {
        this.local_inter_minute = local_inter_minute;
    }

    public Double getLocal_inter_sec() {
        return local_inter_sec;
    }

    public void setLocal_inter_sec(Double local_inter_sec) {
        this.local_inter_sec = local_inter_sec;
    }

    public Double getStd_inter_minute() {
        return std_inter_minute;
    }

    public void setStd_inter_minute(Double std_inter_minute) {
        this.std_inter_minute = std_inter_minute;
    }

    public Double getStd_inter_sec() {
        return std_inter_sec;
    }

    public void setStd_inter_sec(Double std_inter_sec) {
        this.std_inter_sec = std_inter_sec;
    }

    public Double getLocal_intra_minute() {
        return local_intra_minute;
    }

    public void setLocal_intra_minute(Double local_intra_minute) {
        this.local_intra_minute = local_intra_minute;
    }

    public Double getLocal_intra_sec() {
        return local_intra_sec;
    }

    public void setLocal_intra_sec(Double local_intra_sec) {
        this.local_intra_sec = local_intra_sec;
    }

    public Double getStd_intra_minute() {
        return std_intra_minute;
    }

    public void setStd_intra_minute(Double std_intra_minute) {
        this.std_intra_minute = std_intra_minute;
    }

    public Double getStd_intra_sec() {
        return std_intra_sec;
    }

    public void setStd_intra_sec(Double std_intra_sec) {
        this.std_intra_sec = std_intra_sec;
    }

}
