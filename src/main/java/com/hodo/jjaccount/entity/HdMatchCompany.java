package com.hodo.jjaccount.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;


/**
 * @author Mr.AG
 * @version 2019-04-11 09:03:45
 * @email 463540703@qq.com
 */
@Table(name = "HD_MATCH_COMPANY")
public class HdMatchCompany implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键
    @Id
    @GeneratedValue(generator = "UUID")
    private String id;

    //外部单位抬头
    @Excel(name = "外部单位抬头")
    @Column(name = "CUSTOMER_NAME")
    private String customerName;

    //公司名称
    @Excel(name = "公司名称")
    @Column(name = "COMPANY_NAME")
    private String companyName;

    //内部单位抬头
    @Excel(name = "内部单位抬头")
    @Column(name = "INNER_COMPANY")
    private String innerCompany;
    @Column(name = "TENANTID")
    private String tenantId;
    //创建时间 新增
    @Column(name = "CREATE_TIME")
    private Date createTime;

    //创建人名称 新增
    @Column(name = "CREATE_BY")
    private String createBy;
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
     * 设置：外部单位抬头
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * 获取：外部单位抬头
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * 设置：公司名称
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * 获取：公司名称
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * 设置：内部单位抬头
     */
    public void setInnerCompany(String innerCompany) {
        this.innerCompany = innerCompany;
    }

    /**
     * 获取：内部单位抬头
     */
    public String getInnerCompany() {
        return innerCompany;
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
}
