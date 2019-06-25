package com.hodo.jjaccount.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;


/**
 * @author Mr.AG
 * @version 2019-04-11 09:03:45
 * @email 463540703@qq.com
 */
@Table(name = "HD_BANK_PENDING")
public class HdBankPending implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键
    @Id
    @GeneratedValue(generator = "UUID")
    private String id;

    //日期
    @Excel(name = "日期", format = "yyyy-MM-dd")
    @Column(name = "ACCOUNT_DATE")
    private Date accountDate;

    //凭证编号
    //@Excel(name="凭证编号")
    @Column(name = "NO")
    private BigDecimal no;

    //对方科目
    @Excel(name = "对方科目")
    @Column(name = "SUBJECTS")
    private String subjects;

    //摘要
    @Excel(name = "摘要")
    @Column(name = "REMARK")
    private String remark;

    //收入
    @Excel(name = "收入")
    @Column(name = "INCOME")
    private BigDecimal income;

    //支出
    @Excel(name = "支出")
    @Column(name = "PAY")
    private BigDecimal pay;

    //结存金额
    //@Excel(name="结存金额")
    @Column(name = "BALANCE")
    private BigDecimal balance;

    //公司名
    @Excel(name = "公司名")
    @Column(name = "COMPANY_NAME")
    private String companyName;

    //创建人名称
    @Column(name = "CREATE_NAME")
    private String createName;

    //创建人登录名称
    @Column(name = "CREATE_BY")
    private String createBy;

    //外转id
    @Column(name = "RID")
    private String rid;

    //内转id
    @Column(name = "SHEETID")
    private String sheetid;

    //银行
    @Excel(name = "银行")
    @Column(name = "BANKNAME")
    private String bankname;

    //我方科目
    @Excel(name = "我方科目")
    @Column(name = "MYSUBJECTS")
    private String mysubjects;

    //九恒星系统内的账单日期不变动
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "SYNACCOUNT_DATE")
    private Date synAccountDate;
    @Column(name = "TENANTID")
    private String tenantId;
    //新增账单类型
    private String accountType;
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * 设置：主键
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取：主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置：日期
     */
    public void setAccountDate(Date accountDate) {
        this.accountDate = accountDate;
    }

    /**
     * 获取：日期
     */
    public Date getAccountDate() {
        return accountDate;
    }

    /**
     * 设置：凭证编号
     */
    public void setNo(BigDecimal no) {
        this.no = no;
    }

    /**
     * 获取：凭证编号
     */
    public BigDecimal getNo() {
        return no;
    }

    /**
     * 设置：对方科目
     */
    public void setSubjects(String subjects) {
        this.subjects = subjects;
    }

    /**
     * 获取：对方科目
     */
    public String getSubjects() {
        return subjects;
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
     * 设置：收入
     */
    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    /**
     * 获取：收入
     */
    public BigDecimal getIncome() {
        return income;
    }

    /**
     * 设置：支出
     */
    public void setPay(BigDecimal pay) {
        this.pay = pay;
    }

    /**
     * 获取：支出
     */
    public BigDecimal getPay() {
        return pay;
    }

    /**
     * 设置：结存金额
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * 获取：结存金额
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * 设置：公司名
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * 获取：公司名
     */
    public String getCompanyName() {
        return companyName;
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
     * 设置：创建人登录名称
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    /**
     * 获取：创建人登录名称
     */
    public String getCreateBy() {
        return createBy;
    }

    /**
     * 设置：外转id
     */
    public void setRid(String rid) {
        this.rid = rid;
    }

    /**
     * 获取：外转id
     */
    public String getRid() {
        return rid;
    }

    /**
     * 设置：内转id
     */
    public void setSheetid(String sheetid) {
        this.sheetid = sheetid;
    }

    /**
     * 获取：内转id
     */
    public String getSheetid() {
        return sheetid;
    }

    /**
     * 设置：银行
     */
    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    /**
     * 获取：银行
     */
    public String getBankname() {
        return bankname;
    }

    /**
     * 设置：我方科目
     */
    public void setMysubjects(String mysubjects) {
        this.mysubjects = mysubjects;
    }

    /**
     * 获取：我方科目
     */
    public String getMysubjects() {
        return mysubjects;
    }

    public Date getSynAccountDate() {
        return synAccountDate;
    }

    public void setSynAccountDate(Date synAccountDate) {
        this.synAccountDate = synAccountDate;
    }
    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
