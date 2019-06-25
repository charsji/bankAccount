package com.hodo.jjaccount.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;


/**
 * @author Mr.AG
 * @version 2019-04-09 16:29:07
 * @email 463540703@qq.com
 */
@Table(name = "HD_BANK_RECORD")
public class HdBankRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    //主键
    @Id
    //@GeneratedValue(generator = "UUID")
    private String id;

    //创建人名称
    @Column(name = "CREATE_NAME")
    private String createName;

    //创建日期
    @Column(name = "CREATE_DATE")
    private Date createDate;

    //操作类型
    @Excel(name = "操作类型")
    @Column(name = "OPERTYPE")
    private String opertype;

    //凭证编号
    @Excel(name = "凭证编号")
    @Column(name = "NUM")
    private String num;

    //摘要
    @Excel(name = "摘要")
    @Column(name = "REMARK")
    private String remark;

    //公司名
    @Excel(name = "公司名")
    @Column(name = "COMPANYID")
    private String companyid;

    //收支方向(0:收入;1:支出)
    @Excel(name = "收支方向")
    @Column(name = "FLAG")
    private String flag;

    //金额
    @Excel(name = "金额")
    @Column(name = "MONEY")
    private String money;
    @Column(name = "TENANTID")
    private String tenantId;

    //创建人  新增6.24
    @Column(name = "CREATE_BY")
    private String createBy;
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * 设置：
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取：
     */
    public String getId() {
        return id;
    }

    /**
     * 设置：创建人名称
     */
    public void setCreateName(String createName) {
        this.createName = createName;
    }

    /**
     * 获取：创建人名称
     */
    public String getCreateName() {
        return createName;
    }

    /**
     * 设置：创建日期
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * 获取：创建日期
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * 设置：操作类型
     */
    public void setOpertype(String opertype) {
        this.opertype = opertype;
    }

    /**
     * 获取：操作类型
     */
    public String getOpertype() {
        return opertype;
    }

    /**
     * 设置：凭证编号
     */
    public void setNum(String num) {
        this.num = num;
    }

    /**
     * 获取：凭证编号
     */
    public String getNum() {
        return num;
    }

    /**
     * 设置：摘要
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取：摘要
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置：公司名
     */
    public void setCompanyid(String companyid) {
        this.companyid = companyid;
    }

    /**
     * 获取：公司名
     */
    public String getCompanyid() {
        return companyid;
    }

    /**
     * 设置：收支方向
     */
    public void setFlag(String flag) {
        this.flag = flag;
    }

    /**
     * 获取：收支方向
     */
    public String getFlag() {
        return flag;
    }

    /**
     * 设置：金额
     */
    public void setMoney(String money) {
        this.money = money;
    }

    /**
     * 获取：金额
     */
    public String getMoney() {
        return money;
    }

    /**
     * 设置：创建人
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }
    /**
     * 获取：创建人
     */
    public String getCreateBy() {
        return createBy;
    }
}
