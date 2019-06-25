package com.hodo.jjaccount.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;


/**
 * @author Mr.AG
 * @version 2019-04-09 16:28:43
 * @email 463540703@qq.com
 */
@Table(name = "HD_COMPANY")
public class HdCompany implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键
    @Id
    @GeneratedValue(generator = "UUID")
    private String id;
    //公司名
    @Excel(name = "公司名")
    @Column(name = "COMPANY_NAME")
    private String companyName;
    @Transient
    private String value;
    @Column(name = "TENANTID")
    private String tenantId;
    //公司编号
    @Column(name = "CODE")
    private String code;
    //创建时间 新增
    @Column(name = "CREATE_TIME")
    private Date createTime;

    //创建人 新增
    @Column(name = "CREATE_BY")
    private String createBy;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    /**
     * 设置：公司编号
     */
    public void setCode(String code) {
        this.code = code;
    }
    /**
     * 获取：公司编号
     */
    public String getCode() {
        return code;
    }
    /**
     * 设置：创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    /**
     * 获取：创建时间
     */
    public Date getCreateTime() {
        return createTime;
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
