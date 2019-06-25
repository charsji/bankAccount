package com.hodo.jjaccount.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;


/**
 * @author Mr.AG
 * @version 2019-04-11 09:03:44
 * @email 463540703@qq.com
 */
@Table(name = "HD_NZ_DICT")
public class HdNzDict implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键
    @Id
    @GeneratedValue(generator = "UUID")
    private String id;

    //内转账号
    @Excel(name = "内转账号")
    @Column(name = "NZID")
    private String nzid;

    //外部账户
    @Excel(name = "外部账户")
    @Column(name = "WBZH")
    private String wbzh;
    //银行名称
    @Excel(name = "银行名称")
    @Column(name = "BANK")
    private String bank;

    //外部索引
    //@Excel(name="外部索引")
    @Column(name = "WBSY")
    private String wbsy;

    //账户名称
    @Excel(name = "账户抬头名称")
    @Column(name = "ZHNAME")
    private String zhname;

    @Column(name = "TENANTID")
    private String tenantId;
    //创建日期 新增
    @Column(name = "CREATE_TIME")
    private Date createTime;

    //创建人名称 新增
    @Column(name = "CREATE_BY")
    private String createBy;
    @Column(name = "BANKTYPE")
    private String bankType;
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
     * 设置：内转账号
     */
    public void setNzid(String nzid) {
        this.nzid = nzid;
    }

    /**
     * 获取：内转账号
     */
    public String getNzid() {
        return nzid;
    }

    /**
     * 设置：银行名称
     */
    public void setBank(String bank) {
        this.bank = bank;
    }

    /**
     * 获取：银行名称
     */
    public String getBank() {
        return bank;
    }

    /**
     * 设置：外部索引
     */
    public void setWbsy(String wbsy) {
        this.wbsy = wbsy;
    }

    /**
     * 获取：外部索引
     */
    public String getWbsy() {
        return wbsy;
    }

    /**
     * 设置：外部账户
     */
    public void setWbzh(String wbzh) {
        this.wbzh = wbzh;
    }

    /**
     * 获取：外部账户
     */
    public String getWbzh() {
        return wbzh;
    }

    /**
     * 设置：账户名称
     */
    public void setZhname(String zhname) {
        this.zhname = zhname;
    }

    /**
     * 获取：账户名称
     */
    public String getZhname() {
        return zhname;
    }
    /**
     * 设置：创建日期
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    /**
     * 获取：创建日期
     */
    public Date getCreateTime() {
        return createTime;
    }
    /**
     * 设置：创建人名称
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }
    /**
     * 获取：创建人名称
     */
    public String getCreateBy() {
        return createBy;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }
}
